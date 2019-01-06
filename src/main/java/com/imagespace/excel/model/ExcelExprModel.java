package com.imagespace.excel.model;

import java.util.List;

/**
 * @author gusaishuai
 * @since 2018/12/29
 */
public class ExcelExprModel {

    //表达式，例如：(1&2)|(3&4)
    private String expr;
    //EXCEL表达式对象列表，按顺序对应表达式的1、2、3、4...
    private List<ExcelExpr> excelExprList;

    public String getExpr() {
        return expr;
    }

    public void setExpr(String expr) {
        this.expr = expr;
    }

    public List<ExcelExpr> getExcelExprList() {
        return excelExprList;
    }

    public void setExcelExprList(List<ExcelExpr> excelExprList) {
        this.excelExprList = excelExprList;
    }
}
