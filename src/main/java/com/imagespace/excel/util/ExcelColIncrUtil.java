package com.imagespace.excel.util;

/**
 * @author gusaishuai
 * @since 2019/1/3
 */
public class ExcelColIncrUtil {

    private static String[] letters = {"A", "B", "C", "D"};

    public static void main(String[] args) {
        //1 2 3 4 5  6  7  8  9  10 11 12 13 14 15 16 17 18 19 20 21  22
        //A B C D AA AB AC AD BA BB BC BD CA CB CC CD DA DB DC DD AAA AAB
//        System.out.println(21 / letters.length);
//        System.out.println(21 % letters.length);

        System.out.println(getLetter(21));
    }

    private static String getLetter(int index) {
        int divide = index / letters.length;
        int mod = index % letters.length;
        if (divide == 0) {
            return letters[mod - 1];
        }
        String letter = "";
        if (divide > letters.length) {
            letter = getLetter(divide);
        }
        if (mod == 0) {
            if (divide - 2 < 0) {
                return letters[letters.length - 1];
            }
            return letter + letters[divide - 2] + letters[letters.length - 1];
        }
        return letter + letters[divide - 1] + letters[mod - 1];
    }

}
