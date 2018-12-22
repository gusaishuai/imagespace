package com.imagespace.user.dao;

import com.imagespace.user.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author gusaishuai
 * @since 18/12/16
 */
@Mapper
public interface UserDao {

    User queryById(Long id);

    User queryByLoginName(String loginName);

    void updatePwd(@Param("userId") Long userId, @Param("password") String password);

}
