package com.imagespace.quartz.model.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * @author gusaishuai
 * @since 19/1/19
 */
@Setter
@Getter
public class QuartzVo {

    /**
     * 定时任务名称
     */
    private String quartzName;
    /**
     * 类名
     */
    private String className;
    /**
     * 方法名
     */
    private String methodName;
    /**
     * 开始时间
     */
    private String startTime;
    /**
     * CRON表达式
     */
    private String cronExpression;
    /**
     * 运行间隔
     */
    private String intervalTime;
    /**
     * 重复次数
     */
    private String repeatNum;
    /**
     * 状态：开启或关闭
     */
    private String status;

}
