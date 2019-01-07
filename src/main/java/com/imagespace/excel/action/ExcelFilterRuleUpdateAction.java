package com.imagespace.excel.action;

import com.imagespace.common.model.CallResult;
import com.imagespace.common.model.ResultCode;
import com.imagespace.common.service.ICallApi;
import com.imagespace.common.util.ExceptionUtil;
import com.imagespace.excel.model.ExcelFilterRule;
import com.imagespace.excel.model.ExcelFilterRuleDetail;
import com.imagespace.excel.service.ExcelService;
import com.imagespace.excel.util.ExprValidUtil;
import com.imagespace.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
            //过滤规则名称
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

            if (StringUtils.isBlank(filterRuleName)) {
                throw new IllegalArgumentException("过滤规则名称不能为空");
            }
            if (StringUtils.length(filterRuleName) > 128) {
                throw new IllegalArgumentException("过滤规则名称过长，请设置小于128个字符");
            }
            if (exprRows == null || exprRows.length == 0) {
                throw new IllegalArgumentException("至少要存在一条过滤规则");
            }
            List<Integer> exprRowList = Arrays.stream(exprRows).map(Integer::valueOf).collect(Collectors.toList());
            //校验表达式合法性
            ExprValidUtil.validExpr(exprRowList, leftBracket, colNum, matched, regex, rightBracket, conj);
            //组装入参
            ExcelFilterRule filterRule = new ExcelFilterRule();
            filterRule.setName(filterRuleName);
            filterRule.setUserId(_user.getId());

            List<ExcelFilterRuleDetail> filterRuleDetailList = new ArrayList<>();
            for (Integer exprRow : exprRowList) {
                ExcelFilterRuleDetail filterRuleDetail = new ExcelFilterRuleDetail();
                filterRuleDetail.setLeftBracket(leftBracket[exprRow]);
                filterRuleDetail.setColNum(colNum[exprRow]);
                filterRuleDetail.setMatched(matched[exprRow]);
                filterRuleDetail.setRegex(regex[exprRow]);
                filterRuleDetail.setRightBracket(rightBracket[exprRow]);
                filterRuleDetail.setConj(conj[exprRow]);
                filterRuleDetailList.add(filterRuleDetail);
            }
            //过滤规则更新
            excelService.updateFilterRule(filterRule, filterRuleDetailList);
            return new CallResult();
        } catch (IllegalArgumentException e) {
            return new CallResult(ResultCode.FAIL, e.getMessage());
        } catch (Exception e) {
            log.error("excel.filterRuleUpdate error", e);
            return new CallResult(ResultCode.FAIL, ExceptionUtil.getExceptionTrace(e));
        }
    }

}
