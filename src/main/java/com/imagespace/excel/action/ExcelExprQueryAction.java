package com.imagespace.excel.action;

import com.alibaba.fastjson.JSON;
import com.imagespace.common.model.CallResult;
import com.imagespace.common.model.Constant;
import com.imagespace.common.model.Page;
import com.imagespace.common.model.ResultCode;
import com.imagespace.common.service.ICallApi;
import com.imagespace.common.util.ExceptionUtil;
import com.imagespace.excel.model.ExcelExpr;
import com.imagespace.excel.service.ExcelService;
import com.imagespace.excel.util.ExprValidUtil;
import com.imagespace.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author gusaishuai
 * @since 2018/12/29
 */
@Slf4j
@Service("excel.exprQuery")
public class ExcelExprQueryAction implements ICallApi {

    @Value("${excel.upload.tempdir}")
    private String tempDir;

    private final ExcelService excelService;

    @Autowired
    public ExcelExprQueryAction(ExcelService excelService) {
        this.excelService = excelService;
    }

    @Override
    public CallResult exec(User _user, HttpServletRequest request, HttpServletResponse response) {
        try {
            //sheet数
            String sheetNumStr = request.getParameter("sheetNum");
            //表头行数
            String topNumStr = request.getParameter("topNum");
            //当前页数
            String pageNoStr = request.getParameter("pageNo");
            //代表以下6项内容的下标值
            String[] exprRows = request.getParameterValues("exprRows[]");
            //左括号，可多个，如：((，整体的表达式中，左括号必须和右括号的数量一致
            String[] leftBracket = request.getParameterValues("leftBracket[]");
            //列数
            String[] colNum = request.getParameterValues("colNum[]");
            //满足条件 1-满足 0-不满足
            String[] matched = request.getParameterValues("matched[]");
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
            File excel = new File(tempDir + excelName);
            if (!excel.exists()) {
                throw new IllegalArgumentException("请重新上传EXCEL");
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
                //校验表达式合法性
                ExprValidUtil.validExpr(exprRowList, leftBracket, colNum, matched, regex, rightBracket, conj);
                //拼接表达式
                StringBuilder exprSb = new StringBuilder();
                for (int exprRow : exprRowList) {
                    String leftBracketValue = leftBracket[exprRow];
                    String rightBracketValue = rightBracket[exprRow];
                    String conjValue = conj[exprRow];
                    exprSb.append(StringUtils.isBlank(leftBracketValue) ? "" : leftBracketValue);
                    ExcelExpr excelExpr = new ExcelExpr();
                    excelExpr.setColNum(Integer.valueOf(colNum[exprRow]));
                    excelExpr.setMatched(StringUtils.equals("1", matched[exprRow]));
                    excelExpr.setRegex(regex[exprRow]);
                    exprSb.append(JSON.toJSONString(excelExpr));
                    exprSb.append(StringUtils.isBlank(rightBracketValue) ? "" : rightBracketValue);
                    exprSb.append(StringUtils.isBlank(conjValue) ? "" : conjValue);
                }
                expr = exprSb.toString();
            }

            int sheetNum = StringUtils.isBlank(sheetNumStr) ? 1 : Integer.valueOf(sheetNumStr);
            int topNum = StringUtils.isBlank(topNumStr) ? 0 : Integer.valueOf(topNumStr);
            int pageNo = StringUtils.isBlank(pageNoStr) ? 1 : Integer.valueOf(pageNoStr);

            //根据表达式过滤表格中符合的数据
            Page<Map<String, String>> excelModelPage = excelService.filterExcel(excel, expr, sheetNum, topNum, pageNo);
            return new CallResult(excelModelPage);
        } catch (IllegalArgumentException e) {
            return new CallResult(ResultCode.FAIL, e.getMessage());
        } catch (Exception e) {
            log.error("excel.exprQuery error", e);
            return new CallResult(ResultCode.FAIL, ExceptionUtil.getExceptionTrace(e));
        }
    }

}
