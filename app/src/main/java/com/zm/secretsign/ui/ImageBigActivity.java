package com.zm.secretsign.ui;


import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.zhou.library.bean.Event;
import com.zhou.library.utils.ScreenUtil;
import com.zm.secretsign.utils.BarcodeUtil;
import com.zm.secretsign.Constant;
import com.zm.secretsign.R;
import com.zm.secretsign.ui.base.BaseTitleActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

public class ImageBigActivity extends BaseTitleActivity {

    @BindView(R.id.iv_code)
    ImageView ivCode;

    private String code;

    @Override
    protected void onInit(@Nullable Bundle savedInstanceState) {
        bindContentView(R.layout.activity_image_big);

        setTitle("二维码");
        if (savedInstanceState == null) {
            code = getIntent().getStringExtra("p0");
        } else {
            code = savedInstanceState.getString("p0");
        }

        ivCode.setImageBitmap(BarcodeUtil.createQrcodeBitmap(code, ScreenUtil.getScreenWidth()));
        EventBus.getDefault().register(this);
        hideProgress();
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
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("p0", code);
        super.onSaveInstanceState(outState);
    }

    @OnClick(R.id.iv_code)
    void code() {
        finish();
    }
}
