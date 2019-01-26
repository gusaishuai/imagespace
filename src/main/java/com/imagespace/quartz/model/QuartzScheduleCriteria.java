package com.imagespace.quartz.model;

import java.util.Date;

/**
 * 多种任务器类型
 * @author gusaishuai
 * @since 17/4/2
 */
public class QuartzScheduleCriteria {

    /**
     * 定时任务开始时间
     */
    private Date startDate;
    /**
     * 默认的任务器
     */
    private DefaultScheduleCriteria defaultSchedule;
    /**
     * cron表达式的任务器
     */
    private CronScheduleCriteria cronSchedule;

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public DefaultScheduleCriteria getDefaultSchedule() {
        return defaultSchedule;
    }

    public void setDefaultSchedule(DefaultScheduleCriteria defaultSchedule) {
        this.defaultSchedule = defaultSchedule;
    }

    public CronScheduleCriteria getCronSchedule() {
        return cronSchedule;
    }

    public void setCronSchedule(CronScheduleCriteria cronSchedule) {
        this.cronSchedule = cronSchedule;
    }

    public String getStartDateStr() {
//        return QuartzUtils.INSTANCE.formatDateStr(this.startDate);
        return "";
    }

    /**
     * 获取任务器类型
     */
    public ScheduleType getScheduleType() {
        if (defaultSchedule != null) {
            return ScheduleType.DEFAULT;
        } else if (cronSchedule != null) {
            return ScheduleType.CRON;
        } else {
            return null;
        }
    }

}
