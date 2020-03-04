package com.zm.secretsign.ui.base;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ClientCertRequest;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.core.widget.PopupWindowCompat;

import com.zhou.library.utils.EventBusUtil;
import com.zhou.library.utils.LogUtil;
import com.zhou.library.utils.SPUtil;
import com.zm.secretsign.Constant;
import com.zm.secretsign.R;
import com.zm.secretsign.ui.LoginActivity;
import com.zm.secretsign.utils.DialogUtil;
import com.zm.secretsign.utils.JSCallBack;
import com.zm.secretsign.utils.WebJsUtil;
import com.zm.secretsign.view.PopupWindowWrapper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 标题操作栏活动
 *
 * @author golden(huaguoting @ gmail.com)
 * @since 2016-03-11 16:59
 */
public abstract class BaseTitleActivity extends BaseActivity {
    protected LayoutInflater layoutInflater;
    protected ViewGroup rootElement;
    /**
     * 容器视图
     */
    protected FrameLayout container;
    /**
     * 导航条
     */
    @BindView(R.id.app_bar)
    protected View titleBarLayout;
    /**
     * 标题视图
     */
    @BindView(R.id.title)
    protected TextView titleView;
    /**
     * 标题图标视图
     */
    @BindView(R.id.title_icon)
    protected ImageView titleIconView;
    /**
     * 左侧图标视图
     */
    @BindView(R.id.up_icon)
    protected ImageView upIconView;
    /**
     * 左侧文字视图
     */
    @BindView(R.id.up_text)
    protected TextView upTextView;
    /**
     * 右侧图标视图
     */
    @BindView(R.id.down_icon)
    protected ImageView downIconView;
    /**
     * 右侧文字视图
     */
    @BindView(R.id.down_text)
    protected TextView downTextView;

    /**
     * 无网络刷新
     */
    @BindView(R.id.no_network)
    protected RelativeLayout noNetworkLayout;
    /**
     * 沉浸式效果视图
     */
    protected View immersionStyleView;
    /**
     * ButterKnife绑定对象
     */
    private Unbinder unbinder;


