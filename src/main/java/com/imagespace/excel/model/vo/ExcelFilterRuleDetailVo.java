package com.imagespace.excel.model.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * @author gusaishuai
 * @since 2019/1/6
 */
@Setter
@Getter
public class ExcelFilterRuleDetailVo {

    /**
     * 左括号
     */
    private String leftBracket;
    /**
     * 列数
     */
    private String colNum;
    /**
     * 是否满足 0-不满足 1-满足
     */
    private String matched;
    /**
     * 值或正则表达式
     */
    private String regex;
    /**
     * 右括号
     */
    private String rightBracket;
    /**
     * 连接符 &-并且 |-或者
     */
    private String conj;

}
