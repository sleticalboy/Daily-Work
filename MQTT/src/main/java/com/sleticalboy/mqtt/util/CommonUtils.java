package com.sleticalboy.mqtt.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Base64;

import androidx.core.content.ContextCompat;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public final class CommonUtils {
    
    private static final String IV = "a0fe7c7c98e09e8c";
    private static final char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static MessageDigest md5 = null;
    private static MessageDigest sha1 = null;
    
    static {
        try {
            md5 = MessageDigest.getInstance("MD5");
            sha1 = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException ignored) {
        }
    }
    
    public static String getMD5String(String input) {
        byte[] inputByteArray = input.getBytes();
        md5.update(inputByteArray);
        return bufferToHex(md5.digest());
    }
    
    private static String bufferToHex(byte bytes[]) {
        return bufferToHex(bytes, 0, bytes.length);
    }
    
    private static String bufferToHex(byte bytes[], int m, int n) {
        StringBuffer stringbuffer = new StringBuffer(2 * n);
        int k = m + n;
        for (int l = m; l < k; l++) {
            appendHexPair(bytes[l], stringbuffer);
        }
        return stringbuffer.toString();
    }
    
    private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
        char c0 = hexDigits[(bt & 0xf0) >> 4];// 取字节中高 4 位的数字转换, >>>
        // 为逻辑右移，将符号位一起右移,此处未发现两种符号有何不同
        char c1 = hexDigits[bt & 0xf];// 取字节中低 4 位的数字转换
        stringbuffer.append(c0);
        stringbuffer.append(c1);
    }
    
    public static String encrypt(String key, String clearText) {
        byte[] result = null;
        try {
            result = encrypt(IV, toByte(getMD5String(getMD5String(key))), clearText.getBytes());
        } catch (Exception e) {
        }
        return toHex(result);
    }
    
    public static String encrypt(String iv, String key, String clearText) {
        byte[] result = null;
        try {
            result = encrypt(iv, toByte(getMD5String(getMD5String(key))), clearText.getBytes());
        } catch (Exception e) {
        }
        return toHex(result);
    }
    
    public static String getRawKey() {
        try {
            String seed = System.currentTimeMillis() + "";
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            sr.setSeed(toByte(seed));
            kgen.init(128, sr); // 192 and 256 bits may not be available
            SecretKey skey = kgen.generateKey();
            byte[] raw = skey.getEncoded();
            return Base64.encodeToString(raw, Base64.NO_WRAP);
        } catch (NoSuchAlgorithmException e) {
        }
        return null;
    }
    
    private static byte[] encrypt(String IV, byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(IV.getBytes()));
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }
    
    public static String decrypt(String IV, String key, String result) throws Exception {
        byte[] raw = toByte(getMD5String(getMD5String(key)));
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(IV.getBytes()));
        return new String(cipher.doFinal(toByte(result)));
    }
    
    private static byte[] toByte(String hexString) {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++) {
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2), 16).byteValue();
        }
        return result;
    }
    
    private static String toHex(byte[] buf) {
        if (buf == null)
            return "";
        StringBuffer result = new StringBuffer(2 * buf.length);
        for (int i = 0; i < buf.length; i++) {
            appendHex(result, buf[i]);
        }
        return result.toString();
    }
    
    private static void appendHex(StringBuffer sb, byte b) {
        final String HEX = "0123456789abcdef";
        sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
    }
    
    /**
     * Only for safe keyboard, cause using same secretKey,
     * do not support decryption for CommonUtils encryption method.
     *
     * @param code
     * @return
     */
    public static String decrypt(String code) {
        if (code == null || code.length() == 0)
            return null;
        
        String mStrSecretKey = "minxing000000000";
        byte[] decrypted = null;
        SecretKeySpec skeySpec = new SecretKeySpec(mStrSecretKey.getBytes(), "AES");
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(IV.getBytes()));
            
            decrypted = cipher.doFinal(toByte(code));
        } catch (Exception e) {
            return null;
        }
        if (decrypted == null) {
            return null;
        }
        return new String(decrypted);
    }
    
    public static String getUuid(Context context) {
        String deviceId = "";
        String androidId = "";
        String SerialNum = "";
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                    == PackageManager.PERMISSION_GRANTED) {
                deviceId = tm.getDeviceId();
            } else {
                deviceId = "";
            }
        } catch (Exception e) {
            deviceId = "";
        }
        try {
            androidId = Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SerialNum = android.os.Build.SERIAL;
        String packageName = context.getPackageName();
        String uuid = deviceId + androidId + SerialNum + packageName;
        
        try {
            return sha1(sha1(uuid));
        } catch (UnsupportedEncodingException e) {
            return uuid;
        }
    }
    
    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }
    
    public static String sha1(String text) throws UnsupportedEncodingException {
        sha1.update(text.getBytes("UTF-8"));
        byte[] sha1hash = sha1.digest();
        return convertToHex(sha1hash);
    }
    
    public static String clientCheckSum(String url, String username) {
        try {
            return sha1(Uri.parse(url).getHost() + ":" + username);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }
    
    public static String userAgent(Context context) {
        //MinxingMessenger/5.3.7 (HUAWEI;  Android/4.2)
        String userAgent;
        String verName = null;
        try {
            verName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.getStackTrace();
        }
        if (verName != null && !"".equals(verName)) {
            userAgent = "MinxingMessenger/" + verName;
        } else {
            userAgent = "MinxingMessenger/1.0.0";
        }
        
        return userAgent + " (" + Build.MANUFACTURER + "-" + Build.MODEL + ";"
                + " Android/" + Build.VERSION.RELEASE + ")";
    }
}
