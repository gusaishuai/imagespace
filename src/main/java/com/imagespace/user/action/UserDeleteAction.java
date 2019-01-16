package com.imagespace.user.action;

import com.imagespace.common.model.CallResult;
import com.imagespace.common.model.ResultCode;
import com.imagespace.common.service.ICallApi;
import com.imagespace.common.util.ExceptionUtil;
import com.imagespace.user.model.User;
import com.imagespace.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author gusaishuai
 * @since 19/1/12
 */
@Slf4j
@Service("user.deleteUser")
public class UserDeleteAction implements ICallApi {

    private final UserService userService;

    @Autowired
    public UserDeleteAction(UserService userService) {
        this.userService = userService;
    }

    @Override
    public CallResult exec(User _user, HttpServletRequest request, HttpServletResponse response) {
        try {
            String userId = request.getParameter("userId");
            if (StringUtils.isBlank(userId)) {
                throw new IllegalArgumentException("用户ID为空");
            }
            //删除用户
            userService.deleteUser(Long.valueOf(userId));
            return new CallResult();
        } catch (IllegalArgumentException e) {
            return new CallResult(ResultCode.FAIL, e.getMessage());
        } catch (Exception e) {
            log.error("user.userDelete error", e);
            return new CallResult(ResultCode.FAIL, ExceptionUtil.getExceptionTrace(e));
        }
    }

}
