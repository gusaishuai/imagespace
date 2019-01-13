package com.imagespace.user.dao;

import com.imagespace.common.model.Page;
import com.imagespace.common.model.Pagination;
import com.imagespace.user.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author gusaishuai
 * @since 18/12/16
 */
@Mapper
public interface UserDao {

    User queryById(Long id);

    User queryByLoginName(String loginName);

    void updatePwd(@Param("userId") Long userId, @Param("password") String password);

    List<User> queryUserByPage(@Param("loginName") String loginName, @Param("pagination") Pagination pagination);

    void deleteById(Long userId);

}
