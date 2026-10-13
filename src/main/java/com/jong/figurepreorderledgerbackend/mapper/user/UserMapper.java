package com.jong.figurepreorderledgerbackend.mapper.user;

import com.jong.figurepreorderledgerbackend.vo.user.UserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/* users 테이블 자체라 로그인과 가입 중복 확인은 user_id 없이 로그인 아이디, 이메일로 조회한다 */
@Mapper
public interface UserMapper {

    UserVO selectByLoginId(@Param("loginId") String loginId);

    UserVO selectByEmail(@Param("email") String email);

    UserVO selectById(@Param("userId") Long userId);

    int insert(@Param("loginId") String loginId, @Param("email") String email,
               @Param("name") String name, @Param("passwordHash") String passwordHash);

    Long selectLastInsertId();

    int delete(@Param("userId") Long userId);
}
