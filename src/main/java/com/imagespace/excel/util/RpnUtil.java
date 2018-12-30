package com.imagespace.excel.util;

import com.imagespace.excel.model.RpnPattern;
import com.imagespace.excel.service.IRpnExcelCalc;

import java.util.Stack;

/**
 * @author gusaishuai
 * @since 18/12/30
 */
public class RpnUtil {

    /**
     * 生成逆波兰表达式
     */
    public static String[] generateRpnExpr(String expr) {
        //运算符
        Stack<Character> patterns = new Stack<>();
        //输出结果
        Stack<String> output = new Stack<>();

        char[] chars = expr.toCharArray();
        //非运算符可能有多位，标志开始位置
        int pre;
        //是否运算符
        boolean isPattern = true;
        int leftBracket = 0; // 左括号的数量

        for (int i = 0; i < chars.length;) {
            pre = i;
            //截取数字
            while (i < chars.length && !RpnPattern.isPattern(chars[i])) {
                i++;
                isPattern = false;
            }
            if (!isPattern) {
                //非运算符直接压栈
                output.push(expr.substring(pre, i));
            } else {
                char o = chars[i++]; //运算符
                if (o == RpnPattern.LEFT_BRACKET.getPattern()) {
                    leftBracket++;
                }
                if (leftBracket > 0) {
                    if (o == RpnPattern.RIGHT_BRACKET.getPattern()) {
                        while (!patterns.empty()) {
                            char top = patterns.pop();
                            if (top == RpnPattern.LEFT_BRACKET.getPattern()) {
                                break;
                            }
                            output.push(String.valueOf(top));
                        }
                        leftBracket--;
                    } else {
                        //如果栈顶为左括号，则直接添加，不顾其优先级
                        //如果之前有左括号，但是左括号不在栈顶，则需判断其优先级，如果优先级比栈顶的低，则依次出栈
                        while (!patterns.empty() && patterns.peek() != RpnPattern.LEFT_BRACKET.getPattern()
                                && RpnPattern.calcPriority(o, patterns.peek()) <= 0) {
                            output.push(String.valueOf(patterns.pop()));
                        }
                        patterns.push(o);
                    }
                } else {
                    while (!patterns.empty() && RpnPattern.calcPriority(o, patterns.peek()) <= 0) {
                        output.push(String.valueOf(patterns.pop()));
                    }
                    patterns.push(o);
                }
            }

        }
        //遍历结束，将运算符栈全部压入output
        while (!patterns.empty()) {
            output.push(String.valueOf(patterns.pop()));
        }
        String[] outputArray = new String[output.size()];
        return output.toArray(outputArray);
    }

    /**
     * 逆波兰表达式计算
     */
    public static boolean calcRpnExpr(String[] rpnExprArray, IRpnExcelCalc rpnExcelCalc) {
        Stack<Boolean> calcResult = new Stack<>();
        for (String rpnExpr : rpnExprArray) {
            if (!RpnPattern.isPattern(rpnExpr)) {
                calcResult.push(rpnExcelCalc.calc(Integer.valueOf(rpnExpr)));
            } else {
                calcResult.push(RpnPattern.calcBoolean(
                        calcResult.pop(), calcResult.pop(), rpnExpr.charAt(0)));
            }
        }
        return calcResult.size() == 1 ? calcResult.pop() : false;
    }

}
