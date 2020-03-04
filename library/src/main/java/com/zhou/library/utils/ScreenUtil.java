package com.zhou.library.utils;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

/**
 * author : Zhouzhou
 * e-mail : 553419781@qq.com
 * date   : 2019/8/1 11:02
 */
public class ScreenUtil {

    /**
     * 获取屏幕宽高像素
     */
    public static Point getDisplay() {
        WindowManager manager = (WindowManager) AppUtil.app().getSystemService(Context.WINDOW_SERVICE);
        if (manager == null) return null;
        Display display = manager.getDefaultDisplay();
        Point outSize = new Point();
        display.getSize(outSize);
        return outSize;
    }

    public static int getScreenWidth() {
        Point point = getDisplay();
        if (point != null) {
            return point.x;
        } else {
            return 0;
        }

    }

    public static int getScreenHeight() {
        Point point = getDisplay();
        if (point != null) {
            return point.y;
        } else {
            return 0;
        }
    }
}
