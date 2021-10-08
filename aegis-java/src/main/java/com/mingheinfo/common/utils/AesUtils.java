package com.mingheinfo.common.utils;

import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

public class AesUtils {
    public static String encrypt(String content, String aeskey)  throws Exception {
    	byte[] keyByte = aeskey.getBytes("UTF-8");
    	
        final IvParameterSpec ivSpec = new IvParameterSpec(Arrays.copyOfRange(keyByte, 0, 16));
        final Cipher cipher = Cipher.getInstance("AES/CFB/NoPadding");
        final SecretKeySpec keySpec = new SecretKeySpec(keyByte, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        final byte[] data = content.getBytes();
        byte[] result = cipher.doFinal(data);// 加密

        return Base64.encode(result); 
    }
    
    public static byte[] decrypt(final byte[] aeskey, final String b64encrypted) throws Exception {
        final IvParameterSpec ivSpec = new IvParameterSpec(Arrays.copyOfRange(aeskey, 0, 16));
        final Cipher cipher = Cipher.getInstance("AES/CFB/NoPadding");
        final SecretKeySpec keySpec = new SecretKeySpec(aeskey, "AES");
        cipher.init(2, keySpec, ivSpec);
        final byte[] data = Base64.decode(b64encrypted);
        final byte[] plainText = new byte[cipher.getOutputSize(data.length)];
        final int ptLength = cipher.update(data, 0, data.length, plainText, 0);
        cipher.doFinal(plainText, ptLength);
        return plainText;
    }    
}
