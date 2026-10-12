package com.jong.figurepreorderledgerbackend.service.figure.impl;

import com.jong.figurepreorderledgerbackend.common.constant.FigureStatus;
import com.jong.figurepreorderledgerbackend.common.constant.GlobalExceptionCode;
import com.jong.figurepreorderledgerbackend.common.constant.PaymentType;
import com.jong.figurepreorderledgerbackend.common.exception.BusinessException;
import com.jong.figurepreorderledgerbackend.mapper.figure.FigureMapper;
import com.jong.figurepreorderledgerbackend.service.figure.FigureService;
import com.jong.figurepreorderledgerbackend.service.figure.FigureStatusService;
import com.jong.figurepreorderledgerbackend.vo.figure.FigureBulkStatusRequest;
import com.jong.figurepreorderledgerbackend.vo.figure.FigureCurrentStatus;
import com.jong.figurepreorderledgerbackend.vo.figure.FigureListResponse;
import com.jong.figurepreorderledgerbackend.vo.figure.FigureStatusRequest;
import com.jong.figurepreorderledgerbackend.vo.figure.FigureStatusUpdateParam;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FigureStatusServiceImpl implements FigureStatusService {

    private final FigureMapper figureMapper;
    private final FigureService figureService;

    @Override
    @Transactional
    public FigureListResponse change(Long userId, Long figureId, FigureStatusRequest request) {
        FigureCurrentStatus current = figureMapper.selectCurrentStatus(userId, figureId);
        if (current == null) {
            throw new BusinessException(GlobalExceptionCode.NOT_FOUND);
        }
        FigureStatusUpdateParam param = buildParam(userId, current, request.getStatus(), request.getPaymentType(),
                request.getDepositAmount(), request.getDepositPaidAt(), request.getBalanceDueDate(),
                request.getPaidAt(), request.getDeliveredAt());
        figureMapper.updateStatus(param);
        return figureService.getListItem(userId, figureId);
    }

    @Override
    @Transactional
    public List<Long> changeAll(Long userId, FigureBulkStatusRequest request) {
        if (request.getStatus() == FigureStatus.RESERVED) {
            throw new BusinessException(GlobalExceptionCode.BAD_REQUEST, "예약금 결제는 일괄 변경할 수 없습니다");
        }
        List<Long> figureIds = new ArrayList<>(new LinkedHashSet<>(request.getFigureIds()));

        Map<Long, FigureCurrentStatus> currentById = new HashMap<>();
        for (FigureCurrentStatus current : figureMapper.selectCurrentStatusByIds(userId, figureIds)) {
            currentById.put(current.getFigureId(), current);
        }

        List<FigureStatusUpdateParam> params = new ArrayList<>();
        List<Long> problemIds = new ArrayList<>();
        for (Long figureId : figureIds) {
            FigureCurrentStatus current = currentById.get(figureId);
            if (current == null) {
                problemIds.add(figureId);
                continue;
            }
            try {
                params.add(buildParam(userId, current, request.getStatus(), request.getPaymentType(),
                        null, null, null, request.getPaidAt(), request.getDeliveredAt()));
            } catch (BusinessException e) {
                problemIds.add(figureId);
            }
        }
        if (!problemIds.isEmpty()) {
            throw new BusinessException(GlobalExceptionCode.INVALID_STATUS_TRANSITION,
                    "변경할 수 없는 상품이 있어 아무것도 바꾸지 않았습니다. 문제 figureId: " + problemIds);
        }

        for (FigureStatusUpdateParam param : params) {
            figureMapper.updateStatus(param);
        }
        return figureIds;
    }

    /*
     * 전이 규칙을 검사하고 UPDATE에 넘길 값을 만든다. 날짜를 생략하면 서버의 오늘 날짜를 쓴다.
     *  CART → UNPURCHASED     : 결제 방식 없음
     *  UNPURCHASED → RESERVED : 결제 방식 DEPOSIT 필수, 예약금 필수
     *  UNPURCHASED → PURCHASED: 결제 방식 FULL 필수
     *  RESERVED → PURCHASED   : 결제 방식은 이미 정해져 있어 받지 않음
     *  PURCHASED → DELIVERED  : 결제 방식 받지 않음
     */
    private FigureStatusUpdateParam buildParam(Long userId, FigureCurrentStatus current, FigureStatus next,
                                               PaymentType paymentType, Integer depositAmount,
                                               LocalDate depositPaidAt, LocalDate balanceDueDate,
                                               LocalDate paidAt, LocalDate deliveredAt) {
        FigureStatus from = current.getStatus();
        if (!from.canMoveTo(next)) {
            throw new BusinessException(GlobalExceptionCode.INVALID_STATUS_TRANSITION,
                    from + "에서 " + next + "(으)로는 변경할 수 없습니다");
        }

        LocalDate today = LocalDate.now();
        FigureStatusUpdateParam param = new FigureStatusUpdateParam();
        param.setUserId(userId);
        param.setFigureId(current.getFigureId());
        param.setStatus(next);

        if (next == FigureStatus.RESERVED) {
            requirePaymentType(paymentType, PaymentType.DEPOSIT);
            if (depositAmount == null) {
                throw new BusinessException(GlobalExceptionCode.BAD_REQUEST, "예약금(depositAmount)이 필요합니다");
            }
            param.setPaymentType(paymentType);
            param.setDepositAmount(depositAmount);
            param.setDepositPaidAt(depositPaidAt == null ? today : depositPaidAt);
            param.setBalanceDueDate(balanceDueDate);
        } else if (next == FigureStatus.PURCHASED) {
            if (from == FigureStatus.UNPURCHASED) {
                requirePaymentType(paymentType, PaymentType.FULL);
                param.setPaymentType(paymentType);
            } else {
                requireNoPaymentType(paymentType);
            }
            param.setPaidAt(paidAt == null ? today : paidAt);
        } else {
            requireNoPaymentType(paymentType);
            if (next == FigureStatus.DELIVERED) {
                param.setDeliveredAt(deliveredAt == null ? today : deliveredAt);
            }
        }
        return param;
    }

    private void requirePaymentType(PaymentType actual, PaymentType expected) {
        if (actual != expected) {
            throw new BusinessException(GlobalExceptionCode.BAD_REQUEST,
                    "결제 방식(paymentType)은 " + expected + "이어야 합니다");
        }
    }

    private void requireNoPaymentType(PaymentType actual) {
        if (actual != null) {
            throw new BusinessException(GlobalExceptionCode.BAD_REQUEST, "이 전이에서는 결제 방식(paymentType)을 받지 않습니다");
        }
    }
}
