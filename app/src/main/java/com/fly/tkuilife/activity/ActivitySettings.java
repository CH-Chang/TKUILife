package com.fly.tkuilife.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.fly.tkuilife.R;

public class ActivitySettings extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private LinearLayout language, theme;
    private TextView language_show, theme_show;

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
            case R.id.settings_language:
                final String[] languages = new String[]{"繁體中文"};
                new AlertDialog.Builder(this)
                        .setTitle("請選擇語言")
                        .setItems(languages, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences common = getSharedPreferences("common", MODE_PRIVATE);
                                common.edit().putString("language", languages[which]).commit();
                                loadingLanguage();
                                Toast.makeText(ActivitySettings.this, "請重新啟動本應用以套用語言", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
                break;
            case R.id.settings_theme:
                String[] themes = new String[]{"根據系統設置","淺色主題","深色主題"};
                new AlertDialog.Builder(this)
                        .setItems(themes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences common = getSharedPreferences("common", MODE_PRIVATE);
                                common.edit().putInt("theme", which).commit();
                                loadingTheme();
                                Toast.makeText(ActivitySettings.this, "請重新啟動本應用以套用主題", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setTitle("選擇主題")
                        .show();
                break;
        }
    }

    private void init(){
        initView();
        initActionBar();
        initInteraction();
    }
    private void initView(){
        setContentView(R.layout.activity_settings);
        toolbar = findViewById(R.id.settings_toolbar);
        language = findViewById(R.id.settings_language);
        theme = findViewById(R.id.settings_theme);
        language_show = findViewById(R.id.settings_languageshow);
        theme_show = findViewById(R.id.settings_themeshow);
    }
    private void initActionBar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    private void initInteraction(){
        theme.setOnClickListener(this);
        language.setOnClickListener(this);
    }

    private void loading(){
        loadingTheme();
        loadingLanguage();
    }
    private void loadingTheme(){
        SharedPreferences common = getSharedPreferences("common", MODE_PRIVATE);

        if (common.getInt("theme", 0)==0) theme_show.setText("根據系統設置");
        else if (common.getInt("theme",0)==1) theme_show.setText("淺色主題");
        else theme_show.setText("深色主題");
    }
    private void loadingLanguage(){
        SharedPreferences common = getSharedPreferences("common", MODE_PRIVATE);

        language_show.setText(common.getString("language","繁體中文"));
    }



}
