package com.imagespace.common.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author gusaishuai
 * @since 2018/12/16
 */
@Setter
@Getter
public class CallResult implements Serializable {

    private static final long serialVersionUID = 4486644554222823209L;

    private int code;
    private String msg;
    private Object result;

    public CallResult() {
        this.code = ResultCode.SUCCESS.getCode();
    }

    public CallResult(ResultCode resultCode, String msg) {
        this.code = resultCode.getCode();
        this.msg = msg;
    }

    public CallResult(Object result) {
        this.code = ResultCode.SUCCESS.getCode();
        this.result = result;
    }

}
