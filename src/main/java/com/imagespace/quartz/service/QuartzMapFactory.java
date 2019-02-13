package com.imagespace.quartz.service;

import com.imagespace.common.model.Constant;
import com.imagespace.common.model.Page;
import com.imagespace.quartz.model.*;
import com.imagespace.quartz.model.anno.Quartz;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * 定时任务map的工厂
 * @author gusaishuai
 * @since 2017/3/28
 */
public enum QuartzMapFactory {

    INSTANCE;

    /**
     * class+method作为key
     */
    private Map<String, QuartzCriteria> quartzMap = new HashMap<>();

    /**
     * map中是否有定时任务
     */
    public boolean hasQuartz() {
        return MapUtils.isNotEmpty(quartzMap);
    }

    /**
     * 存入定时任务map,用于查询
     */
    public QuartzCriteria putQuartzMap(Object clazz, String methodName, Date startDate, Quartz quartz) {
        QuartzCriteria quartzCriteria = new QuartzCriteria();
        quartzCriteria.setName(quartz.name());
        quartzCriteria.setClazz(clazz);
        quartzCriteria.setMethodName(methodName);
        quartzCriteria.setOpen(true);
        quartzCriteria.setMemo(quartz.memo());
        quartzCriteria.setStartDate(startDate);
        //判断使用哪种定时器,如果都启用,则使用默认定时器
        if (quartz.defaultSchedule().use()) {
            DefaultScheduleCriteria defaultScheduleCriteria = new DefaultScheduleCriteria();
            defaultScheduleCriteria.setInternalInSecond(quartz.defaultSchedule().internalInSecond());
            defaultScheduleCriteria.setRepeatCount(quartz.defaultSchedule().repeatCount());
            quartzCriteria.setDefaultSchedule(defaultScheduleCriteria);
        } else if (quartz.cronSchedule().use()) {
            CronScheduleCriteria cronScheduleCriteria = new CronScheduleCriteria();
            cronScheduleCriteria.setExpression(quartz.cronSchedule().expression());
            quartzCriteria.setCronSchedule(cronScheduleCriteria);
        }
        quartzMap.put(quartzCriteria.getClassMethod(), quartzCriteria);
        return quartzCriteria;
    }

    /**
     * 根据key查询对应定时任务对象
     */
    public QuartzCriteria getQuartzMap(String key) {
        return quartzMap.get(key);
    }

    /**
     * 根据条件查询定时任务列表信息
     */
    public Page<QuartzCriteria> queryQuartzMap(String name, String methodName, int pageNo) {
        List<QuartzCriteria> quartzCriteriaList = new ArrayList<>(quartzMap.values());
        CollectionUtils.filter(quartzCriteriaList, quartzCriteria -> {
            if (StringUtils.isNotBlank(name)
                    && !quartzCriteria.getName().contains(name)) {
                return false;
            }
            if (StringUtils.isNotBlank(methodName)
                    && !StringUtils.equals(methodName, quartzCriteria.getMethodName())) {
                return false;
            }
            return true;
        });
        Page<QuartzCriteria> quartzCriteriaPage = new Page<>(pageNo, Constant.PAGE_SIZE);
        int start = quartzCriteriaPage.start() > quartzCriteriaList.size()
                ? quartzCriteriaList.size() : quartzCriteriaPage.start();
        int end = quartzCriteriaPage.end() > quartzCriteriaList.size()
                ? quartzCriteriaList.size() : quartzCriteriaPage.end();
        quartzCriteriaPage.setTotalCount(quartzCriteriaList.size());
        quartzCriteriaPage.setList(quartzCriteriaList.subList(start, end));
        return quartzCriteriaPage;
    }

    /**
     * 更新定时任务信息
     */
    public void updateQuartzMap(QuartzUpdateCriteria quartzUpdateCriteria) {
        QuartzCriteria quartzCriteria = quartzMap.get(quartzUpdateCriteria.getKey());
        quartzCriteria.setStartDate(quartzUpdateCriteria.getStartDate());
        if (quartzUpdateCriteria.getScheduleType() == ScheduleType.DEFAULT) {
            DefaultScheduleCriteria defaultSchedule = new DefaultScheduleCriteria();
            defaultSchedule.setInternalInSecond(quartzUpdateCriteria.getDefaultSchedule().getInternalInSecond());
            defaultSchedule.setRepeatCount(quartzUpdateCriteria.getDefaultSchedule().getRepeatCount());
            quartzCriteria.setDefaultSchedule(defaultSchedule);
        } else if (quartzUpdateCriteria.getScheduleType() == ScheduleType.CRON) {
            CronScheduleCriteria cronScheduleCriteria = new CronScheduleCriteria();
            cronScheduleCriteria.setExpression(quartzUpdateCriteria.getCronSchedule().getExpression());
            quartzCriteria.setCronSchedule(cronScheduleCriteria);
        }
    }

    /**
     * 更新定时任务状态
     */
    public void updateStatusQuartzMap(String classMethod, boolean open) {
        QuartzCriteria quartzCriteria = quartzMap.get(classMethod);
        quartzCriteria.setOpen(open);
    }

}
