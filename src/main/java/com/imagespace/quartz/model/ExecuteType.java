package com.imagespace.quartz.model;

/**
 * 定时任务的执行类型
 * @author gusaishuai
 * @since 2017/3/30
 */
public enum ExecuteType {

    AUTO(1, "自动"),
    MANUAL(2, "手动");

    ExecuteType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private int code;
    /**
     * 描述
     */
    private String desc;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
