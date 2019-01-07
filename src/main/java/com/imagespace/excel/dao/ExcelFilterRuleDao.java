package com.imagespace.excel.dao;

import com.imagespace.excel.model.ExcelFilterRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author gusaishuai
 * @since 19/1/6
 */
@Mapper
public interface ExcelFilterRuleDao {

    List<ExcelFilterRule> queryByUserId(Long userId);

    int countByName(@Param("name") String name, @Param("userId") Long userId);

    void insert(ExcelFilterRule filterRule);

}
