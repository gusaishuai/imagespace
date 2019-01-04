package com.imagespace.excel.model;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * @author gusaishuai
 * @since 2019/1/4
 */
public class ExcelModel {

    //excel数据总数（不是过滤下来的总数）
    private int totalCount;
    private List<ExcelRow> excelRowList;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<ExcelRow> getExcelRowList() {
        return excelRowList;
    }

    public void setExcelRowList(List<ExcelRow> excelRowList) {
        this.excelRowList = excelRowList;
    }

    public int getFilterCount() {
        return CollectionUtils.isEmpty(excelRowList) ? 0 : excelRowList.size();
    }
}
