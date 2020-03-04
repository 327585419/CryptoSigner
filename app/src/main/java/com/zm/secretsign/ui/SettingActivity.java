package com.zm.secretsign.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.zhou.library.bean.Event;
import com.zhou.library.utils.AppUtil;
import com.zhou.library.utils.EventBusUtil;
import com.zhou.library.utils.LogUtil;
import com.zm.secretsign.BaseApplication;
import com.zm.secretsign.Constant;
import com.zm.secretsign.R;
import com.zm.secretsign.ui.base.BaseTitleActivity;
import com.zm.secretsign.utils.DialogUtil;
import com.zm.secretsign.utils.PasswordUtil;
import com.zm.secretsign.utils.WebJsUtil;
import com.zm.secretsign.view.StatementDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class SettingActivity extends BaseTitleActivity {


    @Override
    protected void onInit(@Nullable Bundle savedInstanceState) {
        bindContentView(R.layout.activity_setting);

        setUpTextLeftBackIcon(R.string.security_setting);

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
            case WebJsUtil.PAGE_FINISHED:
                hideProgress();
                break;
        }
    }

    @OnClick({R.id.tv_pwd, R.id.tv_safety, R.id.tv_exit})
    public void onButtonClick(View view) {
        switch (view.getId()) {
            case R.id.tv_pwd:
                advance(PwdUpdateActivity.class);
                break;
            case R.id.tv_safety:
                new StatementDialog(mContext, 1, null).show();
                break;
            case R.id.tv_exit:
                EventBusUtil.post("exit");
                finish();
                break;
        }
    }


}
