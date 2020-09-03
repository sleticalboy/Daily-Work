package com.sleticalboy.learning.encryption;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class SM4Utils {

    // 秘钥
    private String secretKey = "";
    // 向量
    private String iv = "";
    // 16 进制字符串
    private boolean hexString = false;

    private static final SM4Utils instance = new SM4Utils();

    public SM4Utils() {
    }

    public static SM4Utils getInstance() {
        return instance;
    }

    /**
     * 加密（电子密码本模式）
     *
     * @param plainText 明文
     * @return 密文
     */
    public String encryptECB(String plainText) {
        try {
            SM4Context ctx = new SM4Context();
            ctx.setPadding(true);
            ctx.setMode(SM4.SM4_ENCRYPT);

            byte[] keyBytes;
            if (hexString) {
                keyBytes = Util.hexStringToBytes(secretKey);
            } else {
                keyBytes = secretKey.getBytes();
            }

            SM4 sm4 = new SM4();
            sm4.setEncryptKey(ctx, keyBytes);
            byte[] input = plainText.getBytes("GBK");
            byte[] encrypted = sm4.cryptECB(ctx, input);
            String cipherText = new BASE64Encoder().encode(encrypted);
            if (cipherText != null && cipherText.trim().length() > 0) {
                Pattern p = Pattern.compile("\\s*|\t|\r|\n");
                Matcher m = p.matcher(cipherText);
                cipherText = m.replaceAll("");
            }
            return cipherText;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 解密（电子密码本模式）
     *
     * @param cipherText 密文
     * @return 明文
     */
    public String decryptECB(String cipherText) {
        try {
            SM4Context ctx = new SM4Context();
            ctx.setPadding(true);
            ctx.setMode(SM4.SM4_DECRYPT);

            byte[] keyBytes;
            if (hexString) {
                keyBytes = Util.hexStringToBytes(secretKey);
            } else {
                keyBytes = secretKey.getBytes();
            }

            SM4 sm4 = new SM4();
            sm4.setDecryptKey(ctx, keyBytes);
            byte[] decrypted = sm4.cryptECB(ctx, new BASE64Decoder().decodeBuffer(cipherText));
            return new String(decrypted, "GBK");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * (加密块链模式)
     *
     * @param plainText
     * @return
     */
    public String encryptCBC(String plainText) {
        try {
            SM4Context ctx = new SM4Context();
            ctx.setPadding(true);
            ctx.setMode(SM4.SM4_ENCRYPT);

            byte[] keyBytes;
            byte[] ivBytes;
            if (hexString) {
                keyBytes = Util.hexStringToBytes(secretKey);
                ivBytes = Util.hexStringToBytes(iv);
            } else {
                keyBytes = secretKey.getBytes();
                ivBytes = iv.getBytes();
            }

            SM4 sm4 = new SM4();
            sm4.setEncryptKey(ctx, keyBytes);
            byte[] encrypted = sm4.cryptCBC(ctx, ivBytes, plainText.getBytes("GBK"));
            String cipherText = new BASE64Encoder().encode(encrypted);
            if (cipherText != null && cipherText.trim().length() > 0) {
                Pattern p = Pattern.compile("\\s*|\t|\r|\n");
                Matcher m = p.matcher(cipherText);
                cipherText = m.replaceAll("");
            }
            return cipherText;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * (加密块链模式)
     *
     * @param cipherText
     * @return
     */
    public String decryptCBC(String cipherText) {
        try {
            SM4Context ctx = new SM4Context();
            ctx.setPadding(true);
            ctx.setMode(SM4.SM4_DECRYPT);

            byte[] keyBytes;
            byte[] ivBytes;
            if (hexString) {
                keyBytes = Util.hexStringToBytes(secretKey);
                ivBytes = Util.hexStringToBytes(iv);
            } else {
                keyBytes = secretKey.getBytes();
                ivBytes = iv.getBytes();
            }

            SM4 sm4 = new SM4();
            sm4.setDecryptKey(ctx, keyBytes);
            byte[] decrypted = sm4.cryptCBC(ctx, ivBytes, new BASE64Decoder().decodeBuffer(cipherText));
            return new String(decrypted, "GBK");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 加密
     */
    public String getEncryptStr(String inputStr, String secretKey) {
        this.secretKey = secretKey;  //meF8U9wHFOMfs2Y9
        this.hexString = false;

        System.out.println("ECB模式");
        String cipherText = encryptECB(inputStr);
        System.out.println("ECB模式");
        return cipherText;
    }

    /**
     * 解密
     */
    public String getDecryptStr(String inputStr, String secretKey) {
        this.secretKey = secretKey; // meF8U9wHFOMfs2Y9
        this.hexString = false;
        return decryptECB(inputStr);
    }

}
