package com.zm.secretsign.ui.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.zhou.library.utils.EventBusUtil;
import com.zhou.library.utils.ToastUtil;
import com.zm.secretsign.R;
import com.zm.secretsign.utils.SoundUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * author : Zhouzhou
 * e-mail : 553419781@qq.com
 * date   : 2019/9/16 21:59
 */
public class MyCaptureActivity extends AppCompatActivity {

    private CaptureManager capture;
    private DecoratedBarcodeView bv_barcode;

    private int scanType;//0单个扫描  1连续扫描
    private String lastScanResult;
//    private List<String> scanRecords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.zhou.zxing.R.layout.activity_my_captrue);

        if (savedInstanceState == null) {
            scanType = getIntent().getIntExtra("p0", 0);
        } else {
            scanType = savedInstanceState.getInt("p0");
        }
        Log.e("扫描", "scanType =" + scanType);
        bv_barcode = findViewById(com.zhou.zxing.R.id.bv_barcode);
        findViewById(com.zhou.zxing.R.id.up_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        SoundUtil.initSound(this);
        capture = new CaptureManager(this, bv_barcode);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();
        if (scanType == 1) {
//            scanRecords = new ArrayList<>();
            bv_barcode.decodeContinuous(new BarcodeCallback() {
                @Override
                public void barcodeResult(BarcodeResult result) {
                    String scanResult = result.getText();
                    Log.e("扫描", "barcodeResult:" + scanResult);
                    if (TextUtils.isEmpty(scanResult)) {
                        return;
                    }

                    if (!TextUtils.isEmpty(lastScanResult) && lastScanResult.equals(scanResult)) {
                        return;
                    }

                    ToastUtil.showShort(R.string.success_next);
                    SoundUtil.playBee(MyCaptureActivity.this);
                    lastScanResult = scanResult;
                    EventBusUtil.post("BarcodeResult", scanResult);
                }

                @Override
                public void possibleResultPoints(List<ResultPoint> resultPoints) {

                }
            });
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
        SoundUtil.freeSound();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
        outState.putInt("p0", scanType);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        capture.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return bv_barcode.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }
}
