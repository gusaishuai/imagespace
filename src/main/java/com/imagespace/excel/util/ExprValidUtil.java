package com.imagespace.excel.util;

import com.imagespace.excel.model.RpnPattern;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;
import java.util.List;

/**
 * @author gusaishuai
 * @since 2019/1/7
 */
public class ExprValidUtil {

    /**
     * 校验表达式合法性
     */
    public static void validExpr(List<Integer> exprRowList, String[] leftBracket, String[] colNum,
                                 String[] matched, String[] regex, String[] rightBracket, String[] conj) {
        if (CollectionUtils.isEmpty(exprRowList)) {
            return;
        }
        Integer maxExprRow = exprRowList.stream().max(Comparator.naturalOrder())
                .orElseThrow(() -> new IllegalArgumentException("下标值获取行数失败")) + 1;

        if (leftBracket == null || maxExprRow != leftBracket.length) {
            throw new IllegalArgumentException("#左括号#的数据不正确");
        }
        String allLeftBracket = StringUtils.join(leftBracket, "");
        int leftBracketNum = StringUtils.countMatches(allLeftBracket, RpnPattern.LEFT_BRACKET.getPattern());
        if (allLeftBracket.length() != leftBracketNum) {
            throw new IllegalArgumentException("#左括号#中有非左括号的符号");
        }
        if (colNum == null || maxExprRow != colNum.length) {
            throw new IllegalArgumentException("#列数#的数据不正确");
        }
        if (exprRowList.stream().anyMatch(r -> StringUtils.isBlank(colNum[r]) || !StringUtils.isNumeric(colNum[r]))) {
            throw new IllegalArgumentException("#列数#存在为空或非数字的行");
        }
        if (exprRowList.stream().anyMatch(r -> StringUtils.length(colNum[r]) > 8)) {
            throw new IllegalArgumentException("#列数#存在过大的数字，请设置小于8位数");
        }
        if (matched == null || maxExprRow != matched.length) {
            throw new IllegalArgumentException("#满足条件#的数据不正确");
        }
        if (exprRowList.stream().anyMatch(r -> StringUtils.isBlank(matched[r])
                || (!StringUtils.equals("0", matched[r]) && !StringUtils.equals("1", matched[r])))) {
            throw new IllegalArgumentException("#满足条件#存在为空或非法字符的行");
        }
        if (regex == null || maxExprRow != regex.length) {
            throw new IllegalArgumentException("#值或正则表达式#的数据不正确");
        }
        if (exprRowList.stream().anyMatch(r -> StringUtils.isBlank(regex[r]))) {
            throw new IllegalArgumentException("#值或正则表达式#存在为空的行");
        }
        if (exprRowList.stream().anyMatch(r -> StringUtils.length(regex[r]) > 256)) {
            throw new IllegalArgumentException("#值或正则表达式#字符过多，请设置小于256个字符");
        }
        if (rightBracket == null || maxExprRow != rightBracket.length) {
            throw new IllegalArgumentException("#右括号#的数据不正确");
        }
        String allRightBracket = StringUtils.join(rightBracket, "");
        int rightBracketNum = StringUtils.countMatches(allRightBracket, RpnPattern.RIGHT_BRACKET.getPattern());
        if (allRightBracket.length() != rightBracketNum) {
            throw new IllegalArgumentException("#右括号#中有非右括号的符号");
        }
        if (leftBracketNum != rightBracketNum) {
            throw new IllegalArgumentException("表达式#左括号#和#右括号#数不匹配");
        }
        if (conj == null || maxExprRow != conj.length) {
            throw new IllegalArgumentException("#连接符（并且、或者）#的数据不正确");
        }
        int row = 0;
        for (int exprRow : exprRowList) {
            String conjValue = conj[exprRow];
            if (row == exprRowList.size() - 1) {
                if (StringUtils.isNotBlank(conjValue)) {
                    throw new IllegalArgumentException("最后一行规则不能有#连接符（并且、或者）#");
                }
            } else {
                if (StringUtils.isBlank(conjValue)) {
                    throw new IllegalArgumentException("#连接符（并且、或者）#存在为空的行，导致无法连接规则");
                }
            }
            if (StringUtils.isNotBlank(conjValue)
                    && !StringUtils.equals(String.valueOf(RpnPattern.AND.getPattern()), conjValue)
                    && !StringUtils.equals(String.valueOf(RpnPattern.OR.getPattern()), conjValue)) {
                throw new IllegalArgumentException("#连接符（并且、或者）#存在非法字符的行");
            }
            row++;
        }
    }

}
