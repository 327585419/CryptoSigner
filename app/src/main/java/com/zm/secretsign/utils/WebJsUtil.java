package com.zm.secretsign.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Build;
import android.webkit.ClientCertRequest;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.RequiresApi;

import com.zhou.library.utils.LogUtil;

/**
 * author : Zhouzhou
 * e-mail : 553419781@qq.com
 * date   : 2019/12/3 21:24
 */
public class WebJsUtil {

    public static final String PAGE_FINISHED = "onPageFinished";

    public static boolean isJsFinish;
    public static WebView mWebView;

    @SuppressLint("SetJavaScriptEnabled")
    public static void initWebView(Context mContext, WebView webView) {
        mWebView = webView;
//        isJsFinish = false;
//        WebSettings webSettings = webView.getSettings();
//
////        webSettings.setBlockNetworkImage(false);
////        webSettings.setBlockNetworkLoads(false);
//        // 设置与Js交互的权限
//        webSettings.setJavaScriptEnabled(true);
//        // 设置允许JS弹窗
//        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
//
//        // 先载入JS代码
//        // 格式规定为:file:///android_asset/文件名.html
//        webView.loadUrl("file:///android_asset/index.html");
//        webView.setWebChromeClient(new WebChromeClient() {
//            @Override
//            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
//                LogUtil.e("onJsAlert:" + url + "-----" + message);
//                DialogUtil.showNormalDialog(mContext, message, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        result.confirm();
//                    }
//                }).setCancelable(false);
//                return true;
//            }
//        });
//
//        webView.setWebViewClient(new WebViewClient() {
//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                super.onPageStarted(view, url, favicon);
//                LogUtil.e("onPageStarted:" + url);
//            }
//
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                super.onPageFinished(view, url);
//                LogUtil.e("onPageFinished:" + url);
//                isJsFinish = true;
//            }
//
//            @Override
//            public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
//                super.onReceivedClientCertRequest(view, request);
//                LogUtil.e("onReceivedClientCertRequest:");
//            }
//        });

    }

    public static void callJavaScriptFunction(String strJS, ValueCallback callback) {
//        if (!isJsFinish) {
//            showLongToast(R.string.load_finish);
//            return;
//        }

        mWebView.post(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                // 注意调用的JS方法名要对应上
                LogUtil.e("调用的JS方法:" + strJS);
                mWebView.evaluateJavascript(strJS, callback);
            }
        });
    }
}
