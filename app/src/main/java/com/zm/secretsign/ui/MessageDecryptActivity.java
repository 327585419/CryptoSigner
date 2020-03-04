package com.zm.secretsign.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.ValueCallback;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.zhou.library.bean.Event;
import com.zhou.library.utils.LogUtil;
import com.zm.secretsign.utils.BarcodeUtil;
import com.zm.secretsign.BaseApplication;
import com.zm.secretsign.Constant;
import com.zm.secretsign.R;
import com.zm.secretsign.bean.AddressKey;
import com.zm.secretsign.ui.base.BaseTitleActivity;
import com.zm.secretsign.utils.WebJsUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

public class MessageDecryptActivity extends BaseTitleActivity {

    //    @BindView(R.id.web_view)
//    WebView webView;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.tv_button)
    TextView tvButton;
    @BindView(R.id.et_message)
    EditText etMessage;
    @BindView(R.id.tv_save)
    TextView tvSave;
    @BindView(R.id.tv_sign)
    TextView tvSign;
    @BindView(R.id.iv_code)
    ImageView ivCode;
    @BindView(R.id.tv_code)
    TextView tvCode;

    private String privateKey;

    @Override
    protected void onInit(@Nullable Bundle savedInstanceState) {
        bindContentView(R.layout.activity_message_decrypt);

        setUpTextLeftBackIcon(R.string.message_decrypt);
        setDownTextRightIcon();
        EventBus.getDefault().register(this);

//        tvSign.setText("周周L5j2wqKLogidAdovp1WL7cV4Ps5Erqyyjq812hHe7RjCDJroFNLP");
//        tvSign.setKeyListener(null);

    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @OnClick({
            R.id.tv_address,
            R.id.tv_button,
            R.id.tv_save,
    })
    public void onButtonClick(View view) {
        switch (view.getId()) {
            case R.id.tv_address:
                advance(ManageKeyActivity.class, 1);
                break;
            case R.id.tv_button:
                BarcodeUtil.goScanContinue(mActivity);
                break;
            case R.id.tv_save:
                //生成签名
                if (TextUtils.isEmpty(privateKey)) {
                    showShortToast(R.string.select_address_please);
                    return;
                }

                String message = etMessage.getText().toString();
                if (TextUtils.isEmpty(message)) {
                    showShortToast(R.string.scan_message_please);
                    return;
                }

                //注意调用的JS方法名要对应上
                //加密文本
                String coinType = "var data =\"" + message + "\";";
                //私钥
                String secretKey = "var pkWIF =\"" + privateKey + "\";";
                String strJS = "javascript:" + coinType + secretKey + " decryptData(data, pkWIF)";
                callJavaScriptFunction(strJS);
                break;
        }
    }

    @Override
    protected void onJSCallBack(String value) {
        super.onJSCallBack(value);
        //此处为 js 返回的结果
        LogUtil.e("js 返回的结果:" + value);
        if (TextUtils.isEmpty(value)) {
            showShortToast(R.string.create_sign_error);
            return;
        }

        tvSign.setVisibility(View.VISIBLE);
        ivCode.setVisibility(View.VISIBLE);
        tvCode.setVisibility(View.VISIBLE);
        //去掉引号
        String sign = value.replace("\"", "");
        tvSign.setText(sign);
        int dp = getResources().getDimensionPixelSize(R.dimen.dp_140);
        ivCode.setImageBitmap(BarcodeUtil.createQrcodeBitmap(sign, dp));
//        code();
    }

    @OnClick(R.id.iv_code)
    void code() {
        advance(ImageBigActivity.class, tvSign.getText().toString());
    }


//    // Get the results:
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
//
//        if (result == null || TextUtils.isEmpty(result.getContents())) {
//            super.onActivityResult(requestCode, resultCode, data);
//            return;
//        }
//
//        String currentScanResult = result.getContents();
//        LogUtil.e("本次扫描到的：" + currentScanResult);
//        String before = etMessage.getText().toString();
//        LogUtil.e("之前编辑框里的：" + before);
//        etMessage.append(result.getContents());
//    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveEvent(Event event) {
        switch (event.name) {
            case Constant.SELECT_ADDRESS:
                AddressKey addressKey = (AddressKey) event.object;
                privateKey = BaseApplication.decrypt(addressKey.getKey());
                tvAddress.setText(String.format(getString(R.string.select_address_s), addressKey.getAddress()));
                break;
            case Constant.SET_PWD:
                goLogin();
                break;
            case WebJsUtil.PAGE_FINISHED:
                hideProgress();
                break;
            case "BarcodeResult":
                String currentScanResult = event.param;
                LogUtil.e("本次扫描到的：" + currentScanResult);
                String before = etMessage.getText().toString();
                LogUtil.e("之前编辑框里的：" + before);
                etMessage.append(currentScanResult);
                break;
        }
    }

    @Override
    protected void onPopItemClick(String popPosition) {
        super.onPopItemClick(popPosition);

        privateKey = "";
        tvAddress.setText(R.string.select_address_);
        etMessage.setText("");
        tvSign.setText("");
        tvSign.setVisibility(View.GONE);
        ivCode.setVisibility(View.GONE);
        tvCode.setVisibility(View.GONE);
    }
}
