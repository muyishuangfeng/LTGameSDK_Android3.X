package com.gnetop.ltgame.core.util;

import android.util.Base64;

import com.gnetop.ltgame.core.common.Constants;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * description: 加解密帮助类
 * @author: MHeaven
 * created at: 2019-11-05 15:19
 */
public class EncryptAndDecryptUtil {
    private static SecretKeySpec sSecretKeySpec;
    /*****************************************MD5 encryption****************************************************/
    /**
     * md5加密
     *
     * @param str
     * @return
     */
    public static String getMD5Str(String str) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            System.out.println("NoSuchAlgorithmException caught!");
            System.exit(-1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] byteArray = messageDigest.digest();
        StringBuffer md5StrBuff = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
        }
        return md5StrBuff.toString();
    }
    /*****************************************AES encryption****************************************************/
    /**
     * @return the Base64-encode bytes of AES encryption
     */
    private static final String TRANSFORMATION = "AES/CBC/PKCS7Padding";

    public static String encryptAES2Base64( String textToEncrypt) {
        if (textToEncrypt == null) {
            return "";
        } else {
            if (base64Encode(encryptAES(textToEncrypt))== null) {
                return "";
            } else {
                return new String(base64Encode(encryptAES(textToEncrypt)), StandardCharsets.UTF_8);
            }
        }
    }

    /**
     * @return the bytes of AES decryption for Base64-encode bytes
     */
    public static String decryptBase64AES( String encryptedData) {
        if (encryptedData == null) {
            return "";
        } else {
            if (decryptAES(base64Decode(encryptedData.getBytes()))== null) {
                return "";
            } else {
                return new String(decryptAES(base64Decode(encryptedData.getBytes())),StandardCharsets.UTF_8);
            }
        }
    }

    public static byte[] decryptAES(byte[] encryptedData) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            IvParameterSpec spec = new IvParameterSpec(getIv());
            cipher.init(Cipher.DECRYPT_MODE, generateSecretkey(), spec);
            return cipher.doFinal(encryptedData);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException |
                InvalidAlgorithmParameterException | InvalidKeyException
                | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] encryptAES(String textToEncrypt) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            // 初始化密钥模式
            cipher.init(Cipher.ENCRYPT_MODE, generateSecretkey(), new IvParameterSpec(getIv()));
            return cipher.doFinal(textToEncrypt.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException
                | NoSuchPaddingException | InvalidKeyException
                | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] getIv() {
        return "A-32-Byte-String".getBytes();
    }

    public static byte[] base64Encode( byte[] input) {
        if (input == null) {
            return null;
        } else {
            return Base64.encode(input, Base64.DEFAULT);
        }
    }

    public static byte[] base64Decode( byte[] input) {
        if (input == null) {
            return null;
        } else {
            return Base64.decode(input, Base64.DEFAULT);
        }
    }

    /**
     * 加密
     */
    public static SecretKey generateSecretkey() {
        sSecretKeySpec = new SecretKeySpec(Constants.TAG.getBytes(), "AES");
        return sSecretKeySpec;
    }
}
