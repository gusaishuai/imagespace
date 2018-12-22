package com.imagespace.menu.service;

import com.imagespace.menu.model.Menu;

import java.util.List;

public interface MenuService {

    List<Menu> queryByUserId(Long userId);

}
