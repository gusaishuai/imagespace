package com.imagespace.excel.model;

import com.imagespace.common.model.Pagination;

import java.util.List;
import java.util.Map;

/**
 * @author gusaishuai
 * @since 2019/1/4
 */
public class ExcelModel {

    //分页信息
    private Pagination pagination;
    //EXCEL信息
    private List<Map<String, String>> excelDataList;

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public List<Map<String, String>> getExcelDataList() {
        return excelDataList;
    }

    public void setExcelDataList(List<Map<String, String>> excelDataList) {
        this.excelDataList = excelDataList;
    }
}
