package com.imagespace.common.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * @author gusaishuai
 * @since 2018/12/16
 */
@Component
@Slf4j
public class SpringContext implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        SpringContext.applicationContext = applicationContext;
    }

    public static String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotation) {
        return applicationContext.getBeanNamesForAnnotation(annotation);
    }

    public static <T> T getBean(String className) {
        if (StringUtils.isBlank(className)) {
            return null;
        }
        try {
            return (T) applicationContext.getBean(className);
        } catch (BeansException e) {
            log.warn("SpringContext getBean error : {}", e.getMessage());
            return null;
        }
    }

}
