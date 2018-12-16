package com.imagespace.user.dao;

import com.imagespace.user.model.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author gusaishuai
 * @since 18/12/16
 */
@Mapper
public interface UserDao {

    User queryById(Long id);

    User queryByLoginName(String loginName);

}
