package com.imagespace.excel.action;

import com.alibaba.fastjson.JSON;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author gusaishuai
 * @since 2018/12/26
 */
public class RNPTest {

    public static void main(String[] args) throws IOException {
        LineIterator lt = FileUtils.lineIterator(new File("C:\\Users\\Administrator\\Desktop\\1.txt"));
        List<List<String>> rowColListList = new ArrayList<>();
        while (lt.hasNext()) {
            String next = lt.next();
            rowColListList.add(new ArrayList<>(Arrays.asList(next.split("\t"))));
        }

        String v = "({\"col\":\"1\",\"regex\":\"男\"}) & ({\"col\":\"1\",\"regex\":\"女\"})";
        Map<String, Object> allRegex = getAllRegex(v, "\\{.*?\\}");
        System.out.println(allRegex);
        String str = (String) allRegex.get("str");
        List<RNP> regexList = (List<RNP>) allRegex.get("regex");
        str = StringUtils.deleteWhitespace(str);
        Stack<String> rpn = rpn(str);
        String[] aa = new String[rpn.size()];
        String[] aaaList = rpn.toArray(aa);
        List<String> resultList = new ArrayList<>();
        int title = 1;
        for (int i = 0;i<rowColListList.size();i++) {
            if (i<title) {
                continue;
            }
            List<String> rowList = rowColListList.get(i);
            if (!evalRPN(aaaList, regexList, rowList)) {
                resultList.add(rowList.toString());
            }
        }
        System.out.println(resultList);
    }

    public static boolean evalRPN(String[] tokens, List<RNP> regexList, List<String> rowList) {
        Stack<Boolean> stack = new Stack<>();
        for(String token : tokens){
            if(!RPNPriority.isOperator(token)) {
                RNP rnp = regexList.get(Integer.valueOf(token));
                boolean res = rowList.get(rnp.getCol()-1).matches(rnp.getRegex());
                stack.push(res);
            }else{
                Boolean right = stack.pop();
                Boolean left = stack.pop();
                boolean tmp = compute(left, right, token.charAt(0));
                stack.push(tmp);
            }
        }
        boolean aa = false;
        if(stack.size() == 1){
            aa = stack.pop();
        }
        return aa;
    }

    private static boolean compute(Boolean left, Boolean right, char c) {
        if (c == '&') {
            return left && right;
        } else if (c == '|') {
            return left || right;
        }
        return false;
    }

    public static Stack<String> rpn(String str) {
        Stack<Character> operators = new Stack<>(); //运算符
        Stack<String> output = new Stack<>(); //输出结果

        char[] chars = str.toCharArray();
        int pre;
        boolean digital; //是否为数字（只要不是运算符，都是数字），用于截取字符串
        int len = chars.length;
        int bracket = 0; // 左括号的数量

        for (int i = 0; i < len; ) {
            pre = i;
            digital = Boolean.FALSE;
            //截取数字
            while (i < len && !RPNPriority.isOperator(chars[i])) {
                i++;
                digital = Boolean.TRUE;
            }

            if (digital) {
                output.push(str.substring(pre, i));
            } else {
                char o = chars[i++]; //运算符
                if (o == '(') {
                    bracket++;
                }
                if (bracket > 0) {
                    if (o == ')') {
                        while (!operators.empty()) {
                            char top = operators.pop();
                            if (top == '(') {
                                break;
                            }
                            output.push(String.valueOf(top));
                        }
                        bracket--;
                    } else {
                        //如果栈顶为 ( ，则直接添加，不顾其优先级
                        //如果之前有 ( ，但是 ( 不在栈顶，则需判断其优先级，如果优先级比栈顶的低，则依次出栈
                        while (!operators.empty() && operators.peek() != '(' && RPNPriority.cmp(o, operators.peek()) <= 0) {
                            output.push(String.valueOf(operators.pop()));
                        }
                        operators.push(o);
                    }
                } else {
                    while (!operators.empty() && RPNPriority.cmp(o, operators.peek()) <= 0) {
                        output.push(String.valueOf(operators.pop()));
                    }
                    operators.push(o);
                }
            }

        }
        //遍历结束，将运算符栈全部压入output
        while (!operators.empty()) {
            output.push(String.valueOf(operators.pop()));
        }
        return output;
    }

    private static Map<String, Object> getAllRegex(String text, String pattern) {
        Map<String, Object> allRegexMap = new HashMap<>();
        List<RNP> rnpList = new ArrayList<>();
        try {
            Pattern p = Pattern.compile(pattern);
            Matcher matcher = p.matcher(text);
            int i = 0;
            StringBuffer sb = new StringBuffer();
            while (matcher.find()) {
                rnpList.add(JSON.parseObject(matcher.group(), RNP.class));
                matcher.appendReplacement(sb, ""+i);
                i++;
            }
            matcher.appendTail(sb);
            allRegexMap.put("str", sb.toString());
            allRegexMap.put("regex", rnpList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allRegexMap;
    }

}
