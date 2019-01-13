package com.imagespace.user.dao;

import com.imagespace.user.model.UserMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author gusaishuai
 * @since 19/1/13
 */
@Mapper
public interface UserMenuDao {

    void insertBatch(@Param("itemList") List<UserMenu> userMenuList);

    void deleteByUserId(Long userId);
}
