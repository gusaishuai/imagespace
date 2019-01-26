package com.imagespace.quartz.service;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;

/**
 * 定时任务执行
 * @author gusaishuai
 * @since 2017/3/28
 */
public class QuartzJob implements Job {

    public static final Logger logger = LoggerFactory.getLogger(QuartzJob.class);

    public void execute(JobExecutionContext context) throws JobExecutionException {
//        QuartzCriteria quartzCriteria = null;
//        try {
//            //jobName（class+method）作为key，获取到map中对应的定时任务信息
//            quartzCriteria = QuartzMapFactory.INSTANCE.getQuartzMap(context.getJobDetail().getKey().getName());
//            Method method = quartzCriteria.getClazz().getClass().getMethod(quartzCriteria.getMethodName());
//            //开始日期
//            Date startDate = Calendar.getInstance().getTime();
//            long startMill = System.currentTimeMillis();
//            logger.info("start invoke quartz [" + quartzCriteria.getQuartzName() + "] at : "
//                    + DateFormatUtils.format(Calendar.getInstance(), "yyyy-MM-dd HH:mm:ss"));
//            //执行任务
//            method.invoke(quartzCriteria.getClazz());
//            //执行时长
//            long duration = System.currentTimeMillis() - startMill;
//            logger.info("end invoke quartz [" + quartzCriteria.getQuartzName() + "] at : "
//                    + DateFormatUtils.format(Calendar.getInstance(), "yyyy-MM-dd HH:mm:ss"));
//            //本次执行的时间，这里就算上次执行时间了
//            quartzCriteria.setPreExecuteDate(context.getScheduledFireTime());
//            //下次执行的时间
//            quartzCriteria.setNextExecuteDate(context.getNextFireTime());
//            //加入定时任务执行历史
//            quartzCriteria.addQuartzExecuteDetailListFIFO(startDate, duration, ExecuteType.AUTO);
//        } catch (Exception e) {
//            String quartzName = quartzCriteria == null ? "null" : quartzCriteria.getQuartzName();
//            logger.error("quartz [" + quartzName + "] invoke error : " + QuartzErrorTrace.getExceptionTrace(e));
//        }

    }

}
