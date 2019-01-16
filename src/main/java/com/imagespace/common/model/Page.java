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

    public Page(int pageNo, int pageSize) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    public int start() {
        return pageNo > 0 ? (pageNo - 1) * pageSize : 0;
    }

    public int end() {
        return pageNo > 0 ? pageNo * pageSize : 0;
    }

}
