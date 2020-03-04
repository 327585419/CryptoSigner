package com.zm.secretsign.utils;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.zhou.library.utils.AppUtil;
import com.zhou.library.utils.LogUtil;
import com.zhou.library.utils.SPUtil;
import com.zhou.library.utils.ToastUtil;
import com.zm.secretsign.BaseApplication;
import com.zm.secretsign.Constant;
import com.zm.secretsign.R;
import com.zm.secretsign.bean.AddressKey;
import com.zm.secretsign.bean.Password;
import com.zm.secretsign.view.CreateAddressDialog;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * author : Zhouzhou
 * e-mail : 553419781@qq.com
 * date   : 2019/8/13 17:12
 */
public class PasswordUtil {
    public static void saveAddressKey(Context mContext, String privateKey, String address, String tag, boolean isVerify) {
        if (TextUtils.isEmpty(privateKey) || TextUtils.isEmpty(address)) {
            ToastUtil.showShort(R.string.create_address_first);
            return;
        }

        if (TextUtils.isEmpty(tag)) {
            ToastUtil.showShort(R.string.input_tag);
//            etTag.requestFocus();
            return;
        }

        //检查当前地址是否已保存过
        AddressKey keyDb = DBUtil.getKeyByAddress(address);
        if (keyDb == null) {
            //执行保存 到数据库
            AddressKey key = new AddressKey();
            key.setKey(BaseApplication.encrypt(privateKey));
            key.setAddress(address);
            key.setTag(tag);
            key.setSaveTime(System.currentTimeMillis());
            key.setOrder(DBUtil.getKeyMaxId() + 1);
            key.setCoinType(SPUtil.getString(Constant.COIN_TYPE, "fch"));

            LogUtil.e("保存：" + JSON.toJSONString(key));
            DBUtil.insertKey(key);
        } else {
            //执行 覆盖更新
            keyDb.setKey(BaseApplication.encrypt(privateKey));
//            keyDb.setAddress(address);
            keyDb.setTag(tag);
            keyDb.setSaveTime(System.currentTimeMillis());
//            keyDb.setOrder(DBUtil.getKeyMaxId() + 1);
            keyDb.setCoinType(SPUtil.getString(Constant.COIN_TYPE, "fch"));
            DBUtil.updateKey(keyDb);
        }

        new CreateAddressDialog(mContext, privateKey, address, isVerify).show();
    }


    public static void saveAddressKey(Context mContext, String privateKey, String address, String tag) {
        saveAddressKey(mContext, privateKey, address, tag, false);
    }

    public static void savePassword(String password) {
        //            1 首次登录输入密码，
//            后台处理逻辑。
//            a 密码用sha256生成256位hash值。
//            b 用256位hash值计算AES的密钥
//            c 使用b计算出来的密钥和AES对称算法加密256位的hash值
//            d 计算的结果存储起来

        //保存到程序里，供其他界面加密使用
        String key = PasswordUtil.getSHA256(password);
        String encryStr = AesUtils.encrypt(key, password);
//        String decryStr = AesUtils.decrypt(key, encryStr);
//        LogUtil.e("AES解密:" + decryStr);

//        SPUtil.put(Constant.SET_PWD, encryStr.replace("\n", "").trim());
        //保存到数据库
        Password item = new Password();
        item.setEncryStr(encryStr);
        item.setSha256(key);
        item.setSaveTime(System.currentTimeMillis());
        DBUtil.insertPwd(item);
    }

    public static boolean checkPasswordCorrect(String inputPwd) {
        Password password = DBUtil.getPwdFirst();
        if (password == null) {
            return false;
        } else
            return checkPasswordCorrect(password, inputPwd);
    }

    public static boolean checkPasswordCorrect(Password password, String inputPwd) {
        String key = PasswordUtil.getSHA256(inputPwd);
        String encryStr = AesUtils.encrypt(key, inputPwd);
//        String setPwd = SPUtil.getString(Constant.SET_PWD);
        String setPwd = password.getEncryStr();
        LogUtil.e("保存的密码：" + setPwd);
        return !setPwd.equals(encryStr);
    }

    /**
     * 利用java原生的摘要实现SHA256加密
     */
    public static String getSHA256(String password) {
        System.out.println("password:" + password);
        MessageDigest messageDigest;
        String encodeStr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(password.getBytes("UTF-8"));
            encodeStr = byte2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println("sha256:" + encodeStr);
        return encodeStr;
    }

    /**
     * 将byte转为16进制
     *
     * @param bytes
     * @return
     */
    private static String byte2Hex(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte aByte : bytes) {
            String temp = Integer.toHexString(aByte & 0xFF);
            if (temp.length() == 1) {
                //1得到一位的进行补0操作
                stringBuilder.append("0");
            }
            stringBuilder.append(temp);
        }
        return stringBuilder.toString();
    }

}
