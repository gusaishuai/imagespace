package com.imagespace.excel.service;

import com.imagespace.common.model.Page;
import com.imagespace.excel.model.ExcelFilterRule;
import com.imagespace.excel.model.ExcelFilterRuleDetail;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface ExcelService {

    /**
     * 通过过滤规则查询EXCEL
     */
    Page<Map<String, String>> filterExcel(File excel, String expr, int sheetNum, int topNum, int pageNo);

    /**
     * 获取用户下的过滤规则
     */
    List<ExcelFilterRule> getFilterRuleList(Long userId);

    /**
     * 根据规则ID查询规则列表
     */
    List<ExcelFilterRuleDetail> getFilterRuleDetailList(Long ruleId);

    /**
     * 过滤规则更新
     */
    void updateFilterRule(ExcelFilterRule filterRule, List<ExcelFilterRuleDetail> filterRuleDetailList);

    /**
     * 过滤规则删除
     */
    void deleteFilterRule(Long ruleId);

}
