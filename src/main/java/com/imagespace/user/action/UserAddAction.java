package com.imagespace.user.action;

import com.imagespace.common.model.CallResult;
import com.imagespace.common.model.ResultCode;
import com.imagespace.common.service.ICallApi;
import com.imagespace.common.util.ExceptionUtil;
import com.imagespace.user.model.User;
import com.imagespace.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author gusaishuai
 * @since 19/1/13
 */
@Slf4j
@Service("user.addUser")
public class UserAddAction implements ICallApi {

    @Value("${initial.password}")
    private String initialPassword;

    private final UserService userService;

    @Autowired
    public UserAddAction(UserService userService) {
        this.userService = userService;
    }

    @Override
    public CallResult exec(User _user, HttpServletRequest request, HttpServletResponse response) {
        try {
            String loginName = request.getParameter("loginName");
            if (StringUtils.isBlank(loginName)) {
                throw new IllegalArgumentException("用户名为空");
            }
            String nick = request.getParameter("nick");
            User user = new User();
            user.setLoginName(loginName);
            user.setNick(StringUtils.isBlank(nick) ? loginName : nick);
            user.setPassword(DigestUtils.md5Hex(initialPassword));
            //新增用户
            userService.addUser(user);
            return new CallResult();
        } catch (IllegalArgumentException e) {
            return new CallResult(ResultCode.FAIL, e.getMessage());
        } catch (Exception e) {
            log.error("user.addUser error", e);
            return new CallResult(ResultCode.FAIL, ExceptionUtil.getExceptionTrace(e));
        }
    }

}
