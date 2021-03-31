package com.fly.tkuilife.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.fly.tkuilife.R;
import com.fly.tkuilife.fragment.FragmentCurriculum;
import com.fly.tkuilife.fragment.FragmentGrade;
import com.fly.tkuilife.fragment.FragmentHome;
import com.fly.tkuilife.fragment.FragmentNews;
import com.fly.tkuilife.fragment.FragmentOther;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ActivityMain extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private FragmentManager fragmentManager;
    private FragmentHome fragmentHome;
    private FragmentCurriculum fragmentCurriculum;
    private FragmentGrade fragmentGrade;
    private FragmentNews fragmentNews;
    private FragmentOther fragmentOther;

    private BottomNavigationView nav;


    private long lastBackPressed;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.main_nav_home:
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right, R.anim.slide_in_left, R.anim.slide_out_left)
                        .show(fragmentHome)
                        .hide(fragmentCurriculum)
                        .hide(fragmentGrade)
                        .hide(fragmentNews)
                        .hide(fragmentOther)
                        .commit();
                break;
            case R.id.main_nav_curriculum:
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right, R.anim.slide_in_left, R.anim.slide_out_left)
                        .hide(fragmentHome)
                        .show(fragmentCurriculum)
                        .hide(fragmentGrade)
                        .hide(fragmentNews)
                        .hide(fragmentOther)
                        .commit();
                break;
            case R.id.main_nav_grade:
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right, R.anim.slide_in_left, R.anim.slide_out_left)
                        .hide(fragmentHome)
                        .hide(fragmentCurriculum)
                        .show(fragmentGrade)
                        .hide(fragmentNews)
                        .hide(fragmentOther)
                        .commit();
                break;
            case R.id.main_nav_news:
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right, R.anim.slide_in_left, R.anim.slide_out_left)
                        .hide(fragmentHome)
                        .hide(fragmentCurriculum)
                        .hide(fragmentGrade)
                        .show(fragmentNews)
                        .hide(fragmentOther)
                        .commit();
                break;
            case R.id.main_nav_other:
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right, R.anim.slide_in_left, R.anim.slide_out_left)
                        .hide(fragmentHome)
                        .hide(fragmentCurriculum)
                        .hide(fragmentGrade)
                        .hide(fragmentNews)
                        .show(fragmentOther)
                        .commit();
                break;
        }
        return true;
    }
    @Override
    public void onBackPressed() {
        long pressedtime = System.currentTimeMillis();
        if (pressedtime-lastBackPressed>2000){
            Toast.makeText(this, "再按一次後退鍵退出本應用", Toast.LENGTH_SHORT).show();
            lastBackPressed = pressedtime;
        }
        else {
            finish();
        }
    }

    private void init(){
        initView();
        initBottomNavigationView();
        initFragment();
        initInteraction();
        initOther();
    }
    private void initView(){
        setContentView(R.layout.activity_main);
        nav = (BottomNavigationView) findViewById(R.id.main_nav);
    }
    private void initBottomNavigationView(){
        nav.setSelectedItemId(R.id.main_nav_home);
    }
    private void initInteraction(){
        nav.setOnNavigationItemSelectedListener(this);
    }
    private void initFragment(){
        fragmentManager = getSupportFragmentManager();
        fragmentHome = new FragmentHome();
        fragmentCurriculum = new FragmentCurriculum();
        fragmentGrade = new FragmentGrade();
        fragmentNews = new FragmentNews();
        fragmentOther = new FragmentOther();
        fragmentManager.beginTransaction()
                .add(R.id.main_fragment, fragmentHome)
                .show(fragmentHome)
                .add(R.id.main_fragment, fragmentCurriculum)
                .hide(fragmentCurriculum)
                .add(R.id.main_fragment, fragmentGrade)
                .hide(fragmentGrade)
                .add(R.id.main_fragment, fragmentNews)
                .hide(fragmentNews)
                .add(R.id.main_fragment, fragmentOther)
                .hide(fragmentOther)
                .commit();
    }
    private void initOther(){
        lastBackPressed = 0;
    }

    private void authorizate(){
        SharedPreferences lock = getSharedPreferences("lock", MODE_PRIVATE);
        if (lock.getBoolean("lock", false)) startActivity(new Intent(this, ActivityAuthorization.class).putExtra("mode",0));
    }

    public void refreshLogin(boolean loaded){
        fragmentCurriculum.setLoaded(loaded);
        fragmentGrade.setLoaded(loaded);
        fragmentOther.setLoaded(loaded);
    }


}
