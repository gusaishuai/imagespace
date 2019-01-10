package com.imagespace.user.action;

import com.imagespace.common.model.CallResult;
import com.imagespace.common.model.Page;
import com.imagespace.common.model.ResultCode;
import com.imagespace.common.service.ICallApi;
import com.imagespace.common.util.ExceptionUtil;
import com.imagespace.user.model.User;
import com.imagespace.user.model.vo.UserVo;
import com.imagespace.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gusaishuai
 * @since 2019/1/10
 */
@Slf4j
@Service("user.userQuery")
public class UserQueryAction implements ICallApi {

    @Autowired
    private UserService userService;

    @Override
    public CallResult exec(User _user, HttpServletRequest request, HttpServletResponse response) {
        try {
            String pageNoStr = request.getParameter("pageNo");
            int pageNo = StringUtils.isBlank(pageNoStr) ? 1 : Integer.valueOf(pageNoStr);
            //登录名
            String loginName = request.getParameter("loginName");
            //分页查询用户信息列表
            Page<User> userPage = userService.getUserByPage(loginName, pageNo);
            //构建返回参数
            List<UserVo> voList = buildVo(userPage.getList());
            return new CallResult(voList);
        } catch (IllegalArgumentException e) {
            return new CallResult(ResultCode.FAIL, e.getMessage());
        } catch (Exception e) {
            log.error("user.userQuery error", e);
            return new CallResult(ResultCode.FAIL, ExceptionUtil.getExceptionTrace(e));
        }
    }

    private List<UserVo> buildVo(List<User> userList) {
        List<UserVo> voList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(userList)) {
            userList.forEach(r -> {
                UserVo vo = new UserVo();
                vo.setId(r.getId());
                vo.setLoginName(r.getLoginName());
                vo.setNick(r.getNick());
                voList.add(vo);
            });
        }
        return voList;
    }

}
