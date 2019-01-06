package com.imagespace.menu.service.impl;

import com.imagespace.menu.dao.MenuDao;
import com.imagespace.menu.model.Menu;
import com.imagespace.menu.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author gusaishuai
 * @since 2018/12/22
 */
@Component
public class MenuServiceImpl implements MenuService {

    @Autowired
    private MenuDao menuDao;

    @Override
    public List<Menu> queryByUserId(Long userId) {
        return menuDao.queryByUserId(userId);
    }

}
