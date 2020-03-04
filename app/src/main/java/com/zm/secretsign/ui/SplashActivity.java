package com.zm.secretsign.ui;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.ClientCertRequest;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;

import com.zhou.library.bean.Event;
import com.zhou.library.utils.LogUtil;
import com.zm.secretsign.BaseApplication;
import com.zm.secretsign.Constant;
import com.zm.secretsign.R;
import com.zm.secretsign.bean.AddressKey;
import com.zm.secretsign.ui.base.BaseStatusBarActivity;
import com.zm.secretsign.utils.AesUtils;
import com.zm.secretsign.utils.DBUtil;
import com.zm.secretsign.utils.DialogUtil;
import com.zm.secretsign.utils.PasswordUtil;
import com.zm.secretsign.utils.WebJsUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SplashActivity extends BaseStatusBarActivity {

    @BindView(R.id.web_view)
    WebView webView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setStatusBar();
        ButterKnife.bind(this);

//        WebJsUtil.initWebView(getApplicationContext(), webView);
        initWebView(mContext, webView);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void initWebView(Context mContext, WebView webView) {
//        mWebView = webView;
        WebJsUtil.mWebView = webView;
        WebJsUtil.isJsFinish = false;
        WebSettings webSettings = webView.getSettings();

//        webSettings.setBlockNetworkImage(false);
//        webSettings.setBlockNetworkLoads(false);
        // 设置与Js交互的权限
        webSettings.setJavaScriptEnabled(true);
        // 设置允许JS弹窗
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        // 先载入JS代码
        // 格式规定为:file:///android_asset/文件名.html
        webView.loadUrl("file:///android_asset/index.html");
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                LogUtil.e("onJsAlert:" + url + "-----" + message);
                DialogUtil.showNormalDialog(mContext, message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        result.confirm();
                    }
                }).setCancelable(false);
                return true;
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                LogUtil.e("onPageStarted:" + url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                LogUtil.e("onPageFinished:" + url);
//                isJsFinish = true;
                WebJsUtil.isJsFinish = true;
                advance(LoginActivity.class);
            }

            @Override
            public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
                super.onReceivedClientCertRequest(view, request);
                LogUtil.e("onReceivedClientCertRequest:");
            }
        });

    }


}
