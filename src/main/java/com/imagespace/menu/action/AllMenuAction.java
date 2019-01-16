package com.imagespace.menu.action;

import com.imagespace.common.model.CallResult;
import com.imagespace.common.model.ResultCode;
import com.imagespace.common.service.ICallApi;
import com.imagespace.common.util.ExceptionUtil;
import com.imagespace.menu.model.Menu;
import com.imagespace.menu.model.vo.MenuVo;
import com.imagespace.menu.service.MenuService;
import com.imagespace.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gusaishuai
 * @since 19/1/13
 */
@Slf4j
@Service("menu.getAllMenu")
public class AllMenuAction implements ICallApi {

    private final MenuService menuService;

    @Autowired
    public AllMenuAction(MenuService menuService) {
        this.menuService = menuService;
    }

    @Override
    public CallResult exec(User _user, HttpServletRequest request, HttpServletResponse response) {
        try {
            //查询所有菜单
            List<Menu> menuList = menuService.queryAll();
            //构建返回参数
            List<MenuVo> menuVoList = buildVo(menuList);
            return new CallResult(menuVoList);
        } catch (Exception e) {
            log.error("menu.getAllMenu error", e);
            return new CallResult(ResultCode.FAIL, ExceptionUtil.getExceptionTrace(e));
        }
    }

    private List<MenuVo> buildVo(List<Menu> menuList) {
        List<MenuVo> voList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(menuList)) {
            menuList.forEach(r -> {
                MenuVo vo = new MenuVo();
                vo.setId(r.getId());
                vo.setName(r.getName());
                voList.add(vo);
            });
        }
        return voList;
    }

}
