package com.fly.tkuilife.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.fly.tkuilife.R;

public class ActivityAbout extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private LinearLayout tku, cwb, ptx, team;
    private TextView device;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        loading();
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
            case R.id.about_tku:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.tku.edu.tw/")));
                break;
            case R.id.about_cwb:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.cwb.gov.tw/V8/C/")));
                break;
            case R.id.about_ptx:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://ptx.transportdata.tw/PTX/")));
                break;
            case R.id.about_team:
                startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:hew12233@gms.tku.edu.tw")).putExtra(Intent.EXTRA_SUBJECT,"淡江i生活意見反饋"));
                break;
        }
    }

    private void init(){
        initView();
        initActionBar();
        initInteraction();
    }
    private void initView(){
        setContentView(R.layout.activity_about);
        toolbar = findViewById(R.id.about_toolbar);
        tku = findViewById(R.id.about_tku);
        cwb = findViewById(R.id.about_cwb);
        ptx = findViewById(R.id.about_ptx);
        team = findViewById(R.id.about_team);
        device = findViewById(R.id.about_device);
    }
    private void initActionBar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }
    private void initInteraction(){
        tku.setOnClickListener(this);
        cwb.setOnClickListener(this);
        ptx.setOnClickListener(this);
        team.setOnClickListener(this);
    }

    private void loading(){
        device.setText(Build.BRAND.toUpperCase()+" "+Build.MODEL+" Powered By Android "+Build.VERSION.RELEASE);
    }



}
