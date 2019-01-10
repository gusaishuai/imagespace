package com.imagespace.common.interceptor;

import com.alibaba.fastjson.JSON;
import com.imagespace.common.model.CallResult;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @author gusaishuai
 * @since 2018/12/16
 */
@Aspect
@Component
public class CallInterceptor {

//    @Around(value = "execution(* com.imagespace.common.controller.CallController.*(..))")
//    public Object around(ProceedingJoinPoint proxy) throws Throwable {
//        Object result = proxy.proceed();
//        if (result instanceof CallResult) {
//            return JSON.toJSONString(result);
//        }
//        return result;
//    }

}
