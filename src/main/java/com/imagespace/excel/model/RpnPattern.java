package com.imagespace.excel.model;

/**
 * @author gusaishuai
 * @since 2018/12/29
 */
public enum RpnPattern {

    LEFT_BRACKET('(', 2),
    RIGHT_BRACKET(')', 0),
    AND('&', 1),
    OR('|', 1);

    RpnPattern(char pattern, int priority) {
        this.pattern = pattern;
        this.priority = priority;
    }

    private char pattern;
    private int priority;

    public char getPattern() {
        return pattern;
    }

    public void setPattern(char pattern) {
        this.pattern = pattern;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

}
