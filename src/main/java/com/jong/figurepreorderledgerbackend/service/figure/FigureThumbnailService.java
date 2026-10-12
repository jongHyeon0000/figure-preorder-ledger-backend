package com.jong.figurepreorderledgerbackend.service.figure;

import com.jong.figurepreorderledgerbackend.vo.figure.FigureResponseVO;
import org.springframework.web.multipart.MultipartFile;

public interface FigureThumbnailService {

    /*
     * 상품의 썸네일을 올린다. 이미 있으면 새 파일로 바꾸고 옛 파일을 지운다.
     * 확장자와 파일 내용(앞부분 바이트)이 모두 허용된 이미지여야 한다.
     * 변경된 상품 한 건을 목록 항목 모양으로 돌려준다.
     */
    FigureResponseVO upload(Long userId, Long figureId, MultipartFile file);

    /* 상품의 썸네일을 지운다(파일과 행). 썸네일이 없어도 오류 없이 상품 한 건을 돌려준다 */
    FigureResponseVO delete(Long userId, Long figureId);
}
