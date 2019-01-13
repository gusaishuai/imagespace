package com.imagespace.excel.service.impl;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.imagespace.common.model.Pagination;
import com.imagespace.common.service.impl.RedisPool;
import com.imagespace.excel.dao.ExcelFilterRuleDao;
import com.imagespace.excel.dao.ExcelFilterRuleDetailDao;
import com.imagespace.excel.model.*;
import com.imagespace.excel.service.ExcelService;
import com.imagespace.excel.util.ExcelColIndexUtil;
import com.imagespace.excel.util.RpnUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author gusaishuai
 * @since 2018/12/29
 */
@Component
public class ExcelServiceImpl implements ExcelService {

    private final RedisPool redisPool;
    private final ExcelFilterRuleDao excelFilterRuleDao;
    private final ExcelFilterRuleDetailDao excelFilterRuleDetailDao;

    @Autowired
    public ExcelServiceImpl(RedisPool redisPool,
                            ExcelFilterRuleDao excelFilterRuleDao,
                            ExcelFilterRuleDetailDao excelFilterRuleDetailDao) {
        this.redisPool = redisPool;
        this.excelFilterRuleDao = excelFilterRuleDao;
        this.excelFilterRuleDetailDao = excelFilterRuleDetailDao;
    }

    /**
     * 通过过滤规则查询EXCEL
     */
    public ExcelModel filterExcel(File excel, String expr, int sheetNum, int topNum, int pageNo) {
        FileInputStream fis = null;
        try {
            ExcelModel excelModel = new ExcelModel();
            //文件名（即文件MD5）+表达式 作为key
            String key = String.format("%s_%s_%s_%s", excel.getName(), sheetNum, topNum,
                    StringUtils.isBlank(expr) ? "" : DigestUtils.md5Hex(expr));

            Pagination pagination = new Pagination(pageNo, 15);
            //缓存数据
            if (redisPool.keyExist(key)) {
                pagination.setTotalCount(redisPool.listLength(key));
                //需要过滤表头的行数
                List<String> excelDataList = redisPool.getList(key, pagination.start(), pagination.end() - 1);
                excelModel.setPagination(pagination);
                if (CollectionUtils.isNotEmpty(excelDataList)) {
                    excelModel.setExcelDataList(excelDataList.stream().map(r -> JSON.parseObject(r,
                            new TypeReference<LinkedHashMap<String, String>>() {})).collect(Collectors.toList()));
                }
                return excelModel;
            }

            //表达式替换json
            ExcelExprModel excelExprModel = exprReplaceJson(expr);
            //生成逆波兰表达式
            String[] rpnExprArray = RpnUtil.generateRpnExpr(excelExprModel.getExpr());

            List<Map<String, String>> excelDataList = new ArrayList<>();

            fis = FileUtils.openInputStream(excel);

            //读取EXCEL
            EasyExcelFactory.readBySax(fis, new Sheet(sheetNum, topNum),
                    new AnalysisEventListener<List<String>>() {
                @Override
                public void invoke(List<String> colList, AnalysisContext context) {
                    //去除空的列
                    List<String> noNullValueColList = colList.stream()
                            .filter(StringUtils::isNotBlank).collect(Collectors.toList());
                    int currentRowNum = context.getCurrentRowNum() + 1;
                    //是否符合表达式
                    boolean match = RpnUtil.calcRpnExpr(rpnExprArray, position -> {
                        ExcelExpr excelExpr = excelExprModel.getExcelExprList().get(position);
                        if (excelExpr.getColNum() > noNullValueColList.size()) {
                            return false;
                        }
                        if (excelExpr.isMatched()) {
                            return noNullValueColList.get(excelExpr.getColNum() - 1).matches(excelExpr.getRegex());
                        } else {
                            return !noNullValueColList.get(excelExpr.getColNum() - 1).matches(excelExpr.getRegex());
                        }
                    });
                    //把满足的行过滤出来
                    if (match) {
                        int i = 1;
                        Map<String, String> colMap = new LinkedHashMap<>();
                        colMap.put("row", String.valueOf(currentRowNum));
                        for (String col : noNullValueColList) {
                            colMap.put(ExcelColIndexUtil.getColIndex(i), col);
                            i++;
                        }
                        excelDataList.add(colMap);
                    }
                }
                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {

                }
            });

            if (CollectionUtils.isNotEmpty(excelDataList)) {
                //放入缓存
                redisPool.setList(key, excelDataList.stream()
                        .map(JSON::toJSONString).collect(Collectors.toList()), 24 * 60 * 60);
            }
            pagination.setTotalCount(excelDataList.size());
            excelModel.setPagination(pagination);
            int start = pagination.start() > excelDataList.size() ? excelDataList.size() : pagination.start();
            int end = pagination.end() > excelDataList.size() ? excelDataList.size() : pagination.end();
            excelModel.setExcelDataList(excelDataList.subList(start, end));

            return excelModel;
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取用户下的过滤规则
     */
    @Override
    public List<ExcelFilterRule> getFilterRuleList(Long userId) {
        return excelFilterRuleDao.queryByUserId(userId);
    }

    /**
     * 根据规则ID查询规则列表
     */
    @Override
    public List<ExcelFilterRuleDetail> getFilterRuleDetailList(Long ruleId) {
        return excelFilterRuleDetailDao.queryByRuleId(ruleId);
    }

    /**
     * 更新规则
     */
    @Override
    @Transactional
    public void updateFilterRule(ExcelFilterRule filterRule, List<ExcelFilterRuleDetail> filterRuleDetailList) {
        int count = excelFilterRuleDao.countByName(filterRule.getName(), filterRule.getUserId());
        if (count > 0) {
            throw new IllegalArgumentException(String.format("规则名称：%s 已经存在", filterRule.getName()));
        }
        try {
            //更新规则名称
            excelFilterRuleDao.insert(filterRule);
            filterRuleDetailList.forEach(r -> r.setRuleId(filterRule.getId()));
            //更新规则详细
            excelFilterRuleDetailDao.insertBatch(filterRuleDetailList);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new RuntimeException(e);
        }
    }

    /**
     * 删除规则
     */
    @Override
    @Transactional
    public void deleteFilterRule(Long ruleId) {
        try {
            //删除规则名称
            excelFilterRuleDao.deleteById(ruleId);
            //删除规则详细
            excelFilterRuleDetailDao.deleteByRuleId(ruleId);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new RuntimeException(e);
        }
    }

    /**
     * 表达式替换json
     */
    private ExcelExprModel exprReplaceJson(String expr) {
        ExcelExprModel excelExprModel = new ExcelExprModel();
        if (StringUtils.isBlank(expr)) {
            return excelExprModel;
        }
        List<ExcelExpr> excelExprList = new ArrayList<>();
        Pattern p = Pattern.compile("\\{.*?\\}");
        Matcher matcher = p.matcher(expr);
        int i = 0;
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            excelExprList.add(JSON.parseObject(matcher.group(), ExcelExpr.class));
            matcher.appendReplacement(sb, String.valueOf(i));
            i++;
        }
        matcher.appendTail(sb);
        excelExprModel.setExpr(sb.toString());
        excelExprModel.setExcelExprList(excelExprList);
        return excelExprModel;
    }
    
}
