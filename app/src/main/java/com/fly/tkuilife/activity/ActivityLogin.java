package com.fly.tkuilife.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.fly.tkuilife.R;
import com.fly.tkuilife.utils.AESHelper;
import com.fly.tkuilife.utils.KeyStoreHelper;

import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ActivityLogin extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private EditText name;
    private TextView status, college, department, year;
    private Button connect;
    private WebView webView;
    private Dialog loadingDialog;

    private SharedPreferences account, common;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initActionBar();
        initSharePreference();
        initLoadingDialog();
        initInteraction();
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

    private void initView(){
        setContentView(R.layout.activity_login);
        toolbar = (Toolbar)findViewById(R.id.login_toolbar);
        name = (EditText) findViewById(R.id.login_name);
        status = (TextView) findViewById(R.id.login_status);
        college = (TextView) findViewById(R.id.login_college);
        department = (TextView) findViewById(R.id.login_department);
        year = (TextView) findViewById(R.id.login_year);
        connect = (Button) findViewById(R.id.login_connect);
        webView = (WebView) findViewById(R.id.login_webview);
    }
    private void initActionBar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    private void initSharePreference(){
        account = getSharedPreferences("account",MODE_PRIVATE);
        common = getSharedPreferences("common", MODE_PRIVATE);
    }
    private void initLoadingDialog(){
        loadingDialog = new Dialog(this, R.style.loadingDialog);
        loadingDialog.setContentView(R.layout.layout_loadingdialog);
        loadingDialog.setCancelable(false);
    }
    private void initInteraction(){
        connect.setOnClickListener(this);
        status.setOnClickListener(this);
        year.setOnClickListener(this);
        college.setOnClickListener(this);
        department.setOnClickListener(this);
    }
    


    private void loadingWebview(){
        WebSettings webSettings = webView.getSettings();
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient());
        webView.addJavascriptInterface(new JsCallback(this), "ExtObj");
        webView.loadUrl("https://sso.tku.edu.tw/ilife/CoWork/AndroidSsoLogin.cshtml");
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login_connect:
                if(isAllFilledIn()){
                    webView.setVisibility(View.VISIBLE);
                    loadingWebview();
                }
                else {
                    Toast.makeText(this, "請確認所有欄位皆正確填寫完畢", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.login_status:
                final String statuses [] = {"學生","教授"};
                new AlertDialog.Builder(this)
                        .setItems(statuses, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                status.setText(statuses[which]);
                            }
                        })
                        .setTitle("請選擇身分")
                        .show();
                break;
            case R.id.login_year:
                final String[] years;
                if (status.getText().equals("教授")) years = new String[]{"教授"};
                else years = new String[]{"大學一年級","大學二年級","大學三年級","大學四年級","大學五年級","大學六年級"};
                new AlertDialog.Builder(this)
                        .setTitle("請選擇年級")
                        .setItems(years, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                year.setText(years[which]);
                            }
                        })
                        .show();
                break;
            case R.id.login_college:
                final String colleges[] = {"文學院","理學院","工學院","商管學院","外國語文學院","國際事務學院","教育學院","全球發展學院"};
                new AlertDialog.Builder(this)
                        .setTitle("請選擇學院")
                        .setItems(colleges, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                college.setText(colleges[which]);
                            }
                        })
                        .show();
                break;
            case R.id.login_department:
                final String[] departments;
                if(college.getText().equals("文學院")) departments = new String[]{"中國語文學系", "歷史學系", "資訊與圖書館學系", "大眾傳播學系", "資訊傳播學系"};
                else if(college.getText().equals("理學院")) departments = new String[]{"數學學系", "物理學系", "化學學系", "尖端材料科學學士學位學程"};
                else if(college.getText().equals("工學院")) departments = new String[]{"建築學系", "土木工程學系", "水資源與環境工程學系", "機械與機電工程學系", "化學工程與材料工程學系", "電機工程學系", "資訊工程學系", "航空太空工程學系"};
                else if(college.getText().equals("商管學院")) departments = new String[]{"國際企業學系", "財務金融學系", "風險管理與保險學系", "產業經濟學系", "經濟學系", "企業管理學系", "會計學系", "統計學系", "資訊管理學系", "運輸管理學系","公共行政學系", "管理科學學系"};
                else if(college.getText().equals("外國語文學院")) departments = new String[]{"英文學系","西班牙語文學系","法國語文學系","德國語文學系","日本語文學系","俄國語文學系"};
                else if(college.getText().equals("國際事務學院")) departments = new String[]{"外交與國際關係學系全英語學士班"};
                else if(college.getText().equals("教育學院")) departments = new String[]{"教育科技學系"};
                else departments = new String[]{"資訊創新與科技學系","國際觀光管理學系全英語學士班","英美語言文化學系全英語學士班","全球政治經濟學系全英語學士班"};
                new AlertDialog.Builder(this)
                        .setTitle("請選擇系所")
                        .setItems(departments, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                department.setText(departments[which]);
                            }
                        })
                        .show();
                break;
        }
    }
    @Override
    public void onBackPressed() {
        if(webView.getVisibility()==View.VISIBLE) webView.setVisibility(View.GONE);
        else finish();
    }

    private boolean isAllFilledIn(){
        if (name.getText().toString().isEmpty()) return false;
        if (status.getText().toString().equals("請點擊此處選擇")) return false;
        if (year.getText().toString().equals("請點擊此處選擇")) return false;
        if (college.getText().toString().equals("請點擊此處選擇")) return false;
        if (department.getText().toString().equals("請點擊此處選擇")) return false;
        return true;
    }

    private class JsCallback{
        private Activity activity;
        public JsCallback(Activity activity){
            this.activity = activity;
        }
        @JavascriptInterface
        public void responseResult(String sso_id){
            DateFormat formator = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            if(sso_id.length()>0){
                ActivityLogin.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadingDialog.show();
                    }
                });

                KeyStoreHelper keyStoreHelper = new KeyStoreHelper(ActivityLogin.this);
                AESHelper aesHelper = new AESHelper();
                try {
                    String[] secret = new String[]{keyStoreHelper.getKey(), keyStoreHelper.getIv()};
                    account.edit()
                            .putString("id", Base64.encodeToString(aesHelper.encrypt(sso_id, secret[0], secret[1]), Base64.NO_WRAP))
                            .putString("name", Base64.encodeToString(aesHelper.encrypt(String.valueOf(name.getText()), secret[0], secret[1]), Base64.NO_WRAP))
                            .putString("college", Base64.encodeToString(aesHelper.encrypt((String)college.getText(), secret[0], secret[1]), Base64.NO_WRAP))
                            .putString("department", Base64.encodeToString(aesHelper.encrypt((String)department.getText(), secret[0], secret[1]), Base64.NO_WRAP))
                            .putString("status", Base64.encodeToString(aesHelper.encrypt((String)status.getText(), secret[0], secret[1]), Base64.NO_WRAP))
                            .putString("year", Base64.encodeToString(aesHelper.encrypt((String)year.getText(), secret[0], secret[1]), Base64.NO_WRAP))
                            .putString("university", Base64.encodeToString(aesHelper.encrypt("淡江大學", secret[0], secret[1]), Base64.NO_WRAP))
                            .putString("pk", Base64.encodeToString(aesHelper.encrypt(formator.format(new Date(System.currentTimeMillis())), secret[0], secret[1]), Base64.NO_WRAP))
                            .commit();
                    common.edit()
                            .putBoolean("login", true)
                            .commit();
                } catch (UnrecoverableKeyException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (KeyStoreException e) {
                    e.printStackTrace();
                }


                ActivityLogin.this.setResult(RESULT_OK, getIntent());
                ActivityLogin.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadingDialog.dismiss();
                        ActivityLogin.this.webView.loadUrl("https://sso.tku.edu.tw/pkmslogout");
                        finish();
                    }
                });
            }
        }
    }
    private class WebViewClient extends android.webkit.WebViewClient{
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            view.loadUrl("javascript:window.ExtObj.responseResult(getSsoLoginToken())");
        }
    }
}
