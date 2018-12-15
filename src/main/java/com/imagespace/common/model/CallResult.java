package com.imagespace.common.model;

import java.io.Serializable;

/**
 * @author gusaishuai
 * @since 2018/12/16
 */
public class CallResult implements Serializable {

    private static final long serialVersionUID = 4486644554222823209L;

    private int resultCode;
    private String msg;
    private Object result;

    public CallResult() {
        this.resultCode = ResultCode.SUCCESS.getCode();
    }

    public CallResult(ResultCode resultCode, String msg) {
        this.resultCode = resultCode.getCode();
        this.msg = msg;
    }

    public CallResult(Object result) {
        this.resultCode = ResultCode.SUCCESS.getCode();
        this.result = result;
    }

}
