package com.imagespace.sql.model;

import org.apache.commons.lang3.StringUtils;

/**
 * @author gusaishuai
 * @since 17/5/2
 */
public enum SqlKeyWord {

    SELECT {
        @Override
        public boolean start(String sql) {
            return StringUtils.startsWithIgnoreCase(StringUtils.trim(sql), name());
        }
    },
    INSERT {
        @Override
        public boolean start(String sql) {
            return StringUtils.startsWithIgnoreCase(StringUtils.trim(sql), name());
        }
    },
    UPDATE {
        @Override
        public boolean start(String sql) {
            return StringUtils.startsWithIgnoreCase(StringUtils.trim(sql), name());
        }
    },
    DELETE {
        @Override
        public boolean start(String sql) {
            return StringUtils.startsWithIgnoreCase(StringUtils.trim(sql), name());
        }
    };

    public abstract boolean start(String sql);

}
