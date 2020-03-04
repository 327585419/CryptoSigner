package com.zhou.library.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.StringRes;

/**
 * author : Zhouzhou
 * e-mail : 553419781@qq.com
 * date   : 2019/6/6 16:31
 */
public class ToastUtil {

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    public static void init(Context c) {
        context = c;
    }

    public static void showLong(@StringRes int message) {
        showLong(context.getString(message));
    }

    public static void showLong(String message) {
        if (TextUtils.isEmpty(message)) {
            return;
        }

        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void showShort(@StringRes int message) {
        showShort(context.getString(message));
    }

    public static void showShort(String message) {
        if (TextUtils.isEmpty(message)) {
            return;
        }

        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
