package com.zm.secretsign.ui.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import com.zhou.library.utils.ToastUtil;
import com.zm.secretsign.ui.LoginActivity;
import com.zm.secretsign.utils.WebJsUtil;

import java.io.Serializable;


/**
 * 基础操作类~
 *
 * @author golden(huaguoting @ gmail.com)
 * @since 2018-03-07 10:29
 */
public abstract class BaseActivity extends AppCompatActivity {
    /**
     * 点击间隔时间
     **/
    private static final long CLICK_INTERVAL_TIME = 2000;
    /**
     * 异步处理助手
     */
    protected final Handler handler = new Handler();
    /**
     * 活动堆栈信息管理
     */
//    public ActivityManager activityManager;
    /**
     * 上次点击时间
     **/
    private long lastClickedTime = 0;
    /**
     * 加载等待框
     */
//    private LoadingDialog mLoadingDialog;


    protected Context mContext;
    protected Activity mActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.mContext = this;
        this.mActivity = this;
//        activityManager = application.getActivityManager();
        // 兼容API 26系统bug，因为android:windowIsTranslucent此属性不能与android:screenOrientation="portrait"属性共存
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        if (!WebJsUtil.isJsFinish) {
            showProgress();
        }
    }


//    @Override
//    protected void onStop() {
//        super.onStop();
//        activityManager.removeActivity(this);
//        Timber.e("======onStop=======");
//    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        activityManager.addActivity(this);
//        Timber.e("======onStart=======");
//    }


    /**
     * 长时间展示弱提示
     *
     * @param msg 消息体
     */
    protected void showLongToast(@NonNull String msg) {
        ToastUtil.showLong(msg);
    }

    /**
     * 长时间展示弱提示
     *
     * @param msg 消息体
     */
    protected void showShortToast(@NonNull String msg) {
        ToastUtil.showShort(msg);
    }

    protected void showShortToast(@StringRes int msg) {
        showShortToast(getString(msg));
    }

    protected void showLongToast(@StringRes int msg) {
        showLongToast(getString(msg));
    }


    //    /**
//     * 显示加载框
//     */
//    protected void showLoadingDialog() {
//        if (mLoadingDialog == null) {
//            mLoadingDialog = new LoadingDialog(this);
//        }
//        mLoadingDialog.show();
//    }
//
//    /**
//     * 消失加载框
//     */
//    protected void dismissLoadingDialog() {
//        if (mLoadingDialog != null) {
//            mLoadingDialog.dismiss();
//        }
//    }
    public void advance(Class<?> cls, Object... params) {
        this.advance(this.getAdvanceIntent(cls, params));
    }

    public void advance(String clsName, Object... params) {
        this.advance(this.getAdvanceIntent(clsName, params));
    }

    public void advance(Intent intent) {
        this.startActivity(intent);
        this.setStartActivityAnim();
    }

    public void advanceForResult(Class<?> cls, int requestCode, Object... params) {
        this.advanceForResult(this.getAdvanceIntent(cls, params), requestCode);
    }

    public void advanceForResult(String clsName, int requestCode, Object... params) {
        this.advanceForResult(this.getAdvanceIntent(clsName, params), requestCode);
    }

    public void advanceForResult(Intent intent, int requestCode) {
        this.startActivityForResult(intent, requestCode);
        this.setStartActivityAnim();
    }

    private void setStartActivityAnim() {
//        if (VERSION.SDK_INT >= 5) {
//            this.overridePendingTransition(anim.view_in_from_right, anim.view_out_to_left);
//        }

    }

    private Intent getAdvanceIntent(Class<?> cls, Object... params) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        return this.putParams(intent, params);
    }

    private Intent getAdvanceIntent(String clsName, Object... params) {
        Intent intent = new Intent();
        intent.setClassName(this, clsName);
        return this.putParams(intent, params);
    }

    private Intent putParams(Intent intent, Object... params) {
        if (intent != null && params != null && params.length > 0) {
            for (int i = 0; i < params.length; ++i) {
                intent.putExtra("p" + i, (Serializable) params[i]);
            }
        }

        return intent;
    }

    protected void fullScreen(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
                Window window = activity.getWindow();
                View decorView = window.getDecorView();
                //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                decorView.setSystemUiVisibility(option);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
                //导航栏颜色也可以正常设置
//                window.setNavigationBarColor(Color.TRANSPARENT);
            } else {
                Window window = activity.getWindow();
                WindowManager.LayoutParams attributes = window.getAttributes();
                int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
                int flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
                attributes.flags |= flagTranslucentStatus;
//                attributes.flags |= flagTranslucentNavigation;
                window.setAttributes(attributes);
            }
        }
    }

    protected void goLogin() {
        advance(LoginActivity.class, "screen");
    }

    private ProgressDialog mypDialog;

    protected void showProgress() {
        if (mypDialog == null) {
            mypDialog = ProgressDialog.show(mContext, "提示", "数据加载中...");
        }
        mypDialog.show();
    }

    protected void hideProgress() {
        if (mypDialog == null || !mypDialog.isShowing()) {
            return;
        }

        mypDialog.dismiss();
    }

}
