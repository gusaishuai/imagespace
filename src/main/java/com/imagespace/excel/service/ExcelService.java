package com.imagespace.excel.service;

import com.imagespace.excel.model.ExcelFilterRule;
import com.imagespace.excel.model.ExcelFilterRuleDetail;
import com.imagespace.excel.model.ExcelModel;

import java.io.File;
import java.util.List;

public interface ExcelService {

    /**
     * 通过过滤规则查询EXCEL
     */
    ExcelModel filterExcel(File excel, String expr, int sheetNum, int topNum, int pageNo);

    /**
     * 获取用户下的过滤规则
     */
    List<ExcelFilterRule> getFilterRuleList(Long userId);

    /**
     * 根据规则ID查询规则列表
     */
    List<ExcelFilterRuleDetail> getFilterRuleDetailList(Long ruleId);

}
