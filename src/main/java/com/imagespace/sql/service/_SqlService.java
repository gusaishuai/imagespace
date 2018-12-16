package com.imagespace.sql.service;

import com.imagespace.sql.model.SqlExecResult;
import com.imagespace.sql.model.SqlPagination;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author gusaishuai
 * @since 2018/12/16
 */
@Service
public class _SqlService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public _SqlService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        // 10秒超时
        this.jdbcTemplate.setQueryTimeout(10);
    }

    /**
     * 查询
     */
    public SqlExecResult select(String sql, int pageNo) {
        sql = StringUtils.trim(sql).split(";")[0];
        String countSql = String.format("SELECT COUNT(1) FROM (%s) AS TOTAL", sql);
        // 查询总数
        Map<String, Object> countMap = jdbcTemplate.queryForMap(countSql);
        int totalCount = CollectionUtils.isEmpty(countMap) ? 0 : Integer.valueOf(countMap.get("count(1)").toString());
        SqlPagination pagination = new SqlPagination(pageNo, totalCount);
        List<Map<String, Object>> resultList;
        if (totalCount == 0) {
            resultList = new ArrayList<>();
        } else {
            // 查询
            sql = String.format("%s LIMIT %s,%s", sql, pagination.start(), pagination.getPageSize());
            resultList = jdbcTemplate.queryForList(sql);
        }
        return new SqlExecResult(pagination, resultList);
    }

    /**
     * 新增、更新、删除
     */
    public SqlExecResult update(String sql) {
        // 更新，包含插入、更新和删除
        int count = jdbcTemplate.update(StringUtils.trim(sql).split(";")[0]);
        SqlPagination pagination = new SqlPagination(1, 1);
        Map<String, Object> map = new HashMap<>();
        map.put("count", count);
        map.put("time", DateFormatUtils.format(Calendar.getInstance(), "yyyy-MM-dd HH:mm:ss"));
        List<Map<String, Object>> resultList = Collections.singletonList(map);
        return new SqlExecResult(pagination, resultList);
    }

}
