package com.imagespace.sql.model;

import com.imagespace.common.model.Pagination;
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
public class SqlExecVo implements Serializable {

    private static final long serialVersionUID = -7788986212759792736L;

    private Pagination pagination;
    private List<Map<String, Object>> resultList;

    public SqlExecVo(Pagination pagination, List<Map<String, Object>> resultList) {
        this.pagination = pagination;
        this.resultList = resultList;
    }

}
