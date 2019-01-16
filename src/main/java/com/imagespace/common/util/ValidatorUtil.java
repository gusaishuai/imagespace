package com.imagespace.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author gusaishuai
 * @since 2019/1/16
 */
@Slf4j
public class ValidatorUtil {

    //15位身份证最小年份
    private static final int MIN_YEAR = 1930;
    //身份证前两位，表示城市
    private static Map<String, String> cityCodeMap = new HashMap<>();
    static {
        cityCodeMap.put("11", "北京");
        cityCodeMap.put("12", "天津");
        cityCodeMap.put("13", "河北");
        cityCodeMap.put("14", "山西");
        cityCodeMap.put("15", "内蒙古");
        cityCodeMap.put("21", "辽宁");
        cityCodeMap.put("22", "吉林");
        cityCodeMap.put("23", "黑龙江");
        cityCodeMap.put("31", "上海");
        cityCodeMap.put("32", "江苏");
        cityCodeMap.put("33", "浙江");
        cityCodeMap.put("34", "安徽");
        cityCodeMap.put("35", "福建");
        cityCodeMap.put("36", "江西");
        cityCodeMap.put("37", "山东");
        cityCodeMap.put("41", "河南");
        cityCodeMap.put("42", "湖北");
        cityCodeMap.put("43", "湖南");
        cityCodeMap.put("44", "广东");
        cityCodeMap.put("45", "广西");
        cityCodeMap.put("46", "海南");
        cityCodeMap.put("50", "重庆");
        cityCodeMap.put("51", "四川");
        cityCodeMap.put("52", "贵州");
        cityCodeMap.put("53", "云南");
        cityCodeMap.put("54", "西藏");
        cityCodeMap.put("61", "陕西");
        cityCodeMap.put("62", "甘肃");
        cityCodeMap.put("63", "青海");
        cityCodeMap.put("64", "宁夏");
        cityCodeMap.put("65", "新疆");
        cityCodeMap.put("71", "台湾");
        cityCodeMap.put("81", "香港");
        cityCodeMap.put("82", "澳门");
        cityCodeMap.put("91", "国外");
    }

    /**
     * 验证手机号是否合法
     */
    public static boolean validPhone(String phone) {
        if (StringUtils.isBlank(phone)) {
            return false;
        }
        return phone.matches("^(1[3-9])\\d{9}$");
    }

    /**
     * 验证email是否合法
     */
    public static boolean validEmail(String email) {
        if (StringUtils.isBlank(email)) {
            return false;
        }
        return email.matches("^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$");
    }

    /**
     * 验证身份证是否合法
     */
    public static boolean validIdNo(String idNo) {
        if (StringUtils.isBlank(idNo)) {
            return false;
        }
        return validIdNo18(idNo.trim()) || validIdNo15(idNo.trim());
    }

    /**
     * 验证18位身份编码是否合法
     */
    private static boolean validIdNo18(String idNo) {
        if (idNo.length() != 18) {
            return false;
        }
        //前17位
        String idNo17 = idNo.substring(0, 17);
        if (!StringUtils.isNumeric(idNo17)) {
            return false;
        }
        int[] intArray = convertCharToInt(idNo17.toCharArray());
        int pSum17 = getPowerSum(intArray);
        //获取校验位
        String checkCode = getCheckCode18(pSum17);
        //第18位
        String idNo18 = idNo.substring(17);
        return StringUtils.equalsIgnoreCase(checkCode, idNo18);
    }

    /**
     * 验证15位身份编码是否合法
     */
    private static boolean validIdNo15(String idNo) {
        if (idNo.length() != 15 || !StringUtils.isNumeric(idNo)) {
            return false;
        }
        String cityCode = idNo.substring(0, 2);
        if (!cityCodeMap.containsKey(cityCode)) {
            return false;
        }
        String birthCode = idNo.substring(6, 12);
        Date birthDate;
        try {
            birthDate = new SimpleDateFormat("yy").parse(birthCode.substring(0, 2));
        } catch (ParseException e) {
            log.error("验证15位身份编码转换年份错误", e);
            return false;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(birthDate);
        //年份是否合法
        return validDate(cal.get(Calendar.YEAR),
                Integer.valueOf(birthCode.substring(2, 4)), Integer.valueOf(birthCode.substring(4, 6)));
    }

    /**
     * 将字符数组转换成数字数组
     */
    private static int[] convertCharToInt(char[] charArray) {
        int length = charArray.length;
        int[] intArray = new int[length];
        try {
            for (int i = 0; i < length; i++) {
                intArray[i] = Integer.parseInt(String.valueOf(charArray[i]));
            }
        } catch (NumberFormatException e) {
            log.error("验证15位身份转换char到int错误", e);
        }
        return intArray;
    }

    /**
     * 将身份证的每位和对应位的加权因子相乘之后，再得到和值
     */
    private static int getPowerSum(int[] intArray) {
        int power[] = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
        int pSum = 0;
        if (power.length == intArray.length) {
            for (int i = 0; i < intArray.length; i++) {
                for (int j = 0; j < power.length; j++) {
                    if (i == j) {
                        pSum = pSum + intArray[i] * power[j];
                    }
                }
            }
        }
        return pSum;
    }

    /**
     * 将power和值与11取模获得余数进行校验码判断
     */
    private static String getCheckCode18(int pSum) {
        String checkCode = "";
        switch (pSum % 11) {
            case 10:
                checkCode = "2";
                break;
            case 9:
                checkCode = "3";
                break;
            case 8:
                checkCode = "4";
                break;
            case 7:
                checkCode = "5";
                break;
            case 6:
                checkCode = "6";
                break;
            case 5:
                checkCode = "7";
                break;
            case 4:
                checkCode = "8";
                break;
            case 3:
                checkCode = "9";
                break;
            case 2:
                checkCode = "x";
                break;
            case 1:
                checkCode = "0";
                break;
            case 0:
                checkCode = "1";
                break;
        }
        return checkCode;
    }

    /**
     * 验证日期是否合法
     */
    private static boolean validDate(int yy, int mm, int dd) {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int datePerMonth;
        if (yy < MIN_YEAR || yy >= year) {
            return false;
        }
        if (mm < 1 || mm > 12) {
            return false;
        }
        switch (mm) {
            case 4:
            case 6:
            case 9:
            case 11:
                datePerMonth = 30;
                break;
            case 2:
                boolean dm = (yy % 4 == 0 && yy % 100 != 0 || yy % 400 == 0) && yy > MIN_YEAR;
                datePerMonth = dm ? 29 : 28;
                break;
            default:
                datePerMonth = 31;
        }
        return (dd >= 1) && (dd <= datePerMonth);
    }
    
}
