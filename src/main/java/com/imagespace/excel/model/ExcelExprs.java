package com.imagespace.excel.model;

import java.util.List;

/**
 * @author gusaishuai
 * @since 2018/12/29
 */
public class ExcelExprs {

    private String expr;
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
