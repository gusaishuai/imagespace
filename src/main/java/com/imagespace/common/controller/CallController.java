package com.imagespace.common.controller;

import com.imagespace.common.anno.IgnoreUserCheck;
import com.imagespace.common.model.CallResult;
import com.imagespace.common.model.Constant;
import com.imagespace.common.model.ResultCode;
import com.imagespace.common.service.ICallApi;
import com.imagespace.common.service.impl.SpringContext;
import com.imagespace.common.util.ExceptionUtil;
import com.imagespace.common.util.TripleDESUtil;
import com.imagespace.user.model.User;
import com.imagespace.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class CallController {

    private final UserService userService;

    @Autowired
    public CallController(UserService userService) {
        this.userService = userService;
    }

    @ResponseBody
    @RequestMapping("/exec")
    public CallResult exec(String _mt, HttpServletRequest request, HttpServletResponse response) {
        try {
            ICallApi callApi = SpringContext.getBean(_mt);
            if (callApi == null) {
                return new CallResult(ResultCode.FAIL, "找不到方法：" + _mt);
            }
            User user = null;
            if (!callApi.getClass().isAnnotationPresent(IgnoreUserCheck.class)) {
                String cookieUserKey = Arrays.stream(request.getCookies())
                        .filter(r -> StringUtils.equals(r.getName(), Constant.COOKIE_USER_KEY))
                        .findFirst().map(Cookie::getValue).orElse(null);
                if (StringUtils.isBlank(cookieUserKey)) {
                    return new CallResult(ResultCode.RE_LOGIN, "用户未登录");
                }
                //3DES解密
                String userId = TripleDESUtil.decrypt(cookieUserKey, Constant.TRIPLE_DES_KEY);
                if (StringUtils.isBlank(userId) || !StringUtils.isNumeric(userId)) {
                    return new CallResult(ResultCode.RE_LOGIN, "用户登录信息已被恶意修改");
                }
                //查询用户表
                user = userService.queryById(Long.valueOf(userId));
                if (user == null) {
                    return new CallResult(ResultCode.RE_LOGIN, "用户不存在");
                }
            }
            return callApi.exec(user, request, response);
        } catch (Exception e) {
            log.error("method : {} , exec error", _mt, e);
            return new CallResult(ResultCode.FAIL, ExceptionUtil.getExceptionTrace(e));
        }
    }

}
