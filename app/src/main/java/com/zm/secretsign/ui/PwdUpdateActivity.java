package com.zm.secretsign.ui;


import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.zhou.library.bean.Event;
import com.zhou.library.utils.StringUtil;
import com.zm.secretsign.BaseApplication;
import com.zm.secretsign.Constant;
import com.zm.secretsign.R;
import com.zm.secretsign.ui.base.BaseStatusBarActivity;
import com.zm.secretsign.bean.AddressKey;
import com.zm.secretsign.utils.AesUtils;
import com.zm.secretsign.utils.DBUtil;
import com.zm.secretsign.utils.PasswordUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PwdUpdateActivity extends BaseStatusBarActivity {

    @BindView(R.id.et_pwd_current)
    EditText etPwdCurrent;
    @BindView(R.id.et_pwd)
    EditText etPwd;
    @BindView(R.id.et_pwd_again)
    EditText etPwdAgain;
    @BindView(R.id.tv_login)
    TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pwd_update);
        setStatusBar();
        ButterKnife.bind(this);
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

    @OnClick(R.id.tv_title)
    void back() {
        finish();
    }

    @OnClick(R.id.tv_login)
    void login() {
        String pwdCurrent = etPwdCurrent.getText().toString();
        if (TextUtils.isEmpty(pwdCurrent)) {
            showShortToast(R.string.input_password_current_please);
            return;
        }

        String pwd = etPwd.getText().toString();
        if (TextUtils.isEmpty(pwd)) {
            showShortToast(R.string.input_password_new_please);
            return;
        }

        if (pwdCurrent.length() < 4 || pwd.length() < 4) {
            showShortToast(R.string.password_length);
            return;
        }

//        if (!StringUtil.hasDigit(pwdCurrent) || !StringUtil.hasLetter(pwdCurrent) ||
//                !StringUtil.hasDigit(pwd) || !StringUtil.hasLetter(pwd)) {
//            showShortToast(R.string.password_contains);
//            return;
//        }

        String pwdAgain = etPwdAgain.getText().toString();
        if (TextUtils.isEmpty(pwdAgain)) {
            showShortToast(R.string.input_password_new_again_please);
            return;
        }

        if (!pwd.equals(pwdAgain)) {
            showShortToast(R.string.password_different);
            return;
        }

        //和保存的密码进行校验
        if (!pwdCurrent.equals(BaseApplication.getPassword())) {
            showShortToast(R.string.password_current_incorrect);
            return;
        }

        //将保存在数据库的所有地址全部更新密钥
        List<AddressKey> list = DBUtil.queryAllKeyItems();
        String currentKey = PasswordUtil.getSHA256(pwdCurrent);
        String newKey = PasswordUtil.getSHA256(pwd);
        if (list != null && list.size() > 0) {
            for (AddressKey keySave : list) {
                //用当前密码 先解密
                String decryptStr = AesUtils.decrypt(currentKey, keySave.getKey());
                //用新的密码 重新加密
                String encryptStr = AesUtils.encrypt(newKey, decryptStr);
                keySave.setKey(encryptStr);
            }
            //更新到数据库
            DBUtil.updateKeys(list);
        }

        //保存新密码
        PasswordUtil.savePassword(pwd);
        showLongToast(R.string.update_password_success);

        //登录
        BaseApplication.setPassword(pwd);
        advance(LoginActivity.class);
        finish();
    }
}
