package com.fly.tkuilife.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Process;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fly.tkuilife.R;
import com.fly.tkuilife.activity.ActivityAbout;
import com.fly.tkuilife.activity.ActivityCalendar;
import com.fly.tkuilife.activity.ActivityContact;
import com.fly.tkuilife.activity.ActivityContrast;
import com.fly.tkuilife.activity.ActivityEdit;
import com.fly.tkuilife.activity.ActivityLibrary;
import com.fly.tkuilife.activity.ActivityLock;
import com.fly.tkuilife.activity.ActivityMap;
import com.fly.tkuilife.activity.ActivityNewsPaper;
import com.fly.tkuilife.activity.ActivitySettings;
import com.fly.tkuilife.activity.ActivitySplash;
import com.fly.tkuilife.activity.ActivityWifi;
import com.fly.tkuilife.utils.AESHelper;
import com.fly.tkuilife.utils.KeyStoreHelper;

import java.lang.ref.WeakReference;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

public class FragmentOther extends Fragment implements View.OnClickListener {

    private View view;
    private TextView name, id;
    private LinearLayout wifi, library, newspaper, map, contact, lock, editpersonaldata, logout, about, contrast, calendar, settings;

    private Dialog loading;

    private boolean loaded;

    private SQLiteDatabase db;
    private Cursor cursor;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_other, container, false);
        init();
        return view;
    }
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden){ }
        else {
            if (!loaded) loading();
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.other_map:
                startActivity(new Intent(getContext(), ActivityMap.class));
                break;
            case R.id.other_contact:
                startActivity(new Intent(getContext(), ActivityContact.class));
                break;
            case R.id.other_lock:
                startActivity(new Intent(getContext(), ActivityLock.class));
                break;
            case R.id.other_editpersonaldata:
                startActivity(new Intent(getContext(), ActivityEdit.class));
                break;
            case R.id.other_logout:
                logout();
                break;
            case R.id.other_library:
                startActivity(new Intent(getContext(), ActivityLibrary.class));
                break;
            case R.id.other_wifi:
                startActivity(new Intent(getContext(), ActivityWifi.class));
                break;
            case R.id.other_newspaper:
                startActivity(new Intent(getContext(), ActivityNewsPaper.class));
                break;
            case R.id.other_about:
                startActivity(new Intent(getContext(), ActivityAbout.class));
                break;
            case R.id.other_contrast:
                startActivity(new Intent(getContext(), ActivityContrast.class));
                break;
            case R.id.other_calendar:
                startActivity(new Intent(getContext(), ActivityCalendar.class));
                break;
            case R.id.other_settings:
                startActivity(new Intent(getContext(), ActivitySettings.class));
        }
    }

    private void init(){
        initView();
        initLoadingDialog();
        initInteraction();
        initDataBases();
        initOther();
    }
    private void initView(){
        name = (TextView) view.findViewById(R.id.other_name);
        id = (TextView) view.findViewById(R.id.other_id);
        wifi = (LinearLayout) view.findViewById(R.id.other_wifi);
        library = (LinearLayout) view.findViewById(R.id.other_library);
        newspaper = (LinearLayout) view.findViewById(R.id.other_newspaper);
        map = (LinearLayout) view.findViewById(R.id.other_map);
        contact = (LinearLayout) view.findViewById(R.id.other_contact);
        lock = (LinearLayout) view.findViewById(R.id.other_lock);
        editpersonaldata = (LinearLayout) view.findViewById(R.id.other_editpersonaldata);
        logout = (LinearLayout) view.findViewById(R.id.other_logout);
        about = (LinearLayout) view.findViewById(R.id.other_about);
        contrast = (LinearLayout) view.findViewById(R.id.other_contrast);
        calendar = (LinearLayout) view.findViewById(R.id.other_calendar);
        settings = (LinearLayout) view.findViewById(R.id.other_settings);
    }
    private void initLoadingDialog(){
        loading = new Dialog(getContext(), R.style.loadingDialog);
        loading.setContentView(R.layout.layout_loadingdialog);
        loading.setCancelable(false);
    }
    private void initInteraction(){
        wifi.setOnClickListener(this);
        library.setOnClickListener(this);
        newspaper.setOnClickListener(this);
        map.setOnClickListener(this);
        contact.setOnClickListener(this);
        lock.setOnClickListener(this);
        editpersonaldata.setOnClickListener(this);
        logout.setOnClickListener(this);
        about.setOnClickListener(this);
        contrast.setOnClickListener(this);
        calendar.setOnClickListener(this);
        settings.setOnClickListener(this);
    }
    private void initDataBases(){
        db = getContext().openOrCreateDatabase("curriculum", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS CURRICULUM (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "NAME VARCHAR(255) NOT NULL, " +
                "TEACHER VARCHAR(50) NOT NULL, " +
                "ROOM VARCHAR(50) NOT NULL, " +
                "SEATNUM INTEGER NOT NULL, " +
                "WEEK INTEGER NOT NULL, " +
                "SESSION INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS COURSECHG(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "DEPARTMENT VARCHAR(80) NOT NULL, " +
                "CURSE VARCHAR(255) NOT NULL, " +
                "TEACHER VARCHAR(50) NOT NULL, " +
                "BEF VARCHAR(255) NOT NULL, " +
                "AFT VARCHAR(255) NOT NULL, " +
                "START DATE NOT NULL, " +
                "TYPE INTEGER NOT NULL, " +
                "ID VARCHAR(50) NOT NULL" +
                ")");
        db.execSQL("CREATE TABLE IF NOT EXISTS EXAM(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "EXAMINEE VARCHAR(50) NOT NULL, " +
                "COURSE VARCHAR(255) NOT NULL, " +
                "REQUIRED INTEGER NOT NULL, " +
                "CREDIT INTEGER NOT NULL, " +
                "SEATNUM INTEGER NOT NULL, " +
                "KIND VARCHAR(50) NOT NULL, " +
                "TYPE VARCHAR(50) NOT NULL, " +
                "DAYTIME DATE, " +
                "SESSION INTEGER, " +
                "ROOM VARCHAR(50), " +
                "EXAMNUM INTEGER, " +
                "EXAMTIME INTEGER NOT NULL)");

    }
    private void initOther(){
        loaded = false;
    }

    private void loading(){
        loadingAccount();
        loadingFunction();
    }
    private void loadingAccount(){
        SharedPreferences common = getContext().getSharedPreferences("common", Context.MODE_PRIVATE);
        if (common.getBoolean("login", false)) new LoadingAccount(this).execute();
        else name.setText("請先登入");
    }
    private void loadingFunction(){
        SharedPreferences common = getContext().getSharedPreferences("common",Context.MODE_PRIVATE);
        if (!common.getBoolean("login",false)){
            logout.setVisibility(View.GONE);
            editpersonaldata.setVisibility(View.GONE);
        }
        else {
            logout.setVisibility(View.VISIBLE);
            editpersonaldata.setVisibility(View.VISIBLE);
        }
    }

    private void logout(){
        new AlertDialog.Builder(getContext())
                .setTitle("提示")
                .setMessage("請問是否登出本帳號")
                .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences account  = getContext().getSharedPreferences("account",Context.MODE_PRIVATE);
                        account.edit().clear().commit();
                        SharedPreferences common = getContext().getSharedPreferences("common",Context.MODE_PRIVATE);
                        common.edit().putBoolean("login", false).commit();
                        db.execSQL("DELETE FROM COURSECHG");
                        db.execSQL("DELETE FROM CURRICULUM");
                        db.execSQL("DELETE FROM EXAM");
                        startActivity(new Intent(getContext(), ActivitySplash.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        Process.killProcess(Process.myPid());
                    }
                })
                .setNegativeButton("取消", null)
                .setCancelable(true)
                .show();
    }

    private static class LoadingAccount extends AsyncTask<Void, Void, String[]>{
        private WeakReference<FragmentOther> reference;

        public LoadingAccount(FragmentOther fragmentOther){
            reference = new WeakReference<FragmentOther>(fragmentOther);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            FragmentOther fragmentOther = reference.get();
            if (fragmentOther==null||fragmentOther.isDetached()) return;

            updateViewPrepare(fragmentOther);
        }
        @Override
        protected String[] doInBackground(Void... voids) {
            return decryptAccount();
        }
        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            FragmentOther fragmentOther = reference.get();
            if (fragmentOther==null||fragmentOther.isDetached()) return;

            if (strings!=null){
                fragmentOther.loaded = true;
                updateViewAccount(fragmentOther, strings);
            }
            else updateViewFailed(fragmentOther);
        }

        private String[] decryptAccount(){
            FragmentOther fragmentOther = reference.get();
            if (fragmentOther==null||fragmentOther.isDetached()) return null;

            SharedPreferences account = fragmentOther.getContext().getSharedPreferences("account", Context.MODE_PRIVATE);
            KeyStoreHelper keyStoreHelper = new KeyStoreHelper(fragmentOther.getContext());
            AESHelper aesHelper = new AESHelper();

            try {
                String secret[] = new String[]{keyStoreHelper.getKey(), keyStoreHelper.getIv()};
                String res[] = new String[]{
                        aesHelper.decrypt(Base64.decode(account.getString("name",""), Base64.NO_WRAP), secret[0], secret[1]),
                        aesHelper.decrypt(Base64.decode(account.getString("id",""), Base64.NO_WRAP), secret[0], secret[1])
                };
                return res;
            } catch (UnrecoverableKeyException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            }
            return null;
        }

        private void updateViewPrepare(FragmentOther fragmentOther){
            fragmentOther.loading.show();
        }
        private void updateViewAccount(FragmentOther fragmentOther, String[] account){
            fragmentOther.name.setText(account[0]);
            fragmentOther.id.setText(account[1]);
            fragmentOther.loading.dismiss();
        }
        private void updateViewFailed(FragmentOther fragmentOther){
            fragmentOther.loading.dismiss();
            Toast.makeText(fragmentOther.getContext(), "讀取帳戶資料錯誤，請聯絡開發人員", Toast.LENGTH_SHORT).show();
        }
    }


    public void setLoaded(boolean loaded){
        this.loaded = loaded;
    }


}
