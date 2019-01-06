package com.imagespace.excel.service.impl;

import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.imagespace.common.model.Pagination;
import com.imagespace.common.service.impl.RedisPool;
import com.imagespace.excel.model.ExcelExpr;
import com.imagespace.excel.model.ExcelExprModel;
import com.imagespace.excel.model.ExcelModel;
import com.imagespace.excel.util.ExcelColIndexUtil;
import com.imagespace.excel.util.RpnUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author gusaishuai
 * @since 2018/12/29
 */
@Service
public class ExcelService {

    private final RedisPool redisPool;

    @Autowired
    public ExcelService(RedisPool redisPool) {
        this.redisPool = redisPool;
    }

    public ExcelModel filterExcel(File excel, String expr, int sheetNum, int topNum, int pageNo) {
        FileInputStream fis = null;
        try {
            ExcelModel excelModel = new ExcelModel();
            //文件名（即文件MD5）+表达式 作为key
            String key = String.format("%s_%s_%s_%s", excel.getName(), sheetNum, topNum,
                    StringUtils.isBlank(expr) ? "" : DigestUtils.md5Hex(expr));
            //缓存数据
            String value = redisPool.get(key);
            if (StringUtils.isNotBlank(value)) {
                List<LinkedHashMap<String, String>> excelDataMapList = JSON.parseObject(
                        value, new TypeReference<List<LinkedHashMap<String, String>>>(){});
                Pagination pagination = new Pagination(pageNo, excelDataMapList.size());
                excelModel.setPagination(pagination);
                excelModel.setExcelDataList(new ArrayList<>(
                        excelDataMapList.subList(pagination.start(), pagination.end())));
                return excelModel;
            }
            //表达式替换json
            ExcelExprModel excelExprModel = exprReplaceJson(expr);
            //生成逆波兰表达式
            String[] rpnExprArray = RpnUtil.generateRpnExpr(excelExprModel.getExpr());

            List<Map<String, String>> excelDataList = new ArrayList<>();

            fis = FileUtils.openInputStream(excel);
            //读取EXCEL
            new ExcelReader(fis, null, new AnalysisEventListener<List<String>>() {
                @Override
                public void invoke(List<String> colList, AnalysisContext context) {
                    int currentRowNum = context.getCurrentRowNum() + 1;
                    //是否符合表达式
                    boolean match = RpnUtil.calcRpnExpr(rpnExprArray, position -> {
                        ExcelExpr excelExpr = excelExprModel .getExcelExprList().get(position);
                        if (excelExpr.isMatch()) {
                            return colList.get(excelExpr.getColNum() - 1).matches(excelExpr.getRegex());
                        } else {
                            return !colList.get(excelExpr.getColNum() - 1).matches(excelExpr.getRegex());
                        }
                    });
                    //把满足的行过滤出来
                    if (match) {
                        int i = 1;
                        Map<String, String> colMap = new LinkedHashMap<>();
                        colMap.put("row", String.valueOf(currentRowNum));
                        for (String col : colList) {
                            colMap.put(ExcelColIndexUtil.getColIndex(i), col);
                            i++;
                        }
                        excelDataList.add(colMap);
                    }
                }
                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {

                }
            }, false).read(new Sheet(sheetNum, topNum));

            //放入缓存
            redisPool.set(key, JSON.toJSONString(excelDataList), 24 * 60 * 60);

            Pagination pagination = new Pagination(pageNo, excelDataList.size());
            excelModel.setPagination(pagination);
            excelModel.setExcelDataList(excelDataList.subList(pagination.start(), pagination.end()));

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
