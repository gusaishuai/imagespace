package com.imagespace.excel.action;

import com.imagespace.common.model.CallResult;
import com.imagespace.common.model.ResultCode;
import com.imagespace.common.service.ICallApi;
import com.imagespace.common.util.ExceptionUtil;
import com.imagespace.excel.service.ExcelService;
import com.imagespace.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author gusaishuai
 * @since 2019/1/6
 */
@Slf4j
@Service("excel.filterRuleUpdate")
public class ExcelFilterRuleUpdateAction implements ICallApi {

    @Autowired
    private ExcelService excelService;

    @Override
    public CallResult exec(User _user, HttpServletRequest request, HttpServletResponse response) {
        try {
            //当前页数
            String filterRuleName = request.getParameter("filterRuleName");
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
            return new CallResult();
        } catch (IllegalArgumentException e) {
            return new CallResult(ResultCode.FAIL, e.getMessage());
        } catch (Exception e) {
            log.error("excel.filterRuleUpdate error", e);
            return new CallResult(ResultCode.FAIL, ExceptionUtil.getExceptionTrace(e));
        }
    }

}
