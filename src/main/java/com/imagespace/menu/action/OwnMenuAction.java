package com.imagespace.menu.action;

import com.imagespace.common.model.CallResult;
import com.imagespace.common.model.ResultCode;
import com.imagespace.common.service.ICallApi;
import com.imagespace.common.util.ExceptionUtil;
import com.imagespace.menu.model.Menu;
import com.imagespace.menu.model.vo.OwnMenuVo;
import com.imagespace.menu.service.MenuService;
import com.imagespace.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author gusaishuai
 * @since 18/12/22
 */
@Slf4j
@Service("menu.getOwnMenu")
public class OwnMenuAction implements ICallApi {

    @Autowired
    private MenuService menuService;

    @Override
    public CallResult exec(User _user, HttpServletRequest request, HttpServletResponse response) {
        try {
            List<Menu> menuList = menuService.queryByUserId(_user.getId());
            OwnMenuVo vo = new OwnMenuVo();
            vo.setMenuList(menuList);
            vo.setNick(StringUtils.isNotBlank(_user.getNick()) ? _user.getNick() : _user.getLoginName());
            return new CallResult(vo);
        } catch (Exception e) {
            log.error("menu.getMenu error", e);
            return new CallResult(ResultCode.FAIL, ExceptionUtil.getExceptionTrace(e));
        }
    }

}
