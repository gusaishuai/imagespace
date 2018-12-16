package com.imagespace.login.service;

import com.imagespace.common.anno.IgnoreUserCheck;
import com.imagespace.common.model.CallResult;
import com.imagespace.common.model.Constant;
import com.imagespace.common.model.ResultCode;
import com.imagespace.common.service.ICallApi;
import com.imagespace.common.service.impl.RedisPool;
import com.imagespace.common.util.CommonUtil;
import com.imagespace.common.util.ExceptionUtil;
import com.imagespace.common.util.TripleDESUtil;
import com.imagespace.user.dao.UserDao;
import com.imagespace.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * @author gusaishuai
 * @since 2018/12/16
 */
@Slf4j
@IgnoreUserCheck
@Service("login.checkUser")
public class CheckUserService implements ICallApi {

    private final RedisPool redisPool;
    private final UserDao userDao;

    @Autowired
    public CheckUserService(RedisPool redisPool, UserDao userDao) {
        this.redisPool = redisPool;
        this.userDao = userDao;
    }

    @Override
    public CallResult exec(User _user, HttpServletRequest request, HttpServletResponse response) {
        String cookieCaptchaKey = null;
        try {
            String captcha = request.getParameter("captcha");
            cookieCaptchaKey = Arrays.stream(request.getCookies())
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
            if (StringUtils.isBlank(userName)) {
                return new CallResult(ResultCode.FAIL, "用户名为空");
            }
            String password = request.getParameter("password");
            if (StringUtils.isBlank(password)) {
                return new CallResult(ResultCode.FAIL, "密码为空");
            }
            //ip+用户名
            String redisUserKey = CommonUtil.getIpAddr(request) + userName;
            String failNumStr = redisPool.get(redisUserKey);
            int failNum = StringUtils.isBlank(failNumStr) ? 0 : Integer.valueOf(failNumStr);
            if (failNum > Constant.PASSWORD_FAIL_LIMIT) {
                return new CallResult(ResultCode.FAIL,
                        String.format("用户名：%s 由于密码失败过多已被禁止登录，请等待1小时后自动解锁", userName));
            }
            //根据用户名称查询用户信息
            User user = userDao.queryByLoginName(userName);
            if (user == null || !StringUtils.equals(password, user.getPassword())) {
                String extraMsg = "";
                int restNum = Constant.PASSWORD_FAIL_LIMIT - (failNum + 1);
                if (restNum <= 2) {
                    extraMsg = "|再失败" + restNum + "次，该用户将会被禁止登录";
                }
                redisPool.set(redisUserKey, String.valueOf(failNum + 1), 24 * 60 * 60);
                return new CallResult(ResultCode.FAIL, "用户名密码错误" + extraMsg);
            }
            //登录成功删除之前失败的计数次数
            redisPool.del(redisUserKey);
            //保存到cookie中
            saveCookie(response, user.getId());
            return new CallResult();
        } catch (Exception e) {
            log.error("checkUser error", e);
            return new CallResult(ResultCode.FAIL, ExceptionUtil.getExceptionTrace(e));
        } finally {
            //无论登录失败或成功，都需要删除验证码，防止用户在一个时间段内使用同一个验证码登录
            if (StringUtils.isNotBlank(cookieCaptchaKey)) {
                redisPool.del(cookieCaptchaKey);
            }
        }
    }

    /**
     * 保存到cookie中
     */
    private void saveCookie(HttpServletResponse response, Long userId) {
        Cookie cookie = new Cookie(Constant.COOKIE_USER_KEY,
                TripleDESUtil.encrypt(String.valueOf(userId), Constant.TRIPLE_DES_KEY));
        cookie.setHttpOnly(true);
        cookie.setMaxAge(24 * 60 * 60);
        response.addCookie(cookie);
    }

}
