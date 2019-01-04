package com.imagespace.excel.model;

/**
 * @author gusaishuai
 * @since 2019/1/4
 */
public class ExcelCol {

    //列序号（字母，如：AA）
    private String index;
    private String value;

    public ExcelCol(String index, String value) {
        this.index = index;
        this.value = value;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
