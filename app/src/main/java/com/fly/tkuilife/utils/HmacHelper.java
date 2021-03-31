package com.fly.tkuilife.utils;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HmacHelper {

    public String getSignture(String xDate, String AppKey){
        String res = null;
        try {
            SecretKeySpec singningKey = new SecretKeySpec(AppKey.getBytes("UTF-8"), "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(singningKey);
            byte[] rawHmac = mac.doFinal(xDate.getBytes("UTF-8"));
            res = Base64.encodeToString(rawHmac, Base64.NO_WRAP);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return res;
    }
}
