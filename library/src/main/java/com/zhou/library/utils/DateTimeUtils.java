package com.zhou.library.utils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 时间格式工具类
 * Created by MIN on 2017/image_check/18.
 */

public class DateTimeUtils {

    public static final long ONE_MINUTE_MILLIONS = 60 * 1000;
    public static final long ONE_HOUR_MILLIONS = 60 * ONE_MINUTE_MILLIONS;
    public static final long ONE_DAY_MILLIONS = 24 * ONE_HOUR_MILLIONS;

    public static final String YMDHMS = "yyyy-MM-dd HH:mm:ss";
    public static final String YMDHM = "yyyy-MM-dd HH:mm";
    public static final String YMDE = "yyyy-MM-dd E";
    public static final String YMD = "yyyy-MM-dd";
    public static final String YM = "yyyy-MM";
    public static final String MD = "MM-dd";
    public static final String MDHMS = "MM-dd HH:mm:ss";
    public static final String MDHM = "MM-dd HH:mm";
    public static final String HMS = "HH:mm:ss";
    public static final String HM = "HH:mm";
    public static final String EHM = "E HH:mm";
    public static final String E = "E";
    public static final String DHM = "dd日 HH:mm";
    public static final String MONTH = "月前";
    public static final String WEEL = "周前";
    public static final String DAY = "天前";
    public static final String YESTERDAY = "昨天";
    public static final String HOUR = "小时前";
    public static final String MINUTE = "分钟前";
    public static final String JUST = "刚刚";
    public static final String UNKNOW = "Unknow";

    /**
     * 将秒数转换成00:00的字符串，如 118秒 -> 01:58
     *
     * @param time
     * @return
     */
    public static String secToTime(int time) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return "00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":"
                        + unitFormat(second);
            }
        }
        return timeStr;
    }

    public static String formatTime(int ms) {
        int totalSeconds = ms / 1000;
        int seconds = totalSeconds % 60;
        int minutes = totalSeconds / 60 % 60;
        int hours = totalSeconds / 60 / 60;
        String timeStr = "";
        if (hours > 9) {
            timeStr += hours + ":";
        } else if (hours > 0) {
            timeStr += "0" + hours + ":";
        }
        if (minutes > 9) {
            timeStr += minutes + ":";
        } else if (minutes > 0) {
            timeStr += "0" + minutes + ":";
        } else {
            timeStr += "00:";
        }
        if (seconds > 9) {
            timeStr += seconds;
        } else if (seconds > 0) {
            timeStr += "0" + seconds;
        } else {
            timeStr += "00";
        }
        return timeStr;
    }

    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

    public static String getFormatCurrentForMinus() {
        return getMinusFormat().format(new Date());
    }

    public static String getFormatForMinus(long time) {
        return getMinusFormat().format(time);
    }

    public static long getTimeForMinus(String time) throws ParseException {
        return getMinusFormat().parse(time).getTime();
    }

    public static String getFormatCurrentForMinusDay() {
        return getMinusDayFormat().format(new Date());
    }

    public static String getFormatForMinusDay(long time) {
        return getMinusDayFormat().format(time);
    }

    public static long getTimeForMinusDay(String time) throws ParseException {
        return getMinusDayFormat().parse(time).getTime();
    }

    public static String getFormatCurrentForMinusAndColon() {
        return getMinusAndColonFormat().format(new Date());
    }

    public static String getFormatForMinusAndColon(long time) {
        return getMinusAndColonFormat().format(time);
    }

    public static String getFormatForMinusAndColonNoSecond(Date date) {
        return getMinusAndColonNoSecondFormat().format(date);
    }

    public static long getTimeForMinusAndColon(String time) throws ParseException {
        return getMinusAndColonFormat().parse(time).getTime();
    }

    public static String getFormatForPoint(long time) {
        return getPointFormat().format(time);
    }

    public static Date getDateForPoint(String format) throws ParseException {
        return getPointFormat().parse(format);
    }

    public static String getFormatForChineseDay(Date date) {
        return getChineseDayFormat().format(date);
    }

    public static String getFormatCurrentForChineseDay() {
        return getChineseDayFormat().format(new Date());
    }

    public static Date getDateForChinese(String format) throws ParseException {
        return getChineseFormat().parse(format);
    }


    public static Date getDateForMinus(String format) throws ParseException {
        return getMinusAndColonFormat().parse(format);
    }

    public static Date getDateForMinusDay(String format) throws ParseException {
        return getMinusDayFormat().parse(format);
    }

    public static String getFormatCurrentForChinese() {
        return getChineseFormat().format(new Date());
    }

    public static long getTimeForChinese(String time) throws ParseException {
        return getChineseFormat().parse(time).getTime();
    }

    public static long getTimeForChineseDay(String time) throws ParseException {
        return getChineseDayFormat().parse(time).getTime();
    }

    public static String getFormatForMinutesSecond(long time) {
        return getMinutesSecondFormat().format(time);
    }

    public static String getFormatCurrentForHourMinutes() {
        return getHourMinutesFormat().format(new Date());
    }


    private static SimpleDateFormat getChineseDayFormat() {
        return new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
    }

    private static SimpleDateFormat getChineseFormat() {
        return new SimpleDateFormat("yyyy年MM月", Locale.CHINA);
    }

    private static SimpleDateFormat getMinusDayFormat() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    }

    private static SimpleDateFormat getMinusFormat() {
        return new SimpleDateFormat("yyyy-MM", Locale.CHINA);
    }

    private static SimpleDateFormat getPointFormat() {
        return new SimpleDateFormat("yyyy.MM.dd", Locale.CHINA);
    }

    private static SimpleDateFormat getMinusAndColonFormat() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    }

    private static SimpleDateFormat getMinusAndColonNoSecondFormat() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
    }

    private static SimpleDateFormat getMinutesSecondFormat() {
        return new SimpleDateFormat("mm:ss", Locale.CHINA);
    }

    private static SimpleDateFormat getHourMinutesFormat() {
        return new SimpleDateFormat("HH:mm", Locale.CHINA);
    }

    private static SimpleDateFormat getMinusMonthDayMinutesSecondFormat() {
        return new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA);
    }

    public static String getMediaTime(int second) {
        return second > 60 ? second / 60 + "\'" + (second % 60 > 0 ? second % 60 + "\"" : "") : second + "\"";
    }

    public static String getMediaTimeNumber(int second) {
        if (second > 60) {
            int minute = second / 60;
            int sec = second % 60;
            String secStr = sec > 9 ? String.valueOf(sec) : "0" + sec;

            return (minute > 9 ? minute : "0" + minute) + ":" + secStr;
        } else {
            return second > 9 ? "00:" + second : "00:0" + second;
        }
    }

    public static String getDays(long millis) {
        LogUtil.e("getDays:current" + System.currentTimeMillis());
        return (millis * 1000 - System.currentTimeMillis()) / (1000 * 60 * 60 * 24) + "天后";
    }
}
