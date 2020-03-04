//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.zhou.library.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Proxy;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;

public class NetworkUtil {
    public static final String NETWORK_TYPE_MOBILE = "Mobile";
    public static final String NETWORK_TYPE_4G = "4G";
    public static final String NETWORK_TYPE_3G = "3G";
    public static final String NETWORK_TYPE_2G = "2G";
    public static final String NETWORK_TYPE_WAP = "WAP";
    public static final String NETWORK_TYPE_WIFI = "WiFi";
    public static final String NETWORK_TYPE_UNKNOWN = "Unknown";
    public static final String NETWORK_TYPE_DISCONNECT = "Disconnect";

    public NetworkUtil() {
    }

    public static ConnectivityManager getConnectivityManager() {
        return (ConnectivityManager) AppUtil.app().getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @SuppressLint({"MissingPermission"})
    public static NetworkInfo getNetworkInfo() {
        ConnectivityManager cm = getConnectivityManager();
        return cm == null ? null : cm.getActiveNetworkInfo();
    }

    public static boolean isActiveNetwork() {
        NetworkInfo info = getNetworkInfo();
        return info != null && info.isConnectedOrConnecting();
    }

    public static boolean isConnected() {
        NetworkInfo info = getNetworkInfo();
        return info != null && info.isConnected() && info.getState() == State.CONNECTED;
    }

    public static int getNetworkType() {
        NetworkInfo networkInfo = getNetworkInfo();
        return networkInfo == null ? -1 : networkInfo.getType();
    }

    public static String getNetworkTypeName() {
        int netType = getNetworkType();
        switch (netType) {
            case -1:
                return "Disconnect";
            case 0:
                String proxyHost = Proxy.getDefaultHost();
                return TextUtils.isEmpty(proxyHost) ? getMobileNetworkName() : "WAP";
            case 1:
                return "WiFi";
            default:
                return "Unknown";
        }
    }

    private static String getMobileNetworkName() {
        TelephonyManager telephonyManager = (TelephonyManager) AppUtil.app().getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            switch (telephonyManager.getNetworkType()) {
                case 0:
                    return "Unknown";
                case 1:
                case 2:
                case 4:
                case 7:
                case 11:
                    return "2G";
                case 3:
                case 5:
                case 6:
                case 8:
                case 9:
                case 10:
                case 12:
                case 14:
                case 15:
                    return "3G";
                case 13:
                    return "4G";
                default:
                    String strSubTypeName = getNetworkInfo().getSubtypeName();
                    return !strSubTypeName.equalsIgnoreCase("TD-SCDMA") && !strSubTypeName.equalsIgnoreCase("WCDMA") && !strSubTypeName.equalsIgnoreCase("CDMA2000") ? "Mobile" : "3G";
            }
        } else {
            return "Unknown";
        }
    }

    @SuppressLint({"MissingPermission,WifiManagerLeak"})
    private static WifiInfo getWifiInfo() {
        WifiManager wifiManager = (WifiManager) AppUtil.app().getSystemService(Context.WIFI_SERVICE);
        return wifiManager == null ? null : wifiManager.getConnectionInfo();
    }

    public static String mac() {
        String result = getWlanMac();
        if (TextUtils.isEmpty(result)) {
            result = getMobiMac();
        }

        return result;
    }

    public static String getWlanMac() {
        String result = null;

        try {
            String path = "sys/class/net/wlan0/address";
            if ((new File(path)).exists()) {
                FileInputStream fis = new FileInputStream(path);
                byte[] buffer = new byte[8192];
                int byteCount = fis.read(buffer);
                if (byteCount > 0) {
                    result = new String(buffer, 0, byteCount, "UTF-8");
                }
            }
        } catch (Exception var5) {
        }

        if (TextUtils.isEmpty(result) && getNetworkTypeName().equals("WiFi")) {
            result = getWifiInfo().getMacAddress();
        }

        return result;
    }

    public static String getMobiMac() {
        try {
            String path = "sys/class/net/eth0/address";
            if ((new File(path)).exists()) {
                FileInputStream fis = new FileInputStream(path);
                byte[] buffer = new byte[8192];
                int byteCount = fis.read(buffer);
                if (byteCount > 0) {
                    return new String(buffer, 0, byteCount, "UTF-8");
                }
            }
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        return null;
    }

    public static String getWifiName() {
        WifiInfo wifiInfo = getWifiInfo();
        String wifiName = wifiInfo.getSSID();
        if (wifiName != null && !wifiName.contains("<unknown ssid>") && wifiName.length() > 2) {
            if (wifiName.startsWith("\"") && wifiName.endsWith("\"")) {
                wifiName = wifiName.subSequence(1, wifiName.length() - 1).toString();
            }

            return wifiName;
        } else {
            return null;
        }
    }

    public static String getWifiIpAddress() {
        WifiInfo localWifiInfo = getWifiInfo();
        return localWifiInfo != null ? convertIntToIp(localWifiInfo.getIpAddress()) : null;
    }

    private static String convertIntToIp(int paramInt) {
        return (paramInt & 255) + "." + (255 & paramInt >> 8) + "." + (255 & paramInt >> 16) + "." + (255 & paramInt >> 24);
    }
}
