package com.fly.tkuilife.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
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
import com.fly.tkuilife.adapter.AdapterRecyclerViewNewsPaperNews;
import com.fly.tkuilife.adapter.AdapterRecyclerViewNewsPaperPhoto;
import com.fly.tkuilife.adapter.AdapterViewPagerNews;
import com.fly.tkuilife.adapter.AdapterViewPagerNewsPaper;
import com.fly.tkuilife.bean.BeanNewsPaperNews;
import com.fly.tkuilife.bean.BeanNewsPaperPhoto;
import com.fly.tkuilife.utils.RecyclerViewItemClickSupport;
import com.fly.tkuilife.utils.SecretResource;
import com.google.android.material.tabs.TabLayout;

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

public class ActivityNewsPaper extends AppCompatActivity implements TabLayout.OnTabSelectedListener, RecyclerViewItemClickSupport.OnItemClickListener {

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
                if (loaded[0]==false) loadingLatest();
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                if (loaded[3]==false) loadingGallery();
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
            case R.id.newspaper_viewpager_latest_recyclerview:
                AdapterRecyclerViewNewsPaperNews adapter = (AdapterRecyclerViewNewsPaperNews) recyclerView.getAdapter();
                startActivity(new Intent(ActivityNewsPaper.this, ActivityNewsPaperReader.class).putExtra("id", adapter.getItem(position).getId()).putExtra("kind", adapter.getItem(position).getKind()));
                break;
                case R.id.newspaper_viewpager_gallery_recyclerview:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://tkutimes.tku.edu.tw/gallery")));
                break;
        }
    }


    private void init(){
        initView();
        initOther();
        initActionBar();
        initViewpager();
        initInteraction();
        initTabLayout();
    }
    private void initView(){
        setContentView(R.layout.activity_newspaper);
        toolbar = findViewById(R.id.newspaper_toolbar);
        viewPager = findViewById(R.id.newspaper_viewpager);
        tabLayout = findViewById(R.id.newspaper_tablayout);
    }
    private void initActionBar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }
    private void initViewpager(){
        AdapterViewPagerNewsPaper adapter = new AdapterViewPagerNewsPaper();

        adapter.addItemView(LayoutInflater.from(this).inflate(R.layout.layout_newspaper_viewpager_latest, null));
        adapter.addItemView(LayoutInflater.from(this).inflate(R.layout.layout_preparing, null));
        adapter.addItemView(LayoutInflater.from(this).inflate(R.layout.layout_preparing, null));
        adapter.addItemView(LayoutInflater.from(this).inflate(R.layout.layout_newspaper_viewpager_gallery, null));

        viewPager.setAdapter(adapter);
    }
    private void initTabLayout(){
        String[] tab = {"最新一期","演講看板","活動看板","近期圖輯"};
        for (int i=0;i<tab.length;i++) tabLayout.addTab(tabLayout.newTab());
        tabLayout.setupWithViewPager(viewPager, false);
        for (int i=0;i<tab.length;i++) tabLayout.getTabAt(i).setText(tab[i]);
    }
    private void initOther(){
        loaded = new boolean[]{false, false, false, false};
    }
    private void initInteraction(){
        tabLayout.addOnTabSelectedListener(this);
        ((SwipeRefreshLayout)((AdapterViewPagerNewsPaper)viewPager.getAdapter()).getItemView(0).findViewById(R.id.newspaper_viewpager_latest_swiperefreshlayout)).setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadingLatest();
            }
        });
        ((SwipeRefreshLayout)((AdapterViewPagerNewsPaper)viewPager.getAdapter()).getItemView(3).findViewById(R.id.newspaper_viewpager_gallery_swiperefreshlayout)).setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadingGallery();
            }
        });
        RecyclerViewItemClickSupport.addTo((RecyclerView) ((AdapterViewPagerNewsPaper)viewPager.getAdapter()).getItemView(3).findViewById(R.id.newspaper_viewpager_gallery_recyclerview)).setOnItemClickListener(this);
        RecyclerViewItemClickSupport.addTo((RecyclerView) ((AdapterViewPagerNewsPaper)viewPager.getAdapter()).getItemView(0).findViewById(R.id.newspaper_viewpager_latest_recyclerview)).setOnItemClickListener(this);
    }

    private void loadingLatest(){
        new FetchPostLatest(this).execute();
    }
    private void loadingGallery(){
        new FetchPostGallery(this).execute();
    }




    private static class FetchPostLatest extends AsyncTask<Void, Void, ArrayList<BeanNewsPaperNews>> {
        private WeakReference<ActivityNewsPaper> reference;

        public FetchPostLatest(ActivityNewsPaper activityNewsPaper){
            reference = new WeakReference<ActivityNewsPaper>(activityNewsPaper);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            ActivityNewsPaper activityNewsPaper = reference.get();
            if (activityNewsPaper==null||activityNewsPaper.isDestroyed()) return;

            updateViewPrepare(activityNewsPaper);
        }
        @Override
        protected ArrayList doInBackground(Void... voids) {
            return fetchPost();
        }
        @Override
        protected void onPostExecute(ArrayList<BeanNewsPaperNews> newsPaperNews) {
            super.onPostExecute(newsPaperNews);

            ActivityNewsPaper activityNewsPaper = reference.get();
            if (activityNewsPaper==null||activityNewsPaper.isDestroyed()) return;

            if (newsPaperNews!=null){
                activityNewsPaper.loaded[0] = true;
                updateViewSuccessful(activityNewsPaper, newsPaperNews);
            }
            else {
                updateViewFailed(activityNewsPaper);
            }
        }

        private ArrayList<BeanNewsPaperNews> fetchPost(){
            ArrayList<BeanNewsPaperNews> newsPapers = new ArrayList<>();
            SecretResource secretResource = new SecretResource();
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .build();
            for (int i=1;i<=5;i++){
                Request request = new Request.Builder()
                        .url(secretResource.getURL_NewsPaperLatest()+i+".xml")
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
                        newsPapers = parseXML(newsPapers, response.body().byteStream(), i);
                    } catch (XmlPullParserException e) {
                        newsPapers = null;
                        e.printStackTrace();
                    } catch (IOException e) {
                        newsPapers = null;
                        e.printStackTrace();
                    }
                }

            }
            return newsPapers;
        }
        private ArrayList<BeanNewsPaperNews> parseXML(ArrayList<BeanNewsPaperNews> newsPapers, InputStream xml, int kind) throws XmlPullParserException, IOException {
            SecretResource secretResource = new SecretResource();
            BeanNewsPaperNews newsPaper = null;
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(xml, "utf-16");
            int eventType = parser.getEventType();
            while (eventType!=XmlPullParser.END_DOCUMENT){
                String name = parser.getName();
                switch (eventType){
                    case XmlPullParser.START_TAG:
                        if (name.equals("cht")) newsPaper = new BeanNewsPaperNews();
                        else if (name.equals("編號")) newsPaper.setId(parser.nextText().trim());
                        else if (name.equals("標題")) newsPaper.setTitle(parser.nextText().trim());
                        else if (name.equals("圖片路徑")) newsPaper.setImg(secretResource.getURL_NewsPaperLatestImg()+parser.nextText().trim());
                        else if (name.equals("期別")||name.equals("發佈日期")) newsPaper = null;
                        break;
                    case XmlPullParser.END_TAG:
                        if (name.equals("cht")){
                            if (newsPaper!=null){
                                switch (kind){
                                    case 1:
                                        newsPaper.setKind("學校要聞");
                                        break;
                                    case 2:
                                        newsPaper.setKind("趨勢巨流河");
                                        break;
                                    case 3:
                                        newsPaper.setKind("學習新視界");
                                        break;
                                    case 4:
                                        newsPaper.setKind("學生大代誌");
                                        break;
                                    case 5:
                                        newsPaper.setKind("特別企劃");
                                        break;
                                }
                                newsPapers.add(newsPaper);
                                newsPaper = null;
                            }
                        }
                        break;
                }
                eventType = parser.next();
            }
            return newsPapers;
        }

        private void updateViewPrepare(ActivityNewsPaper activityNewsPaper){
            SwipeRefreshLayout swipeRefreshLayout = ((AdapterViewPagerNewsPaper)activityNewsPaper.viewPager.getAdapter()).getItemView(0).findViewById(R.id.newspaper_viewpager_latest_swiperefreshlayout);
            if (!swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(true);
        }
        private void updateViewSuccessful(ActivityNewsPaper activityNewsPaper, ArrayList<BeanNewsPaperNews> newsPapers){
            SwipeRefreshLayout swipeRefreshLayout = ((AdapterViewPagerNewsPaper)activityNewsPaper.viewPager.getAdapter()).getItemView(0).findViewById(R.id.newspaper_viewpager_latest_swiperefreshlayout);
            RecyclerView recyclerView = ((AdapterViewPagerNewsPaper)activityNewsPaper.viewPager.getAdapter()).getItemView(0).findViewById(R.id.newspaper_viewpager_latest_recyclerview);

            AdapterRecyclerViewNewsPaperNews adapter = new AdapterRecyclerViewNewsPaperNews();
            for (BeanNewsPaperNews newsPaper: newsPapers) adapter.addItem(newsPaper);
            recyclerView.setLayoutManager(new LinearLayoutManager(activityNewsPaper));
            recyclerView.setAdapter(adapter);

            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
        }
        private void updateViewFailed(ActivityNewsPaper activityNewsPaper){
            SwipeRefreshLayout swipeRefreshLayout = ((AdapterViewPagerNewsPaper)activityNewsPaper.viewPager.getAdapter()).getItemView(0).findViewById(R.id.newspaper_viewpager_latest_swiperefreshlayout);
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(activityNewsPaper, "刷新最新一期淡江時報失敗，請重試", Toast.LENGTH_SHORT).show();
        }


    }
    private static class FetchPostGallery extends AsyncTask<Void, Void, ArrayList<BeanNewsPaperPhoto>>{
        private WeakReference<ActivityNewsPaper> reference;

        public FetchPostGallery(ActivityNewsPaper activityNewsPaper){
            reference = new WeakReference<ActivityNewsPaper>(activityNewsPaper);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            ActivityNewsPaper activityNewsPaper = reference.get();
            if (activityNewsPaper==null||activityNewsPaper.isDestroyed()) return;

            updateViewPrepare(activityNewsPaper);
        }
        @Override
        protected ArrayList<BeanNewsPaperPhoto> doInBackground(Void... voids) {
            return fetchPostPeriod();
        }
        @Override
        protected void onPostExecute(ArrayList<BeanNewsPaperPhoto> beanNewsPaperPhotos) {
            super.onPostExecute(beanNewsPaperPhotos);

            ActivityNewsPaper activityNewsPaper = reference.get();
            if (activityNewsPaper==null||activityNewsPaper.isDestroyed()) return;

            if (beanNewsPaperPhotos!=null){
                activityNewsPaper.loaded[3] = true;
                updateViewSuccessful(activityNewsPaper, beanNewsPaperPhotos);
            }
            else {
                updateViewFailed(activityNewsPaper);
            }

        }


        private ArrayList<BeanNewsPaperPhoto> fetchPostPeriod(){
            SecretResource secretResource = new SecretResource();
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .build();
            Request request = new Request.Builder()
                    .url(secretResource.getURL_NewsPaperGallery()+".xml")
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
                    ArrayList<String[]> periods = parseXMLPeriod(response.body().byteStream());
                    ArrayList<BeanNewsPaperPhoto> photos = new ArrayList<>();
                    for (String[] period:periods) photos = fetchPostPhoto(photos, period);
                    return photos;
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        private ArrayList<BeanNewsPaperPhoto> fetchPostPhoto(ArrayList<BeanNewsPaperPhoto> photos, String[] period){
            SecretResource secretResource = new SecretResource();
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .build();
            Request request = new Request.Builder()
                    .url(secretResource.getURL_NewsPaperGallery()+"-"+period[0]+".xml")
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
                    photos = parseXMLPhoto(photos, period, response.body().byteStream());
                    return photos;
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        private ArrayList<String[]> parseXMLPeriod(InputStream xml) throws XmlPullParserException, IOException {
            ArrayList<String[]> periods = null;
            String[] period = null;
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(xml, "utf-16");
            int eventType = parser.getEventType();
            while (eventType!=XmlPullParser.END_DOCUMENT){
                String name = parser.getName();
                switch (eventType){
                    case XmlPullParser.START_DOCUMENT:
                        if (periods==null) periods = new ArrayList<>();
                        break;
                    case XmlPullParser.START_TAG:
                        if (name.equals("pic_db")) period = new String[]{null, null};
                        else if (name.equals("期別")) period[0] = parser.nextText().trim();
                        else if (name.equals("發佈日期")) period[1] = parser.nextText().trim();
                        break;
                    case XmlPullParser.END_TAG:
                        if (name.equals("pic_db")){
                            if (period!=null){
                                periods.add(period);
                                period = null;
                            }
                        }
                        break;
                }
                eventType = parser.next();
            }
            return periods;
        }
        private ArrayList<BeanNewsPaperPhoto> parseXMLPhoto(ArrayList<BeanNewsPaperPhoto> photos, String[] period, InputStream xml) throws XmlPullParserException, IOException {
            SecretResource secretResource = new SecretResource();
            BeanNewsPaperPhoto photo = null;
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(xml, "utf-16");
            int eventType = parser.getEventType();
            while (eventType!=XmlPullParser.END_DOCUMENT){
                String name = parser.getName();
                switch (eventType){
                    case XmlPullParser.START_TAG:
                        if (name.equals("pic_db")) photo = new BeanNewsPaperPhoto();
                        else if (name.equals("圖片標題")) photo.setTitle(parser.nextText().trim());
                        else if (name.equals("圖片路徑")) photo.setUrl(secretResource.getURL_NewsPaperLatestImg()+parser.nextText().trim());
                        else if (name.equals("攝影者")) photo.setPhotographer(parser.nextText().trim());
                        break;
                    case XmlPullParser.END_TAG:
                        if (name.equals("pic_db")){
                            if (period!=null){
                                photo.setPeriod(period[0]);
                                photo.setPubdate(period[1]);
                                photos.add(photo);
                            }
                        }
                        break;
                }
                eventType = parser.next();
            }
            return photos;
        }

        private void updateViewPrepare(ActivityNewsPaper activityNewsPaper){
            SwipeRefreshLayout swipeRefreshLayout = ((AdapterViewPagerNewsPaper)activityNewsPaper.viewPager.getAdapter()).getItemView(3).findViewById(R.id.newspaper_viewpager_gallery_swiperefreshlayout);
            if (!swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(true);
        }
        private void updateViewSuccessful(ActivityNewsPaper activityNewsPaper, ArrayList<BeanNewsPaperPhoto> photos){
            SwipeRefreshLayout swipeRefreshLayout = ((AdapterViewPagerNewsPaper)activityNewsPaper.viewPager.getAdapter()).getItemView(3).findViewById(R.id.newspaper_viewpager_gallery_swiperefreshlayout);
            RecyclerView recyclerView = ((AdapterViewPagerNewsPaper)activityNewsPaper.viewPager.getAdapter()).getItemView(3).findViewById(R.id.newspaper_viewpager_gallery_recyclerview);

            AdapterRecyclerViewNewsPaperPhoto adapter = new AdapterRecyclerViewNewsPaperPhoto();
            for (BeanNewsPaperPhoto photo:photos) adapter.addItem(photo);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(activityNewsPaper));

            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
        }
        private void updateViewFailed(ActivityNewsPaper activityNewsPaper){
            SwipeRefreshLayout swipeRefreshLayout = ((AdapterViewPagerNewsPaper)activityNewsPaper.viewPager.getAdapter()).getItemView(3).findViewById(R.id.newspaper_viewpager_gallery_swiperefreshlayout);
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(activityNewsPaper, "更新近期圖輯資料失敗，請重試", Toast.LENGTH_SHORT).show();
        }
    }
}
