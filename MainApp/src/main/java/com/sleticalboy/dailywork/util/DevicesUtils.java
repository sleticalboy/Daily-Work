package com.sleticalboy.dailywork.util;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.List;

/**
 * Created on 18-5-14.
 *
 * @author sleticalboy
 * @description
 */
public class DevicesUtils {

    public static String getConnectedWifiMacAddress(Context context) {
        String connectedWifiMacAddress = null;
        WifiManager wifiManager = (WifiManager) context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        List<ScanResult> wifiList;
        if (wifiManager != null) {
            wifiList = wifiManager.getScanResults();
            WifiInfo info = wifiManager.getConnectionInfo();
            if (wifiList != null && info != null) {
                for (int i = 0; i < wifiList.size(); i++) {
                    ScanResult result = wifiList.get(i);
                    if (info.getBSSID().equals(result.BSSID)) {
                        connectedWifiMacAddress = result.BSSID;
                    }
                }
            }
        }
        return connectedWifiMacAddress;
    }

    public static String getWifiMacAddress(Context context) {
        String address = getWifiMacAddressBeforeM(context);
        if (TextUtils.isEmpty(address) || "02:00:00:00:00:00".equals(address)) {
            address = getWifiMacAddressAfterM();
        }
        Log.d("DevicesUtils", address + "");
        return address;
    }

    /**
     * before android M
     */
    private static String getWifiMacAddressBeforeM(Context context) {
        WifiManager wm = (WifiManager) context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        final WifiInfo wifiInfo = wm.getConnectionInfo();
        return wifiInfo.getMacAddress();
    }

    /**
     * after android M
     *
     * @return the wifi mac address
     */
    private static String getWifiMacAddressAfterM() {
        NetworkInterface networkInterface;
        StringBuilder buf = new StringBuilder();
        byte[] addr;
        try {
            /*for android 6.0~8.0*/
            networkInterface = NetworkInterface.getByName("eth0");
            if (networkInterface == null) {
                /*for android P*/
                networkInterface = NetworkInterface.getByName("wlan0");
            }
            if (networkInterface == null) {
                return null;
            }
            addr = networkInterface.getHardwareAddress();
        } catch (SocketException e) {
            Log.d("DevicesUtils", e.getMessage());
            return null;
        }
        for (byte b : addr) {
            buf.append(String.format("%02X:", b));
        }
        if (buf.length() > 0) {
            buf.deleteCharAt(buf.length() - 1);
        }
        return buf.toString();
    }

}
