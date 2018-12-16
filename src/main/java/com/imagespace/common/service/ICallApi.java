package com.imagespace.common.service;

import com.imagespace.common.model.CallResult;
import com.imagespace.user.model.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author gusaishuai
 * @since 2018/12/16
 */
public interface ICallApi {

    CallResult exec(User _user, HttpServletRequest request, HttpServletResponse response);

}
