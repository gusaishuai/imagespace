package com.imagespace.user.service.impl;

import com.imagespace.common.model.Page;
import com.imagespace.common.model.Pagination;
import com.imagespace.user.dao.UserDao;
import com.imagespace.user.dao.UserMenuDao;
import com.imagespace.user.model.User;
import com.imagespace.user.model.UserMenu;
import com.imagespace.user.service.UserService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gusaishuai
 * @since 18/12/16
 */
@Component
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final UserMenuDao userMenuDao;

    @Autowired
    public UserServiceImpl(UserDao userDao, UserMenuDao userMenuDao) {
        this.userDao = userDao;
        this.userMenuDao = userMenuDao;
    }

    @Override
    public User queryById(Long id) {
        return userDao.queryById(id);
    }

    @Override
    public User queryByLoginName(String loginName) {
        return userDao.queryByLoginName(loginName);
    }

    @Override
    public void updatePwd(Long userId, String password) {
        userDao.updatePwd(userId, password);
    }

    @Override
    public Page<User> queryUserByPage(String loginName, Pagination pagination) {
        List<User> userList = userDao.queryUserByPage(loginName, pagination);
        Page<User> userPage = new Page<>(pagination.getPageNo(), pagination.getPageSize());
        userPage.setTotalCount(pagination.getTotalCount());
        userPage.setList(userList);
        return userPage;
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        try {
            userDao.deleteById(userId);
            //删除用户拥有的菜单
            userMenuDao.deleteByUserId(userId);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public void addUserMenu(Long userId, List<Long> menuIdList) {
        try {
            //先删除，后插入
            userMenuDao.deleteByUserId(userId);
            if (CollectionUtils.isNotEmpty(menuIdList)) {
                List<UserMenu> userMenuList = new ArrayList<>();
                menuIdList.forEach(r -> {
                    UserMenu userMenu = new UserMenu();
                    userMenu.setUserId(userId);
                    userMenu.setMenuId(r);
                    userMenuList.add(userMenu);
                });
                userMenuDao.insertBatch(userMenuList);
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addUser(User user) {
        int count = userDao.countByLoginName(user.getLoginName());
        if (count > 0) {
            throw new IllegalArgumentException(String.format("用户名：%s 已经存在", user.getLoginName()));
        }
        userDao.insert(user);
    }

}
