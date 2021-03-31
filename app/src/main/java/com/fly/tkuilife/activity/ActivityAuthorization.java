package com.fly.tkuilife.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.Instrumentation;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;

import com.fly.tkuilife.R;
import com.fly.tkuilife.utils.AESHelper;
import com.fly.tkuilife.utils.KeyStoreHelper;

import java.lang.ref.WeakReference;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.concurrent.Executor;

public class ActivityAuthorization extends AppCompatActivity implements View.OnClickListener {

    private int mode, trytimes;

    private Button biometric, one, two, three, four, five, six, seven, eight, nine, zero, delete, backspace;
    private View[] pinview;
    private TextView title;
    private Dialog loading;

    private String pin, compare;

    private SharedPreferences lock;

    private BiometricManager biometricManager;

    private Handler handler;
    private Executor executor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        loading();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.authorization_biometric:
                biometricAuthorizate();
                break;
            case R.id.authorization_one:
                edit('1');
                break;
            case R.id.authorization_two:
                edit('2');
                break;
            case R.id.authorization_three:
                edit('3');
                break;
            case R.id.authorization_four:
                edit('4');
                break;
            case R.id.authorization_five:
                edit('5');
                break;
            case R.id.authorization_six:
                edit('6');
                break;
            case R.id.authorization_seven:
                edit('7');
                break;
            case R.id.authorization_eight:
                edit('8');
                break;
            case R.id.authorization_nine:
                edit('9');
                break;
            case R.id.authorization_zero:
                edit('0');
                break;
            case R.id.authorization_delete:
                edit('D');
                break;
            case R.id.authorization_backspace:
                edit('B');
                break;
        }
    }

    private void init(){
        initOther();
        initView();
        initLoadingDialog();
        initSharedPreferences();
        initInstantation();
        initInteraction();

    }
    private void initView(){
        setContentView(R.layout.activity_authorization);
        biometric = findViewById(R.id.authorization_biometric);
        one = findViewById(R.id.authorization_one);
        two = findViewById(R.id.authorization_two);
        three = findViewById(R.id.authorization_three);
        four = findViewById(R.id.authorization_four);
        five = findViewById(R.id.authorization_five);
        six = findViewById(R.id.authorization_six);
        seven = findViewById(R.id.authorization_seven);
        eight = findViewById(R.id.authorization_eight);
        nine = findViewById(R.id.authorization_nine);
        zero = findViewById(R.id.authorization_zero);
        delete = findViewById(R.id.authorization_delete);
        backspace = findViewById(R.id.authorization_backspace);
        pinview[0] = findViewById(R.id.authorization_pin1);
        pinview[1] = findViewById(R.id.authorization_pin2);
        pinview[2] = findViewById(R.id.authorization_pin3);
        pinview[3] = findViewById(R.id.authorization_pin4);
        title = findViewById(R.id.authorization_title);
    }
    private void initInteraction(){
        biometric.setOnClickListener(this);
        one.setOnClickListener(this);
        two.setOnClickListener(this);
        three.setOnClickListener(this);
        four.setOnClickListener(this);
        five.setOnClickListener(this);
        six.setOnClickListener(this);
        seven.setOnClickListener(this);
        eight.setOnClickListener(this);
        nine.setOnClickListener(this);
        zero.setOnClickListener(this);
        delete.setOnClickListener(this);
        backspace.setOnClickListener(this);
    }
    private void initOther(){
        pinview = new View[]{null, null, null, null};
        pin = compare = "";
        mode = getIntent().getIntExtra("mode",-1);
        trytimes = 0;
    }
    private void initSharedPreferences(){
        lock = getSharedPreferences("lock",MODE_PRIVATE);
    }
    private void initInstantation(){
        biometricManager = BiometricManager.from(this);
        handler = new Handler();
        executor = new Executor() {
            @Override
            public void execute(Runnable command) {
                handler.post(command);
            }
        };
    }
    private void initLoadingDialog(){
        loading = new Dialog(this, R.style.loadingDialog);
        loading.setContentView(R.layout.layout_loadingdialog);
        loading.setCancelable(false);
    }

    private void loading(){
        loadingTitle();
        loadingPinView();
        loadingBiometric();
    }
    private void loadingBiometric(){
        if (mode!=2){
            if (lock.getBoolean("biometric",false)&&isAboveApi23()&&isSupportFingerprint()&&hasEnrolledFingerprints()){
                biometric.setVisibility(View.VISIBLE);
                biometricAuthorizate();
            }
            else biometric.setVisibility(View.INVISIBLE);
        }
        else biometric.setVisibility(View.INVISIBLE);
    }
    private void loadingTitle(){
        switch (mode){
            case 0: //普通驗證模式
                title.setText("請輸入PIN碼");
                break;
            case 1: //修改驗證模式
                title.setText("請驗證您的身分");
                break;
            case 2: //首次設定模式
                break;
        }
    }
    private void loadingPinView(){
        syncPINView();
    }

    private void edit(char num){
        if (num=='B'){
            int length = pin.length();
            if (length>0){
                pin = pin.substring(0, length-1);
            }
        }
        else if (num=='D'){
            pin="";
        }
        else {
            if (pin.length()==3){
                pin+=num;
                start();
            }
            else {
                pin+=num;
            }
        }
        syncPINView();
    }
    private void syncPINView(){
        int length = pin.length()-1;
        for (int i=0;i<4;i++){
            if (i>length) pinview[i].setEnabled(false);
            else pinview[i].setEnabled(true);
        }
    }




    private void biometricAuthorizate() {
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("生物識別驗證")
                .setDescription("請使用生物識別驗證完成解鎖")
                .setNegativeButtonText("取消")
                .build();
        BiometricPrompt.AuthenticationCallback authenticationCallback = new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                finish(true);
                super.onAuthenticationSucceeded(result);
            }
            @Override
            public void onAuthenticationFailed() {
                finish(false);
                Toast.makeText(ActivityAuthorization.this, "生物辨識失敗", Toast.LENGTH_SHORT).show();
                super.onAuthenticationFailed();
            }
        };
        BiometricPrompt biometricPrompt = new BiometricPrompt(ActivityAuthorization.this, executor, authenticationCallback);
        biometricPrompt.authenticate(promptInfo);
    }


    private void start(){
        switch (mode){
            case 0:
                startModeAuthorization();
                break;
            case 1:
                startModeChange();
                break;
            case 2:
                startModeSetting();
                break;
        }
    }
    private void startModeAuthorization(){
        new DecryptWithCompare(this).execute(pin);
    }
    private void startModeChange(){
        new DecryptWithCompare(this).execute(pin);
    }
    private void startModeSetting(){
        if (compare.equals("")){
            compare = pin;
            pin = "";
            title.setText("請再輸入一次PIN碼");
            syncPINView();
        }
        else if (!(compare.equals(""))&&compare.equals(pin)){
            new EncryptWithStore(this).execute(pin);
        }
        else {
            pin = "";
            title.setText("輸入PIN碼不符合，請重試");
            syncPINView();
        }
    }

    private void finish(boolean result){
        switch (mode){
            case 0:
                finishModeAuthorization(result);
                break;
            case 1:
                finishModeChange(result);
                break;
        }
    }
    private void finishModeChange(boolean result){
        if (result){
            title.setText("請輸入新PIN碼");
            mode = 2;
            pin = "";
            syncPINView();
        }
        else {
            Vibrator vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(100);
            title.setText("驗證失敗，請重試");
            pin = "";
            syncPINView();
        }
    }
    private void finishModeAuthorization(boolean result){
        if (result){
            Toast.makeText(this, "驗證成功", Toast.LENGTH_SHORT).show();
            finish();
        }
        else {
            Vibrator vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(100);
            title.setText("驗證失敗，請重試");
            pin = "";
            syncPINView();
        }
    }

    private static class DecryptWithCompare extends AsyncTask<String, Void, Boolean>{
        private WeakReference<ActivityAuthorization> reference;

        public DecryptWithCompare(ActivityAuthorization activityAuthorization){
            reference = new WeakReference<ActivityAuthorization>(activityAuthorization);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            ActivityAuthorization activityAuthorization = reference.get();
            if (activityAuthorization==null||activityAuthorization.isDestroyed()) return;

            updateViewPrepare(activityAuthorization);
        }
        @Override
        protected Boolean doInBackground(String... strings) {
            String pin = decrypt();
            if (pin!=null) return pin.equals(strings[0]);
            else return false;

        }
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            ActivityAuthorization activityAuthorization = reference.get();
            if (activityAuthorization==null||activityAuthorization.isDestroyed()) return;

            updateViewFinish(activityAuthorization, aBoolean);
        }

        private String decrypt(){
            ActivityAuthorization activityAuthorization = reference.get();
            if (activityAuthorization==null||activityAuthorization.isDestroyed()) return null;

            SharedPreferences lock = activityAuthorization.getSharedPreferences("lock",MODE_PRIVATE);
            KeyStoreHelper keyStoreHelper = new KeyStoreHelper(activityAuthorization);
            AESHelper aesHelper = new AESHelper();

            String[] secret = null;
            try {
                secret = new String[]{keyStoreHelper.getKey(), keyStoreHelper.getIv()};
            } catch (UnrecoverableKeyException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            }
            if (secret==null) return null;
            return aesHelper.decrypt(Base64.decode(lock.getString("pin",""), Base64.NO_WRAP), secret[0], secret[1]);
        }

        private void updateViewPrepare(ActivityAuthorization activityAuthorization){
            activityAuthorization.loading.show();
        }
        private void updateViewFinish(ActivityAuthorization activityAuthorization, boolean result){
            activityAuthorization.loading.dismiss();
            activityAuthorization.finish(result);
        }
    }
    private static class EncryptWithStore extends AsyncTask<String, Void, Boolean>{
        private WeakReference<ActivityAuthorization> reference;

        public EncryptWithStore(ActivityAuthorization activityAuthorizationac){
            reference = new WeakReference<ActivityAuthorization>(activityAuthorizationac);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            ActivityAuthorization activityAuthorization = reference.get();
            if (activityAuthorization==null||activityAuthorization.isDestroyed()) return;

            updateViewPrepare(activityAuthorization);
        }
        @Override
        protected Boolean doInBackground(String... strings) {
            return store(encrypt(strings[0]));
        }
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            ActivityAuthorization activityAuthorization = reference.get();
            if (activityAuthorization==null||activityAuthorization.isDestroyed()) return;

            if (aBoolean) updateViewSuccessful(activityAuthorization);
            else updateViewFailed(activityAuthorization);
        }


        private String encrypt(String pin){
            ActivityAuthorization activityAuthorization = reference.get();
            if (activityAuthorization==null||activityAuthorization.isDestroyed()) return null;

            KeyStoreHelper keyStoreHelper = new KeyStoreHelper(activityAuthorization);
            AESHelper aesHelper = new AESHelper();

            String[] secret = null;
            try {
                secret = new String[]{keyStoreHelper.getKey(), keyStoreHelper.getIv()};
            } catch (UnrecoverableKeyException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            }

            if (secret!=null){
                String encrypted = Base64.encodeToString(aesHelper.encrypt(pin, secret[0], secret[1]), Base64.NO_WRAP);
                return encrypted;
            }

            return null;
        }
        private boolean store(String encrypted){
            ActivityAuthorization activityAuthorization = reference.get();
            if (activityAuthorization==null||activityAuthorization.isDestroyed()) return false;

            if (encrypted!=null){
                SharedPreferences lock = activityAuthorization.getSharedPreferences("lock",MODE_PRIVATE);
                lock.edit().putString("pin", encrypted).commit();
                return true;
            }

            return false;
        }

        private void updateViewPrepare(ActivityAuthorization activityAuthorization){
            activityAuthorization.loading.show();
        }
        private void updateViewSuccessful(ActivityAuthorization activityAuthorization){
            activityAuthorization.setResult(Activity.RESULT_OK);
            activityAuthorization.loading.dismiss();
            activityAuthorization.finish();
        }
        private void updateViewFailed(ActivityAuthorization activityAuthorization){
            activityAuthorization.setResult(2);
            activityAuthorization.loading.dismiss();
            activityAuthorization.finish();
        }
    }

    private boolean isSupportFingerprint(){
        if (biometricManager!=null){
            if (biometricManager.canAuthenticate()==BiometricManager.BIOMETRIC_SUCCESS) return true;
        }
        return false;
    }
    private boolean isAboveApi23(){
        return Build.VERSION.SDK_INT>=Build.VERSION_CODES.M;
    }
    private boolean hasEnrolledFingerprints(){
        if (biometricManager!=null){
            if (!(biometricManager.canAuthenticate()==BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED)) return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (mode!=0) super.onBackPressed();
    }
}
