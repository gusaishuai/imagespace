package com.imagespace.common.model;

public enum ResultCode {

    SUCCESS(0, "成功"),
    FAIL(-1, "失败"),
    RE_LOGIN(1001, "用户需要重新登录"),;

    ResultCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private int code;
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
