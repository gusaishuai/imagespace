package com.imagespace.menu.dao;

import com.imagespace.menu.model.Menu;
import com.imagespace.user.model.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author gusaishuai
 * @since 18/12/16
 */
@Mapper
public interface MenuDao {

    List<Menu> queryByUserId(Long userId);

}
