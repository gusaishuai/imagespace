package com.imagespace.quartz.model;

import com.imagespace.quartz.util.IntervalUtil;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;

/**
 * 定时任务执行详细
 * @author gusaishuai
 * @since 2017/3/30
 */
public class QuartzExecuteDetailCriteria {

    /**
     * 执行时间
     */
    private Date executeDate;
    /**
     * 执行时长（毫秒）
     */
    private Long executeDuration;
    /**
     * 执行类型（手动、自动）
     */
    private ExecuteType executeType;

    public Date getExecuteDate() {
        return executeDate;
    }

    public void setExecuteDate(Date executeDate) {
        this.executeDate = executeDate;
    }

    public long getExecuteDuration() {
        return executeDuration;
    }

    public void setExecuteDuration(long executeDuration) {
        this.executeDuration = executeDuration;
    }

    public ExecuteType getExecuteType() {
        return executeType;
    }

    public void setExecuteType(ExecuteType executeType) {
        this.executeType = executeType;
    }

    public String getExecuteDateStr() {
        return DateFormatUtils.format(executeDate, "yyyy-MM-dd HH:mm:ss");
    }

    public String getExecuteDurationStr() {
        return IntervalUtil.formatMillSec(executeDuration);
    }

}
