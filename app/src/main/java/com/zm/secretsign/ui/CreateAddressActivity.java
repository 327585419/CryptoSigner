package com.zm.secretsign.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.zhou.library.bean.Event;
import com.zhou.library.utils.AppUtil;
import com.zhou.library.utils.LogUtil;
import com.zm.secretsign.Constant;
import com.zm.secretsign.R;
import com.zm.secretsign.ui.base.BaseTitleActivity;
import com.zm.secretsign.utils.DialogUtil;
import com.zm.secretsign.utils.PasswordUtil;
import com.zm.secretsign.utils.WebJsUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class CreateAddressActivity extends BaseTitleActivity {

    //    @BindView(R.id.web_view)
//    WebView webView;
    @BindView(R.id.et_text)
    EditText etText;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.tv_key)
    TextView tvKey;
    @BindView(R.id.et_tag)
    EditText etTag;

    private String privateKey;
    private String address;

    @Override
    protected void onInit(@Nullable Bundle savedInstanceState) {
        bindContentView(R.layout.activity_create_address);

        setUpTextLeftBackIcon(R.string.create_address);
        setDownTextRightIcon();

        etText.setSingleLine(true);
        etText.setMaxLines(1);
        //TODO Test
//        etText.setText("12345");
//        etTag.setText("zhou");
//        initWebView(webView);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveEvent(Event event) {
        switch (event.name) {
            case Constant.SET_PWD:
                goLogin();
                break;
            case WebJsUtil.PAGE_FINISHED:
                hideProgress();
                break;
        }
    }

    @OnClick({R.id.tv_button, R.id.tv_save})
    public void onButtonClick(View view) {
        switch (view.getId()) {
            case R.id.tv_button:
                //生成地址
                String text = etText.getText().toString();
                if (TextUtils.isEmpty(text)) {
                    showShortToast(R.string.input_crypt_please);
                    return;
                }

                //TODO 校验密语是否太简单
                if (text.length() <= 2) {
                    DialogUtil.showMessageDialog(mContext, getString(R.string.crypt_simple));
                    return;
                }

                //注意调用的JS方法名要对应上
                String coinType = "var coinType =\"" + downTextView.getText().toString().toLowerCase() + "\";";
                String secretKey = "var secretKey =\"" + text + "\";";
                String strJS = "javascript:" + coinType + secretKey + " generateAdress(coinType, secretKey)";
                callJavaScriptFunction(strJS);
                break;
            case R.id.tv_save:
                AppUtil.hideKeyboard(mContext, etTag);
                PasswordUtil.saveAddressKey(mContext, privateKey, address, etTag.getText().toString(), true);
                break;
        }
    }

    @Override
    protected void onJSCallBack(String value) {
        super.onJSCallBack(value);

        //此处为 js 返回的结果
        LogUtil.e("js 返回的结果:" + value);
        //第一个是私钥，第二个是地址
        List<String> data = JSON.parseArray(value, String.class);
        if (data == null) {
            return;
        }

//        AppUtil.hideKeyboard(mContext, etText);
        etTag.requestFocus();
        if (data.size() > 0) {
            privateKey = data.get(0);
            tvKey.setText(privateKey);
        }

        if (data.size() > 1) {
            address = data.get(1);
            tvAddress.setText(address);
        }
        //去掉引号
//                                address = value.replace("\"", "");
    }

    @Override
    protected void onPopItemClick(String popPosition) {
        super.onPopItemClick(popPosition);
        privateKey = "";
        address = "";
        etText.setText("");
        tvAddress.setText("");
        tvKey.setText("");
        etTag.setText("");
    }


}
