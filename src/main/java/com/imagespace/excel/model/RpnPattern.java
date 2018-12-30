package com.imagespace.excel.model;

import com.imagespace.excel.action.RPNPriority;
import org.apache.commons.lang3.StringUtils;

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

    /**
     * 是否运算符
     */
    public static boolean isPattern(char pattern) {
        for (RpnPattern rpnPattern : RpnPattern.values()) {
            if (pattern == rpnPattern.getPattern()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否运算符
     */
    public static boolean isPattern(String pattern) {
        return StringUtils.isNotBlank(pattern)
                && pattern.toCharArray().length == 1 && isPattern(pattern.charAt(0));
    }

    /**
     * 计算优先级
     */
    public static int calcPriority(char c1, char c2) {
        int p1 = 0;
        int p2 = 0;
        for (RpnPattern o : RpnPattern.values()) {
            if (o.getPattern() == c1) {
                p1 = o.getPriority();
            }
            if (o.getPattern() == c2) {
                p2 = o.getPriority();
            }
        }
        return p1 - p2;
    }

    /**
     * 连接符计算布尔值
     */
    public static boolean calcBoolean(boolean b1, boolean b2, char pattern) {
        if (pattern == RpnPattern.AND.getPattern()) {
            return b1 && b2;
        } else if (pattern == RpnPattern.OR.getPattern()) {
            return b1 || b2;
        }
        return false;
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
