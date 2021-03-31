package com.fly.tkuilife.utils;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.OAEPParameterSpec;

public class RSAHelper {

    private Cipher cipher;

    public RSAHelper(){
        try {
            this.cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    public byte[] encrypt(String src, PublicKey publicKey){
        try {
            byte[] raw_src = src.getBytes("UTF-8");

            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            byte[] raw_encrypted = cipher.doFinal(raw_src);

            return raw_encrypted;
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }
    public String decrypt(byte[] src, PrivateKey privateKey){
        try {
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] raw_decrypted = cipher.doFinal(src);
            return new String(raw_decrypted);
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }
}
