package com.imagespace.quartz.model;

/**
 * 定时任务job更新需要的参数
 * @author gusaishuai
 * @since 2017/4/1
 */
public class QuartzUpdateCriteria extends QuartzScheduleCriteria {

    /**
     * class+method
     */
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
