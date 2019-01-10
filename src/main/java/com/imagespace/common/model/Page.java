package com.imagespace.common.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author gusaishuai
 * @since 2019/1/10
 */
@Setter
@Getter
public class Page<T> {

    /**
     * 当前页数
     */
    private int pageNo;
    /**
     * 每页展示
     */
    private int pageSize;
    private int totalCount;
    private List<T> list;

}
