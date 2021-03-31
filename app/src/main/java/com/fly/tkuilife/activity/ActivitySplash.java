package com.fly.tkuilife.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;

import com.fly.tkuilife.R;

import java.io.IOException;
import java.util.ArrayList;

public class ActivitySplash extends AppCompatActivity {

    private TextView title, subtitle, copyright;
    private ImageView icon;

    private Handler handler;
    private Runnable splah;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        checkPermission();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==0){
            boolean granted = true;
            for(int grantResult : grantResults) if(grantResult==-1) granted=false;
            if(granted) splash();
            else finish();
        }
    }
    @Override
    protected void onDestroy() {
        handler.removeCallbacks(splah);
        super.onDestroy();
    }

    private void init(){
        initinstantiate();
        initView();
        initTheme();
        initAnim();
        initSplash();
    }
    private void initinstantiate(){
        handler = new Handler();
    }
    private void initTheme(){
        SharedPreferences common = getSharedPreferences("common", MODE_PRIVATE);
        if (common.getInt("theme",0)==1) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        else if (common.getInt("theme",0)==2) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }
    private void initView(){
        setContentView(R.layout.activity_splash);
        title = (TextView) findViewById(R.id.splash_title);
        subtitle = (TextView) findViewById(R.id.splash_subtitle);
        copyright = (TextView) findViewById(R.id.splash_copyright);
        icon = (ImageView) findViewById(R.id.splash_icon);
    }
    private void initAnim(){
        Animation fade_in_bottom = AnimationUtils.loadAnimation(this, R.anim.fade_in_bottom);
        title.startAnimation(fade_in_bottom);
        subtitle.startAnimation(fade_in_bottom);
        copyright.startAnimation(fade_in_bottom);
        icon.startAnimation(fade_in_bottom);

    }
    private void initSplash(){
        splah = new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(ActivitySplash.this, ActivityMain.class));
                finish();
            }
        };
    }

    private void checkPermission(){
        ArrayList<String> requestPermission = new ArrayList<>();

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED) requestPermission.add(Manifest.permission.ACCESS_FINE_LOCATION);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED) requestPermission.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE)!=PackageManager.PERMISSION_GRANTED) requestPermission.add(Manifest.permission.ACCESS_NETWORK_STATE);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET)!=PackageManager.PERMISSION_GRANTED) requestPermission.add(Manifest.permission.INTERNET);


        if(requestPermission.size()==0) splash();
        else ActivityCompat.requestPermissions(this, requestPermission.toArray(new String[requestPermission.size()]), 0);
    }

    private void splash(){
        handler.postDelayed(splah, 2000);
    }


}
