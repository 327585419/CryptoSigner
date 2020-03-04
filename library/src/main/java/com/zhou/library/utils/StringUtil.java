package com.zhou.library.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author : Zhouzhou
 * e-mail : 553419781@qq.com
 * date   : 2019/8/13 17:04
 */
public class StringUtil {

    // 判断一个字符串是否都为数字
    public boolean isDigit(String strNum) {
        return strNum.matches("[0-9]{1,}");
    }

//    // 判断一个字符串是否都为数字
//    public boolean isDigit(String strNum) {
//        Pattern pattern = Pattern.compile("[0-9]{1,}");
//        Matcher matcher = pattern.matcher((CharSequence) strNum);
//        return matcher.matches();
//    }

    //截取数字
    public String getNumbers(String content) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            return matcher.group(0);
        }
        return "";
    }

    // 截取非数字
    public String splitNotNumber(String content) {
        Pattern pattern = Pattern.compile("\\D+");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            return matcher.group(0);
        }
        return "";
    }


    // 判断一个字符串是否含有数字
    public static boolean hasDigit(String content) {
        boolean flag = false;
        Pattern p = Pattern.compile(".*\\d+.*");
        Matcher m = p.matcher(content);
        if (m.matches()) {
            flag = true;
        }
        return flag;
    }

    public static boolean hasLetter(String content) {
        return content.matches(".*[a-zA-z].*");
    }


    /**
     * 校验一个字符是否是汉字
     *
     * @param c 被校验的字符
     * @return true代表是汉字
     */
    public static boolean isChineseChar(String c) {
        try {
            return c.getBytes("UTF-8").length > 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
