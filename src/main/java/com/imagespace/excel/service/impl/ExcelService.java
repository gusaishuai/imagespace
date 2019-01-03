package com.imagespace.excel.service.impl;

import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.fastjson.JSON;
import com.imagespace.excel.model.ExcelExpr;
import com.imagespace.excel.model.ExcelExprs;
import com.imagespace.excel.util.RpnUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author gusaishuai
 * @since 2018/12/29
 */
@Service
public class ExcelService {

    @Value("${excel.upload.tempdir}")
    private String tempDir;

    public List<List<String>> queryByExpr(String excelName, int sheetNum, int topNum, String expr) {
        FileInputStream fis = null;
        try {
            //表达式替换json
            ExcelExprs excelExprs = exprReplaceJson(expr);
            //生成逆波兰表达式
            String[] rpnExprArray = RpnUtil.generateRpnExpr(excelExprs.getExpr());

            List<List<String>> resultList = new ArrayList<>();

            fis = FileUtils.openInputStream(new File(tempDir + excelName));
            //读取EXCEL
            new ExcelReader(fis, null, new AnalysisEventListener<List<String>>() {
                @Override
                public void invoke(List<String> colList, AnalysisContext context) {
                    boolean match;
                    //表头数据不参与判断
                    if (context.getCurrentRowNum() + 1 <= topNum) {
                        match = true;
                    } else {
                        match = RpnUtil.calcRpnExpr(rpnExprArray, position -> {
                            ExcelExpr excelExpr = excelExprs.getExcelExprList().get(position);
                            if (excelExpr.isMatch()) {
                                return colList.get(excelExpr.getColNum() - 1).matches(excelExpr.getRegex());
                            } else {
                                return !colList.get(excelExpr.getColNum() - 1).matches(excelExpr.getRegex());
                            }
                        });
                    }
                    //把满足的行过滤出来
                    if (match) {
                        resultList.add(colList);
                    }
                }
                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {

                }
            }, false).read(new Sheet(sheetNum, 0));

            return resultList;
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
    private ExcelExprs exprReplaceJson(String expr) {
        ExcelExprs excelExprs = new ExcelExprs();
        if (StringUtils.isBlank(expr)) {
            return excelExprs;
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
        excelExprs.setExpr(sb.toString());
        excelExprs.setExcelExprList(excelExprList);
        return excelExprs;
    }
    
}
