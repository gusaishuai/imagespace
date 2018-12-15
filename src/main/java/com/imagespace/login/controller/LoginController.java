package com.imagespace.login.controller;

import com.imagespace.common.anno.IgnoreUserCheck;
import com.imagespace.common.model.CallResult;
import com.imagespace.common.model.Constant;
import com.imagespace.common.model.ResultCode;
import com.imagespace.common.service.impl.RedisPool;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * @author gusaishuai
 * @since 2018/12/16
 */
@Controller
@RequestMapping("/")
@IgnoreUserCheck
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private RedisPool redisPool;

    @ResponseBody
    @RequestMapping("/checkUser")
    public CallResult checkUser(HttpServletRequest request) {
        String captcha = request.getParameter("captcha");
        String cookieCaptchaKey = Arrays.stream(request.getCookies())
                .filter(r -> StringUtils.equals(r.getName(), Constant.COOKIE_CAPTCHA_KEY))
                .findFirst().map(Cookie::getValue).orElse(null);
        if (StringUtils.isBlank(cookieCaptchaKey)) {
            return new CallResult(ResultCode.FAIL, "验证码无效");
        }
        String realCaptcha = redisPool.get(cookieCaptchaKey);
        if (StringUtils.isBlank(realCaptcha)) {
            return new CallResult(ResultCode.FAIL, "验证码已过期");
        } else if (!StringUtils.equalsIgnoreCase(captcha, realCaptcha)) {
            return new CallResult(ResultCode.FAIL, "验证码错误");
        }
        String userName = request.getParameter("userName");
        String password = request.getParameter("password");
        return null;
    }

}
