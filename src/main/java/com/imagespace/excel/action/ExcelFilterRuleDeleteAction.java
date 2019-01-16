package com.imagespace.excel.action;

import com.imagespace.common.model.CallResult;
import com.imagespace.common.model.ResultCode;
import com.imagespace.common.service.ICallApi;
import com.imagespace.common.util.ExceptionUtil;
import com.imagespace.excel.service.ExcelService;
import com.imagespace.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author gusaishuai
 * @since 2019/1/8
 */
@Slf4j
@Service("excel.filterRuleDelete")
public class ExcelFilterRuleDeleteAction implements ICallApi {

    private final ExcelService excelService;

    @Autowired
    public ExcelFilterRuleDeleteAction(ExcelService excelService) {
        this.excelService = excelService;
    }

    @Override
    public CallResult exec(User _user, HttpServletRequest request, HttpServletResponse response) {
        try {
            //过滤规则ID
            String ruleId = request.getParameter("ruleId");
            if (StringUtils.isBlank(ruleId)) {
                throw new IllegalArgumentException("过滤规则ID不能为空");
            }
            if (!StringUtils.isNumeric(ruleId)) {
                throw new IllegalArgumentException("过滤规则ID不合法");
            }
            //过滤规则删除
            excelService.deleteFilterRule(Long.valueOf(ruleId));
            return new CallResult();
        } catch (IllegalArgumentException e) {
            return new CallResult(ResultCode.FAIL, e.getMessage());
        } catch (Exception e) {
            log.error("excel.filterRuleDelete error", e);
            return new CallResult(ResultCode.FAIL, ExceptionUtil.getExceptionTrace(e));
        }
    }

}
