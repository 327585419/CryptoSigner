package com.zm.secretsign.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.ClientCertRequest;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.divider.GridSpacingItemDecoration;
import com.zhou.library.bean.Event;
import com.zhou.library.utils.EventBusUtil;
import com.zhou.library.utils.LogUtil;
import com.zm.secretsign.Constant;
import com.zm.secretsign.R;
import com.zm.secretsign.ui.base.BaseListActivity;
import com.zm.secretsign.utils.DialogUtil;
import com.zm.secretsign.utils.WebJsUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class MainActivity extends BaseListActivity<Integer> {

    @BindView(R.id.recycler)
    RecyclerView recycler;

    @BindView(R.id.web_view)
    WebView webView;

//    private Integer[] icons = new Integer[]{
//            R.drawable.dizhi, R.drawable.piliangdaoru,
//            R.drawable.ai_password, R.drawable.anquan,
//            R.drawable.xiaoxi, R.drawable.jiaoyiguanli,
//            R.drawable.jiaoyiguanli, R.drawable.jiaoyiguanli,
//    };

    private Integer[] icons = new Integer[]{
            R.mipmap.create_address, R.mipmap.import_key,
            R.mipmap.safe_setting, R.mipmap.manage_key,
            R.mipmap.message_sign, R.mipmap.message_decrypt,
            R.mipmap.advanced_transaction, R.mipmap.simple_transaction,
    };

    @Override
    protected void onInit(@Nullable Bundle savedInstanceState) {
        super.onInit(savedInstanceState);
        bindContentView(R.layout.activity_main);

        container.setBackgroundColor(Color.WHITE);

        setUpIcon(null);
        setTitle(R.string.app_name);

        int dp = getResources().getDimensionPixelSize(R.dimen.dp_05);
        divider = new GridSpacingItemDecoration(dp, dp, Color.parseColor("#F1F1F1"), false);
        layoutManager = new GridLayoutManager(mContext, 2);
        initRecycler();
        List<Integer> names = new ArrayList<>();
        names.add(R.string.create_address);
        names.add(R.string.import_key);
        names.add(R.string.security_setting);
        names.add(R.string.manage_key);
        names.add(R.string.message_sign);
        names.add(R.string.message_decrypt);
        names.add(R.string.high_deal);
        names.add(R.string.simple_deal);
        mAdapter.setNewData(names);

        setDownTextRightIcon();
        EventBus.getDefault().register(this);
        initWebView(mContext, webView);
        hideProgress();
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void initWebView(Context mContext, WebView webView) {
//        mWebView = webView;
        WebJsUtil.mWebView = webView;
        WebJsUtil.isJsFinish = false;
        WebSettings webSettings = webView.getSettings();
        webSettings.setDomStorageEnabled(true);

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
//                advance(LoginActivity.class);
                EventBusUtil.post(WebJsUtil.PAGE_FINISHED);
            }

            @Override
            public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
                super.onReceivedClientCertRequest(view, request);
                LogUtil.e("onReceivedClientCertRequest:");
            }
        });

    }


    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveEvent(Event event) {
        switch (event.name) {
            case Constant.COIN_TYPE:
                downTextView.setText(event.param.toUpperCase());
                break;
            case Constant.SET_PWD:
                goLogin();
                break;
            case "exit":
                finish();
                break;
        }
    }


    @Override
    protected void onItemViewClick(View view, int position, Integer item) {
        super.onItemViewClick(view, position, item);
        switch (position) {
            case 0:
                advance(CreateAddressActivity.class);
                break;
            case 1:
                advance(ImportKeyActivity.class);
                break;
            case 2:
                advance(SettingActivity.class);
                break;
            case 3:
                advance(ManageKeyActivity.class);
                break;
            case 4:
                advance(MessageSignActivity.class);
                break;
            case 5:
                advance(MessageDecryptActivity.class);
                break;
            case 6:
                //高级交易
                advance(TransactionSignActivity.class, 2);
                break;
            case 7:
                //简单交易
                advance(TransactionSignActivity.class, 1);
                break;
        }
    }

    @Override
    public void convertItem(BaseViewHolder holder, Integer item) {
        holder.setText(R.id.tv_name, item)
                .setImageResource(R.id.iv_icon, icons[holder.getLayoutPosition()]);
    }

    @Override
    public int bindItemView() {
        return R.layout.item_main;
    }

    @Override
    public RecyclerView bindRecyclerView() {
        return recycler;
    }
}
