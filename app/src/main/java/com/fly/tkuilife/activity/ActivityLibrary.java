package com.fly.tkuilife.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.fly.tkuilife.adapter.AdapterRecyclerViewEResourcesNews;
import com.fly.tkuilife.adapter.AdapterRecyclerViewLibraryOpen;
import com.fly.tkuilife.adapter.AdapterViewPagerLibrary;
import com.fly.tkuilife.bean.BeanEResourcesNews;
import com.fly.tkuilife.bean.BeanLibraryOpen;
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

public class ActivityLibrary extends AppCompatActivity implements TabLayout.OnTabSelectedListener, RecyclerViewItemClickSupport.OnItemClickListener {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

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
                if (!loaded[0]) loadingLibraryOpen();
            case 4:
                if (!loaded[4]) loadingEResourcesNews();
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
            case R.id.library_viewpager_eresourcesnews_recyclerview:
                startActivity(new Intent().setAction(Intent.ACTION_VIEW).setData(Uri.parse(((AdapterRecyclerViewEResourcesNews)recyclerView.getAdapter()).getItem(position).getLink())));
                break;
        }
    }

    private void init(){
        initView();
        initOther();
        initActionBar();
        initViewpager();
        initInteraction();
        initTablayout();
    }
    private void initView(){
        setContentView(R.layout.activity_library);
        toolbar = findViewById(R.id.library_toolbar);
        tabLayout = findViewById(R.id.library_tablayout);
        viewPager = findViewById(R.id.library_viewpager);
    }
    private void initActionBar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    private void initViewpager(){
        AdapterViewPagerLibrary adapter = new AdapterViewPagerLibrary();

        adapter.addItemView(LayoutInflater.from(this).inflate(R.layout.layout_library_viewpager_libraryopen, null));
        adapter.addItemView(LayoutInflater.from(this).inflate(R.layout.layout_preparing, null));
        adapter.addItemView(LayoutInflater.from(this).inflate(R.layout.layout_preparing, null));
        adapter.addItemView(LayoutInflater.from(this).inflate(R.layout.layout_preparing, null));
        adapter.addItemView(LayoutInflater.from(this).inflate(R.layout.layout_library_viewpager_eresourcesnews, null));

        viewPager.setAdapter(adapter);
    }
    private void initTablayout(){
        String[] tab = {"開放時間","借閱紀錄","逾期紀錄","預約到館","電子資源新訊"};
        for (int i=0;i<tab.length;i++) tabLayout.addTab(tabLayout.newTab());
        tabLayout.setupWithViewPager(viewPager);
        for (int i=0;i<tab.length;i++) tabLayout.getTabAt(i).setText(tab[i]);
    }
    private void initOther(){
        loaded = new boolean[]{false, false, false, false, false};
    }
    private void initInteraction(){
        tabLayout.addOnTabSelectedListener(this);

        AdapterViewPagerLibrary adapter = (AdapterViewPagerLibrary)viewPager.getAdapter();

        ((SwipeRefreshLayout)(adapter.getItemView(0).findViewById(R.id.library_viewpager_libraryopen_swiperefreshlayout))).setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new FetchPostLibraryOpen(ActivityLibrary.this).execute();
            }
        });
        ((SwipeRefreshLayout)(adapter.getItemView(4).findViewById(R.id.library_viewpager_eresourcesnews_swiperefreshlayout))).setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new FetchPostEResourcesNews(ActivityLibrary.this).execute();
            }
        });
        RecyclerViewItemClickSupport.addTo((RecyclerView) adapter.getItemView(4).findViewById(R.id.library_viewpager_eresourcesnews_recyclerview)).setOnItemClickListener(this);
    }


    private void loadingEResourcesNews(){
        new FetchPostEResourcesNews(this).execute();
    }
    private void loadingLibraryOpen(){
        new FetchPostLibraryOpen(this).execute();
    }


    private static class FetchPostEResourcesNews extends AsyncTask<Void, Void, ArrayList<BeanEResourcesNews>>{
        private WeakReference<ActivityLibrary> reference;

        public FetchPostEResourcesNews(ActivityLibrary activityLibrary){
            reference = new WeakReference<ActivityLibrary>(activityLibrary);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            ActivityLibrary activityLibrary = reference.get();
            if (activityLibrary==null||activityLibrary.isDestroyed()) return;

            updateViewPrepare(activityLibrary);
        }
        @Override
        protected ArrayList<BeanEResourcesNews> doInBackground(Void... voids) {
            return fetchPost();
        }
        @Override
        protected void onPostExecute(ArrayList<BeanEResourcesNews> beanEResourcesNews) {
            super.onPostExecute(beanEResourcesNews);

            ActivityLibrary activityLibrary = reference.get();
            if (activityLibrary==null||activityLibrary.isDestroyed()) return;

            if (beanEResourcesNews!=null)  {
                activityLibrary.loaded[4] = true;
                updateViewSuccessful(activityLibrary, beanEResourcesNews);
            }
            else updateViewFailed(activityLibrary);
        }

        private ArrayList<BeanEResourcesNews> fetchPost(){
            SecretResource secretResource = new SecretResource();
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .build();
            Request request = new Request.Builder()
                    .url(secretResource.getURL_EResourcesNews())
                    .method("GET",null)
                    .build();
            Response response = null;
            try {
                response = okHttpClient.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response!=null&&response.isSuccessful()){
                try {
                    return parseXML(response.body().byteStream());
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        private ArrayList<BeanEResourcesNews> parseXML(InputStream xml) throws XmlPullParserException, IOException {
            ArrayList<BeanEResourcesNews> resources = null;
            BeanEResourcesNews resource = null;
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(xml, "utf-8");
            int eventType = parser.getEventType();
            boolean firsttitle = true;
            boolean firstlink = true;
            while (eventType!=XmlPullParser.END_DOCUMENT){
                String name = parser.getName();
                switch (eventType){
                    case XmlPullParser.START_DOCUMENT:
                        resources = new ArrayList<>();
                        break;
                    case XmlPullParser.START_TAG:
                        if (name.equals("item")) resource = new BeanEResourcesNews();
                        else if (name.equals("title")&&firsttitle) firsttitle=false;
                        else if (name.equals("title")&&!firsttitle) resource.setTitle(parser.nextText().trim());
                        else if (name.equals("description")) resource.setDecription(parser.nextText().trim());
                        else if (name.equals("pubDate")) resource.setDate(parser.nextText().trim());
                        else if (name.equals("link")&&firstlink) firstlink=false;
                        else if (name.equals("link")&&!firstlink) resource.setLink(parser.nextText().trim());
                        break;
                    case XmlPullParser.END_TAG:
                        if (name.equals("item")){
                            if (resource!=null){
                                resources.add(resource);
                                resource = null;
                            }
                        }
                        break;
                }
                eventType = parser.next();
            }
            return resources;
        }

        private void updateViewPrepare(ActivityLibrary activityLibrary){
            SwipeRefreshLayout swipeRefreshLayout = ((AdapterViewPagerLibrary)activityLibrary.viewPager.getAdapter()).getItemView(4).findViewById(R.id.library_viewpager_eresourcesnews_swiperefreshlayout);
            if (!swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(true);
        }
        private void updateViewSuccessful(ActivityLibrary activityLibrary, ArrayList<BeanEResourcesNews> resources){
            SwipeRefreshLayout swipeRefreshLayout = ((AdapterViewPagerLibrary)activityLibrary.viewPager.getAdapter()).getItemView(4).findViewById(R.id.library_viewpager_eresourcesnews_swiperefreshlayout);
            RecyclerView recyclerView = ((AdapterViewPagerLibrary)activityLibrary.viewPager.getAdapter()).getItemView(4).findViewById(R.id.library_viewpager_eresourcesnews_recyclerview);

            AdapterRecyclerViewEResourcesNews adapter = new AdapterRecyclerViewEResourcesNews();
            for (BeanEResourcesNews resource:resources) adapter.addItem(resource);

            recyclerView.setLayoutManager(new LinearLayoutManager(activityLibrary));
            recyclerView.setAdapter(adapter);

            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
        }
        private void updateViewFailed(ActivityLibrary activityLibrary){
            SwipeRefreshLayout swipeRefreshLayout = ((AdapterViewPagerLibrary)activityLibrary.viewPager.getAdapter()).getItemView(4).findViewById(R.id.library_viewpager_eresourcesnews_swiperefreshlayout);
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(activityLibrary, "獲取電子資源新訊錯誤，請重試", Toast.LENGTH_SHORT).show();
        }
    }
    private static class FetchPostLibraryOpen extends AsyncTask<Void, Void, ArrayList<BeanLibraryOpen>>{
        private WeakReference<ActivityLibrary> reference;

        public FetchPostLibraryOpen(ActivityLibrary activityLibrary){
            reference = new WeakReference<ActivityLibrary>(activityLibrary);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            ActivityLibrary activityLibrary = reference.get();
            if (activityLibrary==null||activityLibrary.isDestroyed()) return;

            updateViewPrepare(activityLibrary);
        }
        @Override
        protected ArrayList<BeanLibraryOpen> doInBackground(Void... voids) {
            return fetchPost();
        }
        @Override
        protected void onPostExecute(ArrayList<BeanLibraryOpen> beanLibraryOpens) {
            super.onPostExecute(beanLibraryOpens);

            ActivityLibrary activityLibrary = reference.get();
            if (activityLibrary==null||activityLibrary.isDestroyed()) return;

            if (beanLibraryOpens!=null){
                activityLibrary.loaded[0] =true;
                updateViewSuccessful(activityLibrary, beanLibraryOpens);
            }
            else {
                updateViewFailed(activityLibrary);
            }
        }

        private ArrayList<BeanLibraryOpen> fetchPost(){
            SecretResource secretResource = new SecretResource();
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .build();
            Request request = new Request.Builder()
                    .url(secretResource.getURL_LibraryOpen())
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
                    return parseXML(response.body().byteStream());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        private ArrayList<BeanLibraryOpen> parseXML(InputStream xml) throws IOException, XmlPullParserException {
            ArrayList<BeanLibraryOpen> opens = null;
            BeanLibraryOpen open = null;
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(xml, "utf-8");
            int eventType = parser.getEventType();
            while (eventType!=XmlPullParser.END_DOCUMENT){
                String name = parser.getName();
                switch (eventType){
                    case XmlPullParser.START_DOCUMENT:
                        opens = new ArrayList<>();
                        break;
                    case XmlPullParser.START_TAG:
                        if (name.equals("Item")) open = new BeanLibraryOpen();
                        else if (name.equals("Term")) open.setTerm(parser.nextText().trim());
                        else if (name.equals("Building")) open.setBuilding(parser.nextText().trim());
                        else if (name.equals("Weekday")){
                            String weekday = parser.nextText().trim().replace("/","~");
                            if (weekday.length()<2) weekday="週"+weekday;
                            open.setWeekday(weekday);
                        }
                        else if (name.equals("Hour")) open.setHour(parser.nextText().trim());
                        else if (name.equals("Period")){
                            String period = parser.nextText().trim();
                            String[] date = period.split(" - ");
                            for (int i=0;i<date.length;i++){
                                String[] num = date[i].split("/");
                                for (int j=0;j<num.length;j++) if (num[j].length()<2) num[j]="0"+num[j];
                                date[i] = num[0] +"/"+num[1];
                            }
                            if (date.length==1) open.setPeriod(date[0]);
                            else open.setPeriod(date[0]+"~"+date[1]);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (name.equals("Item")){
                            if (open!=null){
                                opens.add(open);
                                open = null;
                            }
                        }
                        break;
                }
                eventType = parser.next();
            }
            return opens;
        }

        private void updateViewPrepare(ActivityLibrary activityLibrary){
            SwipeRefreshLayout swipeRefreshLayout = ((AdapterViewPagerLibrary)activityLibrary.viewPager.getAdapter()).getItemView(0).findViewById(R.id.library_viewpager_libraryopen_swiperefreshlayout);
            if (!swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(true);
        }
        private void updateViewSuccessful(ActivityLibrary activityLibrary, ArrayList<BeanLibraryOpen> opens){
            SwipeRefreshLayout swipeRefreshLayout = ((AdapterViewPagerLibrary)activityLibrary.viewPager.getAdapter()).getItemView(0).findViewById(R.id.library_viewpager_libraryopen_swiperefreshlayout);
            RecyclerView recyclerView = ((AdapterViewPagerLibrary)activityLibrary.viewPager.getAdapter()).getItemView(0).findViewById(R.id.library_viewpager_libraryopen_recyclerview);

            AdapterRecyclerViewLibraryOpen adapter = new AdapterRecyclerViewLibraryOpen();
            for (BeanLibraryOpen open:opens) adapter.addItem(open);
            recyclerView.setLayoutManager(new LinearLayoutManager(activityLibrary));
            recyclerView.setAdapter(adapter);

            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
        }
        private void updateViewFailed(ActivityLibrary activityLibrary){
            SwipeRefreshLayout swipeRefreshLayout = ((AdapterViewPagerLibrary)activityLibrary.viewPager.getAdapter()).getItemView(0).findViewById(R.id.library_viewpager_libraryopen_swiperefreshlayout);
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(activityLibrary, "讀取圖書館開放時間失敗，請重試", Toast.LENGTH_SHORT).show();
        }
    }
}
