package com.fly.tkuilife.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.biometric.BiometricManager;

import com.fly.tkuilife.R;

public class ActivityLock extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private LinearLayout biometric, editpin;
    private Switch biometric_switch, lock_switch;

    private BiometricManager biometricManager;

    private SharedPreferences lock;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        loading();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==0){
            if (resultCode==-1) Toast.makeText(this, "PIN碼設置成功", Toast.LENGTH_SHORT).show();
            else if (requestCode==-2) Toast.makeText(this, "PIN碼設置失敗", Toast.LENGTH_SHORT).show();
            loading();
        }
        else if (requestCode==1){
            if (resultCode==-1) Toast.makeText(this, "PIN碼更改成功", Toast.LENGTH_SHORT).show();
            else if (requestCode==-2) Toast.makeText(this, "PIN碼更改失敗", Toast.LENGTH_SHORT).show();
            loading();
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.lock_editpin:
                startActivityForResult(new Intent(ActivityLock.this, ActivityAuthorization.class).putExtra("mode",1),1);
                break;
            case R.id.lock_biometric_switch:
                if (lock.getBoolean("lock",false)){
                    lock.edit().putBoolean("biometric", biometric_switch.isChecked()).commit();
                }
                else {
                    biometric_switch.setChecked(false);
                    Toast.makeText(this, "請先啟用應用程式鎖", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.lock_lock_switch:
                if (lock.getString("pin","").equals("")){
                    lock_switch.setChecked(false);
                    Toast.makeText(this, "請先新增PIN鎖", Toast.LENGTH_SHORT).show();
                    startActivityForResult(new Intent(ActivityLock.this, ActivityAuthorization.class).putExtra("mode",2), 0);
                }
                else {
                    lock.edit().putBoolean("lock", lock_switch.isChecked()).commit();
                }
                break;
        }
    }

    private void init(){
        initView();
        initInstantation();
        initSharedPreferences();
        initActionBar();
        initInteraction();
    }
    private void initView(){
        setContentView(R.layout.activity_lock);
        toolbar = findViewById(R.id.lock_toolbar);
        biometric = findViewById(R.id.lock_biometric);
        editpin = findViewById(R.id.lock_editpin);
        biometric_switch = findViewById(R.id.lock_biometric_switch);
        lock_switch = findViewById(R.id.lock_lock_switch);
    }
    private void initActionBar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    private void initInteraction(){
        lock_switch.setOnClickListener(this);
        biometric_switch.setOnClickListener(this);
        editpin.setOnClickListener(this);
    }
    private void initInstantation(){
        biometricManager = BiometricManager.from(this);
    }
    private void initSharedPreferences(){
        lock = getSharedPreferences("lock",MODE_PRIVATE);
    }

    private void loading(){
        loadingLock();
        loadingPIN();
        loadingFingerprint();
    }
    private void loadingFingerprint(){
        if (isAboveApi23()){
            if (isSupportFingerprint()){
                if (!hasEnrolledFingerprints()){
                    biometric_switch.setChecked(false);
                    biometric_switch.setEnabled(false);
                }
                else{
                    biometric_switch.setChecked(lock.getBoolean("biometric", false));
                }
            }
            else {
                biometric.setVisibility(View.GONE);
            }
        }
        else {
            biometric.setVisibility(View.GONE);
        }
    }
    private void loadingLock(){
        if (lock.getBoolean("lock", false)) lock_switch.setChecked(true);
        else lock_switch.setChecked(false);

    }
    private void loadingPIN(){
        if (lock.getString("pin","").equals("")) editpin.setVisibility(View.GONE);
        else editpin.setVisibility(View.VISIBLE);
    }


    private boolean isSupportFingerprint(){
        if (biometricManager!=null){
            if (biometricManager.canAuthenticate()==BiometricManager.BIOMETRIC_SUCCESS) return true;
        }
        return false;
    }
    private boolean hasEnrolledFingerprints(){
        if (biometricManager!=null){
            if (!(biometricManager.canAuthenticate()==BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED)) return true;
        }
        return false;
    }
    private boolean isAboveApi23(){
        return Build.VERSION.SDK_INT>=Build.VERSION_CODES.M;
    }
    private boolean isAboveApi28(){
        return Build.VERSION.SDK_INT>=Build.VERSION_CODES.P;
    }



}
