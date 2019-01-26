package com.imagespace.quartz.model;

import lombok.Getter;
import lombok.Setter;

/**
 * cron表达式的任务器model
 * @author gusaishuai
 * @since 17/4/2
 */
@Setter
@Getter
public class CronScheduleCriteria {

    /**
     * cron表达式
     */
    private String expression;

}
