package com.imagespace.menu.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author gusaishuai
 * @since 18/12/22
 */
@Setter
@Getter
public class Menu {

    private Long id;
    private String name;
    /**
     * 路由
     */
    private String route;
    private String logo;
    /**
     * 排序，从小到大
     */
    private Integer sort;

}
