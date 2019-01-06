package com.imagespace.excel.model;

/**
 * @author gusaishuai
 * @since 2018/12/29
 */
public class ExcelExpr {

    //列数
    private int colNum;
    //是否满足
    private boolean matched;
    //值或正则表达式
    private String regex;

    public int getColNum() {
        return colNum;
    }

    public void setColNum(int colNum) {
        this.colNum = colNum;
    }

    public boolean isMatched() {
        return matched;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }
}
