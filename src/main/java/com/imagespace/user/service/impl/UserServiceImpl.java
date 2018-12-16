package com.imagespace.user.service.impl;

import com.imagespace.user.dao.UserDao;
import com.imagespace.user.model.User;
import com.imagespace.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author gusaishuai
 * @since 18/12/16
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public User queryById(Long id) {
        return userDao.queryById(id);
    }

    @Override
    public User queryByLoginName(String loginName) {
        return userDao.queryByLoginName(loginName);
    }

}
