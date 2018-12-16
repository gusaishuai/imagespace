package com.imagespace.common.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

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
        if (SpringContext.applicationContext != null) {
            return;
        }
        SpringContext.applicationContext = applicationContext;
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
