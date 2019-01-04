package com.imagespace.excel.model;

import java.util.List;

/**
 * @author gusaishuai
 * @since 2019/1/4
 */
public class ExcelRow {

    //行序号（数字）
    private int index;
    private List<ExcelCol> colList;

    public ExcelRow(int index, List<ExcelCol> colList) {
        this.index = index;
        this.colList = colList;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<ExcelCol> getColList() {
        return colList;
    }

    public void setColList(List<ExcelCol> colList) {
        this.colList = colList;
    }
}
