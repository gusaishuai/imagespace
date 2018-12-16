package com.imagespace.sql.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author gusaishuai
 * @since 2018/12/16
 */
@Setter
@Getter
public class SqlExecResult implements Serializable {

    private static final long serialVersionUID = -7788986212759792736L;

    private SqlPagination pagination;
    private List<Map<String, Object>> resultList;

    public SqlExecResult(SqlPagination pagination, List<Map<String, Object>> resultList) {
        this.pagination = pagination;
        this.resultList = resultList;
    }

}