    protected abstract void onInit(@Nullable Bundle savedInstanceState);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layoutInflater = getLayoutInflater();
        rootElement = (ViewGroup) layoutInflater.inflate(R.layout.activity_app_container, null);
        container = rootElement.findViewById(R.id.container);
        setContentView(rootElement);
        onInit(savedInstanceState);
    }

    @Override
    public void finish() {
        try {
            if (unbinder != null)
                unbinder.unbind();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.finish();
    }

    protected void setContainerBackgroundRes(@DrawableRes int res) {
        rootElement.setBackgroundResource(res);
    }


    /**
     * 添加内容
     *
     * @param layoutResId 布局文件ID
     */
    protected void bindContentView(@LayoutRes int layoutResId) {
        layoutInflater.inflate(layoutResId, container);
        unbinder = ButterKnife.bind(this, rootElement);
    }

    /**
     * 添加内容
     *
     * @param view 可用视图
     */
    protected void bindContentView(@NonNull View view) {
        container.addView(view);
        unbinder = ButterKnife.bind(this, rootElement);
    }

    /**
     * 移除导航栏
     */
    protected void hideTitleBar() {
        titleBarLayout.setVisibility(View.GONE);
    }

    /**
     * 动态修改状态栏颜色
     *
     * @param color 色值
     */
    protected void setTitleBarColor(int color) {
        titleBarLayout.setBackgroundColor(color);
    }

    /**
     * 动态修改状态栏颜色
     *
     * @param resId 背景图
     */
    protected void setTitleBarDrawable(@DrawableRes int resId) {
        titleBarLayout.setBackgroundResource(resId);
    }

    /**
     * 向上事件
     */
    public void up() {
        finish();
    }

    protected PopupWindowWrapper popupWindowWrapper;

    /**
     * 向下事件
     */
    public void down() {
        List<String> data = new ArrayList<>();
        if (popupWindowWrapper == null) {
            data.add("fch");
            data.add("btc");
            data.add("bch");
            popupWindowWrapper = new PopupWindowWrapper(mContext, data, downTextView.getText().toString().toLowerCase(), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String coinType = popupWindowWrapper.getDefaultType();
                    LogUtil.e("选择了币种：" + coinType);
                    downTextView.setText(coinType.toUpperCase());
                    SPUtil.put(Constant.COIN_TYPE, coinType);
                    EventBusUtil.post(Constant.COIN_TYPE, coinType);
                    onPopItemClick(coinType);
                }
            });
        }
        PopupWindowCompat.showAsDropDown(popupWindowWrapper, downTextView, 0, -getResources().getDimensionPixelSize(R.dimen.dp_10), Gravity.END);
    }

    protected void onPopItemClick(String popPosition) {

    }

    /**
     * 无网络刷新
     */
    public void noNetworkRefresh() {

    }

    /**
     * 设置向上文字
     */
    public void setUpText(int resId) {
        setUpText(getResources().getText(resId));
    }

    /**
     * 设置向上文字
     */
    public void setUpText(CharSequence label) {
        if (TextUtils.isEmpty(label)) {
            upTextView.setVisibility(View.GONE);
        } else {
            upTextView.setText(label);
            upTextView.setVisibility(View.VISIBLE);
            upIconView.setVisibility(View.GONE);
        }
    }

    /**
     * 设置向上图标
     */
    public void setUpIcon(int resId) {
        setUpIcon(ContextCompat.getDrawable(this, resId));
    }

    /**
     * 设置向上图标{@link Drawable}
     */
    public void setUpIcon(Drawable indicator) {
        if (indicator == null) {
            upIconView.setVisibility(View.GONE);
        } else {
            upIconView.setImageDrawable(indicator);
            upIconView.setVisibility(View.VISIBLE);
            upTextView.setVisibility(View.GONE);
        }
    }

    /**
     * 设置标题
     */
    @Override
    public void setTitle(CharSequence title) {
        if (TextUtils.isEmpty(title)) {
            titleView.setVisibility(View.GONE);
        } else {
            titleView.setText(title);
            titleView.setVisibility(View.VISIBLE);
            titleIconView.setVisibility(View.GONE);
        }
    }

    public void setUpTextLeftIcon(CharSequence title, @DrawableRes int res) {
        setUpText(title);
        setUpIcon(null);
        upTextView.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(res), null, null, null);
        upTextView.setPadding(getResources().getDimensionPixelSize(R.dimen.dp_10), 0, 0, 0);
    }

    public void setUpTextLeftBackIcon(CharSequence title) {
        setUpTextLeftIcon(title, R.drawable.daorusiyao);
    }

    public void setUpTextLeftBackIcon(@StringRes int title) {
        setUpTextLeftBackIcon(getString(title));
    }

    /**
     * 动态设置标题颜色
     *
     * @param color 色值
     */
    public void setTitleTextColor(int color) {
        titleView.setTextColor(color);
    }

    /**
     * 设置标题
     */
    @Override
    public void setTitle(int resId) {
        setTitle(getString(resId));
    }

    /**
     * 设置标题图标
     */
    public void setTitleIcon(int resId) {
        setTitleIcon(ContextCompat.getDrawable(this, resId));
    }

    /**
     * 设置标题图标{@link Drawable}
     */
    public void setTitleIcon(Drawable indicator) {
        if (indicator == null) {
            titleIconView.setVisibility(View.GONE);
        } else {
            titleIconView.setImageDrawable(indicator);
            titleIconView.setVisibility(View.VISIBLE);
            titleView.setVisibility(View.GONE);
        }
    }

    /**
     * 设置向下图标
     */
    public void setDownIcon(int resId) {
        setDownIcon(ContextCompat.getDrawable(this, resId));
    }

    /**
     * 设置向下图标{@link Drawable}
     */
    public void setDownIcon(Drawable indicator) {
        if (indicator == null) {
            downIconView.setVisibility(View.GONE);
        } else {
            downIconView.setImageDrawable(indicator);
            downIconView.setVisibility(View.VISIBLE);
            downTextView.setVisibility(View.GONE);
        }
    }

    /**
     * 设置向下标题
     */
    public void setDownText(int resId) {
        setDownText(getString(resId));
    }

    /**
     * 设置向下标题
     */
    public void setDownText(CharSequence title) {
        if (TextUtils.isEmpty(title)) {
            downTextView.setVisibility(View.GONE);
        } else {
            downTextView.setText(title);
            downTextView.setVisibility(View.VISIBLE);
            downIconView.setVisibility(View.GONE);
        }
    }

    public void setDownTextRightIcon() {
        setDownText(SPUtil.getString(Constant.COIN_TYPE, "fch").toUpperCase());
        downTextView.setCompoundDrawablesWithIntrinsicBounds(null, null,
                getResources().getDrawable(R.drawable.fch), null);
        downTextView.setPadding(0, 0, getResources().getDimensionPixelSize(R.dimen.dp_10), 0);
    }

    /**
     * 设置向下字体颜色
     */
    public void setDownTextColor(@ColorRes int color) {
        if (downTextView.getVisibility() == View.VISIBLE) {
            downTextView.setTextColor(getResources().getColor(color));
        }
    }

    /**
     * 显示无网络视图
     */
    public void showNoNetworkView() {
        if (noNetworkLayout != null) {
            noNetworkLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏无网络视图
     */
    public void hideNoNetworkView() {
        if (noNetworkLayout != null) {
            noNetworkLayout.setVisibility(View.GONE);
        }
    }


    /**
     * 沉浸式状态栏
     */
    @SuppressLint("ObsoleteSdkInt")
    protected void initState() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
                Window window = getWindow();
                View decorView = window.getDecorView();
                //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                decorView.setSystemUiVisibility(option);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
//                window.setNavigationBarColor(Color.TRANSPARENT);
            } else {
                int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
                int flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
                Window window = getWindow();
                WindowManager.LayoutParams attributes = window.getAttributes();
                attributes.flags |= flagTranslucentStatus | flagTranslucentNavigation;
                window.setAttributes(attributes);
            }
        }
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS); //透明导航栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }

