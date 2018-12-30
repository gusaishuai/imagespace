package com.imagespace.excel.service.impl;

import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.fastjson.JSON;
import com.imagespace.excel.model.ExcelExpr;
import com.imagespace.excel.model.ExcelExprs;
import com.imagespace.excel.util.RpnUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
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
@Slf4j
@Service
public class ExcelService {

    @Value("${excel.upload.tempdir}")
    private String tempDir;

    public List<List<String>> queryByExpr(String excelName, String expr) {
        FileInputStream fis = null;
        try {
            ExcelExprs excelExprs = buildExpr(expr);
            String[] rpnExprArray = RpnUtil.generateRpnExpr(excelExprs.getExpr());

            List<List<String>> resultList = new ArrayList<>();

            fis = FileUtils.openInputStream(new File(tempDir + excelName));
            new ExcelReader(fis, null, new AnalysisEventListener<List<String>>() {

                @Override
                public void invoke(List<String> colList, AnalysisContext context) {
                    boolean match = RpnUtil.calcRpnExpr(rpnExprArray, position -> {
                        ExcelExpr excelExpr = excelExprs.getExcelExprList().get(position);
                        if (excelExpr.isMatch()) {
                            return colList.get(excelExpr.getColNum() - 1).matches(excelExpr.getRegex());
                        } else {
                            return !colList.get(excelExpr.getColNum() - 1).matches(excelExpr.getRegex());
                        }
                    });
                    //把不满足的行过滤出来
                    if (!match) {
                        resultList.add(colList);
                    }
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {

                }
            }, false).read(new Sheet(1, 0));

            return resultList;
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    log.error("sss");
                }
            }
        }
    }

    private ExcelExprs buildExpr(String expr) {
        ExcelExprs excelExprs = new ExcelExprs();
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
