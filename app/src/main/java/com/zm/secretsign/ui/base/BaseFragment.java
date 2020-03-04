package com.zm.secretsign.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.webkit.ValueCallback;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import com.zhou.library.utils.ToastUtil;
import com.zm.secretsign.R;
import com.zm.secretsign.utils.JSCallBack;
import com.zm.secretsign.utils.WebJsUtil;


/**
 * author : Zhouzhou
 * e-mail : 553419781@qq.com
 * date   : 2019/8/15 15:37
 */
public class BaseFragment extends Fragment {

    protected Context mContext;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = getContext();
    }

    protected void callJavaScriptFunction(String strJS, ValueCallback callback) {
        if (!WebJsUtil.isJsFinish) {
            showLongToast(R.string.load_finish);
            return;
        }

        WebJsUtil.callJavaScriptFunction(strJS, callback);
    }

    protected void callJavaScriptFunction(String strJS) {
        if (!WebJsUtil.isJsFinish) {
            showLongToast(R.string.load_finish);
            return;
        }

        WebJsUtil.callJavaScriptFunction(strJS, new JSCallBack() {
            @Override
            public void onSuccess(String value) {
                super.onSuccess(value);
                onJSCallBack(value);
            }

            @Override
            public void onFailed(String msg) {
                super.onFailed(msg);
                showShortToast(msg);
            }
        });
    }

    protected void onJSCallBack(String value) {

    }

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

    public void clearView() {

    }

}
