//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.zhou.library.utils;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build.VERSION;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AppUtil {
    private static Application app;
    private static boolean debug;

    public AppUtil() {
    }


    public static boolean isTablet() {
        boolean xlarge = ((app.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        boolean large = (app.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE;
        return (xlarge || large);
    }

    public static void init(Application application) {
        app = application;
        debug = (app.getApplicationInfo().flags & 2) != 0;
        ToastUtil.init(application);
        SPUtil.init(application);

        //启动日志打印
        if (isDebug()) {
            LogUtil.plant(new LogUtil.DebugTree());
        }
    }

    @TargetApi(19)
    public static Application app() {
        if (app == null) {
            if (VERSION.SDK_INT >= 19) {
                try {
                    Class<?> renderActionClass = Class.forName("com.android.layoutlib.bridge.impl.RenderAction");
                    Method method = renderActionClass.getDeclaredMethod("getCurrentContext");
                    Context context = (Context) method.invoke((Object) null);
                    app = new AppUtil.MockApplication(context);
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | ClassNotFoundException var3) {
                    throwException();
                }
            } else {
                throwException();
            }
        }

        return app;
    }

    private static void throwException() {
        throw new RuntimeException("please invoke app.init(app) on Application#onCreate() and register your Application in manifest.");
    }

    public static boolean isDebug() {
        return debug;
    }

    private static class MockApplication extends Application {
        MockApplication(Context baseContext) {
            this.attachBaseContext(baseContext);
        }
    }

    /**
     * 打开键盘
     */
    public static void showKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getApplicationContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        view.requestFocus();
        if (imm == null) return;
        imm.showSoftInput(view, 0);
    }

    /**
     * 隐藏键盘
     */
    public static void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getApplicationContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) return;
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
