package com.zm.secretsign.ui;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.PopupWindowCompat;

import com.zhou.library.utils.LogUtil;
import com.zhou.library.utils.SPUtil;
import com.zm.secretsign.utils.BarcodeUtil;
import com.zm.secretsign.BaseApplication;
import com.zm.secretsign.Constant;
import com.zm.secretsign.R;
import com.zm.secretsign.ui.base.BaseStatusBarActivity;
import com.zm.secretsign.bean.AddressKey;
import com.zm.secretsign.bean.Password;
import com.zm.secretsign.utils.DBUtil;
import com.zm.secretsign.utils.PasswordUtil;
import com.zm.secretsign.view.PopupWindowWrapper;
import com.zm.secretsign.view.StatementDialog;
import com.zm.secretsign.view.WarningDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseStatusBarActivity {

//    @BindView(R.id.web_view)
//    WebView webView;

    @BindView(R.id.tv_address_text)
    TextView tvAddressText;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.iv_code)
    ImageView ivCode;
    @BindView(R.id.et_pwd)
    EditText etPwd;
    @BindView(R.id.et_pwd_again)
    EditText etPwdAgain;
    @BindView(R.id.line_pwd_again)
    View linePwdAgain;
    @BindView(R.id.tv_login)
    TextView tvLogin;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    //    private String setPwd;
    private Password password;
    private List<String> coinTypes;
    private List<AddressKey> coinAddresses;
    private String defaultCoin;

    private String type;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        type = getIntent().getStringExtra("p0");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setStatusBar();
        ButterKnife.bind(this);
        hideProgress();

        if (savedInstanceState == null) {
            type = getIntent().getStringExtra("p0");
        } else {
            type = savedInstanceState.getString("p0");
        }

//        WebJsUtil.initWebView(getApplicationContext(), webView);
        //TODO 测试
//        etPwd.setText("1234");
//        etPwdAgain.setText("ww674750");

        //没有设置密码，先设置密码
