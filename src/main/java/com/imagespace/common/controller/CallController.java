package com.imagespace.common.controller;

import com.imagespace.common.anno.IgnoreUserCheck;
import com.imagespace.common.model.CallResult;
import com.imagespace.common.model.Constant;
import com.imagespace.common.model.MediaCallResult;
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
                Cookie[] cookies = request.getCookies();
                String cookieUserKey = cookies == null ? null : Arrays.stream(cookies)
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
            CallResult callResult = callApi.exec(user, request, response);
            if (callResult instanceof MediaCallResult) {
                //流数据
                MediaCallResult mediaCallResult = (MediaCallResult) callResult;
                response.getOutputStream().write(mediaCallResult.getStream());
                response.setHeader("Content-Type", mediaCallResult.getMediaType());
                if (StringUtils.isNotBlank(mediaCallResult.getFileName())) {
                    //下载的文件名
                    response.setHeader("Content-Disposition", "attachment;filename=" + mediaCallResult.getFileName());
                }
                return null;
            } else {
                return callResult;
            }
        } catch (Exception e) {
            log.error("method : {} , exec error", _mt, e);
            return new CallResult(ResultCode.FAIL, ExceptionUtil.getExceptionTrace(e));
        } finally {
            //跨域
            response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
            response.setHeader("Access-Control-Allow-Credentials", "true");
        }
    }

}
