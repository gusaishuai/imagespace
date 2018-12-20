package com.imagespace.sql.service;

import com.imagespace.sql.model.SqlExecResult;
import com.imagespace.sql.model.SqlPagination;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${table.schema}")
    private String tableSchema;

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

    /**
     * 查询所有表
     */
    public List<String> getAllTables() {
        String allTablesSql = "SHOW TABLES";
        return jdbcTemplate.queryForList(allTablesSql, String.class);
    }

    /**
     * 查询表结构
     */
    public List<Map<String, Object>> getTableColumns(String table) {
        String tableColumnSql = String.format("SELECT COLUMN_NAME 列名, COLUMN_COMMENT 列备注, COLUMN_TYPE 列类型, " +
                "COLUMN_DEFAULT 列默认值, IS_NULLABLE 是否可为空, COLUMN_KEY 列索引, EXTRA 其他说明 FROM INFORMATION_SCHEMA.COLUMNS " +
                "WHERE TABLE_SCHEMA = '%s' AND TABLE_NAME = '%s'", tableSchema, table);
        return jdbcTemplate.queryForList(tableColumnSql);
    }

    /**
     * 查询表索引
     */
    public List<Map<String, Object>> getTableIndex(String table) {
        String tableIndexSql = "SHOW INDEX FROM " + table;
        List<Map<String, Object>> indexMapList = jdbcTemplate.queryForList(tableIndexSql);
        List<Map<String, Object>> filterIndexMapList = new ArrayList<>();
        if (CollectionUtils.isEmpty(indexMapList)) {
            return filterIndexMapList;
        }
        for (Map<String, Object> indexMap : indexMapList) {
            Map<String, Object> filterIndexMap = new LinkedHashMap<>();
            filterIndexMap.put("索引名", indexMap.get("Key_name"));
            filterIndexMap.put("列名", indexMap.get("Column_name"));
            filterIndexMap.put("唯一索引（0是 1否）", indexMap.get("Non_unique"));
            filterIndexMap.put("索引序列", indexMap.get("Seq_in_index"));
            filterIndexMap.put("索引类型", indexMap.get("Index_type"));
            filterIndexMap.put("索引备注", indexMap.get("Index_comment"));
            filterIndexMapList.add(filterIndexMap);
        }
        return filterIndexMapList;
    }

    /**
     * 预检测
     */
    public void preCheck(String sql) {
        //预检测
        jdbcTemplate.execute(sql);
    }

    /**
     * 导出
     */
    public String exportQuery(String sql) {
        // 执行sql
        List<Map<String, Object>> mapList = jdbcTemplate.queryForList(sql);
        // tab分隔
        StringBuilder sb = new StringBuilder();
        if (CollectionUtils.isEmpty(mapList)) {
            return sb.toString();
        }
        for (String key : mapList.get(0).keySet()) {
            sb.append(key).append("\t");
        }
        int i = sb.lastIndexOf("\t");
        sb.replace(i, i+1, "\n");
        for (Map<String, Object> map : mapList) {
            for (Object value : map.values()) {
                sb.append(value).append("\t");
            }
            int j = sb.lastIndexOf("\t");
            sb.replace(j, j+1, "\n");
        }
        int k = sb.lastIndexOf("\n");
        sb.delete(k, k+1);
        return sb.toString();
    }

}
