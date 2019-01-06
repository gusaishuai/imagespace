package com.imagespace.excel.action;

import com.imagespace.common.model.CallResult;
import com.imagespace.common.model.ResultCode;
import com.imagespace.common.service.ICallApi;
import com.imagespace.common.util.ExceptionUtil;
import com.imagespace.excel.model.ExcelFilterRule;
import com.imagespace.excel.model.vo.ExcelFilterRuleVo;
import com.imagespace.excel.service.ExcelService;
import com.imagespace.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
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
@Service("excel.filterRuleQuery")
public class ExcelFilterRuleQueryAction implements ICallApi {

    @Autowired
    private ExcelService excelService;

    @Override
    public CallResult exec(User _user, HttpServletRequest request, HttpServletResponse response) {
        try {
            //查询过滤规则
            List<ExcelFilterRule> filterRuleList = excelService.getFilterRuleList(_user.getId());
            //构建返回参数
            List<ExcelFilterRuleVo> voList = buildVo(filterRuleList);
            return new CallResult(voList);
        } catch (IllegalArgumentException e) {
            return new CallResult(ResultCode.FAIL, e.getMessage());
        } catch (Exception e) {
            log.error("excel.filterRuleQuery error", e);
            return new CallResult(ResultCode.FAIL, ExceptionUtil.getExceptionTrace(e));
        }
    }

    private List<ExcelFilterRuleVo> buildVo(List<ExcelFilterRule> filterRuleList) {
        List<ExcelFilterRuleVo> voList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(filterRuleList)) {
            filterRuleList.forEach(r -> {
                ExcelFilterRuleVo vo = new ExcelFilterRuleVo();
                vo.setId(r.getId());
                vo.setName(r.getName());
                voList.add(vo);
            });
        }
        return voList;
    }

}
