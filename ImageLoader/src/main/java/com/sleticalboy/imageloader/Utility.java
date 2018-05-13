package com.sleticalboy.imageloader;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;

/**
 * Created on 18-5-4.
 *
 * @author sleticalboy
 * @description
 */
public final class Utility {

    /**
     * before android 7.0
     *
     * @return mac address
     */
    public static String getWifiMacAddress() {
        try {
            List<NetworkInterface> all =
                    Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!"wlan0".equalsIgnoreCase(nif.getName()))
                    continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return null;
                }

                StringBuilder builder = new StringBuilder();
                for (byte b : macBytes) {
                    builder.append(String.format("%02X:", b));
                }

                if (builder.length() > 0) {
                    builder.deleteCharAt(builder.length() - 1);
                }
                return builder.toString();
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }
}
