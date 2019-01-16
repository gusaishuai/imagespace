package com.imagespace.sql.service;

import com.imagespace.common.model.Page;

import java.util.List;
import java.util.Map;

/**
 * @author gusaishuai
 * @since 2018/12/16
 */
public interface SqlService {

    /**
     * 查询
     */
    Page<Map<String, Object>> select(String sql, int pageNo);

    /**
     * 新增、更新、删除
     */
    Page<Map<String, Object>> update(String sql);

    /**
     * 查询所有表
     */
    List<String> getAllTables();

    /**
     * 查询表结构
     */
    List<Map<String, Object>> getTableColumns(String table);

    /**
     * 查询表索引
     */
    List<Map<String, Object>> getTableIndex(String table);

    /**
     * 导出预检测
     */
    String preExportQuery(String sql);

    /**
     * 导出
     */
    String exportQuery(String exportId);

}
