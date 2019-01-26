package com.imagespace.quartz.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 定时任务配置信息
 * @author gusaishuai
 * @since 2017/3/28
 */
@Setter
@Getter
public class QuartzCriteria extends QuartzScheduleCriteria {

    /**
     * 名称
     */
    private String name;
    /**
     * 类的实例
     */
    private Object clazz;
    /**
     * 方法名
     */
    private String methodName;
    /**
     * 定时任务开始时间
     */
    private Date startDate;
    /**
     * 备注
     */
    private String memo;
    /**
     * 是否开启
     */
    private boolean open;
    /**
     * 默认的任务器
     */
    private DefaultScheduleCriteria defaultSchedule;
    /**
     * cron表达式的任务器
     */
    private CronScheduleCriteria cronSchedule;

    public String getClassMethod() {
        return this.clazz.getClass().getName() + "." + this.methodName;
    }

}
