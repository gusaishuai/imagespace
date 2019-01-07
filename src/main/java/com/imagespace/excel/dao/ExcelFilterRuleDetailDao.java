package com.imagespace.excel.dao;

import com.imagespace.excel.model.ExcelFilterRuleDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author gusaishuai
 * @since 19/1/6
 */
@Mapper
public interface ExcelFilterRuleDetailDao {

    List<ExcelFilterRuleDetail> queryByRuleId(Long ruleId);

    void insertBatch(@Param("itemList") List<ExcelFilterRuleDetail> itemList);

}
