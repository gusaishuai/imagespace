package com.imagespace.quartz.action;

import com.imagespace.common.model.CallResult;
import com.imagespace.common.model.ResultCode;
import com.imagespace.common.service.ICallApi;
import com.imagespace.common.util.ExceptionUtil;
import com.imagespace.quartz.model.QuartzCriteria;
import com.imagespace.quartz.model.QuartzExecuteDetailCriteria;
import com.imagespace.quartz.model.vo.QuartzDetailExecVo;
import com.imagespace.quartz.model.vo.QuartzDetailVo;
import com.imagespace.quartz.service.QuartzMapFactory;
import com.imagespace.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gusaishuai
 * @since 2019/2/14
 */
@Slf4j
@Service("quartz.queryDetailQuartz")
public class QuartzQueryDetailAction implements ICallApi {

    @Override
    public CallResult exec(User _user, HttpServletRequest request, HttpServletResponse response) {
        try {
            String classMethod = request.getParameter("key");
            if (StringUtils.isBlank(classMethod)) {
                throw new IllegalArgumentException("key不能为空");
            }
            QuartzCriteria quartzCriteria = QuartzMapFactory.INSTANCE.getQuartzMap(classMethod);
            QuartzDetailVo vo = new QuartzDetailVo();
            vo.setMemo(quartzCriteria.getMemo());
            vo.setPreExecuteDate(quartzCriteria.getPreExecuteDateStr());
            vo.setNextExecuteDate(quartzCriteria.getNextExecuteDateStr());

            String executeDurationStr = "";
            //获取定时任务执行详细
            List<QuartzExecuteDetailCriteria> quartzDetailList = quartzCriteria.fetchQuartzExecuteDetailListFIFO();
            if (CollectionUtils.isNotEmpty(quartzDetailList)) {
                executeDurationStr = quartzDetailList.get(0).getExecuteDurationStr();
            }
            vo.setExecuteDuration(executeDurationStr);

            List<QuartzDetailExecVo> detailExecList = new ArrayList<>();
            for (QuartzExecuteDetailCriteria quartzDetail : quartzDetailList) {
                QuartzDetailExecVo detailExecVo = new QuartzDetailExecVo();
                detailExecVo.setExecuteDate(quartzDetail.getExecuteDateStr());
                detailExecVo.setExecuteDuration(quartzDetail.getExecuteDurationStr());
                detailExecVo.setExecuteType(quartzDetail.getExecuteType().getDesc());
                detailExecList.add(detailExecVo);
            }
            vo.setDetailExecList(detailExecList);
            return new CallResult(vo);
        } catch (IllegalArgumentException e) {
            return new CallResult(ResultCode.FAIL, e.getMessage());
        } catch (Exception e) {
            log.error("quartz.queryDetailQuartz error", e);
            return new CallResult(ResultCode.FAIL, ExceptionUtil.getExceptionTrace(e));
        }
    }

}
