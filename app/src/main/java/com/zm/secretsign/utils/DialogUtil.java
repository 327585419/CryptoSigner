package com.zm.secretsign.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;

import com.zm.secretsign.view.MessageDialog;

/**
 * author : Zhouzhou
 * e-mail : 553419781@qq.com
 * date   : 2019/7/2 17:33
 */
public class DialogUtil {

    public static void showListDialog(Context context, String title, String[] items, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder listDialog = new AlertDialog.Builder(context);
        if (!TextUtils.isEmpty(title)) {
            listDialog.setTitle(title);
        }
        listDialog.setItems(items, listener);
        listDialog.show();
    }

    public static void showListDialog(Context context, String[] items, DialogInterface.OnClickListener listener) {
        showListDialog(context, "", items, listener);
    }

    public static AlertDialog.Builder showNormalDialog(Context context, String message, DialogInterface.OnClickListener listener) {
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(context);
//        normalDialog.setIcon(R.drawable-xxxhdpi.icon_dialog);
//        normalDialog.setTitle("我是一个普通Dialog")
        normalDialog.setMessage(message);
        normalDialog.setPositiveButton("确定", listener);
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        dialog.dismiss();
                    }
                });
        // 显示
        normalDialog.show();
        return normalDialog;
    }

    public static void showMessageDialog(Context context, String message) {
        new MessageDialog(context, message).show();
    }

}
