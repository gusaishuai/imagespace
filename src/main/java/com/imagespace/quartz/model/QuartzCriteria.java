package com.imagespace.quartz.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 定时任务配置信息
 * @author gusaishuai
 * @since 2017/3/28
 */
@Setter
@Getter
public class QuartzCriteria extends QuartzScheduleCriteria {

    /**
     * 执行详细的队列大小
     */
    private int CAPACITY = 20;

    /**
     * 名称
     */
    private String name;
    /**
     * 类的实例
     */
    private Object clazz;
    /**
     * 方法名
     */
    private String methodName;
    /**
     * 备注
     */
    private String memo;
    /**
     * 是否开启
     */
    private boolean open;
    /**
     * 执行详细的FIFO队列，默认20个元素
     */
    private BlockingQueue<QuartzExecuteDetailCriteria> queueFIFO = new LinkedBlockingQueue<>(CAPACITY);
    /**
     * 上次执行时间
     */
    private Date preExecuteDate;
    /**
     * 下次执行时间
     */
    private Date nextExecuteDate;

    public String getClassName() {
        return clazz.getClass().getName();
    }

    public String getPreExecuteDateStr() {
        return DateFormatUtils.format(preExecuteDate, "yyyy-MM-dd HH:mm:ss");
    }

    public String getNextExecuteDateStr() {
        return DateFormatUtils.format(nextExecuteDate, "yyyy-MM-dd HH:mm:ss");
    }

    public String getOpenStr() {
        return isOpen() ? "开启" : "关闭";
    }

    public String getClassMethod() {
        return clazz.getClass().getName() + "." + methodName;
    }

    public String getQuartzName() {
        return StringUtils.isBlank(name) ? getClassMethod() : getName();
    }

    /**
     * FIFO队列加入到定时任务执行历史
     */
    public void addQuartzExecuteDetailListFIFO(
            Date startDate, long duration, ExecuteType executeType) {
        QuartzExecuteDetailCriteria quartzExecuteDetail = new QuartzExecuteDetailCriteria();
        quartzExecuteDetail.setExecuteDate(startDate);
        quartzExecuteDetail.setExecuteDuration(duration);
        quartzExecuteDetail.setExecuteType(executeType);
        //加入队列
        if (queueFIFO.size() >= CAPACITY) {
            queueFIFO.poll();
        }
        queueFIFO.offer(quartzExecuteDetail);
    }

    /**
     * 获取FIFO队列，并按时间倒序
     */
    public List<QuartzExecuteDetailCriteria> fetchQuartzExecuteDetailListFIFO() {
        if (CollectionUtils.isEmpty(queueFIFO)) {
            return new ArrayList<>();
        }
        List<QuartzExecuteDetailCriteria> quartzDetailList = new ArrayList<>(queueFIFO);
        //进入队列一定是按时间正序,这里直接反序即可,无需使用Collections.sort方法
        Collections.reverse(quartzDetailList);
        return quartzDetailList;
    }

}
