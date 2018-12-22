package com.imagespace.menu.service;

import com.imagespace.common.model.CallResult;
import com.imagespace.common.model.ResultCode;
import com.imagespace.common.service.ICallApi;
import com.imagespace.common.util.ExceptionUtil;
import com.imagespace.menu.dao.MenuDao;
import com.imagespace.menu.model.Menu;
import com.imagespace.menu.model.MenuVo;
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
@Service("login.getMenu")
public class MenuService implements ICallApi {

    @Autowired
    private MenuDao menuDao;

    @Override
    public CallResult exec(User _user, HttpServletRequest request, HttpServletResponse response) {
        try {
            List<Menu> menuList = menuDao.queryByUserId(_user.getId());
            MenuVo vo = new MenuVo();
            vo.setMenuList(menuList);
            vo.setNick(StringUtils.isNotBlank(_user.getNick()) ? _user.getNick() : _user.getLoginName());
            return new CallResult(vo);
        } catch (Exception e) {
            log.error("login.getMenu error", e);
            return new CallResult(ResultCode.FAIL, ExceptionUtil.getExceptionTrace(e));
        }
    }

}
