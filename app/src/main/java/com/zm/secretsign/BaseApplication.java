package com.zm.secretsign;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.zhou.library.utils.AppUtil;
import com.zhou.library.utils.EventBusUtil;
import com.zhou.library.utils.LogUtil;
import com.zhou.library.utils.SPUtil;
import com.zm.secretsign.ui.LoginActivity;
import com.zm.secretsign.utils.AesUtils;
import com.zm.secretsign.utils.DBUtil;
import com.zm.secretsign.utils.PasswordUtil;

public class BaseApplication extends Application {

    /**
     * 设置的密码 (加密之前)
     */
    private static String password;
    private static String sha256;

    @Override
    public void onCreate() {
        super.onCreate();
        AppUtil.init(this);
        DBUtil.initDatabase(this);

        IntentFilter filter = new IntentFilter();
        // 屏幕灭屏广播
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        // 屏幕亮屏广播
        filter.addAction(Intent.ACTION_SCREEN_ON);
        // 屏幕解锁广播
        filter.addAction(Intent.ACTION_USER_PRESENT);
        // 当长按电源键弹出“关机”对话或者锁屏时系统会发出这个广播
        // example：有时候会用到系统对话框，权限可能很高，会覆盖在锁屏界面或者“关机”对话框之上，
        // 所以监听这个广播，当收到时就隐藏自己的对话，如点击pad右下角部分弹出的对话框
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);

        BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, final Intent intent) {
                LogUtil.d("onReceive");
                String action = intent.getAction();

                if (Intent.ACTION_SCREEN_ON.equals(action)) {
                    LogUtil.e("screen on");
//                    EventBusUtil.post(Constant.SET_PWD);

                    Intent intent1 = new Intent();
                    intent1.setClass(context, LoginActivity.class);
                    intent1.putExtra("p0", action);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent1);
                } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                    LogUtil.e("screen off");
                } else if (Intent.ACTION_USER_PRESENT.equals(action)) {
                    LogUtil.e("screen unlock");

                } else if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(intent.getAction())) {
                    LogUtil.e(" receive Intent.ACTION_CLOSE_SYSTEM_DIALOGS");
                }
            }
        };
        LogUtil.d("registerReceiver");
        registerReceiver(mBatInfoReceiver, filter);
        SPUtil.put(Constant.NETWORK_CONNECT, false);
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String pwd) {
        password = pwd;
        setSha256(PasswordUtil.getSHA256(pwd));
    }

    public static String getSha256() {
        return sha256;
    }

    public static void setSha256(String sha256) {
        BaseApplication.sha256 = sha256;
    }

//    public static String encrypt(Object object) {
//        return encrypt(JSON.toJSONString(object));
//    }

    public static String encrypt(String cleartext) {
        return AesUtils.encrypt(getSha256(), cleartext);
    }

    public static String decrypt(String encrypted) {
        return AesUtils.decrypt(getSha256(), encrypted);
    }

//    public static <T> T decrypt2Object(String encrypted, Class<T> clazz) {
//        return JSON.parseObject(decrypt(encrypted), clazz);
//    }
}
