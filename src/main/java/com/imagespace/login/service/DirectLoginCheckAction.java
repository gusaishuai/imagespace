package com.imagespace.login.service;

import com.imagespace.common.model.CallResult;
import com.imagespace.common.service.ICallApi;
import com.imagespace.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author gusaishuai
 * @since 2018/12/22
 */
@Slf4j
@Service("login.checkDirectLogin")
public class DirectLoginCheckAction implements ICallApi {

    @Override
    public CallResult exec(User _user, HttpServletRequest request, HttpServletResponse response) {
        //@see CallController,该切面已经完成对用户的登录信息校验，本方法不做任何处理
        return new CallResult();
    }

}
