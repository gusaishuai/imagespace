package com.imagespace.common.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author gusaishuai
 * @since 2018/12/16
 */
public class SpringContext implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (SpringContext.applicationContext != null) {
            return;
        }
        SpringContext.applicationContext = applicationContext;
    }

    public static <T> T getBean(String className) {
        return StringUtils.isBlank(className) ? null : (T) applicationContext.getBean(className);
    }

}
