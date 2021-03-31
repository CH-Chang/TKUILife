package com.fly.tkuilife.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
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

import java.lang.ref.WeakReference;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.List;

public class ActivityEdit extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private TextView id;
    private EditText name;
    private TextView status, college, department, year;
    private Button edit;
    private Dialog loading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        loading();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.edit_edit:
                if(isAllFilledIn()){
                    edit();
                }
                else {
                    Toast.makeText(this, "請確認所有欄位皆正確填寫完畢", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.edit_status:
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
            case R.id.edit_year:
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
            case R.id.edit_college:
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
            case R.id.edit_department:
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init(){
        initView();
        initActionBar();
        initLoadingDialog();
        initInteraction();
    }
    private void initView(){
        setContentView(R.layout.activity_edit);
        toolbar = findViewById(R.id.edit_toolbar);
        id = findViewById(R.id.edit_id);
        name = findViewById(R.id.edit_name);
        status = findViewById(R.id.edit_status);
        college = findViewById(R.id.edit_college);
        department= findViewById(R.id.edit_department);
        year = findViewById(R.id.edit_year);
        edit = findViewById(R.id.edit_edit);
    }
    private void initActionBar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }
    private void initLoadingDialog(){
        loading = new Dialog(this, R.style.loadingDialog);
        loading.setContentView(R.layout.layout_loadingdialog);
        loading.setCancelable(false);
    }
    private void initInteraction(){
        edit.setOnClickListener(this);
        status.setOnClickListener(this);
        year.setOnClickListener(this);
        college.setOnClickListener(this);
        department.setOnClickListener(this);
    }

    private void edit(){
        String[] data = new String[]{
                name.getText().toString(),
                status.getText().toString(),
                year.getText().toString(),
                college.getText().toString(),
                department.getText().toString()
        };
        new Edit(this).execute(data);
    }
    private void loading(){
        new Loading(this).execute();
    }

    private boolean isAllFilledIn(){
        if (name.getText().toString().isEmpty()) return false;
        if (status.getText().toString().equals("請點擊此處選擇")) return false;
        if (year.getText().toString().equals("請點擊此處選擇")) return false;
        if (college.getText().toString().equals("請點擊此處選擇")) return false;
        if (department.getText().toString().equals("請點擊此處選擇")) return false;
        return true;
    }

    private static class Loading extends AsyncTask<Void, Void, String[]>{
        private WeakReference<ActivityEdit> reference;

        public Loading(ActivityEdit activityEdit){
            reference = new WeakReference<ActivityEdit>(activityEdit);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            ActivityEdit activityEdit = reference.get();
            if (activityEdit==null||activityEdit.isDestroyed()) return;

            updateViewPrepare(activityEdit);
        }
        @Override
        protected String[] doInBackground(Void... voids) {
            return decrypt();
        }
        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);

            ActivityEdit activityEdit = reference.get();
            if (activityEdit==null||activityEdit.isDestroyed()) return;

            if (strings!=null) updateViewSuccessful(activityEdit, strings);
            else updateViewFailed(activityEdit);
        }

        private String[] decrypt(){
            ActivityEdit activityEdit = reference.get();
            if (activityEdit==null||activityEdit.isDestroyed()) return null;

            SharedPreferences account = activityEdit.getSharedPreferences("account", MODE_PRIVATE);
            KeyStoreHelper keyStoreHelper = new KeyStoreHelper(activityEdit);
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

            String[] res = {
                    aesHelper.decrypt(Base64.decode(account.getString("id",""),Base64.NO_WRAP), secret[0], secret[1]),
                    aesHelper.decrypt(Base64.decode(account.getString("name",""),Base64.NO_WRAP), secret[0], secret[1]),
            };

            return res;
        }

        private void updateViewPrepare(ActivityEdit activityEdit){
            activityEdit.loading.show();
        }
        private void updateViewSuccessful(ActivityEdit activityEdit, String[] strings){
            activityEdit.id.setText("學號 - " + strings[0]);
            activityEdit.name.setHint(strings[1]);
            activityEdit.loading.dismiss();
        }
        private void updateViewFailed(ActivityEdit activityEdit){
            activityEdit.loading.dismiss();
            Toast.makeText(activityEdit, "讀取帳戶資訊失敗", Toast.LENGTH_SHORT).show();
        }
    }
    private static class Edit extends AsyncTask<String[], Void, Boolean>{
        private WeakReference<ActivityEdit> reference;

        public Edit(ActivityEdit activityEdit){
            reference = new WeakReference<ActivityEdit>(activityEdit);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            ActivityEdit activityEdit = reference.get();
            if (activityEdit==null||activityEdit.isDestroyed()) return;

            updateViewPrepare(activityEdit);
        }
        @Override
        protected Boolean doInBackground(String[]... strings) {
            return store(strings[0]);
        }
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            ActivityEdit activityEdit = reference.get();
            if (activityEdit==null||activityEdit.isDestroyed()) return;

            if (aBoolean) updateViewSuccessful(activityEdit);
            else updateViewFailed(activityEdit);
        }

        private boolean store(String[] strings){
            ActivityEdit activityEdit = reference.get();
            if (activityEdit==null||activityEdit.isDestroyed()) return false;

            SharedPreferences account = activityEdit.getSharedPreferences("account", MODE_PRIVATE);
            KeyStoreHelper keyStoreHelper = new KeyStoreHelper(activityEdit);
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
            if (secret==null) return false;

            account.edit()
                    .putString("name", Base64.encodeToString(aesHelper.encrypt(strings[0], secret[0], secret[1]), Base64.NO_WRAP))
                    .putString("college", Base64.encodeToString(aesHelper.encrypt(strings[3], secret[0], secret[1]), Base64.NO_WRAP))
                    .putString("department", Base64.encodeToString(aesHelper.encrypt(strings[4], secret[0], secret[1]), Base64.NO_WRAP))
                    .putString("status", Base64.encodeToString(aesHelper.encrypt(strings[1], secret[0], secret[1]), Base64.NO_WRAP))
                    .putString("year", Base64.encodeToString(aesHelper.encrypt(strings[2], secret[0], secret[1]), Base64.NO_WRAP))
                    .commit();

            return true;
        }

        private void updateViewPrepare(ActivityEdit activityEdit){
            activityEdit.loading.show();
        }
        private void updateViewSuccessful(ActivityEdit activityEdit){
            activityEdit.loading.dismiss();
            activityEdit.setResult(Activity.RESULT_OK);
            activityEdit.finish();
        }
        private void updateViewFailed(ActivityEdit activityEdit){
            activityEdit.loading.dismiss();
            activityEdit.setResult(2);
            activityEdit.finish();
        }

    }
}
