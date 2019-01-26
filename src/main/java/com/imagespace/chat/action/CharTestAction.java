package com.imagespace.chat.action;

import com.imagespace.common.model.CallResult;
import com.imagespace.common.service.ICallApi;
import com.imagespace.common.util.ExceptionUtil;
import com.imagespace.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author gusaishuai
 * @since 2019/1/26
 */
@Slf4j
@Service("chat.test")
public class CharTestAction implements ICallApi {

    @Autowired
    private WebSocket webSocket;

    @Override
    public CallResult exec(User _user, HttpServletRequest request, HttpServletResponse response) {
        try {
            String abc = request.getParameter("abc");
            webSocket.sendMessage(abc);
            return new CallResult(abc);
        } catch (Exception e) {
            e.printStackTrace();
            return new CallResult(ExceptionUtil.getExceptionTrace(e));
        }
    }

}
