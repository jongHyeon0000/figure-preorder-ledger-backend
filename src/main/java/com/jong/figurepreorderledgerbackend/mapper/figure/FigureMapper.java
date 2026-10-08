package com.jong.figurepreorderledgerbackend.mapper.figure;

import com.jong.figurepreorderledgerbackend.common.constant.FigureStatus;
import com.jong.figurepreorderledgerbackend.vo.figure.FigureCreateRequest;
import com.jong.figurepreorderledgerbackend.vo.figure.FigureCurrentStatus;
import com.jong.figurepreorderledgerbackend.vo.figure.FigureDetailResponse;
import com.jong.figurepreorderledgerbackend.vo.figure.FigureListResponse;
import com.jong.figurepreorderledgerbackend.vo.figure.FigureStatusUpdateParam;
import com.jong.figurepreorderledgerbackend.vo.figure.FigureUpdateRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FigureMapper {

    List<FigureListResponse> selectList(@Param("userId") Long userId);

    FigureListResponse selectListById(@Param("userId") Long userId, @Param("id") Long id);

    FigureDetailResponse selectDetailById(@Param("userId") Long userId, @Param("id") Long id);

    int insert(@Param("userId") Long userId, @Param("status") FigureStatus status,
               @Param("request") FigureCreateRequest request);

    Long selectLastInsertId();

    int update(@Param("userId") Long userId, @Param("id") Long id, @Param("request") FigureUpdateRequest request);

    int delete(@Param("userId") Long userId, @Param("id") Long id);

    int deleteByIds(@Param("userId") Long userId, @Param("ids") List<Long> ids);

    FigureCurrentStatus selectCurrentStatus(@Param("userId") Long userId, @Param("id") Long id);

    List<FigureCurrentStatus> selectCurrentStatusByIds(@Param("userId") Long userId, @Param("ids") List<Long> ids);

    int updateStatus(FigureStatusUpdateParam param);
}
