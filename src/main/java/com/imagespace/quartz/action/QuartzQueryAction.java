package com.imagespace.quartz.action;

import com.imagespace.common.model.CallResult;
import com.imagespace.common.model.Page;
import com.imagespace.common.model.ResultCode;
import com.imagespace.common.service.ICallApi;
import com.imagespace.common.util.ExceptionUtil;
import com.imagespace.quartz.model.QuartzCriteria;
import com.imagespace.quartz.model.ScheduleType;
import com.imagespace.quartz.model.vo.QuartzVo;
import com.imagespace.quartz.service.QuartzMapFactory;
import com.imagespace.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gusaishuai
 * @since 19/1/19
 */
@Slf4j
@Service("quartz.queryQuartz")
public class QuartzQueryAction implements ICallApi {

    @Override
    public CallResult exec(User _user, HttpServletRequest request, HttpServletResponse response) {
        try {
            //定时任务名称
            String quartzName = request.getParameter("quartzName");
            //方法名
            String methodName = request.getParameter("methodName");
            //当前页数
            String pageNoStr = request.getParameter("pageNo");
            int pageNo = StringUtils.isBlank(pageNoStr) ? 1 : Integer.valueOf(pageNoStr);
            Page<QuartzCriteria> quartzCriteriaPage = QuartzMapFactory.INSTANCE
                    .queryQuartzMap(quartzName, methodName, pageNo);
            Page<QuartzVo> voPage = new Page<>(quartzCriteriaPage.getPageNo(), quartzCriteriaPage.getPageSize());
            voPage.setTotalCount(quartzCriteriaPage.getTotalCount());
            List<QuartzVo> voList = new ArrayList<>();
            for (QuartzCriteria quartzCriteria : quartzCriteriaPage.getList()) {
                QuartzVo vo = new QuartzVo();
                vo.setQuartzName(quartzCriteria.getName());
                vo.setClassName(quartzCriteria.getClassName());
                vo.setMethodName(quartzCriteria.getMethodName());
                vo.setStartTime(quartzCriteria.getStartDateStr());
                if (quartzCriteria.getScheduleType() == ScheduleType.DEFAULT) {
                    vo.setIntervalTime(quartzCriteria.getDefaultSchedule().getInternalInSecondStr());
                    vo.setRepeatNum(quartzCriteria.getDefaultSchedule().getRepeatCountStr());
                } else if (quartzCriteria.getScheduleType() == ScheduleType.CRON) {
                    vo.setCronExpression(quartzCriteria.getCronSchedule().getExpression());
                }
                vo.setStatus(quartzCriteria.getOpenStr());
                voList.add(vo);
            }
            voPage.setList(voList);
            return new CallResult(voPage);
        } catch (IllegalArgumentException e) {
            return new CallResult(ResultCode.FAIL, e.getMessage());
        } catch (Exception e) {
            log.error("quartz.queryQuartz error", e);
            return new CallResult(ResultCode.FAIL, ExceptionUtil.getExceptionTrace(e));
        }
    }

}
