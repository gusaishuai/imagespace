package com.imagespace.excel.model;

import org.apache.commons.lang3.StringUtils;

/**
 * @author gusaishuai
 * @since 19/1/14
 */
public enum ExcelValidType {

    ID_NO("$idNo$", "身份证") {
        @Override
        public boolean valid(String str) {
            return false;
        }
    },
    PHONE("$phone$", "手机号") {
        @Override
        public boolean valid(String str) {
            return str.matches("^(1[3-9])\\d{9}$");
        }
    },
    EMAIL("$email$", "邮箱") {
        @Override
        public boolean valid(String str) {
            return str.matches("^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$");
        }
    },
    IP("$ip$", "ip地址") {
        @Override
        public boolean valid(String str) {
            return false;
        }
    };

    public abstract boolean valid(String str);

    public static ExcelValidType of(String code) {
        for (ExcelValidType excelValidType : ExcelValidType.values()) {
            if (StringUtils.equals(code, excelValidType.getCode())) {
                return excelValidType;
            }
        }
        return null;
    }

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