//        setPwd = SPUtil.getString(Constant.SET_PWD);
        password = DBUtil.getPwdFirst();
        AddressKey addressFch = DBUtil.getKeyFirst("fch");
        AddressKey addressBtc = DBUtil.getKeyFirst("btc");
        AddressKey addressBch = DBUtil.getKeyFirst("bch");


        coinTypes = new ArrayList<>();
        coinAddresses = new ArrayList<>();
        if (addressFch != null) {
            coinTypes.add("fch");
            coinAddresses.add(addressFch);
        }

        if (addressBtc != null) {
            coinTypes.add("btc");
            coinAddresses.add(addressBtc);
        }

        if (addressBch != null) {
            coinTypes.add("bch");
            coinAddresses.add(addressBch);
        }

        if (password == null) {
            //没有设置密码
            tvTitle.setText(R.string.set_password);
            tvAddressText.setVisibility(View.INVISIBLE);
            tvAddress.setVisibility(View.GONE);
            ivCode.setVisibility(View.GONE);
            etPwd.setHint(R.string.set_password);
            etPwdAgain.setVisibility(View.VISIBLE);
            linePwdAgain.setVisibility(View.VISIBLE);
        } else if (coinTypes.size() > 0) {
            //设置了密码，且已有地址
            //先设置sha256 否则解密会失败
            defaultCoin = coinTypes.get(0);
            AddressKey defaultAddressKey = coinAddresses.get(0);
            tvAddressText.setText(String.format(getString(R.string.my_default_address_s), defaultCoin.toUpperCase()));
            if (coinTypes.size() > 1) {
                tvAddressText.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.xiafan), null);
            }

            int dp = getResources().getDimensionPixelSize(R.dimen.dp_140);
            tvAddress.setText(defaultAddressKey.getAddress());
            ivCode.setImageBitmap(BarcodeUtil.createQrcodeBitmap(defaultAddressKey.getAddress(), dp));
        } else {
            //设置了密码，没有地址
            tvAddressText.setVisibility(View.INVISIBLE);
            tvAddress.setVisibility(View.GONE);
            ivCode.setVisibility(View.GONE);
        }


        //启动后检测数据（wifi、移动数据、蓝牙）如果连接，发出“请关闭数据连接，防止私钥泄露！”的红色警告。
        boolean network = SPUtil.getBoolean(Constant.NETWORK_CONNECT);
        if (!network) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                if (activeNetworkInfo != null) {
                    switch (activeNetworkInfo.getType()) {
                        case ConnectivityManager.TYPE_WIFI:
                            LogUtil.e("已连接WiFi");
                            showDialog(getString(R.string.wifi));
                            break;
                        case ConnectivityManager.TYPE_MOBILE:
                            LogUtil.e("已连接数据流量");
                            showDialog(getString(R.string.data));
                            break;
                        default:
                            checkBT();
                            break;
                    }
                } else {
                    checkBT();
                    LogUtil.e("没有联网NetworkInfo为null");
                }
            } else {
                checkBT();
                LogUtil.e("不支持联网ConnectivityManager为null");
            }
        }
    }


    private void checkBT() {
        //判断蓝牙是否开启
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            if (bluetoothAdapter.isEnabled()) {
                showDialog(getString(R.string.bluetooth));
            } else {
                LogUtil.e("蓝牙关闭");
            }
        } else {
            LogUtil.e("不支持蓝牙");
        }
    }

    private void showDialog(String type) {
        SPUtil.put(Constant.NETWORK_CONNECT, true);
        new WarningDialog(mContext, String.format(getString(R.string.warning_network), type)).show();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("p0", type);
        super.onSaveInstanceState(outState);
    }

    protected PopupWindowWrapper popupWindowWrapper;

    @OnClick(R.id.tv_address_text)
    void address() {
        if (coinTypes.size() == 0 || coinTypes.size() == 1) {
            return;
        }

        if (popupWindowWrapper == null) {
            popupWindowWrapper = new PopupWindowWrapper(mContext, coinTypes, defaultCoin, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    defaultCoin = popupWindowWrapper.getDefaultType();
                    tvAddressText.setText(String.format(getString(R.string.my_default_address_s), defaultCoin.toUpperCase()));
                    int dp = getResources().getDimensionPixelSize(R.dimen.dp_140);
                    AddressKey defaultAddressKey = coinAddresses.get(coinTypes.indexOf(defaultCoin));
                    tvAddress.setText(defaultAddressKey.getAddress());
                    ivCode.setImageBitmap(BarcodeUtil.createQrcodeBitmap(defaultAddressKey.getAddress(), dp));
                }
            });
        }

        int dp = getResources().getDimensionPixelSize(R.dimen.dp_20);
        PopupWindowCompat.showAsDropDown(popupWindowWrapper, tvAddressText, dp, -getResources().getDimensionPixelSize(R.dimen.dp_10), Gravity.LEFT);
    }

    @OnClick(R.id.iv_code)
    void code() {
        advance(ImageBigActivity.class, tvAddress.getText().toString());
    }


    @OnClick(R.id.tv_login)
    void login() {
        String pwd = etPwd.getText().toString();
        if (TextUtils.isEmpty(pwd)) {
            showShortToast(R.string.input_password);
            return;
        }

        if (pwd.length() < 4) {
            showShortToast(R.string.password_length);
            return;
        }

//        if (!StringUtil.hasDigit(pwd) || !StringUtil.hasLetter(pwd)) {
//            showShortToast(R.string.password_contains);
//            return;
//        }


        if (password == null) {
            String pwdAgain = etPwdAgain.getText().toString();
            if (TextUtils.isEmpty(pwdAgain)) {
                showShortToast(R.string.input_password_again);
                return;
            }

            if (!pwd.equals(pwdAgain)) {
                showShortToast(R.string.password_different);
                return;
            }

            // 8. 首次运行设置密码后提示法律免责声明，点击“同意”进入下一页，“不同意”则退出。
            new StatementDialog(mContext, 0, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (view.getId()) {
                        case R.id.tv_cancel:
                            //不同意，退出app
                            finish();
                            // 结束进程
                            System.exit(0);
                            break;
                        case R.id.tv_confirm:
                            //同意
                            //保存密码
                            PasswordUtil.savePassword(pwd);
                            BaseApplication.setPassword(pwd);
                            if (TextUtils.isEmpty(type)) {
                                advance(MainActivity.class);
                            }
                            finish();
                            break;
                    }
                }
            }).show();
        } else {
            if (PasswordUtil.checkPasswordCorrect(password, pwd)) {
                //验证密码
                showShortToast(R.string.password_incorrect);
                return;
            }

            //登录
            //保存密码到程序
            BaseApplication.setPassword(pwd);
            if (TextUtils.isEmpty(type)) {
                advance(MainActivity.class);
            }
            finish();
        }
    }

    // 第一种
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (TextUtils.isEmpty(type)) {
            return super.onKeyDown(keyCode, event);
        }

// 按下键盘上返回按钮
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

}