//    /**
//     * 添加沉浸式状态栏
//     */
//    protected void addImmersionStyleBar() {
//        int immersionBarHeight = ViewUtils.getStatusBarHeight(this);
//        addImmersionStyleBar(immersionBarHeight);
//    }
//    protected void addImmersionStyleBar(int height) {
//        if (immersionStyleView == null) {
//            immersionStyleView = new View(this);
//            int screenWidth = getResources().getDisplayMetrics().widthPixels;
//            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(screenWidth, height);
//            immersionStyleView.setLayoutParams(params);
//            immersionStyleView.requestLayout();
//            if (rootElement != null)
//                rootElement.addView(immersionStyleView, 0);
//        }
//    }

    @OnClick(R.id.no_network_refresh)
    public void onNetworkRefresh() {
        noNetworkRefresh();
    }

    @OnClick({
            R.id.up_icon, R.id.up_text, R.id.down_icon, R.id.down_text
    })
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.up_icon:
            case R.id.up_text:
                up();
                break;
            case R.id.down_icon:
            case R.id.down_text:
                down();
                break;
            default:
                showLongToast("Invalid click event!Please check your code.");
                break;
        }
    }

//    protected boolean isJsFinish;

//    @SuppressLint("SetJavaScriptEnabled")
//    protected void initWebView(WebView webView) {
//        WebJsUtil.initWebView(mContext, webView);
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
//    }


    protected void callJavaScriptFunction(WebView webView, String strJS, ValueCallback callback) {
        if (!WebJsUtil.isJsFinish) {
            showLongToast(R.string.load_finish);
            return;
        }

        WebJsUtil.callJavaScriptFunction(strJS, callback);

//        if (!isJsFinish) {
//            showLongToast(R.string.load_finish);
//            return;
//        }
//
//        webView.post(new Runnable() {
//            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//            @Override
//            public void run() {
//                // 注意调用的JS方法名要对应上
//                LogUtil.e("调用的JS方法:" + strJS);
//                webView.evaluateJavascript(strJS, callback);
//            }
//        });
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

//        if (!isJsFinish) {
//            showLongToast(R.string.load_finish);
//            return;
//        }
//
//        webView.post(new Runnable() {
//            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//            @Override
//            public void run() {
//                // 注意调用的JS方法名要对应上
//                LogUtil.e("调用的JS方法:" + strJS);
//                webView.evaluateJavascript(strJS, new JSCallBack() {
//                    @Override
//                    public void onSuccess(String value) {
//                        super.onSuccess(value);
//                        onJSCallBack(value);
//                    }
//
//                    @Override
//                    public void onFailed(String msg) {
//                        super.onFailed(msg);
//                        showShortToast(msg);
//                    }
//                });
//            }
//        });
    }

    protected void onJSCallBack(String value) {

    }


    protected void callJavaScriptFunction(String strJS, ValueCallback callback) {
        if (!WebJsUtil.isJsFinish) {
            showLongToast(R.string.load_finish);
            return;
        }

        WebJsUtil.callJavaScriptFunction(strJS, callback);
    }
}
