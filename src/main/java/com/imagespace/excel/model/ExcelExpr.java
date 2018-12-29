package com.imagespace.excel.model;

/**
 * @author gusaishuai
 * @since 2018/12/29
 */
public class ExcelExpr {

    //列数
    private int colNum;
    //是否满足
    private boolean match;
    //值或正则表达式
    private String regex;

    public int getColNum() {
        return colNum;
    }

    public void setColNum(int colNum) {
        this.colNum = colNum;
    }

    public boolean isMatch() {
        return match;
    }

    public void setMatch(boolean match) {
        this.match = match;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }
}
