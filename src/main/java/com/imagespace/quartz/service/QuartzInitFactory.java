package com.imagespace.quartz.service;

import com.imagespace.common.service.impl.SpringContext;
import com.imagespace.quartz.model.QuartzCriteria;
import com.imagespace.quartz.model.anno.EnableQuartz;
import com.imagespace.quartz.model.anno.Quartz;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.quartz.CronExpression;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author gusaishuai
 * @since 19/1/20
 */
@Slf4j
@Service
public class QuartzInitFactory {

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @PostConstruct
    public void init() {
        executorService.submit(this::initQuartz);
    }

    private void initQuartz() {
        try {
            Arrays.stream(SpringContext.getBeanNamesForAnnotation(EnableQuartz.class)).forEach(beanName -> {
                //类的实例
                Optional.ofNullable(SpringContext.getBean(beanName))
                        .ifPresent(clazz -> Arrays.stream(clazz.getClass().getDeclaredMethods()).forEach(method -> {
                            if (!method.isAnnotationPresent(Quartz.class)) {
                                return;
                            }
                            Quartz quartz = method.getAnnotation(Quartz.class);
                            //定时任务名称，如果没有写注解中的name，则默认为类名+方法名
                            String quartzName = StringUtils.isBlank(quartz.name())
                                    ? clazz.getClass().getName() + "." + method.getName() : quartz.name();
                            try {
                                if (!Objects.equals(Void.TYPE, method.getReturnType())) {
                                    throw new IllegalArgumentException("定时任务返回类型必须为void");
                                }
                                if (method.getParameterCount() != 0) {
                                    throw new IllegalArgumentException("定时任务的入参必须为空");
                                }
                                Date startDate;
                                if (StringUtils.isBlank(quartz.startDate())) {
                                    startDate = Calendar.getInstance().getTime();
                                } else {
                                    try {
                                        //时间格式是否符合规范
                                        startDate = DateUtils.parseDateStrictly(quartz.startDate(), "yyyy-MM-dd HH:mm:ss");
                                    } catch (ParseException e) {
                                        throw new IllegalArgumentException(String.format("定时任务配置开始时间转换错误：%s", quartz.startDate()));
                                    }
                                }
                                if (quartz.defaultSchedule().use()) {
                                    //时间间隔是否符合规范
                                    int internalInSecond = quartz.defaultSchedule().internalInSecond();
                                    if (internalInSecond <= 0) {
                                        throw new IllegalArgumentException(String.format(
                                                "定时任务选择了默认定时类型，运行间隔必须大于0，当前值：%s", internalInSecond));
                                    }
                                    //重复次数是否符合规范
                                    int repeatCount = quartz.defaultSchedule().repeatCount();
                                    if (repeatCount <= 0 && repeatCount != -1 ) {
                                        throw new IllegalArgumentException(String.format(
                                                "定时任务选择了默认定时类型，重复次数必须大于0或为-1，当前值：%s", repeatCount));
                                    }
                                } else if (quartz.cronSchedule().use()) {
                                    //cron表达式是否符合规范
                                    String expression = quartz.cronSchedule().expression();
                                    if (StringUtils.isBlank(expression)) {
                                        throw new IllegalArgumentException("定时任务选择了CRON定时类型，CRON表达式为空");
                                    }
                                    if (!CronExpression.isValidExpression(expression)) {
                                        throw new IllegalArgumentException(String.format(
                                                "定时任务选择了CRON定时类型，CRON表达式不合法：%s", expression));
                                    }
                                } else {
                                    //两个schedule都没启用
                                    throw new IllegalArgumentException("定时任务必须启用一种类型的schedule");
                                }
                                //内存map，可用于查询定时任务
//                                QuartzCriteria quartzCriteria = QuartzMapFactory.INSTANCE
//                                        .putQuartzMap(clazz, method.getName(), startDate, quartz);
//                                log.info("quartz [" + quartzCriteria.getQuartzName() + "] put into quartzMap");
                                //增加定时任务
//                                QuartzSchedulerFactory.INSTANCE.addJob(quartzCriteria);
//                                log.info("quartz [" + quartzCriteria.getQuartzName() + "] put into scheduleJob");
                            } catch (IllegalArgumentException e) {
                                log.error("quartz [" + quartzName + "] can not init : " + e.getMessage());
                            } catch (Exception e) {
//                                log.error("quartz [" + quartzName + "] can not init : " + QuartzErrorTrace.getExceptionTrace(e));
                            }
                        }));
            });
            //项目中有对应的定时任务才启动
//            if (QuartzMapFactory.INSTANCE.hasQuartz()) {
//                QuartzSchedulerFactory.INSTANCE.startJob();
//                log.info("all quartz start ...");
//            }
        } catch (IllegalArgumentException e) {
            log.error("定时任务初始化失败", e.getMessage());
        } catch (Exception e) {
            log.error("定时任务初始化失败", e);
        }
    }

}
