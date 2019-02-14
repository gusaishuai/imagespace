package com.imagespace.quartz.service;

import com.imagespace.quartz.model.ExecuteType;
import com.imagespace.quartz.model.QuartzCriteria;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;

/**
 * 定时任务执行
 * @author gusaishuai
 * @since 2017/3/28
 */
@Slf4j
public class QuartzJob implements Job {

    public void execute(JobExecutionContext context) throws JobExecutionException {
        QuartzCriteria quartzCriteria = null;
        try {
            //jobName（class+method）作为key，获取到map中对应的定时任务信息
            quartzCriteria = QuartzMapFactory.INSTANCE.getQuartzMap(context.getJobDetail().getKey().getName());
            Method method = quartzCriteria.getClazz().getClass().getMethod(quartzCriteria.getMethodName());
            //开始日期
            Date startDate = Calendar.getInstance().getTime();
            long startMill = System.currentTimeMillis();
            log.info("【自动】定时任务：{} 开始执行", quartzCriteria.getQuartzName());
            //执行任务
            method.invoke(quartzCriteria.getClazz());
            //执行时长
            long duration = System.currentTimeMillis() - startMill;
            log.info("【自动】定时任务：{} 结束执行", quartzCriteria.getQuartzName());
            //本次执行的时间，这里就算上次执行时间了
            quartzCriteria.setPreExecuteDate(context.getScheduledFireTime());
            //下次执行的时间
            quartzCriteria.setNextExecuteDate(context.getNextFireTime());
            //加入定时任务执行历史
            quartzCriteria.addQuartzExecuteDetailListFIFO(startDate, duration, ExecuteType.AUTO);
        } catch (Exception e) {
            log.error("【自动】定时任务：{} 执行失败", quartzCriteria == null ? "null" : quartzCriteria.getQuartzName(), e);
        }
    }

}
