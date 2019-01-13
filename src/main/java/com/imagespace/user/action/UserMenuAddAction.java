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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author gusaishuai
 * @since 19/1/13
 */
@Slf4j
@Service("user.addUserMenu")
public class UserMenuAddAction implements ICallApi {

    @Autowired
    private UserService userService;

    @Override
    public CallResult exec(User _user, HttpServletRequest request, HttpServletResponse response) {
        try {
            String userId = request.getParameter("userId");
            if (StringUtils.isBlank(userId)) {
                throw new IllegalArgumentException("用户ID为空");
            }
            String[] menuIds = request.getParameterValues("menuIds[]");
            List<Long> menuIdList = null;
            if (menuIds != null && menuIds.length > 0) {
                menuIdList = Arrays.stream(menuIds).map(Long::valueOf).collect(Collectors.toList());
            }
            //删除用户
            userService.addUserMenu(Long.valueOf(userId), menuIdList);
            return new CallResult();
        } catch (IllegalArgumentException e) {
            return new CallResult(ResultCode.FAIL, e.getMessage());
        } catch (Exception e) {
            log.error("user.userDelete error", e);
            return new CallResult(ResultCode.FAIL, ExceptionUtil.getExceptionTrace(e));
        }
    }

}
