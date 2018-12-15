package com.gss.service;

import com.gss.service.common.XSqlKeyWord;
import com.gss.service.common.XSqlPagination;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author gusaishuai
 * @since 17/4/30
 */
@Service
public class XSqlService {

    private static final Logger logger = LoggerFactory.getLogger(XSqlService.class);

    // 超时时间（秒）
    private final int timeout = 30;
    // 最大并发执行数
    private final int maxNum = 4;
    // 数据库名称
    private final String tableSchema = "checkup";
    // 防止并发执行
    private AtomicInteger execNum = new AtomicInteger(0);
    // 同一个线程中获取对应的分页信息，可以减少参数的传递
    private ThreadLocal<XSqlPagination> threadLocalPagination = new ThreadLocal<>();

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public XSqlService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        // 30秒超时
        this.jdbcTemplate.setQueryTimeout(timeout);
    }

    /**
     * 分页
     */
    public List<Map<String, Object>> execByPage(String sql, int pageNo) {
        if (XSqlKeyWord.SELECT.start(sql)) {
            if (sql.contains(";")) {
                sql = StringUtils.trim(sql);
                sql = sql.substring(0, sql.indexOf(";"));
            }
            String countSql = "SELECT COUNT(1) FROM (" + sql + ") AS TOTAL";
            // 查询总数
            List<Map<String, Object>> countMapList = exec(countSql);
            if (CollectionUtils.isEmpty(countMapList)) {
                return Collections.emptyList();
            }
            int totalCount = Integer.valueOf(countMapList.get(0).get("count(1)").toString());
            XSqlPagination pagination = new XSqlPagination(pageNo, totalCount);
            threadLocalPagination.set(pagination);
            sql += " LIMIT " + pagination.getStart() + "," + pagination.getPageSize();
        }
        return exec(sql);
    }

    /**
     * 导出
     */
    public String export(String sql) {
        // 执行sql
        List<Map<String, Object>> mapList = exec(sql);
        // tab分隔
        StringBuilder sb = new StringBuilder();
        if (!CollectionUtils.isEmpty(mapList)) {
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
        }
        return sb.toString();
    }

    /**
     * 获取分页html
     */
    public String getPageHtml(String url) {
        try {
            XSqlPagination pagination = threadLocalPagination.get();
            return pagination == null ? "" : pagination.getPageHtml(url);
        } finally {
            threadLocalPagination.remove();
        }
    }

    /**
     * 执行sql
     */
    public List<Map<String, Object>> exec(String sql) {
        try {
            // 同时只允许4个线程执行
            if (execNum.incrementAndGet() > maxNum) {
                throw new IllegalThreadStateException("操作太频繁啦，请刷新后重试");
            }
            if (StringUtils.isBlank(sql)) {
                throw new IllegalArgumentException("sql不能为空");
            }
            String[] sqlEachArray = sql.split(";");
            XSqlKeyWord sqlKeyWordFirst = null;
            for (String sqlEach : sqlEachArray) {
                for (XSqlKeyWord sqlKeyWord : XSqlKeyWord.values()) {
                    if (sqlKeyWord.isPrefix() && sqlKeyWord.start(sqlEach)) {
                        if (sqlKeyWordFirst == null) {
                            sqlKeyWordFirst = sqlKeyWord;
                        } else if (!Objects.equals(sqlKeyWordFirst.getCategory(), sqlKeyWord.getCategory())) {
                            throw new IllegalArgumentException("执行sql中有不同范畴(查询或修改)的sql");
                        } else if ((Objects.equals(sqlKeyWordFirst.getCategory(), sqlKeyWord.getCategory()))
                                && StringUtils.equals(sqlKeyWord.getCategory(), "SELECT")) {
                            throw new IllegalArgumentException("查询语句一次只能执行一条");
                        } else if (sqlKeyWord.isNeedWhere() && !XSqlKeyWord.WHERE.contain(sqlEach)) {
                            throw new IllegalArgumentException("删改语句必须增加WHERE条件");
                        }
                        break;
                    }
                }
            }
            if (XSqlKeyWord.SELECT.start(sql) || XSqlKeyWord.SHOW.start(sql)) {
                // 查询
                return jdbcTemplate.queryForList(StringUtils.trim(sqlEachArray[0]));
            } else if (XSqlKeyWord.INSERT.start(sql) || XSqlKeyWord.UPDATE.start(sql)
                    || XSqlKeyWord.DELETE.start(sql)) {
                List<Map<String, Object>> updateMapList = new ArrayList<>();
                for (String sqlEach : sqlEachArray) {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("sql", sqlEach);
                    try {
                        // 更新，包含插入、更新和删除
                        int count = jdbcTemplate.update(StringUtils.trim(sqlEach));
                        map.put("count", count);
                        map.put("error", "");
                    } catch (Exception e) {
                        logger.error("[xsql] XSqlService.exec error : " + ExceptionUtils.getStackTrace(e));
                        map.put("count", 0);
                        map.put("error", e.getMessage());
                    }
                    updateMapList.add(map);
                }
                return updateMapList;
            } else {
                // 其他语句不支持
                throw new IllegalArgumentException("不合法的语句");
            }
        } finally {
            execNum.decrementAndGet();
        }
    }

    /**
     * 查询所有表
     */
    public List<Object> getAllTables() {
        String allTablesSql = "SHOW TABLES";
        List<Map<String, Object>> tableMapList = exec(allTablesSql);
        List<Object> tableList = new ArrayList<>();
        if (CollectionUtils.isEmpty(tableMapList)) {
            return tableList;
        }
        for (Map<String, Object> tableMap : tableMapList) {
            tableList.addAll(tableMap.values());
        }
        return tableList;
    }

    public List<Map<String, Object>> getTableColumns(String table) {
        String tableColumnSql = "SELECT COLUMN_NAME 列名, COLUMN_COMMENT 列备注, COLUMN_TYPE 列类型, " +
                "COLUMN_DEFAULT 列默认值, IS_NULLABLE 是否可为空, COLUMN_KEY 列索引, EXTRA 其他说明 FROM INFORMATION_SCHEMA.COLUMNS " +
                "WHERE TABLE_SCHEMA = '" + tableSchema + "' AND TABLE_NAME = '" + table + "'";
        return exec(tableColumnSql);
    }

    public List<Map<String, Object>> getTableIndex(String table) {
        String tableIndexSql = "SHOW INDEX FROM " + table;
        List<Map<String, Object>> indexMapList = exec(tableIndexSql);
        List<Map<String, Object>> filterIndexMapList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(indexMapList)) {
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
        }
        return filterIndexMapList;
    }

}
