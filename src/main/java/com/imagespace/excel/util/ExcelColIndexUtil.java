package com.imagespace.excel.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author gusaishuai
 * @since 2019/1/3
 */
public class ExcelColIndexUtil {


    //定义初始列号，当大于此列表，才进行扩容
    private static List<String> colIndexList = new ArrayList<>(Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H",
            "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"));

    /**
     * 序号（数字）转字母序号（如：AA），增加缓存区
     */
    public static String getColIndex(int colIndex) {
        if (colIndex <= 0) {
            throw new IllegalArgumentException("列数必须大于0");
        } else if (colIndex > 1 << 16) {
            throw new IllegalArgumentException("列数不要大于65536行");
        }
        if (colIndex > colIndexList.size()) {
            for (int i = colIndexList.size(); i < colIndex; i++) {
                colIndexList.add(colIndex(i + 1));
            }
        }
        return colIndexList.get(colIndex - 1);
    }

    /**
     * 序号（数字）转字母序号（如：AA）
     */
    private static String colIndex(int colIndex) {
        StringBuilder columnStr = new StringBuilder();
        colIndex--;
        while (colIndex > 0) {
            if (columnStr.length() > 0) {
                colIndex--;
            }
            columnStr.insert(0, ((char) (colIndex % 26 + (int) 'A')));
            colIndex = (colIndex - colIndex % 26) / 26;
        }
        return columnStr.toString();
    }

}
