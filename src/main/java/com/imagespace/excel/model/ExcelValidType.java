package com.imagespace.excel.model;

import com.imagespace.common.util.ValidatorUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * @author gusaishuai
 * @since 19/1/14
 */
public enum ExcelValidType {

    ID_NO("#身份证#") {
        @Override
        public boolean valid(String str) {
            return ValidatorUtil.validIdNo(str);
        }
    },
    PHONE("#手机号#") {
        @Override
        public boolean valid(String str) {
            return ValidatorUtil.validPhone(str);
        }
    },
    EMAIL("#邮箱#") {
        @Override
        public boolean valid(String str) {
            return ValidatorUtil.validEmail(str);
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
