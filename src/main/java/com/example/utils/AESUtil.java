package com.example.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;


@Slf4j
public class AESUtil {

    static{
        try{
            Security.addProvider(new BouncyCastleProvider());
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    /**
     * @author miracle.qu
     * @Description AES算法加密明文
     * @param data 明文
     * @return 密文
     */
    public static String encryptAESCFB(String key, String iv, String data) throws Exception {
        try {

            Cipher cipher = Cipher.getInstance("AES/CFB/NoPadding");
//            Cipher.getInstance();

            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
            // CBC模式，需要一个向量iv，可增加加密算法的强度
            IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes("UTF-8"));

            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
            byte[] encrypted = cipher.doFinal(data.getBytes("UTF-8"));
            // BASE64做转码。
            String aesBase64 = AESUtil.encode(encrypted).trim();
//            log.info("aesBase64:{}",aesBase64);
            return aesBase64;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @author miracle.qu
     * @Description AES算法加密明文
     * @param data 明文
     * @return 密文
     */
    public static String encryptAES(String key, String iv, String data) throws Exception {
        try {

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");

            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
            // CBC模式，需要一个向量iv，可增加加密算法的强度
            IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes("UTF-8"));

            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
            byte[] encrypted = cipher.doFinal(data.getBytes("UTF-8"));
            // BASE64做转码。
            String aesBase64 = AESUtil.encode(encrypted).trim();
            log.info("aesBase64:{}",aesBase64);
            String hexString = Hex.encodeHexString(aesBase64.getBytes());
            log.info("hexString:{}",hexString);
            return hexString;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @author miracle.qu
     * @Description AES算法解密密文
     * @param data 密文
     * @return 明文
     */
    public static String decryptAES(String key, String iv, String data) throws Exception {
        try
        {
            byte[] decodeHex = Hex.decodeHex(data);
            String string = Base64.encodeBase64String(decodeHex);
            //先用base64解密
            byte[] encrypted1 = AESUtil.decode(string);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
            IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes("UTF-8"));

            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);

            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original);
            return originalString.trim();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 编码
     * @param byteArray
     * @return
     */
    public static String encode(byte[] byteArray) {
        return new String(new Base64().encode(byteArray));
    }

    /**
     * 解码
     * @param base64EncodedString
     * @return
     */
    public static byte[] decode(String base64EncodedString) {
        return new Base64().decode(base64EncodedString);
    }
}
