package com.zm.secretsign.utils;

import android.app.Activity;
import android.graphics.Bitmap;

import androidx.fragment.app.Fragment;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.integration.android.IntentIntegrator;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.zm.secretsign.ui.base.MyCaptureActivity;

/**
 * author : Zhouzhou
 * e-mail : 553419781@qq.com
 * date   : 2019/8/12 11:29
 */
public class BarcodeUtil {

    public static Bitmap createBitmap(String contents, BarcodeFormat format, int width, int height) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            return barcodeEncoder.encodeBitmap(contents, format, width, height);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap createQrcodeBitmap(String contents, int size) {
        return createBitmap(contents, BarcodeFormat.QR_CODE, size, size);
    }

    public static void goScanContinue(Activity activity) {
        new IntentIntegrator(activity)
                .setCaptureActivity(MyCaptureActivity.class)
                .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
                .setPrompt("扫描二维码")
                .addExtra("p0", 1)
//                .setBeepEnabled(false)
//                .setCameraId(0) // Use a specific camera of the device
                .setBarcodeImageEnabled(false)
                .setOrientationLocked(false)
                .initiateScan(); // `this` is the current Activity
    }

    public static void goScan(Activity activity) {
        new IntentIntegrator(activity)
                .setCaptureActivity(MyCaptureActivity.class)
                .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
                .setPrompt("扫描二维码")
//                .setBeepEnabled(false)
//                .setCameraId(0) // Use a specific camera of the device
                .setBarcodeImageEnabled(false)
                .setOrientationLocked(false)
                .initiateScan(); // `this` is the current Activity
    }

    public static void goScan(Fragment fragment) {
        IntentIntegrator.forSupportFragment(fragment)
                .setCaptureActivity(MyCaptureActivity.class)
                .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
                .setPrompt("扫描二维码")
//                .setBeepEnabled(false)
//                .setCameraId(0) // Use a specific camera of the device
                .setBarcodeImageEnabled(false)
                .setOrientationLocked(false)
                .initiateScan(); // `this` is the current Activity
    }

}
