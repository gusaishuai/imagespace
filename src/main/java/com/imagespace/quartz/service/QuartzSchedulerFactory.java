package com.imagespace.quartz.service;

import com.imagespace.quartz.model.QuartzCriteria;
import com.imagespace.quartz.model.QuartzUpdateCriteria;
import com.imagespace.quartz.model.ScheduleType;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

/**
 * 定时任务job的工厂
 * @author gusaishuai
 * @since 2017/3/28
 */
public enum QuartzSchedulerFactory {

    INSTANCE;

    /**
     * 定时任务工厂
     */
    private SchedulerFactory schedulerFactory = new StdSchedulerFactory();

    /**
     * 新增任务
     */
    public void addJob(QuartzCriteria quartzCriteria) throws SchedulerException {
        Scheduler scheduler = schedulerFactory.getScheduler();
        //任务是否已经存在
        if (scheduler.checkExists(new JobKey(
                quartzCriteria.getClassMethod(), Scheduler.DEFAULT_GROUP))) {
            throw new IllegalArgumentException(String.format("任务已经存在：%s", quartzCriteria.getName()));
        }
        JobDetail jobDetail = JobBuilder.newJob(QuartzJob.class)
                .withIdentity(quartzCriteria.getClassMethod(), Scheduler.DEFAULT_GROUP)
                .build();
        ScheduleBuilder<? extends Trigger> scheduleBuilder;
        if (quartzCriteria.getScheduleType() == ScheduleType.DEFAULT) {
            scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                    .withIntervalInSeconds(quartzCriteria.getDefaultSchedule().getInternalInSecond())
                    .withRepeatCount(quartzCriteria.getDefaultSchedule().getActualRepeatCount());
        } else if (quartzCriteria.getScheduleType() == ScheduleType.CRON) {
            scheduleBuilder = CronScheduleBuilder.cronSchedule(quartzCriteria.getCronSchedule().getExpression());
        } else {
            throw new IllegalArgumentException(String.format("任务类型不合法：%s", quartzCriteria.getScheduleType()));
        }
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(quartzCriteria.getClassMethod(), Scheduler.DEFAULT_GROUP)
                .startAt(quartzCriteria.getStartDate())
                .withSchedule(scheduleBuilder)
                .build();
        scheduler.scheduleJob(jobDetail, trigger);
    }

    /**
     * 删除任务
     */
    public void deleteJob(String classMethod) throws SchedulerException {
        Scheduler scheduler = schedulerFactory.getScheduler();
        JobKey jobKey = new JobKey(classMethod, Scheduler.DEFAULT_GROUP);
        //这里不用判断方法存不存在,不存在会返回false,不会抛出异常
        scheduler.deleteJob(jobKey);
    }

    /**
     * 更新任务
     * <br>找不到对应定时器则新增
     */
    public void updateJob(QuartzUpdateCriteria quartzUpdateCriteria) throws SchedulerException {
        Scheduler scheduler = schedulerFactory.getScheduler();
        //定时任务不存在可能是带执行次数的定时任务已经执行完毕,需要重新做新增操作
        if (!scheduler.checkExists(new JobKey(
                quartzUpdateCriteria.getKey(), Scheduler.DEFAULT_GROUP))) {
            JobDetail jobDetail = JobBuilder.newJob(QuartzJob.class)
                    .withIdentity(quartzUpdateCriteria.getKey(), Scheduler.DEFAULT_GROUP)
                    .build();
            ScheduleBuilder<? extends Trigger> scheduleBuilder = null;
            if (quartzUpdateCriteria.getScheduleType() == ScheduleType.DEFAULT) {
                scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(quartzUpdateCriteria.getDefaultSchedule().getInternalInSecond())
                        .withRepeatCount(quartzUpdateCriteria.getDefaultSchedule().getActualRepeatCount());
            } else if (quartzUpdateCriteria.getScheduleType() == ScheduleType.CRON) {
                scheduleBuilder = CronScheduleBuilder.cronSchedule(quartzUpdateCriteria.getCronSchedule().getExpression());
            }
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(quartzUpdateCriteria.getKey(), Scheduler.DEFAULT_GROUP)
                    .startAt(quartzUpdateCriteria.getStartDate())
                    .withSchedule(scheduleBuilder)
                    .build();
            scheduler.scheduleJob(jobDetail, trigger);
        } else {
            TriggerKey triggerKey = new TriggerKey(quartzUpdateCriteria.getKey(), Scheduler.DEFAULT_GROUP);
            Trigger trigger = scheduler.getTrigger(triggerKey);
            Trigger updateTrigger = null;
            if (quartzUpdateCriteria.getScheduleType() == ScheduleType.DEFAULT) {
                updateTrigger = ((SimpleTrigger) trigger).getTriggerBuilder()
                        .startAt(quartzUpdateCriteria.getStartDate())
                        .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                                .withIntervalInSeconds(quartzUpdateCriteria.getDefaultSchedule().getInternalInSecond())
                                .withRepeatCount(quartzUpdateCriteria.getDefaultSchedule().getActualRepeatCount()))
                        .build();
            } else if (quartzUpdateCriteria.getScheduleType() == ScheduleType.CRON) {
                updateTrigger = ((CronTrigger) trigger).getTriggerBuilder()
                        .startAt(quartzUpdateCriteria.getStartDate())
                        .withSchedule(CronScheduleBuilder.cronSchedule(quartzUpdateCriteria.getCronSchedule().getExpression()))
                        .build();
            }
            scheduler.rescheduleJob(triggerKey, updateTrigger);
        }
    }

    /**
     * 开始运行所有定时任务的定时器
     */
    public void startJob() throws SchedulerException {
        Scheduler scheduler = schedulerFactory.getScheduler();
        scheduler.start();
    }

}
