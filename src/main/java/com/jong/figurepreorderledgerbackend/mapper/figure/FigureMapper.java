package com.jong.figurepreorderledgerbackend.mapper.figure;

import com.jong.figurepreorderledgerbackend.common.constant.FigureStatus;
import com.jong.figurepreorderledgerbackend.vo.figure.FigureResponseVO;
import com.jong.figurepreorderledgerbackend.vo.figure.FigureSaveRequestVO;
import com.jong.figurepreorderledgerbackend.vo.figure.FigureVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FigureMapper {

    List<FigureResponseVO> selectList(@Param("userId") Long userId);

    FigureResponseVO selectListById(@Param("userId") Long userId, @Param("figureId") Long figureId);

    FigureResponseVO selectDetailById(@Param("userId") Long userId, @Param("figureId") Long figureId);

    int insert(@Param("userId") Long userId, @Param("status") FigureStatus status,
               @Param("request") FigureSaveRequestVO request);

    Long selectLastInsertId();

    int update(@Param("userId") Long userId, @Param("figureId") Long figureId, @Param("request") FigureSaveRequestVO request);

    int delete(@Param("userId") Long userId, @Param("figureId") Long figureId);

    int deleteByIds(@Param("userId") Long userId, @Param("figureIds") List<Long> figureIds);

    FigureVO selectCurrentStatus(@Param("userId") Long userId, @Param("figureId") Long figureId);

    List<FigureVO> selectCurrentStatusByIds(@Param("userId") Long userId, @Param("figureIds") List<Long> figureIds);

    int updateStatus(FigureVO param);
}
