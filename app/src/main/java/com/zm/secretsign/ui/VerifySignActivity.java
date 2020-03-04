package com.zm.secretsign.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.zhou.library.utils.EventBusUtil;
import com.zm.secretsign.utils.BarcodeUtil;
import com.zm.secretsign.Constant;
import com.zm.secretsign.R;
import com.zm.secretsign.ui.base.BaseTitleActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class VerifySignActivity extends BaseTitleActivity {

    @BindView(R.id.et_key)
    EditText etKey;
    @BindView(R.id.tv_scan)
    TextView tvScan;
    @BindView(R.id.tv_verify)
    TextView tvVerify;
    @BindView(R.id.tv_ignore)
    TextView tvIgnore;
    private String privateKey;

    @Override
    protected void onInit(@Nullable Bundle savedInstanceState) {
        bindContentView(R.layout.activity_verify_sign);

        setTitle(R.string.verify_sign);
        hideProgress();

        if (savedInstanceState == null) {
            privateKey = getIntent().getStringExtra("p0");
        } else {
            privateKey = savedInstanceState.getString("p0");
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("p0", privateKey);
        super.onSaveInstanceState(outState);
    }

    @OnClick({
            R.id.tv_scan,
            R.id.tv_verify,
            R.id.tv_ignore,
    })
    public void onButtonClick(View view) {
        switch (view.getId()) {
            case R.id.tv_verify:
                String inputKey = etKey.getText().toString();
                if (TextUtils.isEmpty(inputKey)) {
                    showShortToast(R.string.input_or_scan_key);
                    return;
                }

                if (!inputKey.equals(privateKey)) {
                    showShortToast(R.string.backup_error);
                    return;
                }

                showShortToast(R.string.backup_success);
                EventBusUtil.post(Constant.BACKUP_SUCCESS);
                finish();
                break;
            case R.id.tv_scan:
                BarcodeUtil.goScan(mActivity);
                break;
            case R.id.tv_ignore:
                EventBusUtil.post(Constant.BACKUP_SUCCESS);
                finish();
                break;
        }
    }

    // Get the results:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result == null || TextUtils.isEmpty(result.getContents())) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        etKey.setText(result.getContents());
    }
}
