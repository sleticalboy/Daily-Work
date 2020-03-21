package com.sleticalboy.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import androidx.annotation.RequiresPermission;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.util.Enumeration;

import static android.Manifest.permission.ACCESS_WIFI_STATE;
import static android.Manifest.permission.INTERNET;

/**
 * Created on 18-5-14.
 *
 * @author leebin
 * @description
 */
public class DevicesUtils {

    private static final String DEFAULT_MAC_ADDRESS = "02:00:00:00:00:00";

    public static WifiInfo getConnectedWifiInfo(Context context) {
        if (context == null) {
            return null;
        }
        WifiManager mWifi = (WifiManager) context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        assert mWifi != null;
        if (mWifi.isWifiEnabled()) {
            return mWifi.getConnectionInfo();
            //WifiInfo wifiInfo = mWifi.getConnectionInfo();
            ////获取被连接网络的名称
            //String netName = wifiInfo.getSSID();
            //Log.d("AttendUtils", "netName = " + netName);
            ////获取被连接网络的mac地址
            //String netMac = wifiInfo.getBSSID();
            //Log.d("AttendUtils", "netMac = " + netMac);
            ////final String macAddress = wifiInfo.getMacAddress(); // 02:00:00:00:00:00
            ////Log.d("AttendUtils", macAddress);
            //return netMac;
        }
        return null;
    }

    public static String getConnectedWifiMacAddress(Context context) {
        final WifiInfo wifiInfo = getConnectedWifiInfo(context);
        if (wifiInfo != null) {
            return wifiInfo.getBSSID();
        }
        return null;
    }

    public static String getConnectedWifiName(Context context) {
        final WifiInfo wifiInfo = getConnectedWifiInfo(context);
        if (wifiInfo != null) {
            return wifiInfo.getSSID();
        }
        return null;
    }

    /**
     * Return the MAC address.
     * from <a href='https://github.com/Blankj/AndroidUtilCode/blob/master/utilcode/src/main/java/com/blankj/utilcode/util/DeviceUtils.java'>github DeviceUtils.java</a>
     * <p>Must hold
     * {@code <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />},
     * {@code <uses-permission android:name="android.permission.INTERNET" />}</p>
     *
     * @return the MAC address
     */
    @RequiresPermission(allOf = {ACCESS_WIFI_STATE, INTERNET})
    public static String getMacAddress(Context context) {
        String macAddress = getMacAddressByWifiInfo(context);
        if (!DEFAULT_MAC_ADDRESS.equals(macAddress)) {
            return macAddress;
        }
        macAddress = getMacAddressByNetworkInterface();
        if (!DEFAULT_MAC_ADDRESS.equals(macAddress)) {
            return macAddress;
        }
        macAddress = getMacAddressByInetAddress();
        if (!DEFAULT_MAC_ADDRESS.equals(macAddress)) {
            return macAddress;
        }
        macAddress = getMacAddressByFile();
        if (!DEFAULT_MAC_ADDRESS.equals(macAddress)) {
            return macAddress;
        }
        return "please open wifi";
    }

    @SuppressLint({"HardwareIds", "MissingPermission"})
    private static String getMacAddressByWifiInfo(Context context) {
        try {
            WifiManager wifi = (WifiManager) context.getApplicationContext()
                    .getSystemService(Context.WIFI_SERVICE);
            if (wifi != null) {
                WifiInfo info = wifi.getConnectionInfo();
                if (info != null) {
                    return info.getMacAddress();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DEFAULT_MAC_ADDRESS;
    }

    private static String getMacAddressByNetworkInterface() {
        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = nis.nextElement();
                if (ni == null || !"wlan0".equalsIgnoreCase(ni.getName())) {
                    continue;
                }
                byte[] macBytes = ni.getHardwareAddress();
                if (macBytes != null && macBytes.length > 0) {
                    StringBuilder sb = new StringBuilder();
                    for (byte b : macBytes) {
                        sb.append(String.format("%02x:", b));
                    }
                    return sb.substring(0, sb.length() - 1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DEFAULT_MAC_ADDRESS;
    }

    private static String getMacAddressByInetAddress() {
        try {
            InetAddress inetAddress = getInetAddress();
            if (inetAddress != null) {
                NetworkInterface ni = NetworkInterface.getByInetAddress(inetAddress);
                if (ni != null) {
                    byte[] macBytes = ni.getHardwareAddress();
                    if (macBytes != null && macBytes.length > 0) {
                        StringBuilder sb = new StringBuilder();
                        for (byte b : macBytes) {
                            sb.append(String.format("%02x:", b));
                        }
                        return sb.substring(0, sb.length() - 1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DEFAULT_MAC_ADDRESS;
    }

    private static InetAddress getInetAddress() {
        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = nis.nextElement();
                // To prevent phone of xiaomi return "10.0.2.15"
                if (!ni.isUp()) {
                    continue;
                }
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress inetAddress = addresses.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String hostAddress = inetAddress.getHostAddress();
                        if (hostAddress.indexOf(':') < 0) {
                            return inetAddress;
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getMacAddressByFile() {
        ShellUtils.CommandResult result = ShellUtils.execCmd("getprop wifi.interface", false);
        if (result.result == 0) {
            String name = result.successMsg;
            if (name != null) {
                result = ShellUtils.execCmd("cat /sys/class/net/" + name + "/address", false);
                if (result.result == 0) {
                    String address = result.successMsg;
                    if (address != null && address.length() > 0) {
                        return address;
                    }
                }
            }
        }
        return DEFAULT_MAC_ADDRESS;
    }

    /**
     * 根据用户指定的两个经纬度坐标点，计算这两个点间的直线距离，单位为米.<br>
     * from 高德地图 api: <a href='http://lbs.amap.com/api/android-sdk/guide/computing-equipment/calcute-distance-tool'>
     * AMapUtils.calculateLineDistance(latLng1,latLng2)</a>
     */
    public static double getDistance(double lat1, double lng1, double lat2, double lng2) {
        double var4 = lng1;
        double var6 = lat1;
        double var8 = lng2;
        double var10 = lat2;
        var4 *= 0.01745329251994329D;
        var6 *= 0.01745329251994329D;
        var8 *= 0.01745329251994329D;
        var10 *= 0.01745329251994329D;
        double var12 = Math.sin(var4);
        double var14 = Math.sin(var6);
        double var16 = Math.cos(var4);
        double var18 = Math.cos(var6);
        double var20 = Math.sin(var8);
        double var22 = Math.sin(var10);
        double var24 = Math.cos(var8);
        double var26 = Math.cos(var10);
        double[] var28 = new double[3];
        double[] var29 = new double[3];
        var28[0] = var18 * var16;
        var28[1] = var18 * var12;
        var28[2] = var14;
        var29[0] = var26 * var24;
        var29[1] = var26 * var20;
        var29[2] = var22;
        double var30 = Math.sqrt(
                (var28[0] - var29[0]) * (var28[0] - var29[0])
                        + (var28[1] - var29[1]) * (var28[1] - var29[1])
                        + (var28[2] - var29[2]) * (var28[2] - var29[2]));
        final double var31 = (Math.asin(var30 / 2.0D) * 1.27420015798544E7D);
        return Double.parseDouble(new DecimalFormat("##.###").format(var31));
    }

}
