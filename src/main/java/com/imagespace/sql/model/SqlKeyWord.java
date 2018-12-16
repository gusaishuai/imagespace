package com.imagespace.sql.model;

import org.apache.commons.lang3.StringUtils;

/**
 * @author gusaishuai
 * @since 17/5/2
 */
public enum SqlKeyWord {

    SELECT(true, "SELECT", false) {
        @Override
        public boolean start(String sql) {
            return StringUtils.startsWithIgnoreCase(StringUtils.trim(sql), name());
        }
        @Override
        public boolean contain(String sql) {
            return StringUtils.containsIgnoreCase(StringUtils.trim(sql), name());
        }
    },
    SHOW(true, "SELECT", false) {
        @Override
        public boolean start(String sql) {
            return StringUtils.startsWithIgnoreCase(StringUtils.trim(sql), name());
        }
        @Override
        public boolean contain(String sql) {
            return StringUtils.containsIgnoreCase(StringUtils.trim(sql), name());
        }
    },
    INSERT(true, "UPDATE", false) {
        @Override
        public boolean start(String sql) {
            return StringUtils.startsWithIgnoreCase(StringUtils.trim(sql), name());
        }
        @Override
        public boolean contain(String sql) {
            return StringUtils.containsIgnoreCase(StringUtils.trim(sql), name());
        }
    },
    UPDATE(true, "UPDATE", true) {
        @Override
        public boolean start(String sql) {
            return StringUtils.startsWithIgnoreCase(StringUtils.trim(sql), name());
        }
        @Override
        public boolean contain(String sql) {
            return StringUtils.containsIgnoreCase(StringUtils.trim(sql), name());
        }
    },
    DELETE(true, "UPDATE", true) {
        @Override
        public boolean start(String sql) {
            return StringUtils.startsWithIgnoreCase(StringUtils.trim(sql), name());
        }
        @Override
        public boolean contain(String sql) {
            return StringUtils.containsIgnoreCase(StringUtils.trim(sql), name());
        }
    },
    WHERE(false, "", false) {
        @Override
        public boolean start(String sql) {
            return StringUtils.startsWithIgnoreCase(StringUtils.trim(sql), name());
        }
        @Override
        public boolean contain(String sql) {
            return StringUtils.containsIgnoreCase(StringUtils.trim(sql), name());
        }
    };

    SqlKeyWord(boolean isPrefix, String category, boolean needWhere) {
        this.isPrefix = isPrefix;
        this.category = category;
        this.needWhere = needWhere;
    }

    private boolean isPrefix;
    private String category;
    private boolean needWhere;

    public boolean isPrefix() {
        return isPrefix;
    }

    public void setPrefix(boolean prefix) {
        isPrefix = prefix;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isNeedWhere() {
        return needWhere;
    }

    public void setNeedWhere(boolean needWhere) {
        this.needWhere = needWhere;
    }

    public abstract boolean start(String sql);

    public abstract boolean contain(String sql);

}
