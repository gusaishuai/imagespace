package com.imagespace.user.action;

import com.imagespace.common.model.CallResult;
import com.imagespace.common.model.ResultCode;
import com.imagespace.common.service.ICallApi;
import com.imagespace.common.util.ExceptionUtil;
import com.imagespace.menu.model.Menu;
import com.imagespace.menu.service.MenuService;
import com.imagespace.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author gusaishuai
 * @since 19/1/13
 */
@Slf4j
@Service("user.queryUserMenu")
public class UserMenuQueryAction implements ICallApi {

    @Autowired
    private MenuService menuService;

    @Override
    public CallResult exec(User _user, HttpServletRequest request, HttpServletResponse response) {
        try {
            String userId = request.getParameter("userId");
            if (StringUtils.isBlank(userId)) {
                throw new IllegalArgumentException("用户ID为空");
            }
            List<Menu> menuList = menuService.queryByUserId(Long.valueOf(userId));
            //构建返回参数
            List<Long> menuIdList = buildVo(menuList);
            return new CallResult(menuIdList);
        } catch (IllegalArgumentException e) {
            return new CallResult(ResultCode.FAIL, e.getMessage());
        } catch (Exception e) {
            log.error("user.queryUserMenu error", e);
            return new CallResult(ResultCode.FAIL, ExceptionUtil.getExceptionTrace(e));
        }
    }

    private List<Long> buildVo(List<Menu> menuList) {
        if (CollectionUtils.isNotEmpty(menuList)) {
            return menuList.stream().map(Menu::getId).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

}
