package com.imagespace.excel.dao;

import com.imagespace.excel.model.ExcelFilterRule;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author gusaishuai
 * @since 19/1/6
 */
@Mapper
public interface ExcelFilterRuleDao {

    List<ExcelFilterRule> queryByUserId(Long userId);

}
