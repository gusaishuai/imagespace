package com.imagespace.excel.action;

import com.imagespace.common.model.CallResult;
import com.imagespace.common.model.ResultCode;
import com.imagespace.common.service.ICallApi;
import com.imagespace.common.util.ExceptionUtil;
import com.imagespace.excel.model.ExcelFilterRuleDetail;
import com.imagespace.excel.model.vo.ExcelFilterRuleDetailVo;
import com.imagespace.excel.service.ExcelService;
import com.imagespace.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gusaishuai
 * @since 2019/1/6
 */
@Slf4j
@Service("excel.filterRuleDetailQuery")
public class ExcelFilterRuleDetailQueryAction implements ICallApi {

    @Autowired
    private ExcelService excelService;

    @Override
    public CallResult exec(User _user, HttpServletRequest request, HttpServletResponse response) {
        try {
            String ruleId = request.getParameter("ruleId");
            if (StringUtils.isBlank(ruleId)) {
                throw new IllegalArgumentException("规则ID不能为空");
            }
            //查询过滤规则
            List<ExcelFilterRuleDetail> filterRuleDetailList = excelService.getFilterRuleDetailList(Long.valueOf(ruleId));
            //构建返回参数
            List<ExcelFilterRuleDetailVo> voList = buildVo(filterRuleDetailList);
            return new CallResult(voList);
        } catch (IllegalArgumentException e) {
            return new CallResult(ResultCode.FAIL, e.getMessage());
        } catch (Exception e) {
            log.error("excel.filterRuleDetailQuery error", e);
            return new CallResult(ResultCode.FAIL, ExceptionUtil.getExceptionTrace(e));
        }
    }

    private List<ExcelFilterRuleDetailVo> buildVo(List<ExcelFilterRuleDetail> filterRuleDetailList) {
        List<ExcelFilterRuleDetailVo> voList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(filterRuleDetailList)) {
            filterRuleDetailList.forEach(r -> {
                ExcelFilterRuleDetailVo vo = new ExcelFilterRuleDetailVo();
                vo.setLeftBracket(r.getLeftBracket());
                vo.setColNum(r.getColNum());
                vo.setMatched(r.getMatched());
                vo.setRegex(r.getRegex());
                vo.setRightBracket(r.getRightBracket());
                vo.setConj(r.getConj());
                voList.add(vo);
            });
        }
        return voList;
    }

}
