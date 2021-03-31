package com.fly.tkuilife.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.fly.tkuilife.R;
import com.fly.tkuilife.activity.ActivityBus;
import com.fly.tkuilife.activity.ActivityLogin;
import com.fly.tkuilife.activity.ActivityMain;
import com.fly.tkuilife.adapter.AdapterRecyclerViewCurriculum;
import com.fly.tkuilife.adapter.AdapterRecyclerViewTransportation;
import com.fly.tkuilife.adapter.AdapterViewPager;
import com.fly.tkuilife.bean.BeanCourse;
import com.fly.tkuilife.bean.BeanRoute;
import com.fly.tkuilife.utils.AESHelper;
import com.fly.tkuilife.utils.HmacHelper;
import com.fly.tkuilife.utils.KeyStoreHelper;
import com.fly.tkuilife.utils.RecyclerViewItemClickSupport;
import com.fly.tkuilife.utils.SecretResource;
import com.fly.tkuilife.view.ViewPager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.http.RealResponseBody;
import okio.GzipSource;
import okio.Okio;

public class FragmentHome extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener, RecyclerViewItemClickSupport.OnItemClickListener {

    private View view;
    private ViewPager curriculum, transportation, weather;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Button btnlogin;
    private Dialog loading;

    private Handler handler;

    private boolean firsttime;
    private short[] loaded;

