package com.imagespace.excel.model;

import org.apache.commons.lang3.StringUtils;

/**
 * @author gusaishuai
 * @since 19/1/14
 */
public enum ExcelValidType {

    ID_NO("#身份证#") {
        @Override
        public boolean valid(String str) {
            return false;
        }
    },
    PHONE("#手机号#") {
        @Override
        public boolean valid(String str) {
            return str.matches("^(1[3-9])\\d{9}$");
        }
    },
    EMAIL("#邮箱#") {
        @Override
        public boolean valid(String str) {
            return str.matches("^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$");
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

    ExcelValidType(String code) {
        this.code = code;
    }

    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
