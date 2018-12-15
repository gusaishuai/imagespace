package com.imagespace.common.controller;

import com.imagespace.common.anno.IgnoreUserCheck;
import com.imagespace.common.model.CallResult;
import com.imagespace.common.model.Constant;
import com.imagespace.common.model.ResultCode;
import com.imagespace.common.service.ICallApi;
import com.imagespace.common.service.impl.RedisPool;
import com.imagespace.common.service.impl.SpringContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * @author gusaishuai
 * @since 2018/12/16
 */
@Controller
@RequestMapping("/")
public class CallController {

    @Autowired
    private RedisPool redisPool;

    @ResponseBody
    @RequestMapping("/exec")
    public CallResult exec(String _mt, HttpServletRequest request, HttpServletResponse response) {
        ICallApi callApi = SpringContext.getBean(_mt);
        if (!callApi.getClass().isAnnotationPresent(IgnoreUserCheck.class)) {
            String cookieUserKey = Arrays.stream(request.getCookies())
                    .filter(r -> StringUtils.equals(r.getName(), Constant.COOKIE_USER_KEY))
                    .findFirst().map(Cookie::getValue).orElse(null);
            if (StringUtils.isBlank(cookieUserKey)) {
                return new CallResult(ResultCode.NO_LOGIN, "用户未登录");
            }
            String userId = redisPool.get(cookieUserKey);
            if (StringUtils.isBlank(userId)) {
                return new CallResult(ResultCode.NO_LOGIN, "用户登录信息已过期");
            }
            //查询用户表
        }
        return callApi.exec(request, response);
    }

}
