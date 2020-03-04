package com.zm.secretsign.utils;

import android.text.TextUtils;
import android.webkit.ValueCallback;

/**
 * author : Zhouzhou
 * e-mail : 553419781@qq.com
 * date   : 2019/9/2 11:42
 */
public class JSCallBack implements ValueCallback<String> {

    @Override
    public void onReceiveValue(String s) {
        if (TextUtils.isEmpty(s) || s.startsWith("null") || s.startsWith("NULL")) {
            onFailed("请求错误，请检查输入内容");
            return;
        }

        onSuccess(s);
    }

    public void onSuccess(String value) {

    }

    public void onFailed(String msg) {

    }
}
