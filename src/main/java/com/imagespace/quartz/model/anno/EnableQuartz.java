package com.imagespace.quartz.model.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用定时任务
 * 该注解只能加在类上
 * @author gusaishuai
 * @since 2017/3/28
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableQuartz {
}
