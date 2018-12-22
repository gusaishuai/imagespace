package com.imagespace.menu.action;

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
 * @since 2018/12/22
 */
@Slf4j
@Service("menu.modifyPwd")
public class ModifyPwdAction implements ICallApi {

    @Autowired
    private UserService userService;

    @Override
    public CallResult exec(User _user, HttpServletRequest request, HttpServletResponse response) {
        try {
            String oldPassword = request.getParameter("oldPassword");
            if (StringUtils.isBlank(oldPassword)) {
                throw new IllegalArgumentException("原密码为空");
            }
            String newPassword = request.getParameter("newPassword");
            if (StringUtils.isBlank(newPassword)) {
                throw new IllegalArgumentException("新密码为空");
            }
            if (StringUtils.equals(oldPassword, newPassword)) {
                throw new IllegalArgumentException("原密码和新密码不能一致");
            }
            if (!StringUtils.equalsIgnoreCase(_user.getPassword(), oldPassword)) {
                throw new IllegalArgumentException("原密码不正确");
            }
            userService.updatePwd(_user.getId(), newPassword);
            return new CallResult();
        } catch (IllegalArgumentException e) {
            return new CallResult(ResultCode.FAIL, e.getMessage());
        } catch (Exception e) {
            log.error("menu.modifyPwd error", e);
            return new CallResult(ResultCode.FAIL, ExceptionUtil.getExceptionTrace(e));
        }
    }

}
