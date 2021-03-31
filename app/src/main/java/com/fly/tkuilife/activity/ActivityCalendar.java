package com.fly.tkuilife.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.fly.tkuilife.R;
import com.fly.tkuilife.adapter.AdapterRecyclerViewCalenderFirstSemester;
import com.fly.tkuilife.adapter.AdapterRecyclerViewCalenderSecondSemester;
import com.fly.tkuilife.adapter.AdapterViewPagerCalendar;
import com.fly.tkuilife.bean.BeanCalendar;
import com.fly.tkuilife.bean.BeanTest;
import com.fly.tkuilife.utils.SecretResource;
import com.google.android.material.tabs.TabLayout;
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class ActivityCalendar extends AppCompatActivity implements TabLayout.OnTabSelectedListener {

    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private boolean loaded[];

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        new FetchPostTest().execute();
    }
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        switch (tab.getPosition()){
            case 0:
                if (!loaded[0]) loadingFirstSemester();
                break;
            case 1:
                if (!loaded[1]) loadingSecondSemester();
                break;
        }
    }
    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }
    @Override
    public void onTabReselected(TabLayout.Tab tab) {

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
        initOther();
        initActionBar();
        initViewPager();
        initInteraction();
        initTabLayout();
    }
    private void initView(){
        setContentView(R.layout.activity_calendar);
        toolbar = findViewById(R.id.calendar_toolbar);
        viewPager = findViewById(R.id.calendar_viewpager);
        tabLayout = findViewById(R.id.calendar_tablayout);
    }
    private void initActionBar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }
    private void initViewPager(){
        AdapterViewPagerCalendar adapter = new AdapterViewPagerCalendar();

        adapter.addItemView(LayoutInflater.from(this).inflate(R.layout.layout_calendar_viewpager_firstsemester, null));
        adapter.addItemView(LayoutInflater.from(this).inflate(R.layout.layout_calendar_viewpager_secondsemester, null));

        viewPager.setAdapter(adapter);
    }
    private void initTabLayout(){
        String[] tab = {"第一學期","第二學期"};
        for (int i=0;i<tab.length;i++) tabLayout.addTab(tabLayout.newTab());
        tabLayout.setupWithViewPager(viewPager, false);
        for (int i=0;i<tab.length;i++) tabLayout.getTabAt(i).setText(tab[i]);
    }
    private void initOther(){
        loaded = new boolean[]{false, false};
    }
    private void initInteraction(){
        ((SwipeRefreshLayout)((AdapterViewPagerCalendar)viewPager.getAdapter()).getItemView(0).findViewById(R.id.calendar_viewpager_firstsemester_swiperefreshlayout)).setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadingFirstSemester();
            }
        });
        ((SwipeRefreshLayout)((AdapterViewPagerCalendar)viewPager.getAdapter()).getItemView(1).findViewById(R.id.calendar_viewpager_secondsemester_swiperefreshlayout)).setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadingSecondSemester();
            }
        });
        tabLayout.addOnTabSelectedListener(this);
    }

    private void loadingFirstSemester(){
        new FetchPostFirstSemester(this).execute();
    }
    private void loadingSecondSemester(){
        new FetchPostSecondSemester(this).execute();
    }



    private static class FetchPostFirstSemester extends AsyncTask<Void, Void, ArrayList<BeanCalendar>>{
        private WeakReference<ActivityCalendar> reference;

        public FetchPostFirstSemester(ActivityCalendar activityCalendar){
            reference = new WeakReference<ActivityCalendar>(activityCalendar);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            ActivityCalendar activityCalendar = reference.get();
            if (activityCalendar==null||activityCalendar.isDestroyed()) return;

            updateViewPrepare(activityCalendar);
        }
        @Override
        protected ArrayList<BeanCalendar> doInBackground(Void... voids) {
            return fetchPost();
        }
        @Override
        protected void onPostExecute(ArrayList<BeanCalendar> beanCalendars) {
            super.onPostExecute(beanCalendars);

            ActivityCalendar activityCalendar = reference.get();
            if (activityCalendar==null||activityCalendar.isDestroyed()) return;

            if (beanCalendars!=null){
                activityCalendar.loaded[0] = true;
                updateViewSuccessful(activityCalendar, beanCalendars);
            }
            else updateViewFailed(activityCalendar);
        }

        private ArrayList<BeanCalendar> fetchPost(){
            SecretResource secretResource = new SecretResource();
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .build();
            Request request = new Request.Builder()
                    .url(secretResource.getURL_CalendarFirstSemester())
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
                    ArrayList<BeanCalendar> calendars = parseXML(response.body().byteStream());
                    return calendars;
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        private ArrayList<BeanCalendar> parseXML(InputStream xml) throws XmlPullParserException, IOException {
            ArrayList<BeanCalendar> calendars = null;
            BeanCalendar calendar = null;
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(xml, "utf-16");
            int eventType = parser.getEventType();
            while (eventType!=XmlPullParser.END_DOCUMENT){
                String name = parser.getName();
                switch (eventType){
                    case XmlPullParser.START_DOCUMENT:
                        calendars = new ArrayList<>();
                        break;
                    case XmlPullParser.START_TAG:
                        if (name.equals("cal1")) calendar = new BeanCalendar();
                        else if (name.equals("週次")) calendar.setWeek(parser.nextText().trim());
                        else if (name.equals("日期")){
                            String time[] = parser.nextText().trim().split(" ~ ");
                            calendar.setStarttime(time[0]);
                            if (time.length!=1){
                                if (time[1].length()>5) calendar.setEndtime(time[1]);
                                else calendar.setEndtime(time[0].substring(0,5)+time[1]);
                            }
                            else {
                                calendar.setEndtime("");
                            }
                        }
                        else if (name.equals("事項")) calendar.setEvent(parser.nextText().trim());
                        break;
                    case XmlPullParser.END_TAG:
                        if (name.equals("cal1")){
                            if (calendar!=null){
                                calendars.add(calendar);
                                calendar = null;
                            }
                        }
                        break;
                }
                eventType = parser.next();
            }
            return calendars;
        }

        private void updateViewPrepare(ActivityCalendar activityCalendar){
            SwipeRefreshLayout swipeRefreshLayout =((AdapterViewPagerCalendar)activityCalendar.viewPager.getAdapter()).getItemView(0).findViewById(R.id.calendar_viewpager_firstsemester_swiperefreshlayout);
            if (!swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(true);
        }
        private void updateViewSuccessful(ActivityCalendar activityCalendar, ArrayList<BeanCalendar> calendars){
            RecyclerView recyclerView = ((AdapterViewPagerCalendar)activityCalendar.viewPager.getAdapter()).getItemView(0).findViewById(R.id.calendar_viewpager_firstsemester_recyclerview);
            SwipeRefreshLayout swipeRefreshLayout = ((AdapterViewPagerCalendar)activityCalendar.viewPager.getAdapter()).getItemView(0).findViewById(R.id.calendar_viewpager_firstsemester_swiperefreshlayout);

            AdapterRecyclerViewCalenderFirstSemester adapter = new AdapterRecyclerViewCalenderFirstSemester();
            for (BeanCalendar calendar:calendars) adapter.addItem(calendar);

            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(activityCalendar));

            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
        }
        private void updateViewFailed(ActivityCalendar activityCalendar){
            SwipeRefreshLayout swipeRefreshLayout = ((AdapterViewPagerCalendar)activityCalendar.viewPager.getAdapter()).getItemView(0).findViewById(R.id.calendar_viewpager_firstsemester_swiperefreshlayout);
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(activityCalendar, "無法取得行事曆資料，請重試", Toast.LENGTH_SHORT).show();
        }
    }

    private static class FetchPostSecondSemester extends AsyncTask<Void, Void, ArrayList<BeanCalendar>>{
        private WeakReference<ActivityCalendar> reference;

        public FetchPostSecondSemester(ActivityCalendar activityCalendar){
            reference = new WeakReference<ActivityCalendar>(activityCalendar);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            ActivityCalendar activityCalendar = reference.get();
            if (activityCalendar==null||activityCalendar.isDestroyed()) return;

            updateViewPrepare(activityCalendar);
        }
        @Override
        protected ArrayList<BeanCalendar> doInBackground(Void... voids) {
            return fetchPost();
        }
        @Override
        protected void onPostExecute(ArrayList<BeanCalendar> beanCalendars) {
            super.onPostExecute(beanCalendars);

            ActivityCalendar activityCalendar = reference.get();
            if (activityCalendar==null||activityCalendar.isDestroyed()) return;

            if (beanCalendars!=null){
                activityCalendar.loaded[1] = true;
                updateViewSuccessful(activityCalendar, beanCalendars);
            }
            else updateViewFailed(activityCalendar);
        }

        private ArrayList<BeanCalendar> fetchPost(){
            SecretResource secretResource = new SecretResource();
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .build();
            Request request = new Request.Builder()
                    .url(secretResource.getURL_CalendarSecondSemester())
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
                    ArrayList<BeanCalendar> calendars = parseXML(response.body().byteStream());
                    return calendars;
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        private ArrayList<BeanCalendar> parseXML(InputStream xml) throws XmlPullParserException, IOException {
            ArrayList<BeanCalendar> calendars = null;
            BeanCalendar calendar = null;
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(xml, "utf-16");
            int eventType = parser.getEventType();
            while (eventType!=XmlPullParser.END_DOCUMENT){
                String name = parser.getName();
                switch (eventType){
                    case XmlPullParser.START_DOCUMENT:
                        calendars = new ArrayList<>();
                        break;
                    case XmlPullParser.START_TAG:
                        if (name.equals("cal")) calendar = new BeanCalendar();
                        else if (name.equals("週次")) calendar.setWeek(parser.nextText().trim());
                        else if (name.equals("日期")){
                            String time[] = parser.nextText().trim().split(" ~ ");
                            calendar.setStarttime(time[0]);
                            if (time.length!=1){
                                if (time[1].length()>5) calendar.setEndtime(time[1]);
                                else calendar.setEndtime(time[0].substring(0,5)+time[1]);
                            }
                            else {
                                calendar.setEndtime("");
                            }
                        }
                        else if (name.equals("事項")) calendar.setEvent(parser.nextText().trim());
                        break;
                    case XmlPullParser.END_TAG:
                        if (name.equals("cal")){
                            if (calendar!=null){
                                calendars.add(calendar);
                                calendar = null;
                            }
                        }
                        break;
                }
                eventType = parser.next();
            }
            return calendars;
        }

        private void updateViewPrepare(ActivityCalendar activityCalendar){
            SwipeRefreshLayout swipeRefreshLayout =((AdapterViewPagerCalendar)activityCalendar.viewPager.getAdapter()).getItemView(1).findViewById(R.id.calendar_viewpager_secondsemester_swiperefreshlayout);
            if (!swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(true);
        }
        private void updateViewSuccessful(ActivityCalendar activityCalendar, ArrayList<BeanCalendar> calendars){
            RecyclerView recyclerView = ((AdapterViewPagerCalendar)activityCalendar.viewPager.getAdapter()).getItemView(1).findViewById(R.id.calendar_viewpager_secondsemester_recyclerview);
            SwipeRefreshLayout swipeRefreshLayout = ((AdapterViewPagerCalendar)activityCalendar.viewPager.getAdapter()).getItemView(1).findViewById(R.id.calendar_viewpager_secondsemester_swiperefreshlayout);

            AdapterRecyclerViewCalenderSecondSemester adapter = new AdapterRecyclerViewCalenderSecondSemester();
            for (BeanCalendar calendar:calendars) adapter.addItem(calendar);

            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(activityCalendar));

            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
        }
        private void updateViewFailed(ActivityCalendar activityCalendar){
            SwipeRefreshLayout swipeRefreshLayout = ((AdapterViewPagerCalendar)activityCalendar.viewPager.getAdapter()).getItemView(1).findViewById(R.id.calendar_viewpager_secondsemester_swiperefreshlayout);
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(activityCalendar, "無法取得行事曆資料，請重試", Toast.LENGTH_SHORT).show();
        }
    }


    private static class FetchPostTest extends AsyncTask<Void, Void, Void>{

        private Retrofit retrofit;
        private OkHttpClient okHttpClient;

        @Override
        protected Void doInBackground(Void... voids) {
            initInstantation();
            return null;
        }

        private void initInstantation(){
            okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://ilife.tku.edu.tw/")
                    .addConverterFactory(TikXmlConverterFactory.create())
                    .build();
            Test test = retrofit.create(Test.class);
            Call<BeanTest> call = test.gettest();
            retrofit2.Response<BeanTest> response = null;
            try {
                response = call.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response.isSuccessful()){
                Log.i("提示", response.body().toString());
            }
        }

        private interface Test{
            @GET("Data/XML/cal1.xml")
            Call<BeanTest> gettest();
        }
    }
}
