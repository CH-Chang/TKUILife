package com.fly.tkuilife.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESHelper {

    private Cipher cipher;

    public AESHelper(){
        try {
            this.cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    public byte[] encrypt(final String src, final String KEY, final String IV){
        try {
            byte[] raw_key = KEY.getBytes("UTF-8");
            byte[] raw_iv = IV.getBytes("UTF-8");
            SecretKeySpec secretKeySpec = new SecretKeySpec(raw_key, "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(raw_iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

            byte[] raw_encrypted = cipher.doFinal(src.getBytes());

            return raw_encrypted;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String decrypt(final byte[] src, final String KEY, final String IV){
        try {
            byte[] raw_key = KEY.getBytes("UTF-8");
            byte[] raw_iv = IV.getBytes("UTF-8");
            SecretKeySpec secretKeySpec = new SecretKeySpec(raw_key, "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(raw_iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

            byte[] raw_decrypted = cipher.doFinal(src);

            return new String(raw_decrypted);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }



}
