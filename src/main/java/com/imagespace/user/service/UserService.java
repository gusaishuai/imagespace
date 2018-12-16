package com.imagespace.user.service;

import com.imagespace.user.model.User;

/**
 * @author gusaishuai
 * @since 18/12/16
 */
public interface UserService {

    User queryById(Long id);

    User queryByLoginName(String loginName);

}
