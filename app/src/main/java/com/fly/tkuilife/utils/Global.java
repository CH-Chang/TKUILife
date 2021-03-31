package com.fly.tkuilife.utils;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.fly.tkuilife.activity.ActivityAuthorization;

public class Global extends Application {

    private static Context context;
    private long startforeGround, startBackground;

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init(){
        initContext();
        initBackFrontListener();
    }
    private void initContext(){
        this.context = getApplicationContext();
    }
    private void initBackFrontListener(){
        AppFrontBackHelper appFrontBackHelper = new AppFrontBackHelper();
        appFrontBackHelper.register(this, new AppFrontBackHelper.OnAppStatusListener() {
            @Override
            public void onFront() {
                startforeGround = System.currentTimeMillis();
                authorizate();
            }

            @Override
            public void onBack() {
                startBackground = System.currentTimeMillis();
            }
        });
    }
    private void authorizate(){
        SharedPreferences lock = getSharedPreferences("lock",MODE_PRIVATE);
        if (startforeGround-startBackground>5000){
            if (lock.getBoolean("lock", false)){
                startActivity(new Intent(this, ActivityAuthorization.class).putExtra("mode",0).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        }
    }

    public static Context getContext(){
        return context;
    }
}
