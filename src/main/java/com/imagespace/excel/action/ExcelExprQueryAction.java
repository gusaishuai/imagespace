package com.imagespace.excel.action;

import com.alibaba.fastjson.JSON;
import com.imagespace.common.model.CallResult;
import com.imagespace.common.model.Constant;
import com.imagespace.common.model.ResultCode;
import com.imagespace.common.service.ICallApi;
import com.imagespace.common.util.ExceptionUtil;
import com.imagespace.excel.model.ExcelExpr;
import com.imagespace.excel.model.ExcelModel;
import com.imagespace.excel.model.ExcelRow;
import com.imagespace.excel.model.RpnPattern;
import com.imagespace.excel.service.impl.ExcelService;
import com.imagespace.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author gusaishuai
 * @since 2018/12/29
 */
@Slf4j
@Service("excel.exprQuery")
public class ExcelExprQueryAction implements ICallApi {

    @Autowired
    private ExcelService excelService;

    @Override
    public CallResult exec(User _user, HttpServletRequest request, HttpServletResponse response) {
        try {
            //sheet数
            String sheetNumStr = request.getParameter("sheetNum");
            //表头行数
            String topNumStr = request.getParameter("topNum");
            //代表以下6项内容的下标值
            String[] exprRows = request.getParameterValues("exprRows[]");
            //左括号，可多个，如：((，整体的表达式中，左括号必须和右括号的数量一致
            String[] leftBracket = request.getParameterValues("leftBracket[]");
            //列数
            String[] colNum = request.getParameterValues("colNum[]");
            //满足条件 1-满足 0-不满足
            String[] match = request.getParameterValues("match[]");
            //值或正则表达式
            String[] regex = request.getParameterValues("regex[]");
            //右括号，可多个，如：))，整体的表达式中，左括号必须和右括号的数量一致
            String[] rightBracket = request.getParameterValues("rightBracket[]");
            //连接符 &-并且 |-或者
            String[] conj = request.getParameterValues("conj[]");

            Cookie[] cookies = request.getCookies();
            String excelName = cookies == null ? null : Arrays.stream(cookies)
                    .filter(r -> StringUtils.equals(r.getName(), Constant.COOKIE_EXCEL_NAME))
                    .findFirst().map(Cookie::getValue).orElse(null);
            if (StringUtils.isBlank(excelName)) {
                throw new IllegalArgumentException("请上传EXCEL");
            }

            if (StringUtils.isNotBlank(sheetNumStr) && !StringUtils.isNumeric(sheetNumStr)) {
                throw new IllegalArgumentException("sheet数必须为数字");
            }
            if (StringUtils.isNotBlank(topNumStr) && !StringUtils.isNumeric(topNumStr)) {
                throw new IllegalArgumentException("表头行数必须为数字");
            }

            String expr = null;

            if (exprRows != null && exprRows.length > 0) {
                List<Integer> exprRowList = Arrays.stream(exprRows).map(Integer::valueOf).collect(Collectors.toList());

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
                if (match == null || maxExprRow != match.length) {
                    throw new IllegalArgumentException("#满足条件#的数据不正确");
                }
                if (exprRowList.stream().anyMatch(r -> StringUtils.isBlank(match[r])
                        || (!StringUtils.equals("0", match[r]) && !StringUtils.equals("1", match[r])))) {
                    throw new IllegalArgumentException("#满足条件#存在为空或非法字符的行");
                }
                if (regex == null || maxExprRow != regex.length) {
                    throw new IllegalArgumentException("#值或正则表达式#的数据不正确");
                }
                if (exprRowList.stream().anyMatch(r -> StringUtils.isBlank(regex[r]))) {
                    throw new IllegalArgumentException("#值或正则表达式#存在为空的行");
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
                            throw new IllegalArgumentException("最后一行规则请不要加#连接符（并且、或者）#");
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

                StringBuilder exprSb = new StringBuilder();
                for (int exprRow : exprRowList) {
                    String leftBracketValue = leftBracket[exprRow];
                    String rightBracketValue = rightBracket[exprRow];
                    String conjValue = conj[exprRow];
                    exprSb.append(StringUtils.isBlank(leftBracketValue) ? "" : leftBracketValue);
                    ExcelExpr excelExpr = new ExcelExpr();
                    excelExpr.setColNum(Integer.valueOf(colNum[exprRow]));
                    excelExpr.setMatch(StringUtils.equals("1", match[exprRow]));
                    excelExpr.setRegex(regex[exprRow]);
                    exprSb.append(JSON.toJSONString(excelExpr));
                    exprSb.append(StringUtils.isBlank(rightBracketValue) ? "" : rightBracketValue);
                    exprSb.append(StringUtils.isBlank(conjValue) ? "" : conjValue);
                }
                expr = exprSb.toString();
            }

            int sheetNum = StringUtils.isBlank(sheetNumStr) ? 1 : Integer.valueOf(sheetNumStr);
            int topNum = StringUtils.isBlank(topNumStr) ? 0 : Integer.valueOf(topNumStr);

            //根据表达式过滤表格中符合的数据
            ExcelModel excelModel = excelService.queryByExpr(excelName, sheetNum, topNum, expr);

            return new CallResult(excelModel);
        } catch (IllegalArgumentException e) {
            return new CallResult(ResultCode.FAIL, e.getMessage());
        } catch (Exception e) {
            log.error("excel.exprQuery error", e);
            return new CallResult(ResultCode.FAIL, ExceptionUtil.getExceptionTrace(e));
        }
    }

}
