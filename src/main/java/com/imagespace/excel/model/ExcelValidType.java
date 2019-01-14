package com.imagespace.excel.model;

/**
 * @author gusaishuai
 * @since 19/1/14
 */
public enum ExcelValidType {

    ID_NO("_idNo", "身份证") {
        @Override
        public boolean valid(String str) {
            return false;
        }
    },
    PHONE("_phone", "手机号") {
        @Override
        public boolean valid(String str) {
            return str.matches("^(1[3-9])\\d{9}$");
        }
    },
    EMAIL("_email", "邮箱") {
        @Override
        public boolean valid(String str) {
            return str.matches("^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$");
        }
    },
    IP("_ip", "ip地址") {
        @Override
        public boolean valid(String str) {
            return false;
        }
    };

    public abstract boolean valid(String str);

    ExcelValidType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private String code;
    private String desc;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
