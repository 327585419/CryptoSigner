package com.zm.secretsign.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zhou.library.utils.AppUtil;
import com.zhou.library.utils.LogUtil;
import com.zhou.library.utils.SPUtil;
import com.zm.secretsign.Constant;
import com.zm.secretsign.R;
import com.zm.secretsign.ui.base.BaseFragment;
import com.zm.secretsign.utils.PasswordUtil;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 导入私钥 手动输入
 */
public class KeyInputFragment extends BaseFragment {

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
    @BindView(R.id.tv_input)
    TextView tvInput;
    @BindView(R.id.tv_button)
    TextView tvButton;
    @BindView(R.id.tv_save)
    TextView tvSave;

    private View rootView;
    private Unbinder unbinder;

    private String privateKey;
    private String address;

    /**
     * Fragment 实例
     */
    public static KeyInputFragment newInstance(Object... pramars) {
        KeyInputFragment fragment = new KeyInputFragment();
        if (pramars != null && pramars.length > 0) {
            Bundle bundle = new Bundle();
            for (int i = 0; i < pramars.length; i++) {
                bundle.putSerializable("p" + i, (Serializable) pramars[i]);
            }
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.activity_create_address, container, false);
        }
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        tvInput.setText(R.string.input_key);
        tvButton.setText(R.string.sure);
        etText.setHint("");

        //TODO 测试
//        etText.setText("L2oAXFV4KPzoVUCEWgot4qBRAQ4GEDBBPe28XXgPTfNykt1beVtV");
//        initWebView(webView);
    }


    @Override
    public void onDestroyView() {
        if (rootView != null) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
//        EventBus.getDefault().unregister(this);
        unbinder.unbind();
        super.onDestroyView();
    }


    @OnClick({R.id.tv_button, R.id.tv_save})
    public void onButtonClick(View view) {
        switch (view.getId()) {
            case R.id.tv_button:
                //生成地址
                privateKey = etText.getText().toString();
                if (TextUtils.isEmpty(privateKey)) {
                    showShortToast(R.string.input_key_please);
                    return;
                }

                //注意调用的JS方法名要对应上
                String coinType = "var coinType =\"" + SPUtil.getString(Constant.COIN_TYPE, "fch") + "\";";
                String secretKey = "var wif =\"" + privateKey + "\";";
                String strJS = "javascript:" + coinType + secretKey + " importAdressFromWIF(coinType, wif)";
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
        if (TextUtils.isEmpty(value)) {
            showShortToast(R.string.input_key_incorrect);
            return;
        }

//        AppUtil.hideKeyboard(mContext, etText);
        etTag.requestFocus();
        tvKey.setText(privateKey);
        //去掉引号
        address = value.replace("\"", "");
        tvAddress.setText(address);
    }

    @Override
    public void clearView() {
        super.clearView();

        privateKey = "";
        address = "";
        etText.setText("");
        tvAddress.setText("");
        tvKey.setText("");
        etTag.setText("");
    }
}