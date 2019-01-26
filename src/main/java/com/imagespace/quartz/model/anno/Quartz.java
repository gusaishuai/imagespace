package com.imagespace.quartz.model.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 定时任务注解
 * 该注解只能加在方法上，定时任务不能有入参和返回参数
 * @author gusaishuai
 * @since 2017/3/28
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Quartz {

    /**
     * 定时任务名称
     */
    String name();

    /**
     * 开始时间，不写默认为立刻执行
     * 格式:yyyy-MM-dd HH:mm:ss
     */
    String startDate() default "";

    /**
     * 默认的定时任务器
     * 和cronSchedule必须开启一种,如果都启用,则使用defaultSchedule
     */
    DefaultSchedule defaultSchedule() default @DefaultSchedule;

    /**
     * cron表达式的定时任务器
     * 和defaultSchedule必须开启一种,如果都启用,则使用defaultSchedule
     */
    CronSchedule cronSchedule() default @CronSchedule;

    /**
     * 备注
     */
    String memo() default "";

}
