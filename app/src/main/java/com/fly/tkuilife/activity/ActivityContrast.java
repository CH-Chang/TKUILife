package com.fly.tkuilife.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
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
import com.fly.tkuilife.adapter.AdapterRecyclerViewBuildingContrast;
import com.fly.tkuilife.adapter.AdapterRecyclerViewTimetableContrast;
import com.fly.tkuilife.adapter.AdapterViewPagerContrast;
import com.fly.tkuilife.bean.BeanBuildingContrast;
import com.fly.tkuilife.bean.BeanTimetableContrast;
import com.fly.tkuilife.utils.RecyclerViewItemClickSupport;
import com.fly.tkuilife.utils.SecretResource;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ActivityContrast extends AppCompatActivity implements TabLayout.OnTabSelectedListener, RecyclerViewItemClickSupport.OnItemClickListener {

    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private boolean[] loaded;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
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
    public void onTabSelected(TabLayout.Tab tab) {
        switch (tab.getPosition()){
            case 0:
                if (loaded[0]==false) loadingBuildintContrast();
                break;
            case 1:
                if (loaded[1]==false) loadingTimetableContrast();
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
    public void onItemClick(RecyclerView recyclerView, int position, View view) {
        switch (recyclerView.getId()){
            case R.id.contrast_viewpager_buildingcontrast_recyclerview:
                AdapterRecyclerViewBuildingContrast adapter = (AdapterRecyclerViewBuildingContrast) recyclerView.getAdapter();
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query="+adapter.getItem(position).getLatitude()+","+adapter.getItem(position).getLongitude())));
                break;
        }
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
        setContentView(R.layout.activity_contrast);
        toolbar = findViewById(R.id.contrast_toolbar);
        viewPager = findViewById(R.id.contrast_viewpager);
        tabLayout = findViewById(R.id.contrast_tablayout);
    }
    private void initActionBar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    private void initViewPager(){
        AdapterViewPagerContrast adapter = new AdapterViewPagerContrast();

        adapter.addItemView(LayoutInflater.from(this).inflate(R.layout.layout_contrast_viewpager_buildingcontrast, null));
        adapter.addItemView(LayoutInflater.from(this).inflate(R.layout.layout_contrast_viewpager_timetablecontrast, null));

        viewPager.setAdapter(adapter);
    }
    private void initTabLayout(){
        String[] tab = {"建築物對照","課表對照"};
        for (int i=0;i<tab.length;i++) tabLayout.addTab(tabLayout.newTab());
        tabLayout.setupWithViewPager(viewPager);
        for (int i=0;i<tab.length;i++) tabLayout.getTabAt(i).setText(tab[i]);
    }
    private void initOther(){
        loaded = new boolean[]{false, false};
    }
    private void initInteraction(){
        tabLayout.addOnTabSelectedListener(this);
        ((SwipeRefreshLayout)((AdapterViewPagerContrast)viewPager.getAdapter()).getItemView(0).findViewById(R.id.contrast_viewpager_buildingcontrast_swiperefreshlayout)).setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadingBuildintContrast();
            }
        });
        RecyclerViewItemClickSupport.addTo((RecyclerView) ((AdapterViewPagerContrast)viewPager.getAdapter()).getItemView(0).findViewById(R.id.contrast_viewpager_buildingcontrast_recyclerview)).setOnItemClickListener(this);
    }


    private void loadingBuildintContrast(){
        new FetchPostBuilding(this).execute();
    }
    private void loadingTimetableContrast(){
        RecyclerView recyclerView = ((AdapterViewPagerContrast)viewPager.getAdapter()).getItemView(1).findViewById(R.id.contrast_viewpager_timetablecontrast_recyclerview);
        AdapterRecyclerViewTimetableContrast adapter = new AdapterRecyclerViewTimetableContrast();

        adapter.addItem(new BeanTimetableContrast("第一節","08:10 至 09:00"));
        adapter.addItem(new BeanTimetableContrast("第二節","09:10 至 10:00"));
        adapter.addItem(new BeanTimetableContrast("第三節","10:10 至 11:00"));
        adapter.addItem(new BeanTimetableContrast("第四節","11:10 至 12:00"));
        adapter.addItem(new BeanTimetableContrast("第五節","12:10 至 13:00"));
        adapter.addItem(new BeanTimetableContrast("第六節","13:10 至 14:00"));
        adapter.addItem(new BeanTimetableContrast("第七節","14:10 至 15:00"));
        adapter.addItem(new BeanTimetableContrast("第八節","15:10 至 16:00"));
        adapter.addItem(new BeanTimetableContrast("第九節","16:10 至 17:00"));
        adapter.addItem(new BeanTimetableContrast("第十節","17:10 至 18:00"));
        adapter.addItem(new BeanTimetableContrast("第十一節","18:10 至 19:00"));
        adapter.addItem(new BeanTimetableContrast("第十二節","19:10 至 20:00"));
        adapter.addItem(new BeanTimetableContrast("第十三節","20:10 至 21:00"));
        adapter.addItem(new BeanTimetableContrast("第十四節","21:10 至 22:00"));

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loaded[1] = true;
    }




    private static class FetchPostBuilding extends AsyncTask<Void, Void, JSONArray>{
        private WeakReference<ActivityContrast> reference;

        public FetchPostBuilding(ActivityContrast activityContrast){
            reference = new WeakReference<ActivityContrast>(activityContrast);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            ActivityContrast activityContrast = reference.get();
            if (activityContrast==null||activityContrast.isDestroyed()) return;

            updateViewPrepare(activityContrast);
        }
        @Override
        protected JSONArray doInBackground(Void... voids) {
            return fetchPost();
        }
        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            super.onPostExecute(jsonArray);

            ActivityContrast activityContrast = reference.get();
            if (activityContrast==null||activityContrast.isDestroyed()) return;

            if (jsonArray!=null){
                activityContrast.loaded[0] = true;
                updateViewSuccessful(activityContrast, jsonArray);
            }
            else updateViewFailed(activityContrast);
        }

        private JSONArray fetchPost(){
            SecretResource secretResource = new SecretResource();
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .build();
            Request request = new Request.Builder()
                    .url(secretResource.getURL_MapBuilding())
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
                    return new JSONArray(response.body().string());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        private void updateViewPrepare(ActivityContrast activityContrast){
            SwipeRefreshLayout swipeRefreshLayout = ((AdapterViewPagerContrast)activityContrast.viewPager.getAdapter()).getItemView(0).findViewById(R.id.contrast_viewpager_buildingcontrast_swiperefreshlayout);
            if (!swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(true);
        }
        private void updateViewSuccessful(ActivityContrast activityContrast, JSONArray jsonArray){
            RecyclerView recyclerView = ((AdapterViewPagerContrast)activityContrast.viewPager.getAdapter()).getItemView(0).findViewById(R.id.contrast_viewpager_buildingcontrast_recyclerview);
            SwipeRefreshLayout swipeRefreshLayout = ((AdapterViewPagerContrast)activityContrast.viewPager.getAdapter()).getItemView(0).findViewById(R.id.contrast_viewpager_buildingcontrast_swiperefreshlayout);

            AdapterRecyclerViewBuildingContrast adapter = new AdapterRecyclerViewBuildingContrast();
            for (int i=0;i<jsonArray.length();i++){
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    adapter.addItem(new BeanBuildingContrast(jsonObject.getString("id"), jsonObject.getString("ch_name"), jsonObject.getDouble("latitude"), jsonObject.getDouble("longitude")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            recyclerView.setLayoutManager(new LinearLayoutManager(activityContrast));
            recyclerView.setAdapter(adapter);

            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
        }
        private void updateViewFailed(ActivityContrast activityContrast){
            SwipeRefreshLayout swipeRefreshLayout = ((AdapterViewPagerContrast)activityContrast.viewPager.getAdapter()).getItemView(0).findViewById(R.id.contrast_viewpager_buildingcontrast_swiperefreshlayout);
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(activityContrast, "取得建築資料錯誤，請重試", Toast.LENGTH_SHORT).show();
        }
    }
}
