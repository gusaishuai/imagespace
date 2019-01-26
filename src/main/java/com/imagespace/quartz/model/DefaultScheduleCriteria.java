package com.imagespace.quartz.model;

import com.gss.quartz.common.QuartzUtils;
import lombok.Getter;
import lombok.Setter;
import org.quartz.SimpleTrigger;

/**
 * 默认的任务器model
 * @author gusaishuai
 * @since 17/4/2
 */
@Setter
@Getter
public class DefaultScheduleCriteria {

    /**
     * 间隔秒数
     */
    private int internalInSecond;
    /**
     * 重复次数（-1代表无限制）
     */
    private int repeatCount;

    /**
     * 在quartz框架中2代表执行3次,所以需要减1
     */
    public int getActualRepeatCount() {
        return repeatCount == -1 ? SimpleTrigger.REPEAT_INDEFINITELY : repeatCount - 1;
    }

    public String getInternalInSecondStr() {
        return QuartzUtils.INSTANCE.formatMillSec(
                (long) (this.internalInSecond * 1000));
    }

    public String getRepeatCountStr() {
        return repeatCount == -1 ? "无限次" : repeatCount + "次";
    }

}
