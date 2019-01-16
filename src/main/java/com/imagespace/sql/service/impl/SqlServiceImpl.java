package com.imagespace.sql.service.impl;

import com.alibaba.fastjson.JSON;
import com.imagespace.common.model.Page;
import com.imagespace.common.service.impl.RedisPool;
import com.imagespace.sql.service.SqlService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author gusaishuai
 * @since 2018/12/16
 */
@Component
public class SqlServiceImpl implements SqlService {

    @Value("${sql.table.schema}")
    private String tableSchema;
    @Value("${sql.export.limit}")
    private String exportLimit;

    private final JdbcTemplate jdbcTemplate;
    private final RedisPool redisPool;

    @Autowired
    public SqlServiceImpl(JdbcTemplate jdbcTemplate, RedisPool redisPool) {
        this.jdbcTemplate = jdbcTemplate;
        // 10秒超时
        this.jdbcTemplate.setQueryTimeout(10);
        this.redisPool = redisPool;
    }

    /**
     * 查询
     */
    public Page<Map<String, Object>> select(String sql, int pageNo) {
        sql = StringUtils.trim(sql).split(";")[0];
        int totalCount = getTotalCount(sql);
        Page<Map<String, Object>> execPage = new Page<>(pageNo, 15);
        execPage.setTotalCount(totalCount);
        List<Map<String, Object>> resultList;
        if (totalCount == 0) {
            resultList = new ArrayList<>();
        } else {
            // 查询
            sql = String.format("%s LIMIT %s,%s", sql, execPage.start(), execPage.getPageSize());
            resultList = jdbcTemplate.queryForList(sql);
        }
        execPage.setList(resultList);
        return execPage;
    }

    /**
     * 获取总数
     */
    private int getTotalCount(String sql) {
        String countSql = String.format("SELECT COUNT(1) FROM (%s) AS TOTAL", sql);
        // 查询总数
        Map<String, Object> countMap = jdbcTemplate.queryForMap(countSql);
        return CollectionUtils.isEmpty(countMap) ? 0 : Integer.valueOf(countMap.get("count(1)").toString());
    }

    /**
     * 新增、更新、删除
     */
    public Page<Map<String, Object>> update(String sql) {
        // 更新，包含插入、更新和删除
        int count = jdbcTemplate.update(StringUtils.trim(sql).split(";")[0]);
        Page<Map<String, Object>> execPage = new Page<>(1, 15);
        execPage.setTotalCount(1);
        Map<String, Object> map = new HashMap<>();
        map.put("count", count);
        map.put("time", DateFormatUtils.format(Calendar.getInstance(), "yyyy-MM-dd HH:mm:ss"));
        execPage.setList(Collections.singletonList(map));
        return execPage;
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
     * 导出预检测
     */
    public String preExportQuery(String sql) {
        sql = StringUtils.trim(sql).split(";")[0];
        int totalCount = getTotalCount(sql);
        if (totalCount > Integer.valueOf(exportLimit)) {
            throw new IllegalArgumentException(String.format(
                    "查询到：%s条数据，大于系统设定最大值：%s，请缩小范围", totalCount, exportLimit));
        }
        // 执行sql
        List<Map<String, Object>> mapList = jdbcTemplate.queryForList(sql);
        String exportUuid = UUID.randomUUID().toString();
        redisPool.set(exportUuid, JSON.toJSONString(mapList), 60);
        return exportUuid;
    }

    /**
     * 导出
     */
    public String exportQuery(String exportId) {
        String mapListJson = redisPool.get(exportId);
        // tab分隔
        StringBuilder sb = new StringBuilder();
        List<Map> mapList = JSON.parseArray(mapListJson, Map.class);
        if (CollectionUtils.isEmpty(mapList)) {
            return sb.toString();
        }
        for (Object key : mapList.get(0).keySet()) {
            sb.append(key).append("\t");
        }
        int i = sb.lastIndexOf("\t");
        sb.replace(i, i+1, "\n");
        for (Map map : mapList) {
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
