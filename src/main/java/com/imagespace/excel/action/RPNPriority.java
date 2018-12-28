package com.imagespace.excel.action;

import org.apache.commons.lang3.StringUtils;

/**
 * @author gusaishuai
 * @since 2018/12/26
 */
public enum RPNPriority {

    L_PARENTHESIS('(', 2),
    R_PARENTHESIS(')', 0),
    AND('&', 1),
    OR('|', 1);

    RPNPriority(char pattern, int priority) {
        this.pattern = pattern;
        this.priority = priority;
    }

    public static boolean isOperator(char pattern) {
        for (RPNPriority rpnPriority : RPNPriority.values()) {
            if (pattern == rpnPriority.getPattern()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isOperator(String pattern) {
        for (RPNPriority rpnPriority : RPNPriority.values()) {
            if (StringUtils.equals(pattern, ""+rpnPriority.getPattern())) {
                return true;
            }
        }
        return false;
    }

    public static int cmp(char c1, char c2) {
        int p1 = 0;
        int p2 = 0;
        for (RPNPriority o : RPNPriority.values()) {
            if (o.getPattern() == c1) {
                p1 = o.priority;
            }
            if (o.getPattern() == c2) {
                p2 = o.priority;
            }
        }
        return p1 - p2;
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
