package com.imagespace.quartz.model.anno;

import org.quartz.SimpleTrigger;

/**
 * 默认的任务器注解
 * @author gusaishuai
 * @since 17/4/2
 */
public @interface DefaultSchedule {

    /**
     * 是否使用
     */
    boolean use() default false;

    /**
     * 间隔秒数
     */
    int internalInSecond() default -1;

    /**
     * 重复次数
     * 默认无限制
     */
    int repeatCount() default SimpleTrigger.REPEAT_INDEFINITELY;

}
