package com.imagespace.quartz.util;

/**
 * @author gusaishuai
 * @since 2019/2/13
 */
public class IntervalUtil {

    /**
     * 格式化毫秒
     */
    public static String formatMillSec(Long millSec) {
        if (millSec == null) {
            return "";
        }
        if (millSec >= 0 && millSec < 999) {
            return millSec + "毫秒";
        } else if (millSec >= 1000 && millSec < 1000 * 60 - 1) {
            return millSec / 1000 + "秒";
        } else if(millSec >= 1000 * 60 && millSec < 1000 * 60 * 60 - 1) {
            long minute = millSec / (1000 * 60);
            long second = millSec / 1000 - minute * 60;
            String format = minute + "分";
            if (second != 0) {
                format += second + "秒";
            }
            return format;
        } else if (millSec > 1000 * 60 * 60) {
            long hour = millSec / (1000 * 60 * 60);
            long minute = millSec / (1000 * 60) - hour * 60;
            long second = millSec / 1000 - minute * 60 - hour * 60 * 60;
            String format = hour + "小时";
            if (minute != 0) {
                format += minute + "分";
            }
            if (second != 0) {
                format += second + "秒";
            }
            return format;
        }
        return "";
    }

}
