package com.imagespace.quartz.model.anno;

/**
 * cron表达式的任务器注解
 * @author gusaishuai
 * @since 17/4/2
 */
public @interface CronSchedule {

    /**
     * 是否使用
     */
    boolean use() default false;

    /**
     * cron表达式
     */
    String expression() default "";

}
