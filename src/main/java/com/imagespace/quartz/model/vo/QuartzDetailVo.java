package com.imagespace.quartz.model.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author gusaishuai
 * @since 2019/2/14
 */
@Setter
@Getter
public class QuartzDetailVo {

    /**
     * 备注
     */
    private String memo;
    /**
     * 上次执行时间
     */
    private String preExecuteDate;
    /**
     * 下次执行时间
     */
    private String nextExecuteDate;
    /**
     * 上次执行时长
     */
    private String executeDuration;
    /**
     * 详细执行情况列表
     */
    private List<QuartzDetailExecVo> detailExecList;

}
