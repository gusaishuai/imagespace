package com.imagespace.menu.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author gusaishuai
 * @since 18/12/22
 */
@Setter
@Getter
public class MenuVo {

    private List<Menu> menuList;
    private String nick;

}
