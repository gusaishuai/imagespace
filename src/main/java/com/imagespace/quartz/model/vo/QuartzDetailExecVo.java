package com.imagespace.quartz.model.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * @author gusaishuai
 * @since 2019/2/14
 */
@Setter
@Getter
public class QuartzDetailExecVo {

    /**
     * 执行时间
     */
    private String executeDate;
    /**
     * 执行时长
     */
    private String executeDuration;
    /**
     * 执行类型（手动、自动）
     */
    private String executeType;

}
