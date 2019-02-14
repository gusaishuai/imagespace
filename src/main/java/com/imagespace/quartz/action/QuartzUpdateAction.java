package com.imagespace.quartz.action;

import com.imagespace.common.model.CallResult;
import com.imagespace.common.model.ResultCode;
import com.imagespace.common.service.ICallApi;
import com.imagespace.common.util.ExceptionUtil;
import com.imagespace.quartz.model.*;
import com.imagespace.quartz.service.QuartzMapFactory;
import com.imagespace.quartz.service.QuartzSchedulerFactory;
import com.imagespace.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.quartz.CronExpression;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.Date;

/**
 * @author gusaishuai
 * @since 2019/2/13
 */
@Slf4j
@Service("quartz.updateQuartz")
public class QuartzUpdateAction implements ICallApi {

    @Override
    public CallResult exec(User _user, HttpServletRequest request, HttpServletResponse response) {
        try {
            String classMethod = request.getParameter("key");
            if (StringUtils.isBlank(classMethod)) {
                throw new IllegalArgumentException("key不能为空");
            }
            //获取原先的定时任务信息
            QuartzCriteria quartzCriteria = QuartzMapFactory.INSTANCE.getQuartzMap(classMethod);
            if (quartzCriteria == null) {
                throw new IllegalArgumentException(String.format("未找到对应的定时任务：%s", classMethod));
            }
            if (!quartzCriteria.isOpen()) {
                throw new IllegalArgumentException(String.format("定时任务未开启：%s", classMethod));
            }
            //开始日期
            String startDateStr = request.getParameter("startDate");
            //运行间隔(秒)
            String internalInSecondStr = request.getParameter("internalInSecond");
            //重复次数
            String repeatCountStr = request.getParameter("repeatCount");
            //cron表达式
            String expression = request.getParameter("expression");
            QuartzUpdateCriteria quartzUpdateCriteria = new QuartzUpdateCriteria();
            quartzUpdateCriteria.setKey(quartzCriteria.getClassMethod());
            if (StringUtils.isNotBlank(startDateStr)) {
                try {
                    Date startDate = DateUtils.parseDateStrictly(startDateStr, "yyyy-MM-dd HH:mm:ss");
                    quartzUpdateCriteria.setStartDate(startDate);
                } catch (ParseException e) {
                    throw new IllegalArgumentException("开始日期请遵循：yyyy-MM-dd HH:mm:ss形式");
                }
            } else {
                //不填默认还是原先的数据
                quartzUpdateCriteria.setStartDate(quartzCriteria.getStartDate());
            }
            if (quartzCriteria.getScheduleType() == ScheduleType.DEFAULT) {
                DefaultScheduleCriteria defaultSchedule = new DefaultScheduleCriteria();
                if (StringUtils.isNotBlank(internalInSecondStr)) {
                    if (!NumberUtils.isNumber(internalInSecondStr)) {
                        throw new IllegalArgumentException("间隔时间必须为数字");
                    }
                    int internalInSecond = Integer.valueOf(internalInSecondStr);
                    if (internalInSecond <= 0) {
                        throw new IllegalArgumentException("间隔时间必须大于0");
                    }
                    defaultSchedule.setInternalInSecond(internalInSecond);
                } else {
                    defaultSchedule.setInternalInSecond(quartzCriteria.getDefaultSchedule().getInternalInSecond());
                }
                if (StringUtils.isNotBlank(repeatCountStr)) {
                    if (!NumberUtils.isNumber(repeatCountStr)) {
                        throw new IllegalArgumentException("重复次数必须为数字");
                    }
                    int repeatCount = Integer.valueOf(repeatCountStr);
                    if (repeatCount <= 0 && repeatCount != -1) {
                        throw new IllegalArgumentException("重复次数必须大于0或等于-1");
                    }
                    defaultSchedule.setRepeatCount(repeatCount);
                } else {
                    defaultSchedule.setRepeatCount(quartzCriteria.getDefaultSchedule().getRepeatCount());
                }
                quartzUpdateCriteria.setDefaultSchedule(defaultSchedule);
            } else if (quartzCriteria.getScheduleType() == ScheduleType.CRON) {
                CronScheduleCriteria cronSchedule = new CronScheduleCriteria();
                if (StringUtils.isNotBlank(expression)) {
                    if (!CronExpression.isValidExpression(expression)) {
                        throw new IllegalArgumentException("CRON表达式非法");
                    }
                    cronSchedule.setExpression(expression);
                } else {
                    cronSchedule.setExpression(quartzCriteria.getCronSchedule().getExpression());
                }
                quartzUpdateCriteria.setCronSchedule(cronSchedule);
            }
            //先更新schedule中的job
            QuartzSchedulerFactory.INSTANCE.updateJob(quartzUpdateCriteria);
            //再更新内存map中的job
            QuartzMapFactory.INSTANCE.updateQuartzMap(quartzUpdateCriteria);
            return new CallResult();
        } catch (IllegalArgumentException e) {
            return new CallResult(ResultCode.FAIL, e.getMessage());
        } catch (Exception e) {
            log.error("quartz.updateQuartz error", e);
            return new CallResult(ResultCode.FAIL, ExceptionUtil.getExceptionTrace(e));
        }
    }

}
