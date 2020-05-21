package com.gnetop.ltgame.core.util;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 日期操作工具类.
 *
 * @author shimiso
 */
@SuppressLint("SimpleDateFormat")
public class DateUtil {


    /**
     * 精确到毫秒的完整时间    如：yyyy-MM-dd HH:mm:ss.S
     */
    private static String FORMAT_FULL_SN = "yyyyMMddHHmmss";


    // strTime要转换的string类型的时间，formatType要转换的格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日
    // HH时mm分ss秒，
    // strTime的时间格式必须要与formatType的时间格式相同
    private static Date stringToDate(String strTime, String formatType)
            throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = null;
        date = formatter.parse(strTime);
        return date;
    }

    // date要转换的date类型的时间
    private static long dateToLong(Date date) {
        return date.getTime();
    }

    // strTime要转换的String类型的时间
    // formatType时间格式
    // strTime的时间格式和formatType的时间格式必须相同
    public static long stringToLong(String strTime) {
        Date date;
        try {
            date = stringToDate(strTime, FORMAT_FULL_SN); // String类型转成date类型
            if (date == null) {
                return 0;
            } else {
                return dateToLong(date);
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    /**
     * 时间戳转换为时间
     */
    private static String getTime(long time) {
        String sdfLongTime = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_FULL_SN);
            sdfLongTime = sdf.format(new Date(time * 1000L));
            if (sdfLongTime != null) {
                sdfLongTime = sdfLongTime.substring(0, 12) + "00";
                return sdfLongTime;
            } else {
                return "";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }


    /**
     * 将字符串转为时间戳
     */
    public static long getTimeStamp(long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(FORMAT_FULL_SN);
        Date date = new Date();
        try {
            date = dateFormat.parse(getTime(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime() / 1000L;
    }


    /**
     * 获取系统时间的10位的时间戳
     */
    public static long getSystemTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * 时间戳转为时间
     */
    public static String getDate2String(long time) {
        Date date = new Date(time*1000L);
        SimpleDateFormat format = new SimpleDateFormat(FORMAT_FULL_SN, Locale.getDefault());
        return format.format(date);
    }

    /**
     * 日期转为时间戳
     */
    public static long getString2Date(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(FORMAT_FULL_SN, Locale.getDefault());
        Date date = new Date();
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime()/1000L;
    }
}