    private LoadingTransportation loadingTransportation;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        init();
        setup();
        refreshStart();
        return view;
    }
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden){
            loadingTransportation.cancel();
            loadingTransportation = null;
        }
        else {
            loadingTransportation = new LoadingTransportation(this);
            loadingTransportation.start();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (loadingTransportation!=null) loadingTransportation.cancel();
        loadingTransportation = null;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==0){
            if (resultCode==-1){
                refreshStart();
                refreshFragment();
            }
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.home_header_btnlogin:
                startActivityForResult(new Intent(getContext(), ActivityLogin.class), 0);
                break;
        }
    }
    @Override
    public void onRefresh() {
        refreshStart();
    }
    @Override
    public void onItemClick(RecyclerView recyclerView, int position, View view) {
        switch (recyclerView.getId()){
            case R.id.home_body_transportation_viewpager_recyclerview:
                AdapterRecyclerViewTransportation adapter = (AdapterRecyclerViewTransportation) recyclerView.getAdapter();
                startActivity(new Intent(getContext(), ActivityBus.class).putExtra("route", adapter.getItem(position).getRoute()).putExtra("city", adapter.getItem(position).getCity()).putExtra("direction", adapter.getItem(position).getDirection()));
                break;
        }
    }

    private void init(){
        initInstantation();
        initView();
        initLoadingDialog();
        initOther();
        initInteraction();
    }
    private void initInstantation(){
        handler = new Handler();
    }
    private void initView(){
        curriculum = view.findViewById(R.id.home_body_curriculum_viewpager);
        transportation = view.findViewById(R.id.home_body_transportation_viewpager);
        weather = view.findViewById(R.id.home_body_weather_viewpager);
        swipeRefreshLayout = view.findViewById(R.id.home_swiperefreshlayout);
        btnlogin = view.findViewById(R.id.home_header_btnlogin);
    }
    private void initLoadingDialog(){
        loading = new Dialog(getContext(), R.style.loadingDialog);
        loading.setContentView(R.layout.layout_loadingdialog);
        loading.setCancelable(false);
    }
    private void initInteraction(){
        swipeRefreshLayout.setOnRefreshListener(this);
        btnlogin.setOnClickListener(this);
    }
    private void initOther(){
        firsttime = true;
        loaded = new short[]{0,0,0,0};
    }

    private void setup(){
        setupCurriculum();
        setupTransportation();
        setupWeather();
    }
    private void setupCurriculum(){
        LinearLayout indicator = view.findViewById(R.id.home_body_curriculum_indicator);

        AdapterViewPager adapter = new AdapterViewPager(getContext(), indicator);
        adapter.addItemView(LayoutInflater.from(getContext()).inflate(R.layout.layout_home_body_curriculum, null));
        adapter.addItemView(LayoutInflater.from(getContext()).inflate(R.layout.layout_home_body_curriculum, null));
        adapter.addItemView(LayoutInflater.from(getContext()).inflate(R.layout.layout_home_body_curriculum, null));
        adapter.addItemView(LayoutInflater.from(getContext()).inflate(R.layout.layout_home_body_curriculum, null));
        adapter.addItemView(LayoutInflater.from(getContext()).inflate(R.layout.layout_home_body_curriculum, null));
        adapter.addItemView(LayoutInflater.from(getContext()).inflate(R.layout.layout_home_body_curriculum, null));
        adapter.addItemView(LayoutInflater.from(getContext()).inflate(R.layout.layout_home_body_curriculum, null));

        curriculum.setAdapter(adapter);

        adapter.initIndicator();
        curriculum.addOnPageChangeListener(new OnPageChangeListener(indicator));

        for(int i=0;i<adapter.getCount();i++) adapter.getItemView(i).findViewById(R.id.home_body_curriculum_viewpager_recyclerview).setNestedScrollingEnabled(false);
    }
    private void setupTransportation(){
        LinearLayout indicator = view.findViewById(R.id.home_body_transportation_indicator);

        AdapterViewPager adapter = new AdapterViewPager(getContext(), indicator);
        adapter.addItemView(LayoutInflater.from(getContext()).inflate(R.layout.layout_home_body_transportation, null));
        adapter.addItemView(LayoutInflater.from(getContext()).inflate(R.layout.layout_home_body_transportation, null));

        transportation.setAdapter(adapter);

        adapter.initIndicator();
        transportation.addOnPageChangeListener(new OnPageChangeListener(indicator));

        for(int i=0;i<adapter.getCount();i++) {
            adapter.getItemView(i).findViewById(R.id.home_body_transportation_viewpager_recyclerview).setNestedScrollingEnabled(false);
            RecyclerViewItemClickSupport.addTo((RecyclerView) adapter.getItemView(i).findViewById(R.id.home_body_transportation_viewpager_recyclerview)).setOnItemClickListener(this);
        }
    }
    private void setupWeather(){
        LinearLayout indicator = view.findViewById(R.id.home_body_weather_indicator);

        AdapterViewPager adapter = new AdapterViewPager(getContext(), indicator);
        adapter.addItemView(LayoutInflater.from(getContext()).inflate(R.layout.layout_home_body_weather_current, null));
        adapter.addItemView(LayoutInflater.from(getContext()).inflate(R.layout.layout_home_body_weather_hour, null));
        adapter.addItemView(LayoutInflater.from(getContext()).inflate(R.layout.layout_home_body_weather_day, null));
        adapter.addItemView(LayoutInflater.from(getContext()).inflate(R.layout.layout_home_body_weather_night, null));

        weather.setAdapter(adapter);

        adapter.initIndicator();
        weather.addOnPageChangeListener(new OnPageChangeListener(indicator));
    }

    private void loading(){
        loadingHeader();
        loadingCurriculum();
        loadingTransportation();
        loadingWeather();
    }
    private void loadingHeader(){
        SharedPreferences common = getContext().getSharedPreferences("common", Context.MODE_PRIVATE);
        LinearLayout classes, login;
        classes = view.findViewById(R.id.home_header_class);
        login = view.findViewById(R.id.home_header_login);
        if (common.getBoolean("login", false)){
            new LoadingHeader(this).execute();
            login.setVisibility(View.GONE);
            classes.setVisibility(View.VISIBLE);
        }
        else {
            TextView greetings, name, status, university, department, year, stuid;

            greetings = view.findViewById(R.id.home_header_greetings);
            name = view.findViewById(R.id.home_header_name);
            status = view.findViewById(R.id.home_header_status);
            university = view.findViewById(R.id.home_header_university);
            department = view.findViewById(R.id.home_header_department);
            year = view.findViewById(R.id.home_header_year);
            stuid = view.findViewById(R.id.home_header_stuid);

            classes.setVisibility(View.GONE);
            login.setVisibility(View.VISIBLE);
            greetings.setText("歡迎");
            name.setText("新同學");
            status.setText("請先登入");
            university.setText("歡迎使用");
            department.setText("淡江i生活");
            year.setText("請登入");
            stuid.setText("請先登入");

            loaded[0] = 1;
        }
    }
    private void loadingWeather(){
        new FetchPostWeather(this).execute();
    }
    private void loadingCurriculum(){
        SharedPreferences common = getContext().getSharedPreferences("common",Context.MODE_PRIVATE);
        if (common.getBoolean("login", false)){
            AdapterViewPager adapter = (AdapterViewPager)curriculum.getAdapter();
            ((TextView) adapter.getItemView(0).findViewById(R.id.home_body_curriculum_viewpager_week)).setText("週一");
            ((TextView) adapter.getItemView(1).findViewById(R.id.home_body_curriculum_viewpager_week)).setText("週二");
            ((TextView) adapter.getItemView(2).findViewById(R.id.home_body_curriculum_viewpager_week)).setText("週三");
            ((TextView) adapter.getItemView(3).findViewById(R.id.home_body_curriculum_viewpager_week)).setText("週四");
            ((TextView) adapter.getItemView(4).findViewById(R.id.home_body_curriculum_viewpager_week)).setText("週五");
            ((TextView) adapter.getItemView(5).findViewById(R.id.home_body_curriculum_viewpager_week)).setText("週六");
            ((TextView) adapter.getItemView(6).findViewById(R.id.home_body_curriculum_viewpager_week)).setText("週日");

            for(int i=0;i<7;i++){
                RecyclerView recyclerView = ((RecyclerView) adapter.getItemView(i).findViewById(R.id.home_body_curriculum_viewpager_recyclerview));
                if(recyclerView.getAdapter()==null) recyclerView.setAdapter(new AdapterRecyclerViewCurriculum());
                if(recyclerView.getLayoutManager()==null) recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            }

            Calendar calendar = Calendar.getInstance();
            int week = calendar.get(Calendar.DAY_OF_WEEK);
            if (week==1) curriculum.setCurrentItem(6);
            else curriculum.setCurrentItem(week-2);

            new FetchPostCurriculum(this).execute();
        }
        else {
            view.findViewById(R.id.home_body_curriculum).setVisibility(View.GONE);
            loaded[1]=1;
        }
    }
    private void loadingTransportation(){
        AdapterViewPager adapter = (AdapterViewPager)transportation.getAdapter();

        ((TextView) adapter.getItemView(0).findViewById(R.id.home_body_transportation_viewpager_destination)).setText("往 淡江大學站");
        ((TextView) adapter.getItemView(1).findViewById(R.id.home_body_transportation_viewpager_destination)).setText("往 捷運淡水站");

        RecyclerView toTKU_recyclerView = adapter.getItemView(0).findViewById(R.id.home_body_transportation_viewpager_recyclerview);
        RecyclerView fromTKU_recyclerView = adapter.getItemView(1).findViewById(R.id.home_body_transportation_viewpager_recyclerview);

        if (toTKU_recyclerView.getAdapter()==null){
            AdapterRecyclerViewTransportation toTKU_adapter = new AdapterRecyclerViewTransportation();
            toTKU_adapter.addItem(new BeanRoute("紅27", "捷運淡水站", "讀取中", "NewTaipei", R.drawable.ic_bus, 1));
            toTKU_adapter.addItem(new BeanRoute("紅28", "捷運淡水站", "讀取中", "NewTaipei", R.drawable.ic_bus, 0));
            toTKU_adapter.addItem(new BeanRoute("紅28直", "捷運淡水站", "讀取中", "NewTaipei", R.drawable.ic_bus, 0));
            toTKU_adapter.addItem(new BeanRoute("756", "捷運淡水站", "讀取中", "Taipei", R.drawable.ic_bus, 1));
            toTKU_recyclerView.setAdapter(toTKU_adapter);
        }
        if (fromTKU_recyclerView.getAdapter()==null){
            AdapterRecyclerViewTransportation fromTKU_adapter = new AdapterRecyclerViewTransportation();
            fromTKU_adapter.addItem(new BeanRoute("紅27", "淡江大學", "讀取中", "NewTaipei", R.drawable.ic_bus, 0));
            fromTKU_adapter.addItem(new BeanRoute("紅28", "淡江大學城", "讀取中", "NewTaipei", R.drawable.ic_bus, 1));
            fromTKU_adapter.addItem(new BeanRoute("紅28直", "淡江大學城", "讀取中", "NewTaipei", R.drawable.ic_bus, 1));
            fromTKU_adapter.addItem(new BeanRoute("756", "淡江大學", "讀取中", "Taipei", R.drawable.ic_bus, 0));
            fromTKU_recyclerView.setAdapter(fromTKU_adapter);
        }

        if (toTKU_recyclerView.getLayoutManager()==null) toTKU_recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        if (fromTKU_recyclerView.getLayoutManager()==null) fromTKU_recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (loadingTransportation==null){
            loadingTransportation = new LoadingTransportation(this);
            loadingTransportation.start();
        }
        else {
            loadingTransportation.cancel();
            loadingTransportation = new LoadingTransportation(this);
            loadingTransportation.start();
        }
    }

    private void refreshFragment(){
        ((ActivityMain)getActivity()).refreshLogin(false);
    }
    private void refreshStart(){
        if (firsttime) loading.show();
        else swipeRefreshLayout.setRefreshing(true);

        loaded[0] = loaded[1] = loaded[2] = loaded[3] = 0;
        loading();
    }
    private void refreshFinish(){
        if (isAllThreadFinish()){
            if (firsttime){
                loading.dismiss();
                firsttime = false;
            }
            else {
                if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    private boolean isAllThreadFinish(){
        if (loaded[0]!=0&&loaded[1]!=0&&loaded[2]!=0&&loaded[3]!=0) return true;
        else return false;
    }




    private static class FetchPostWeather extends AsyncTask<Void, Void, JSONObject[]>{
        private WeakReference<FragmentHome> reference;

        private SecretResource secretResource;
        private OkHttpClient okHttpClient;

        private LinearLayout current, hour, day, night;
        private TextView current_area, current_temperature, current_high, current_low, current_descripution;
        private ImageView current_icon;
        private ArrayList<TextView> day_time;
        private ArrayList<TextView> day_high;
        private ArrayList<ImageView> day_icon;
        private ArrayList<TextView> day_rain;
        private ArrayList<TextView> day_low;
        private ArrayList<TextView> night_time;
        private ArrayList<TextView> night_high;
        private ArrayList<ImageView> night_icon;
        private ArrayList<TextView> night_rain;
        private ArrayList<TextView> night_low;
        private ArrayList<TextView> hour_time;
        private ArrayList<ImageView> hour_icon;
        private ArrayList<TextView> hour_rain;
        private ArrayList<TextView> hour_temperature;

        public FetchPostWeather(FragmentHome fragmentHome){
            reference = new WeakReference<FragmentHome>(fragmentHome);
            day_time = new ArrayList<>();
            day_high = new ArrayList<>();
            day_icon = new ArrayList<>();
            day_rain = new ArrayList<>();
            day_low = new ArrayList<>();
            night_time = new ArrayList<>();
            night_high = new ArrayList<>();
            night_icon = new ArrayList<>();
            night_rain = new ArrayList<>();
            night_low = new ArrayList<>();
            hour_time = new ArrayList<>();
            hour_icon = new ArrayList<>();
            hour_rain = new ArrayList<>();
            hour_temperature = new ArrayList<>();
        }

        @Override
        protected void onPreExecute() {
            initView();
            super.onPreExecute();
        }
        @Override
        protected JSONObject[] doInBackground(Void... voids) {
            initInstantation();

            JSONObject jsonObject[] = new JSONObject[]{null, null,null};
            jsonObject[0] = fetchPostCurrent();
            jsonObject[1] = fetchPostHour();
            jsonObject[2] = fetchPostWeek();

            return jsonObject;
        }
        @Override
        protected void onPostExecute(JSONObject[] jsonObjects) {
            FragmentHome fragmentHome = reference.get();
            if (fragmentHome==null||fragmentHome.isDetached()) return;

            if (jsonObjects[0]!=null&&jsonObjects[1]!=null&&jsonObjects[2]!=null){
                updateViewCurrent(jsonObjects[0]);
                updateViewHour(jsonObjects[1]);
                updateViewWeek(jsonObjects[2]);
                updateViewFinish(fragmentHome, true);
            }
            else {
                updateViewFinish(fragmentHome, false);
            }

            super.onPostExecute(jsonObjects);
        }

        private void initInstantation(){
            okHttpClient = new OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS).build();
            secretResource = new SecretResource();
        }
        private void initView(){
            FragmentHome fragmentHome = reference.get();
            if(fragmentHome==null||fragmentHome.isDetached()) return;

            AdapterViewPager adapter = (AdapterViewPager) fragmentHome.weather.getAdapter();

            current = (LinearLayout) adapter.getItemView(0);
            hour = (LinearLayout) adapter.getItemView(1);
            day = (LinearLayout) adapter.getItemView(2);
            night = (LinearLayout) adapter.getItemView(3);

            current_area = (TextView) current.findViewById(R.id.home_body_weather_viewpager_current_area);
            current_icon = (ImageView) current.findViewById(R.id.home_body_weather_viewpager_current_icon);
            current_temperature = (TextView)current.findViewById(R.id.home_body_weather_viewpager_current_temperature);
            current_high = (TextView) current.findViewById(R.id.home_body_weather_viewpager_current_high);
            current_low = (TextView) current.findViewById(R.id.home_body_weather_viewpager_current_low);
            current_descripution = (TextView) current.findViewById(R.id.home_body_weather_viewpager_current_descripution);

            hour_time.add((TextView) hour.findViewById(R.id.home_body_weather_viewpager_hour_time_1));
            hour_time.add((TextView) hour.findViewById(R.id.home_body_weather_viewpager_hour_time_2));
            hour_time.add((TextView) hour.findViewById(R.id.home_body_weather_viewpager_hour_time_3));
            hour_time.add((TextView) hour.findViewById(R.id.home_body_weather_viewpager_hour_time_4));
            hour_time.add((TextView) hour.findViewById(R.id.home_body_weather_viewpager_hour_time_5));
            hour_time.add((TextView) hour.findViewById(R.id.home_body_weather_viewpager_hour_time_6));
            hour_time.add((TextView) hour.findViewById(R.id.home_body_weather_viewpager_hour_time_7));
            hour_time.add((TextView) hour.findViewById(R.id.home_body_weather_viewpager_hour_time_8));
            hour_time.add((TextView) hour.findViewById(R.id.home_body_weather_viewpager_hour_time_9));
            hour_time.add((TextView) hour.findViewById(R.id.home_body_weather_viewpager_hour_time_10));
            hour_time.add((TextView) hour.findViewById(R.id.home_body_weather_viewpager_hour_time_11));
            hour_time.add((TextView) hour.findViewById(R.id.home_body_weather_viewpager_hour_time_12));
            hour_icon.add((ImageView) hour.findViewById(R.id.home_body_weather_viewpager_hour_icon_1));
            hour_icon.add((ImageView) hour.findViewById(R.id.home_body_weather_viewpager_hour_icon_2));
            hour_icon.add((ImageView) hour.findViewById(R.id.home_body_weather_viewpager_hour_icon_3));
            hour_icon.add((ImageView) hour.findViewById(R.id.home_body_weather_viewpager_hour_icon_4));
            hour_icon.add((ImageView) hour.findViewById(R.id.home_body_weather_viewpager_hour_icon_5));
            hour_icon.add((ImageView) hour.findViewById(R.id.home_body_weather_viewpager_hour_icon_6));
            hour_icon.add((ImageView) hour.findViewById(R.id.home_body_weather_viewpager_hour_icon_7));
            hour_icon.add((ImageView) hour.findViewById(R.id.home_body_weather_viewpager_hour_icon_8));
            hour_icon.add((ImageView) hour.findViewById(R.id.home_body_weather_viewpager_hour_icon_9));
            hour_icon.add((ImageView) hour.findViewById(R.id.home_body_weather_viewpager_hour_icon_10));
            hour_icon.add((ImageView) hour.findViewById(R.id.home_body_weather_viewpager_hour_icon_11));
            hour_icon.add((ImageView) hour.findViewById(R.id.home_body_weather_viewpager_hour_icon_12));
            hour_rain.add((TextView) hour.findViewById(R.id.home_body_weather_viewpager_hour_rain_1));
            hour_rain.add((TextView) hour.findViewById(R.id.home_body_weather_viewpager_hour_rain_2));
            hour_rain.add((TextView) hour.findViewById(R.id.home_body_weather_viewpager_hour_rain_3));
            hour_rain.add((TextView) hour.findViewById(R.id.home_body_weather_viewpager_hour_rain_4));
            hour_rain.add((TextView) hour.findViewById(R.id.home_body_weather_viewpager_hour_rain_5));
            hour_rain.add((TextView) hour.findViewById(R.id.home_body_weather_viewpager_hour_rain_6));
            hour_rain.add((TextView) hour.findViewById(R.id.home_body_weather_viewpager_hour_rain_7));
            hour_rain.add((TextView) hour.findViewById(R.id.home_body_weather_viewpager_hour_rain_8));
            hour_rain.add((TextView) hour.findViewById(R.id.home_body_weather_viewpager_hour_rain_9));
            hour_rain.add((TextView) hour.findViewById(R.id.home_body_weather_viewpager_hour_rain_10));
            hour_rain.add((TextView) hour.findViewById(R.id.home_body_weather_viewpager_hour_rain_11));
            hour_rain.add((TextView) hour.findViewById(R.id.home_body_weather_viewpager_hour_rain_12));
            hour_temperature.add((TextView) hour.findViewById(R.id.home_body_weather_viewpager_hour_temperature_1));
            hour_temperature.add((TextView) hour.findViewById(R.id.home_body_weather_viewpager_hour_temperature_2));
            hour_temperature.add((TextView) hour.findViewById(R.id.home_body_weather_viewpager_hour_temperature_3));
            hour_temperature.add((TextView) hour.findViewById(R.id.home_body_weather_viewpager_hour_temperature_4));
            hour_temperature.add((TextView) hour.findViewById(R.id.home_body_weather_viewpager_hour_temperature_5));
            hour_temperature.add((TextView) hour.findViewById(R.id.home_body_weather_viewpager_hour_temperature_6));
            hour_temperature.add((TextView) hour.findViewById(R.id.home_body_weather_viewpager_hour_temperature_7));
            hour_temperature.add((TextView) hour.findViewById(R.id.home_body_weather_viewpager_hour_temperature_8));
            hour_temperature.add((TextView) hour.findViewById(R.id.home_body_weather_viewpager_hour_temperature_9));
            hour_temperature.add((TextView) hour.findViewById(R.id.home_body_weather_viewpager_hour_temperature_10));
            hour_temperature.add((TextView) hour.findViewById(R.id.home_body_weather_viewpager_hour_temperature_11));
            hour_temperature.add((TextView) hour.findViewById(R.id.home_body_weather_viewpager_hour_temperature_12));

            day_time.add((TextView) day.findViewById(R.id.home_body_weather_viewpager_day_time_1));
            day_time.add((TextView) day.findViewById(R.id.home_body_weather_viewpager_day_time_2));
            day_time.add((TextView) day.findViewById(R.id.home_body_weather_viewpager_day_time_3));
            day_time.add((TextView) day.findViewById(R.id.home_body_weather_viewpager_day_time_4));
            day_time.add((TextView) day.findViewById(R.id.home_body_weather_viewpager_day_time_5));
            day_time.add((TextView) day.findViewById(R.id.home_body_weather_viewpager_day_time_6));
            day_time.add((TextView) day.findViewById(R.id.home_body_weather_viewpager_day_time_7));
            day_high.add((TextView) day.findViewById(R.id.home_body_weather_viewpager_day_high_1));
            day_high.add((TextView) day.findViewById(R.id.home_body_weather_viewpager_day_high_2));
            day_high.add((TextView) day.findViewById(R.id.home_body_weather_viewpager_day_high_3));
            day_high.add((TextView) day.findViewById(R.id.home_body_weather_viewpager_day_high_4));
            day_high.add((TextView) day.findViewById(R.id.home_body_weather_viewpager_day_high_5));
            day_high.add((TextView) day.findViewById(R.id.home_body_weather_viewpager_day_high_6));
            day_high.add((TextView) day.findViewById(R.id.home_body_weather_viewpager_day_high_7));
            day_icon.add((ImageView) day.findViewById(R.id.home_body_weather_viewpager_day_icon_1));
            day_icon.add((ImageView) day.findViewById(R.id.home_body_weather_viewpager_day_icon_2));
            day_icon.add((ImageView) day.findViewById(R.id.home_body_weather_viewpager_day_icon_3));
            day_icon.add((ImageView) day.findViewById(R.id.home_body_weather_viewpager_day_icon_4));
            day_icon.add((ImageView) day.findViewById(R.id.home_body_weather_viewpager_day_icon_5));
            day_icon.add((ImageView) day.findViewById(R.id.home_body_weather_viewpager_day_icon_6));
            day_icon.add((ImageView) day.findViewById(R.id.home_body_weather_viewpager_day_icon_7));
            day_rain.add((TextView) day.findViewById(R.id.home_body_weather_viewpager_day_rain_1));
            day_rain.add((TextView) day.findViewById(R.id.home_body_weather_viewpager_day_rain_2));
            day_rain.add((TextView) day.findViewById(R.id.home_body_weather_viewpager_day_rain_3));
            day_rain.add((TextView) day.findViewById(R.id.home_body_weather_viewpager_day_rain_4));
            day_rain.add((TextView) day.findViewById(R.id.home_body_weather_viewpager_day_rain_5));
            day_rain.add((TextView) day.findViewById(R.id.home_body_weather_viewpager_day_rain_6));
            day_rain.add((TextView) day.findViewById(R.id.home_body_weather_viewpager_day_rain_7));
            day_low.add((TextView) day.findViewById(R.id.home_body_weather_viewpager_day_low_1));
            day_low.add((TextView) day.findViewById(R.id.home_body_weather_viewpager_day_low_2));
            day_low.add((TextView) day.findViewById(R.id.home_body_weather_viewpager_day_low_3));
            day_low.add((TextView) day.findViewById(R.id.home_body_weather_viewpager_day_low_4));
            day_low.add((TextView) day.findViewById(R.id.home_body_weather_viewpager_day_low_5));
            day_low.add((TextView) day.findViewById(R.id.home_body_weather_viewpager_day_low_6));
            day_low.add((TextView) day.findViewById(R.id.home_body_weather_viewpager_day_low_7));

            night_time.add((TextView) night.findViewById(R.id.home_body_weather_viewpager_night_time_1));
            night_time.add((TextView) night.findViewById(R.id.home_body_weather_viewpager_night_time_2));
            night_time.add((TextView) night.findViewById(R.id.home_body_weather_viewpager_night_time_3));
            night_time.add((TextView) night.findViewById(R.id.home_body_weather_viewpager_night_time_4));
            night_time.add((TextView) night.findViewById(R.id.home_body_weather_viewpager_night_time_5));
            night_time.add((TextView) night.findViewById(R.id.home_body_weather_viewpager_night_time_6));
            night_time.add((TextView) night.findViewById(R.id.home_body_weather_viewpager_night_time_7));
            night_high.add((TextView) night.findViewById(R.id.home_body_weather_viewpager_night_high_1));
            night_high.add((TextView) night.findViewById(R.id.home_body_weather_viewpager_night_high_2));
            night_high.add((TextView) night.findViewById(R.id.home_body_weather_viewpager_night_high_3));
            night_high.add((TextView) night.findViewById(R.id.home_body_weather_viewpager_night_high_4));
            night_high.add((TextView) night.findViewById(R.id.home_body_weather_viewpager_night_high_5));
            night_high.add((TextView) night.findViewById(R.id.home_body_weather_viewpager_night_high_6));
            night_high.add((TextView) night.findViewById(R.id.home_body_weather_viewpager_night_high_7));
            night_icon.add((ImageView) night.findViewById(R.id.home_body_weather_viewpager_night_icon_1));
            night_icon.add((ImageView) night.findViewById(R.id.home_body_weather_viewpager_night_icon_2));
            night_icon.add((ImageView) night.findViewById(R.id.home_body_weather_viewpager_night_icon_3));
            night_icon.add((ImageView) night.findViewById(R.id.home_body_weather_viewpager_night_icon_4));
            night_icon.add((ImageView) night.findViewById(R.id.home_body_weather_viewpager_night_icon_5));
            night_icon.add((ImageView) night.findViewById(R.id.home_body_weather_viewpager_night_icon_6));
            night_icon.add((ImageView) night.findViewById(R.id.home_body_weather_viewpager_night_icon_7));
            night_rain.add((TextView) night.findViewById(R.id.home_body_weather_viewpager_night_rain_1));
            night_rain.add((TextView) night.findViewById(R.id.home_body_weather_viewpager_night_rain_2));
            night_rain.add((TextView) night.findViewById(R.id.home_body_weather_viewpager_night_rain_3));
            night_rain.add((TextView) night.findViewById(R.id.home_body_weather_viewpager_night_rain_4));
            night_rain.add((TextView) night.findViewById(R.id.home_body_weather_viewpager_night_rain_5));
            night_rain.add((TextView) night.findViewById(R.id.home_body_weather_viewpager_night_rain_6));
            night_rain.add((TextView) night.findViewById(R.id.home_body_weather_viewpager_night_rain_7));
            night_low.add((TextView) night.findViewById(R.id.home_body_weather_viewpager_night_low_1));
            night_low.add((TextView) night.findViewById(R.id.home_body_weather_viewpager_night_low_2));
            night_low.add((TextView) night.findViewById(R.id.home_body_weather_viewpager_night_low_3));
            night_low.add((TextView) night.findViewById(R.id.home_body_weather_viewpager_night_low_4));
            night_low.add((TextView) night.findViewById(R.id.home_body_weather_viewpager_night_low_5));
            night_low.add((TextView) night.findViewById(R.id.home_body_weather_viewpager_night_low_6));
            night_low.add((TextView) night.findViewById(R.id.home_body_weather_viewpager_night_low_7));
        }
        private JSONObject fetchPostCurrent(){
            String url = secretResource.getURL_Weather() +
                    "O-A0003-001" +
                    "?Authorization=" + secretResource.getAPIKey_Weather() +
                    "&format=" + "JSON" +
                    "&locationName=" + "%E6%B7%A1%E6%B0%B4" +
                    "&elementName=" + "TIME,TEMP,H_Weather" +
                    "&parameterName=" + "CITY,TOWN";
            Request request = new Request.Builder()
                    .url(url)
                    .method("GET", null)
                    .build();
            Response response = null;

            try {
                response = okHttpClient.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(response!=null&&response.isSuccessful()){
                try {
                    return new JSONObject(response.body().string());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
        private JSONObject fetchPostHour(){
            String url = secretResource.getURL_Weather() +
                    "F-D0047-069" +
                    "?Authorization=" + secretResource.getAPIKey_Weather() +
                    "&format=" + "JSON" +
                    "&locationName=" + "%E6%B7%A1%E6%B0%B4%E5%8D%80" +
                    "&elementName=" + "Wx,T,PoP6h" +
                    "&sort=" + "time";
            Request request = new Request.Builder()
                    .url(url)
                    .method("GET", null)
                    .build();
            Response response = null;

            try {
                response = okHttpClient.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (response!=null&&response.isSuccessful()){
                try {
                    return new JSONObject(response.body().string());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
        private JSONObject fetchPostWeek(){
            String url = secretResource.getURL_Weather() +
                    "F-D0047-071" +
                    "?Authorization=" + secretResource.getAPIKey_Weather() +
                    "&format=" + "JSON" +
                    "&locationName=" + "%E6%B7%A1%E6%B0%B4%E5%8D%80" +
                    "&elementName=" + "MinT,MaxT,PoP12h,Wx" +
                    "&sort=" + "time";
            Request request = new Request.Builder()
                    .url(url)
                    .method("GET", null)
                    .build();
            Response response = null;

            try {
                response = okHttpClient.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (response!=null&&response.isSuccessful()){
                try {
                    return new JSONObject(response.body().string());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
        private void updateViewCurrent(JSONObject jsonObject){
            try {
                int temperature = (int)jsonObject.getJSONObject("records").getJSONArray("location").getJSONObject(0).getJSONArray("weatherElement").getJSONObject(0).getDouble("elementValue");
                current_temperature.setText(String.format("%d", temperature)+"度C");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        private void updateViewHour(JSONObject jsonObject){
            try {
                JSONArray jsonArray = jsonObject.getJSONObject("records").getJSONArray("locations").getJSONObject(0).getJSONArray("location").getJSONObject(0).getJSONArray("weatherElement");
                JSONArray Wx = jsonArray.getJSONObject(0).getJSONArray("time");
                JSONArray T = jsonArray.getJSONObject(1).getJSONArray("time");
                JSONArray PoP6h = jsonArray.getJSONObject(2).getJSONArray("time");

                SimpleDateFormat formator_input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat formator_output = new SimpleDateFormat("EEE\nHH:mm");

                for(int i=0;i<12;i++){
                    hour_time.get(i).setText(formator_output.format(formator_input.parse(T.getJSONObject(i).getString("dataTime"))));
                    hour_temperature.get(i).setText(T.getJSONObject(i).getJSONArray("elementValue").getJSONObject(0).getString("value")+"度C");
                    String rain = PoP6h.getJSONObject(i/2).getJSONArray("elementValue").getJSONObject(0).getString("value");
                    if(rain.equals(" ")) hour_rain.get(i).setText("0%");
                    else hour_rain.get(i).setText(rain+"%");
                    iconSetter(Wx.getJSONObject(i).getJSONArray("elementValue").getJSONObject(1).getInt("value"), hour_icon.get(i), formator_input.parse(T.getJSONObject(i).getString("dataTime")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        private void updateViewWeek(JSONObject jsonObject){
            try {
                String area = jsonObject.getJSONObject("records").getJSONArray("locations").getJSONObject(0).getString("locationsName")+" "+jsonObject.getJSONObject("records").getJSONArray("locations").getJSONObject(0).getJSONArray("location").getJSONObject(0).getString("locationName");
                current_area.setText(area);

                JSONArray jsonArray = jsonObject.getJSONObject("records").getJSONArray("locations").getJSONObject(0).getJSONArray("location").getJSONObject(0).getJSONArray("weatherElement");
                JSONArray PoP12h = jsonArray.getJSONObject(0).getJSONArray("time");
                JSONArray Wx = jsonArray.getJSONObject(1).getJSONArray("time");
                JSONArray MinT = jsonArray.getJSONObject(2).getJSONArray("time");
                JSONArray MaxT = jsonArray.getJSONObject(3).getJSONArray("time");

                SimpleDateFormat formator_input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat formator_output = new SimpleDateFormat("EEE");

                for(int i=0;i<7;i++){
                    if(i==0){
                        current_high.setText(MaxT.getJSONObject(i*2).getJSONArray("elementValue").getJSONObject(0).getString("value")+"度C");
                        current_low.setText(MinT.getJSONObject(i*2).getJSONArray("elementValue").getJSONObject(0).getString("value")+"度C");
                        current_descripution.setText(Wx.getJSONObject(i*2).getJSONArray("elementValue").getJSONObject(0).getString("value"));
                        iconSetter(Wx.getJSONObject(i*2).getJSONArray("elementValue").getJSONObject(1).getInt("value"),current_icon, new Date(System.currentTimeMillis()));
                    }

                    day_time.get(i).setText(formator_output.format(formator_input.parse(PoP12h.getJSONObject(i*2).getString("startTime"))));
                    night_time.get(i).setText(formator_output.format(formator_input.parse(PoP12h.getJSONObject(i*2+1).getString("startTime"))));

                    String rain;
                    rain = PoP12h.getJSONObject(i*2).getJSONArray("elementValue").getJSONObject(0).getString("value");
                    if (rain.equals(" ")) day_rain.get(i).setText("0%");
                    else day_rain.get(i).setText(rain+"%");
                    rain = PoP12h.getJSONObject(i*2+1).getJSONArray("elementValue").getJSONObject(0).getString("value");
                    if (rain.equals(" ")) night_rain.get(i).setText("0%");
                    else night_rain.get(i).setText(rain+"%");

                    day_high.get(i).setText(MaxT.getJSONObject(i*2).getJSONArray("elementValue").getJSONObject(0).getString("value")+"度C");
                    night_high.get(i).setText(MaxT.getJSONObject(i*2+1).getJSONArray("elementValue").getJSONObject(0).getString("value")+"度C");

                    day_low.get(i).setText(MinT.getJSONObject(i*2).getJSONArray("elementValue").getJSONObject(0).getString("value")+"度C");
                    night_low.get(i).setText(MinT.getJSONObject(i*2+1).getJSONArray("elementValue").getJSONObject(0).getString("value")+"度C");

                    iconSetter(Wx.getJSONObject(i*2).getJSONArray("elementValue").getJSONObject(1).getInt("value"),day_icon.get(i), formator_input.parse(PoP12h.getJSONObject(i*2).getString("startTime")));
                    iconSetter(Wx.getJSONObject(i*2+1).getJSONArray("elementValue").getJSONObject(1).getInt("value"),night_icon.get(i), formator_input.parse(PoP12h.getJSONObject(i*2+1).getString("startTime")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        private void updateViewFinish(FragmentHome fragmentHome, boolean issuccessful){
            if (issuccessful) fragmentHome.loaded[3] = 1;
            else fragmentHome.loaded[3] = 2;
            fragmentHome.refreshFinish();
        }
        private void iconSetter(int value, ImageView imageView, Date time){
            SimpleDateFormat formator = new SimpleDateFormat("HH");
            int hour = Integer.valueOf(formator.format(time));
            if(hour>=5&&hour<18){
                switch (value){
                    case 1:
                        imageView.setImageResource(R.drawable.ic_weather_day_01);
                        break;
                    case 2:
                        imageView.setImageResource(R.drawable.ic_weather_day_02);
                        break;
                    case 3:
                        imageView.setImageResource(R.drawable.ic_weather_day_03);
                        break;
                    case 4:
                        imageView.setImageResource(R.drawable.ic_weather_day_04);
                        break;
                    case 5:
                        imageView.setImageResource(R.drawable.ic_weather_day_05);
                        break;
                    case 6:
                        imageView.setImageResource(R.drawable.ic_weather_day_06);
                        break;
                    case 7:
                        imageView.setImageResource(R.drawable.ic_weather_day_07);
                        break;
                    case 8:
                        imageView.setImageResource(R.drawable.ic_weather_day_08);
                        break;
                    case 9:
                        imageView.setImageResource(R.drawable.ic_weather_day_09);
                        break;
                    case 10:
                        imageView.setImageResource(R.drawable.ic_weather_day_10);
                        break;
                    case 11:
                        imageView.setImageResource(R.drawable.ic_weather_day_11);
                        break;
                    case 12:
                        imageView.setImageResource(R.drawable.ic_weather_day_12);
                        break;
                    case 13:
                        imageView.setImageResource(R.drawable.ic_weather_day_13);
                        break;
                    case 14:
                        imageView.setImageResource(R.drawable.ic_weather_day_14);
                        break;
                    case 15:
                        imageView.setImageResource(R.drawable.ic_weather_day_15);
                        break;
                    case 16:
                        imageView.setImageResource(R.drawable.ic_weather_day_16);
                        break;
                    case 17:
                        imageView.setImageResource(R.drawable.ic_weather_day_17);
                        break;
                    case 18:
                        imageView.setImageResource(R.drawable.ic_weather_day_18);
                        break;
                    case 19:
                        imageView.setImageResource(R.drawable.ic_weather_day_19);
                        break;
                    case 20:
                        imageView.setImageResource(R.drawable.ic_weather_day_20);
                        break;
                    case 21:
                        imageView.setImageResource(R.drawable.ic_weather_day_21);
                        break;
                    case 22:
                        imageView.setImageResource(R.drawable.ic_weather_day_22);
                        break;
                    case 23:
                        imageView.setImageResource(R.drawable.ic_weather_day_23);
                        break;
                    case 24:
                        imageView.setImageResource(R.drawable.ic_weather_day_24);
                        break;
                    case 25:
                        imageView.setImageResource(R.drawable.ic_weather_day_25);
                        break;
                    case 26:
                        imageView.setImageResource(R.drawable.ic_weather_day_26);
                        break;
                    case 27:
                        imageView.setImageResource(R.drawable.ic_weather_day_27);
                        break;
                    case 28:
                        imageView.setImageResource(R.drawable.ic_weather_day_28);
                        break;
                    case 29:
                        imageView.setImageResource(R.drawable.ic_weather_day_29);
                        break;
                    case 30:
                        imageView.setImageResource(R.drawable.ic_weather_day_30);
                        break;
                    case 31:
                        imageView.setImageResource(R.drawable.ic_weather_day_31);
                        break;
                    case 32:
                        imageView.setImageResource(R.drawable.ic_weather_day_32);
                        break;
                    case 33:
                        imageView.setImageResource(R.drawable.ic_weather_day_33);
                        break;
                    case 34:
                        imageView.setImageResource(R.drawable.ic_weather_day_34);
                        break;
                    case 35:
                        imageView.setImageResource(R.drawable.ic_weather_day_35);
                        break;
                    case 36:
                        imageView.setImageResource(R.drawable.ic_weather_day_36);
                        break;
                    case 37:
                        imageView.setImageResource(R.drawable.ic_weather_day_37);
                        break;
                    case 38:
                        imageView.setImageResource(R.drawable.ic_weather_day_38);
                        break;
                    case 39:
                        imageView.setImageResource(R.drawable.ic_weather_day_39);
                        break;
                    case 40:
                        imageView.setImageResource(R.drawable.ic_weather_day_40);
                        break;
                    case 41:
                        imageView.setImageResource(R.drawable.ic_weather_day_41);
                        break;
                    case 42:
                        imageView.setImageResource(R.drawable.ic_weather_day_42);
                        break;
                }
            }
            else{
                switch (value){
                    case 1:
                        imageView.setImageResource(R.drawable.ic_weather_night_01);
                        break;
                    case 2:
                        imageView.setImageResource(R.drawable.ic_weather_night_02);
                        break;
                    case 3:
                        imageView.setImageResource(R.drawable.ic_weather_night_03);
                        break;
                    case 4:
                        imageView.setImageResource(R.drawable.ic_weather_night_04);
                        break;
                    case 5:
                        imageView.setImageResource(R.drawable.ic_weather_night_05);
                        break;
                    case 6:
                        imageView.setImageResource(R.drawable.ic_weather_night_06);
                        break;
                    case 7:
                        imageView.setImageResource(R.drawable.ic_weather_night_07);
                        break;
                    case 8:
                        imageView.setImageResource(R.drawable.ic_weather_night_08);
                        break;
                    case 9:
                        imageView.setImageResource(R.drawable.ic_weather_night_09);
                        break;
                    case 10:
                        imageView.setImageResource(R.drawable.ic_weather_night_10);
                        break;
                    case 11:
                        imageView.setImageResource(R.drawable.ic_weather_night_11);
                        break;
                    case 12:
                        imageView.setImageResource(R.drawable.ic_weather_night_12);
                        break;
                    case 13:
                        imageView.setImageResource(R.drawable.ic_weather_night_13);
                        break;
                    case 14:
                        imageView.setImageResource(R.drawable.ic_weather_night_14);
                        break;
                    case 15:
                        imageView.setImageResource(R.drawable.ic_weather_night_15);
                        break;
                    case 16:
                        imageView.setImageResource(R.drawable.ic_weather_night_16);
                        break;
                    case 17:
                        imageView.setImageResource(R.drawable.ic_weather_night_17);
                        break;
                    case 18:
                        imageView.setImageResource(R.drawable.ic_weather_night_18);
                        break;
                    case 19:
                        imageView.setImageResource(R.drawable.ic_weather_night_19);
                        break;
                    case 20:
                        imageView.setImageResource(R.drawable.ic_weather_night_20);
                        break;
                    case 21:
                        imageView.setImageResource(R.drawable.ic_weather_night_21);
                        break;
                    case 22:
                        imageView.setImageResource(R.drawable.ic_weather_night_22);
                        break;
                    case 23:
                        imageView.setImageResource(R.drawable.ic_weather_night_23);
                        break;
                    case 24:
                        imageView.setImageResource(R.drawable.ic_weather_night_24);
                        break;
                    case 25:
                        imageView.setImageResource(R.drawable.ic_weather_night_25);
                        break;
                    case 26:
                        imageView.setImageResource(R.drawable.ic_weather_night_26);
                        break;
                    case 27:
                        imageView.setImageResource(R.drawable.ic_weather_night_27);
                        break;
                    case 28:
                        imageView.setImageResource(R.drawable.ic_weather_night_28);
                        break;
                    case 29:
                        imageView.setImageResource(R.drawable.ic_weather_night_29);
                        break;
                    case 30:
                        imageView.setImageResource(R.drawable.ic_weather_night_30);
                        break;
                    case 31:
                        imageView.setImageResource(R.drawable.ic_weather_night_31);
                        break;
                    case 32:
                        imageView.setImageResource(R.drawable.ic_weather_night_32);
                        break;
                    case 33:
                        imageView.setImageResource(R.drawable.ic_weather_night_33);
                        break;
                    case 34:
                        imageView.setImageResource(R.drawable.ic_weather_night_34);
                        break;
                    case 35:
                        imageView.setImageResource(R.drawable.ic_weather_night_35);
                        break;
                    case 36:
                        imageView.setImageResource(R.drawable.ic_weather_night_36);
                        break;
                    case 37:
                        imageView.setImageResource(R.drawable.ic_weather_night_37);
                        break;
                    case 38:
                        imageView.setImageResource(R.drawable.ic_weather_night_38);
                        break;
                    case 39:
                        imageView.setImageResource(R.drawable.ic_weather_night_39);
                        break;
                    case 40:
                        imageView.setImageResource(R.drawable.ic_weather_night_40);
                        break;
                    case 41:
                        imageView.setImageResource(R.drawable.ic_weather_night_41);
                        break;
                    case 42:
                        imageView.setImageResource(R.drawable.ic_weather_night_42);
                        break;
                }
            }
        }

    }
    private static class FetchPostCurriculum extends AsyncTask<Void, Void, Boolean>{
        private WeakReference<FragmentHome> reference;

        private OkHttpClient okHttpClient;

        private SecretResource secretResource;
        private AESHelper aesHelper;
        private KeyStoreHelper keyStoreHelper;

        private SharedPreferences account;
        private SQLiteDatabase db;
        private Cursor cursor;

        public FetchPostCurriculum(FragmentHome fragmentHome){
            reference = new WeakReference<FragmentHome>(fragmentHome);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            initInstantation();
            if (isInstantationSuccessful()){
                initDataBases();

                String[] account = decryptAccount();

                JSONArray jsonArray[] = new JSONArray[]{null, null, null};

                if(isDecryptAccountSccessful(account)){
                    jsonArray[0] = fetchPostCurriculum(account);
                    jsonArray[1] = fetchPostCourseChgTemporary(account);
                    jsonArray[2] = fetchPostCourseChgPermanent(account);
                }

                if(jsonArray[0]!=null&&jsonArray[1]!=null&&jsonArray[2]!=null) {
                    storeDataBaseCurriculum(jsonArray[0]);
                    storeDataBaseCourseChgTemporary(jsonArray[1]);
                    storeDataBaseCourseChgPermanent(jsonArray[2]);
                    return true;
                }
            }
            return false;
        }
        @Override
        protected void onPostExecute(Boolean b) {
            FragmentHome fragmentHome = reference.get();
            if (fragmentHome==null||fragmentHome.isDetached()) return;

            if (b){
                updateViewCurriculum(fragmentHome);
                updateViewHeader(fragmentHome);
                updateViewFinish(fragmentHome, true);
            }
            else {
                updateViewFinish(fragmentHome, false);
            }
            super.onPostExecute(b);
        }

        private void initInstantation(){
            FragmentHome fragmentHome = reference.get();
            if (fragmentHome==null||fragmentHome.isDetached()){
                account = null;
                db = null;
                okHttpClient = null;
                secretResource = null;
                aesHelper = null;
                keyStoreHelper = null;
                return;
            }

            account = fragmentHome.getActivity().getSharedPreferences("account", Context.MODE_PRIVATE);
            db = fragmentHome.getContext().openOrCreateDatabase("curriculum", Context.MODE_PRIVATE, null);
            okHttpClient = new OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS).build();
            secretResource = new SecretResource();
            aesHelper = new AESHelper();
            keyStoreHelper = new KeyStoreHelper(fragmentHome.getContext());
        }
        private void initDataBases(){
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
                    "REQUIRED VARCHAR(50) NOT NULL, " +
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

        private void storeDataBaseCurriculum(JSONArray jsonArray){
            db.execSQL("DELETE FROM CURRICULUM");
            for(int i=0;i<jsonArray.length();i++){
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    String name = jsonObject.getString("ch_cos_name").trim();
                    String teacher = jsonObject.getString("teach_name").trim();
                    String room = jsonObject.getString("room").trim();
                    int seatnum = jsonObject.getInt("seat_no");
                    int week = jsonObject.getInt("week");

                    if(!jsonObject.getString("sess1").equals("  ")) db.execSQL("INSERT INTO CURRICULUM (NAME, TEACHER, WEEK, SESSION, SEATNUM, ROOM) VALUES ('"+name+"','"+teacher+"',"+String.valueOf(week)+","+String.valueOf(jsonObject.getInt("sess1"))+","+String.valueOf(seatnum)+",'"+room+"'"+")");
                    if(!jsonObject.getString("sess2").equals("  ")) db.execSQL("INSERT INTO CURRICULUM (NAME, TEACHER, WEEK, SESSION, SEATNUM, ROOM) VALUES ('"+name+"','"+teacher+"',"+String.valueOf(week)+","+String.valueOf(jsonObject.getInt("sess2"))+","+String.valueOf(seatnum)+",'"+room+"'"+")");
                    if(!jsonObject.getString("sess3").equals("  ")) db.execSQL("INSERT INTO CURRICULUM (NAME, TEACHER, WEEK, SESSION, SEATNUM, ROOM) VALUES ('"+name+"','"+teacher+"',"+String.valueOf(week)+","+String.valueOf(jsonObject.getInt("sess3"))+","+String.valueOf(seatnum)+",'"+room+"'"+")");
                } catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }
        private void storeDataBaseCourseChgTemporary(JSONArray jsonArray){
            db.execSQL("DELETE FROM COURSECHG");
            try {
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    db.execSQL("INSERT INTO COURSECHG (DEPARTMENT, CURSE, TEACHER, BEF, AFT, START, TYPE, ID) VALUES ("
                            +"'"+jsonObject.getString("系級")+"',"
                            +"'"+jsonObject.getString("科目")+"',"
                            +"'"+jsonObject.getString("教師")+"',"
                            +"'"+jsonObject.getString("原始")+"',"
                            +"'"+jsonObject.getString("異動")+"',"
                            +"'"+jsonObject.getString("d")+"',"
                            +"1, "
                            +"'"+jsonObject.getString("序號")+"'"
                            +")");
                }
            } catch (JSONException e){
                e.printStackTrace();
            }

        }
        private void storeDataBaseCourseChgPermanent(JSONArray jsonArray){
            try {
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    db.execSQL("INSERT INTO COURSECHG (DEPARTMENT, CURSE, TEACHER, BEF, AFT, START, TYPE, ID) VALUES ("
                            +"'"+jsonObject.getString("系級")+"',"
                            +"'"+jsonObject.getString("科目")+"',"
                            +"'"+jsonObject.getString("教師")+"',"
                            +"'"+jsonObject.getString("原始")+"',"
                            +"'"+jsonObject.getString("異動")+"',"
                            +"'"+jsonObject.getString("生效日期")+"',"
                            +"0, "
                            +"'"+jsonObject.getString("序號")+"'"
                            +")");
                }
            } catch (JSONException e){
                e.printStackTrace();
            }
        }

        private void updateViewCurriculum(FragmentHome fragmentHome){
            cursor = db.rawQuery("SELECT * FROM CURRICULUM ORDER BY WEEK ASC", null);
            if (cursor!=null){
                cursor.moveToFirst();

                for(int i=0;i<7;i++) ((AdapterRecyclerViewCurriculum)((RecyclerView)((AdapterViewPager)fragmentHome.curriculum.getAdapter()).getItemView(i).findViewById(R.id.home_body_curriculum_viewpager_recyclerview)).getAdapter()).clearAllItem();

                for(int i=0;i<cursor.getCount();i++){
                    AdapterRecyclerViewCurriculum adapter = (AdapterRecyclerViewCurriculum) ((RecyclerView)((AdapterViewPager)fragmentHome.curriculum.getAdapter()).getItemView(cursor.getInt(5)-1).findViewById(R.id.home_body_curriculum_viewpager_recyclerview)).getAdapter();
                    adapter.addCourse(new BeanCourse(cursor.getInt(6), cursor.getInt(4), cursor.getString(1), cursor.getString(2), cursor.getString(3)));
                    cursor.moveToNext();
                }
                for(int i=0;i<7;i++) ((RecyclerView)((AdapterViewPager)fragmentHome.curriculum.getAdapter()).getItemView(i).findViewById(R.id.home_body_curriculum_viewpager_recyclerview)).getAdapter().notifyDataSetChanged();
            }
            fragmentHome.view.findViewById(R.id.home_body_curriculum).setVisibility(View.VISIBLE);
        }
        private void updateViewHeader(FragmentHome fragmentHome){

            TextView currentclass = fragmentHome.view.findViewById(R.id.home_header_currentclass);
            TextView nextclass = fragmentHome.view.findViewById(R.id.home_header_nextclass);

            SimpleDateFormat formator_hour = new SimpleDateFormat("HH");
            SimpleDateFormat formator_mins = new SimpleDateFormat("mm");
            formator_hour.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            formator_mins.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            Date time = new Date(System.currentTimeMillis());
            Calendar calendar = Calendar.getInstance();
            calendar.setFirstDayOfWeek(Calendar.MONDAY);
            int hour = Integer.valueOf(formator_hour.format(time));
            int mins = Integer.valueOf(formator_mins.format(time));
            int week = calendar.get(Calendar.DAY_OF_WEEK)-1;
            if(mins>=0&&mins<=9){
                currentclass.setText("下課");

                cursor = db.rawQuery("SELECT * FROM CURRICULUM WHERE SESSION="+String.valueOf(hour-7)+" AND WEEK="+String.valueOf(week), null);
                cursor.moveToFirst();
                if(cursor.getCount()!=0) nextclass.setText(cursor.getString(1));
                else nextclass.setText("空堂");
            }
            else {
                cursor = db.rawQuery("SELECT * FROM CURRICULUM WHERE SESSION="+String.valueOf(hour-7)+" AND WEEK="+String.valueOf(week), null);
                cursor.moveToFirst();
                if(cursor.getCount()!=0) currentclass.setText(cursor.getString(1));
                else currentclass.setText("空堂");

                cursor = db.rawQuery("SELECT * FROM CURRICULUM WHERE SESSION="+String.valueOf(hour-6)+" AND WEEK="+String.valueOf(week), null);
                cursor.moveToFirst();
                if(cursor.getCount()!=0) nextclass.setText(cursor.getString(1));
                else nextclass.setText("空堂");
            }
        }
        private void updateViewFinish(FragmentHome fragmentHome, boolean issuccessful){
            if (issuccessful) fragmentHome.loaded[1] = 1;
            else fragmentHome.loaded[1] = 2;
            fragmentHome.refreshFinish();
        }

        private boolean isInstantationSuccessful(){
            if (okHttpClient!=null&&secretResource!=null&&aesHelper!=null&&keyStoreHelper!=null&&account!=null&&db!=null) return true;
            return false;
        }
        private boolean isDecryptAccountSccessful(String[] account){
            if (account[0]!=null&&account[1]!=null) return true;
            return false;
        }

        private String[] decryptAccount(){
            String res[] = null;

            try {
                res = new String[]{null,null};

                String key = keyStoreHelper.getKey();
                String iv = keyStoreHelper.getIv();

                res[0] = Base64.encodeToString(aesHelper.encrypt(aesHelper.decrypt(Base64.decode(account.getString("id",""), Base64.NO_WRAP), key, iv), secretResource.getAES256Key_TKU(), secretResource.getAES256Iv_TKU()), Base64.NO_WRAP);
                res[1] = aesHelper.decrypt(Base64.decode(account.getString("pk",""), Base64.NO_WRAP), key, iv);
            } catch (UnrecoverableKeyException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            }

            return res;
        }

        private JSONArray fetchPostCurriculum(String[] account){
            FormBody formBody = new FormBody.Builder()
                    .add("pk", account[1])
                    .add("uid", account[0])
                    .add("ty","json")
                    .build();
            Request request = new Request.Builder()
                    .url(secretResource.getURL_Curriculum())
                    .post(formBody)
                    .build();
            Response response = null;

            try {
                response = okHttpClient.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (response!=null&&response.isSuccessful()){
                try {
                    return new JSONArray(response.body().string());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
        private JSONArray fetchPostCourseChgTemporary(String[] account){
            FormBody formBody = new FormBody.Builder()
                    .add("pk", account[1])
                    .add("uid", account[0])
                    .add("ty", "json")
                    .build();
            Request request = new Request.Builder()
                    .url(secretResource.getURL_CourseChangeTemporary())
                    .post(formBody)
                    .build();
            Response response = null;

            try {
                response = okHttpClient.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (response!=null&&response.isSuccessful()){
                try {
                    return new JSONArray(response.body().string());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
        private JSONArray fetchPostCourseChgPermanent(String[] account){
            FormBody formBody = new FormBody.Builder()
                    .add("pk", account[1])
                    .add("uid", account[0])
                    .add("ty", "json")
                    .build();
            Request request = new Request.Builder()
                    .url(secretResource.getURL_CourseChangePermanent())
                    .post(formBody)
                    .build();
            Response response = null;

            try {
                response = okHttpClient.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(response!=null&&response.isSuccessful()){
                try {
                    return new JSONArray(response.body().string());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
    }
    private static class FetchPostBusTime extends AsyncTask<Void, Void, Void>{
        private WeakReference<FragmentHome> reference;

        private OkHttpClient okHttpClient;
        private SecretResource secretResource;
        private HmacHelper hmacHelper;
        private AdapterViewPager adapter;

        public FetchPostBusTime(FragmentHome fragmentHome){
            reference = new WeakReference<FragmentHome>(fragmentHome);
        }

        @Override
        protected void onPreExecute() {
            FragmentHome fragmentHome = reference.get();
            if (fragmentHome==null||fragmentHome.isDetached()) return;
            initView(fragmentHome);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            initInstantation();
            updatePrepare();
            publishProgress();
            update();
            return null;
        }
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            updateViewTransporation();
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            FragmentHome fragmentHome = reference.get();
            if (fragmentHome==null||fragmentHome.isDetached()) return;

            updateViewTransporation();
            updateViewFinish(fragmentHome);
        }

        private void initInstantation(){
            okHttpClient = new OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS).build();
            secretResource = new SecretResource();
            hmacHelper = new HmacHelper();
        }
        private void initView(FragmentHome fragmentHome){
            adapter = (AdapterViewPager) fragmentHome.transportation.getAdapter();
        }

        private void updatePrepare(){
            for (int i=0;i<adapter.getCount();i++){
                RecyclerView recyclerView = adapter.getItemView(i).findViewById(R.id.home_body_transportation_viewpager_recyclerview);
                AdapterRecyclerViewTransportation adapter_recyclerview = (AdapterRecyclerViewTransportation) recyclerView.getAdapter();
                for(int j=0;j<adapter_recyclerview.getItemCount();j++){
                    adapter_recyclerview.getItem(j).setTime("刷新中");
                }
            }
        }
        private void update(){
            for (int i=0;i<adapter.getCount();i++){
                RecyclerView recyclerView = adapter.getItemView(i).findViewById(R.id.home_body_transportation_viewpager_recyclerview);
                AdapterRecyclerViewTransportation adapter_recyclerview = (AdapterRecyclerViewTransportation) recyclerView.getAdapter();
                for(int j=0;j<adapter_recyclerview.getItemCount();j++){
                    adapter_recyclerview.getItem(j).setTime(fetchPostRoute(adapter_recyclerview.getItem(j)));
                }
            }
        }

        private String fetchPostRoute(BeanRoute route){
            String url = getURL(route);
            String hmac[] = getHmac();
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", hmac[1])
                    .addHeader("x-date", hmac[0])
                    .addHeader("Accept-Encoding", "gzip, deflate")
                    .method("GET", null)
                    .build();
            Response response = null;
            try {
                response = okHttpClient.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response!=null&&response.isSuccessful()){
                response = decodeGzip(response);
                try {
                    return getEstimateTime(new JSONArray(response.body().string()));
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return "錯誤";
        }
        private String[] getHmac(){
            SimpleDateFormat formator = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
            formator.setTimeZone(TimeZone.getTimeZone("GMT"));
            String xDate = formator.format(new Date(System.currentTimeMillis()));
            String hmac = "hmac username=\"" +
                    secretResource.getAppKey_Teansportation() +
                    "\", algorithm=\"hmac-sha1\", headers=\"x-date\", signature=\"" +
                    hmacHelper.getSignture("x-date: "+xDate, secretResource.getAPIKey_Transportation()) + "\"";
            String res[] = new String[]{xDate, hmac};
            return res;
        }
        private String getURL(BeanRoute route){
            String url = null;
            try {
                url = secretResource.getURL_Transportation() +
                        route.getCity() + "/" +
                        URLEncoder.encode(route.getRoute(), "UTF-8") + "?$filter=" +
                        "StopName%2FZh_tw%20eq%20'" + URLEncoder.encode(route.getStation(), "UTF-8") + "'%20and%20" +
                        "Direction%20eq%20" + String.format("%d", route.getDirection()) +  "%20and%20" +
                        "RouteName%2FZh_tw%20eq%20'" + URLEncoder.encode(route.getRoute(), "UTF-8") + "'" +
                        "&$format=" + "JSON";
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return url;
        }
        private String getEstimateTime(JSONArray jsonArray){
            JSONObject jsonObject = null;
            try {
                jsonObject = jsonArray.getJSONObject(0);
                int stopstatus = jsonObject.getInt("StopStatus");
                if (stopstatus==0){
                    int estimatetime = jsonObject.getInt("EstimateTime");
                    if (estimatetime<60) return "即將到站";
                    else return String.format("%d", estimatetime/60)+"分鐘";
                }
                else if (stopstatus==1) return "尚未發車";
                else if (stopstatus==2) return "交管不停靠";
                else if (stopstatus==3) return "末班車已過";
                else if (stopstatus==4) return "今日未營運";
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return "錯誤";
        }

        private Response decodeGzip(Response src){
            Response res = null;
            String contentEncoding = src.headers().get("Content-Encoding");
            if(contentEncoding!=null){
                if(contentEncoding.equals("gzip")){
                    Long contentLength = src.body().contentLength();
                    GzipSource responseBody = new GzipSource(src.body().source());
                    Headers strippedHeaders = src.headers().newBuilder().build();
                    res = src.newBuilder().headers(strippedHeaders).body(new RealResponseBody(src.body().contentType().toString(), contentLength, Okio.buffer(responseBody))).build();
                }
            }
            if(res!=null) return res;
            return src;
        }

        private void updateViewTransporation(){
            for (int i=0;i<adapter.getCount();i++){
                ((RecyclerView)adapter.getItemView(i).findViewById(R.id.home_body_transportation_viewpager_recyclerview)).getAdapter().notifyDataSetChanged();
            }
        }
        private void updateViewFinish(FragmentHome fragmentHome){
            fragmentHome.loaded[2] = 1;
            fragmentHome.refreshFinish();
        }
    }
    private static class LoadingHeader extends AsyncTask<Void, Void, String[]>{
        private WeakReference<FragmentHome> reference;

        private KeyStoreHelper keyStoreHelper;
        private AESHelper aesHelper;

        private TextView greetings, name, status, university, department, year, stuid;
        private LinearLayout classes, login;

        private SharedPreferences common, account;

        public LoadingHeader(FragmentHome fragmentHome){
            reference = new WeakReference<FragmentHome>(fragmentHome);
        }

        @Override
        protected void onPreExecute() {
            initView();
            super.onPreExecute();
        }
        @Override
        protected String[] doInBackground(Void... voids) {
            FragmentHome fragmentHome = reference.get();
            if (fragmentHome==null||fragmentHome.isDetached()) return null;

            initInstantation(fragmentHome);

            return decrypt();
        }
        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            FragmentHome fragmentHome = reference.get();
            if (fragmentHome==null||fragmentHome.isDetached()) return;

            updateViewGreetings();
            if (strings!=null){
                updateViewHeader(strings);
                updateViewFinish(fragmentHome, true);
            }
            else {
                updateViewFinish(fragmentHome, false);
            }

            fragmentHome.refreshFinish();
        }

        private void initView(){
            FragmentHome fragmentHome = reference.get();
            if (fragmentHome==null||fragmentHome.isDetached()) return;

            greetings = fragmentHome.view.findViewById(R.id.home_header_greetings);
            name = fragmentHome.view.findViewById(R.id.home_header_name);
            status = fragmentHome.view.findViewById(R.id.home_header_status);
            university = fragmentHome.view.findViewById(R.id.home_header_university);
            department = fragmentHome.view.findViewById(R.id.home_header_department);
            year = fragmentHome.view.findViewById(R.id.home_header_year);
            stuid = fragmentHome.view.findViewById(R.id.home_header_stuid);

            classes = fragmentHome.view.findViewById(R.id.home_header_class);
            login = fragmentHome.view.findViewById(R.id.home_header_login);
        }
        private void initInstantation(FragmentHome fragmentHome){
            common = fragmentHome.getContext().getSharedPreferences("common", Context.MODE_PRIVATE);
            account = fragmentHome.getContext().getSharedPreferences("account", Context.MODE_PRIVATE);
            aesHelper = new AESHelper();
            keyStoreHelper = new KeyStoreHelper(fragmentHome.getContext());
        }
        private String[] decrypt(){
            try {
                String[] secret = new String[]{keyStoreHelper.getKey(), keyStoreHelper.getIv()};
                String name = aesHelper.decrypt(Base64.decode(account.getString("name",""), Base64.NO_WRAP), secret[0], secret[1]);
                String status = aesHelper.decrypt(Base64.decode(account.getString("status",""), Base64.NO_WRAP), secret[0], secret[1]);
                String university = aesHelper.decrypt(Base64.decode(account.getString("university",""), Base64.NO_WRAP), secret[0], secret[1]);
                String department = aesHelper.decrypt(Base64.decode(account.getString("department",""),Base64.NO_WRAP), secret[0], secret[1]);
                String year = aesHelper.decrypt(Base64.decode(account.getString("year",""), Base64.NO_WRAP), secret[0], secret[1]);
                String stuid = aesHelper.decrypt(Base64.decode(account.getString("id",""),Base64.NO_WRAP), secret[0], secret[1]);
                return new String[]{name, status, university, department, year, stuid};
            } catch (UnrecoverableKeyException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            }
            return null;
        }
        private void updateViewHeader(String[] strings){
            name.setText(strings[0]);
            status.setText(strings[1]);
            university.setText(strings[2]);
            department.setText(strings[3]);
            year.setText(strings[4]);
            stuid.setText(strings[5]);
        }
        private void updateViewGreetings(){
            SimpleDateFormat formator = new SimpleDateFormat("H");
            int hour = Integer.valueOf(formator.format(new Date(System.currentTimeMillis())));
            if (hour>=5&&hour<=10) greetings.setText("早安");
            else if (hour>=11&&hour<=13) greetings.setText("中午安");
            else if (hour>=14&&hour<=17) greetings.setText("下午安");
            else greetings.setText("晚安");
        }
        private void updateViewFinish(FragmentHome fragmentHome, boolean issuccessful){
            if (issuccessful) fragmentHome.loaded[0] = 1;
            else fragmentHome.loaded[0] = 2;
            fragmentHome.refreshFinish();
        }
    }
    private static class LoadingTransportation extends Thread{
        private WeakReference<FragmentHome> reference;

        private boolean flag;

        public LoadingTransportation(FragmentHome fragmentHome){
            flag = true;
            reference = new WeakReference<FragmentHome>(fragmentHome);
        }

        @Override
        public void run() {
            super.run();
            while (flag){
                try {
                    FragmentHome fragmentHome = reference.get();

                    if (fragmentHome==null||fragmentHome.isDetached()) cancel();
                    else new FetchPostBusTime(fragmentHome).execute();

                    sleep(30000);
                } catch (InterruptedException e) {
                }
            }
        }

        public void cancel(){
            flag = false;
            interrupt();
        }
    }







    private class OnPageChangeListener implements androidx.viewpager.widget.ViewPager.OnPageChangeListener {
        private LinearLayout indicator;
        private int last_position;

        public OnPageChangeListener(LinearLayout indicator){
            this.indicator = indicator;
            last_position = 0;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }
        @Override
        public void onPageSelected(int position) {
            indicator.getChildAt(last_position).setEnabled(false);
            indicator.getChildAt(position).setEnabled(true);
            last_position = position;
        }
        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }




}
