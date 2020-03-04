package com.zm.secretsign.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.zhou.library.bean.Event;
import com.zhou.library.utils.LogUtil;
import com.zhou.library.utils.ScreenUtil;
import com.zhou.library.utils.ToastUtil;
import com.zm.secretsign.Constant;
import com.zm.secretsign.R;
import com.zm.secretsign.bean.AddressKey;
import com.zm.secretsign.ui.ImageBigActivity;
import com.zm.secretsign.ui.VerifySignActivity;
import com.zm.secretsign.utils.BarcodeUtil;
import com.zm.secretsign.utils.DBUtil;
import com.zm.secretsign.utils.JSCallBack;
import com.zm.secretsign.utils.WebJsUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 公钥展示
 */
public class PublicKeyDialog extends Dialog {


    @BindView(R.id.tv_key)
    TextView tvKey;
    @BindView(R.id.iv_key)
    ImageView ivKey;
    @BindView(R.id.tv_button)
    TextView tvButton;

    private boolean isVerify;
    private float screenBright;

    public PublicKeyDialog(@NonNull Context context, String privateKey, String address, boolean isVerify) {
        this(context, privateKey, address);
        this.isVerify = isVerify;
    }

    public PublicKeyDialog(@NonNull Context context, String privateKey, String address) {
        this(context, R.style.Dialog_Apply);
        int dp = context.getResources().getDimensionPixelSize(R.dimen.dp_210);
//        tvKey.setText(privateKey);
//        ivKey.setImageBitmap(BarcodeUtil.createQrcodeBitmap(privateKey, dp));

        //获取公钥
        if (!WebJsUtil.isJsFinish) {
            ToastUtil.showShort(R.string.load_finish);
            return;
        }
        AddressKey key = DBUtil.getKeyByAddress(address);
        if (key == null) {
            return;
        }
        String coinType = "var coinType =\"" + key.getCoinType() + "\";";
        String secretKey = "var wif =\"" + privateKey + "\";";
        String strJS = "javascript:" + coinType + secretKey + " getPublickKeyFromWIF(coinType, wif)";
        WebJsUtil.callJavaScriptFunction(strJS, new JSCallBack() {
            @Override
            public void onSuccess(String value) {
                super.onSuccess(value);
                LogUtil.e("js 返回的结果:" + value);
                //第一个是私钥，第二个是地址
                if (TextUtils.isEmpty(value)) {
                    ToastUtil.showShort(R.string.create_sign_error);
                    return;
                }
                //去掉引号
                value = value.replace("\"", "");

                tvKey.setText(value);
                LogUtil.e("公钥:" + value);
                ivKey.setImageBitmap(BarcodeUtil.createQrcodeBitmap(value, dp));
            }

            @Override
            public void onFailed(String msg) {
                super.onFailed(msg);
            }
        });
    }

    private PublicKeyDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        setContentView(R.layout.dialog_public_key);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        ButterKnife.bind(this);

        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            int screenWidth = ScreenUtil.getScreenWidth();
//            lp.width = (int) (screenWidth * 0.77);
            lp.width = screenWidth;
            lp.gravity = Gravity.CENTER;
            lp.dimAmount = 0.6f;
            window.setAttributes(lp);
//            window.setWindowAnimations(R.style.Dialog_Apply_Animation);
            WindowManager.LayoutParams wl = window.getAttributes();
            window.setAttributes(wl);
        }
        EventBus.getDefault().register(this);
        setWindowBrightness(1);
    }

    private void setWindowBrightness(float brightness) {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        if (screenBright == 0) {
            screenBright = lp.screenBrightness;
        }
        lp.screenBrightness = brightness;
        window.setAttributes(lp);
        LogUtil.e("当前亮度：" + screenBright);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveEvent(Event event) {
        switch (event.name) {
            case Constant.BACKUP_SUCCESS:
                dismiss();
                break;
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        EventBus.getDefault().unregister(this);
        setWindowBrightness(screenBright);
    }

    @OnClick(R.id.tv_button)
    public void onButtonClick(View view) {
//        EventBus.getDefault().post(receiveEvent);
        if (isVerify) {
            Intent intent = new Intent(getContext(), VerifySignActivity.class);
            intent.putExtra("p0", tvKey.getText().toString());
            getContext().startActivity(intent);
        } else {
            dismiss();
        }
    }

    @OnClick(R.id.iv_key)
    void key() {
        Intent intent = new Intent(getContext(), ImageBigActivity.class);
        intent.putExtra("p0", tvKey.getText().toString());
        getContext().startActivity(intent);
    }

//    @OnClick(R.id.iv_address)
//    void address() {
//        Intent intent = new Intent(getContext(), ImageBigActivity.class);
//        intent.putExtra("p0", tvAddress.getText().toString());
//        getContext().startActivity(intent);
//    }


//    @Override
//    public void onAttachedToWindow() {
//        super.onAttachedToWindow();
//    }
//
//    @Override
//    public void onDetachedFromWindow() {
//    }
//
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onFetchEvent(PrinterFetchEvent event) {
//
//    }
}
