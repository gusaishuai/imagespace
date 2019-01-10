package com.imagespace.user.action;

import com.imagespace.common.model.CallResult;
import com.imagespace.common.model.Page;
import com.imagespace.common.model.Pagination;
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
            Page<User> userPage = userService.queryUserByPage(loginName, new Pagination(pageNo));
            //构建返回参数
            Page<UserVo> voPage = buildVo(userPage);
            return new CallResult(voPage);
        } catch (IllegalArgumentException e) {
            return new CallResult(ResultCode.FAIL, e.getMessage());
        } catch (Exception e) {
            log.error("user.userQuery error", e);
            return new CallResult(ResultCode.FAIL, ExceptionUtil.getExceptionTrace(e));
        }
    }

    private Page<UserVo> buildVo(Page<User> userPage) {
        Page<UserVo> voPage = new Page<>();
        voPage.setPageNo(userPage.getPageNo());
        voPage.setPageSize(userPage.getPageSize());
        voPage.setTotalCount(userPage.getTotalCount());

        List<UserVo> voList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(userPage.getList())) {
            userPage.getList().forEach(r -> {
                UserVo vo = new UserVo();
                vo.setId(r.getId());
                vo.setLoginName(r.getLoginName());
                vo.setNick(r.getNick());
                voList.add(vo);
            });
        }
        voPage.setList(voList);
        return voPage;
    }

}
