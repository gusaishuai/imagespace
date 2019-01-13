package com.imagespace.user.service;

import com.imagespace.common.model.Page;
import com.imagespace.common.model.Pagination;
import com.imagespace.user.model.User;

import java.util.List;

/**
 * @author gusaishuai
 * @since 18/12/16
 */
public interface UserService {

    User queryById(Long id);

    User queryByLoginName(String loginName);

    void updatePwd(Long userId, String password);

    Page<User> queryUserByPage(String loginName, Pagination pagination);

    void deleteUser(Long userId);

    void addUserMenu(Long userId, List<Long> menuIdList);

    void addUser(User user);

}
