package com.fly.tkuilife.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.MGF1ParameterSpec;
import java.util.Calendar;

import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.security.auth.x500.X500Principal;

public class KeyStoreHelper {

    private Context context;
    private KeyStore keyStore;
    private SharedPreferences sharedPreferences;
    private AESHelper aesHelper;
    private RSAHelper rsaHelper;

    public KeyStoreHelper(Context context){
        this.context = context;
        initInstantation();
        initSharePreference();
        initKeyStore();
    }

    private void initSharePreference(){
        sharedPreferences = context.getSharedPreferences("keystore", Context.MODE_PRIVATE);
    }
    private void initInstantation(){
        aesHelper = new AESHelper();
        rsaHelper = new RSAHelper();
    }
    private void initKeyStore(){
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);

            if(!keyStore.containsAlias("TKUILife")){
                sharedPreferences.edit().putString("iv", "").commit();
                genKeyStoreRSAKey();
                genAESKey();
            }
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
    }
    private void genKeyStoreRSAKey() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) genKeyStoreRSAKey_AboveApi23();
        else genKeyStoreRSAKey_BelowApi23();
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void genKeyStoreRSAKey_AboveApi23() throws InvalidAlgorithmParameterException, NoSuchProviderException, NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore");
        KeyGenParameterSpec keyGenParameterSpec = new KeyGenParameterSpec
                .Builder("TKUILife", KeyProperties.PURPOSE_ENCRYPT|KeyProperties.PURPOSE_DECRYPT)
                .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                .build();
        keyPairGenerator.initialize(keyGenParameterSpec);
        keyPairGenerator.generateKeyPair();
    }
    private void genKeyStoreRSAKey_BelowApi23() throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        end.add(Calendar.YEAR, 30);

        KeyPairGeneratorSpec keyPairGeneratorSpec = new KeyPairGeneratorSpec
                .Builder(context)
                .setAlias("TKUILife")
                .setSubject(new X500Principal("CN="+"TKUILife"))
                .setSerialNumber(BigInteger.TEN)
                .setStartDate(start.getTime())
                .setEndDate(end.getTime())
                .build();

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore");

        keyPairGenerator.initialize(keyPairGeneratorSpec);
        keyPairGenerator.generateKeyPair();
    }
    private void genAESKey() throws KeyStoreException {

        byte[] raw_Key = new byte[24];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(raw_Key);

        byte[] raw_IV = secureRandom.generateSeed(12);

        sharedPreferences.edit()
                .putString("key", Base64.encodeToString(rsaHelper.encrypt(Base64.encodeToString(raw_Key, Base64.NO_WRAP), keyStore.getCertificate("TKUILife").getPublicKey()), Base64.NO_WRAP))
                .putString("iv", Base64.encodeToString(rsaHelper.encrypt(Base64.encodeToString(raw_IV, Base64.NO_WRAP), keyStore.getCertificate("TKUILife").getPublicKey()), Base64.NO_WRAP))
                .commit();
    }


    public String getKey() throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
        return rsaHelper.decrypt(Base64.decode(sharedPreferences.getString("key",""), Base64.NO_WRAP), (PrivateKey) keyStore.getKey("TKUILife", null));
    }

    public String getIv() throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
        return rsaHelper.decrypt(Base64.decode(sharedPreferences.getString("iv",""), Base64.NO_WRAP), (PrivateKey) keyStore.getKey("TKUILife", null));
    }







}
