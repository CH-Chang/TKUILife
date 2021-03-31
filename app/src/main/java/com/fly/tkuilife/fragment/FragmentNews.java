package com.fly.tkuilife.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.fly.tkuilife.R;
import com.fly.tkuilife.adapter.AdapterRecyclerViewEvent;
import com.fly.tkuilife.adapter.AdapterRecyclerViewMonitor;
import com.fly.tkuilife.adapter.AdapterRecyclerViewRecentAnnounce;
import com.fly.tkuilife.adapter.AdapterRecyclerViewTodayAnnounce;
import com.fly.tkuilife.adapter.AdapterViewPagerNews;
import com.fly.tkuilife.bean.BeanEvent;
import com.fly.tkuilife.bean.BeanRecentAnnounce;
import com.fly.tkuilife.bean.BeanTodayAnnounce;
import com.fly.tkuilife.utils.RecyclerViewItemClickSupport;
import com.fly.tkuilife.utils.SecretResource;
import com.fly.tkuilife.view.TextRoundProgress;
import com.google.android.material.tabs.TabLayout;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FragmentNews extends Fragment implements TabLayout.OnTabSelectedListener, RecyclerViewItemClickSupport.OnItemClickListener {

    private View view;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private boolean[] loaded;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_news, container, false);
        init();
        return view;
    }
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden){ }
        else {
            loading(tabLayout.getSelectedTabPosition());
        }
    }
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        loading(tab.getPosition());
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
            case R.id.news_viewpager_monitor_recyclerview:
                new FetchPostMonitorImg(this).execute(position);
                break;
        }
    }

    private void init(){
        initInstantation();
        initView();
        initViepager();
        initOther();
        initTabLayout();
        initInteraction();
    }
    private void initInstantation(){
    }
    private void initView(){
        tabLayout = (TabLayout) view.findViewById(R.id.news_tablayout);
        viewPager = (ViewPager) view.findViewById(R.id.news_viewpager);
    }
    private void initViepager(){
        AdapterViewPagerNews adapter = new AdapterViewPagerNews();
        adapter.addItemView(LayoutInflater.from(getContext()).inflate(R.layout.layout_news_viewpager_pc, null));
        adapter.addItemView(LayoutInflater.from(getContext()).inflate(R.layout.layout_news_viewpager_monitor, null));
        adapter.addItemView(LayoutInflater.from(getContext()).inflate(R.layout.layout_news_viewpager_todayannounce, null));
        adapter.addItemView(LayoutInflater.from(getContext()).inflate(R.layout.layout_news_viewpager_recentannounce, null));
        adapter.addItemView(LayoutInflater.from(getContext()).inflate(R.layout.layout_news_viewpager_event, null));
        viewPager.setAdapter(adapter);
    }
    private void initTabLayout(){
        String[] tab = {"實習機位", "即時影像", "本日訊息", "近期消息", "近期活動"};
        for(int i=0;i<tab.length;i++) tabLayout.addTab(tabLayout.newTab());
        tabLayout.setupWithViewPager(viewPager, false);
        for (int i=0;i<tab.length;i++) tabLayout.getTabAt(i).setText(tab[i]);
    }
    private void initInteraction(){
        tabLayout.addOnTabSelectedListener(this);

        AdapterViewPagerNews adapter = (AdapterViewPagerNews)viewPager.getAdapter();

        ((SwipeRefreshLayout)adapter.getItemView(0).findViewById(R.id.news_viewpager_pc_swiperefreshlayout)).setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadingPc();
            }
        });
        ((SwipeRefreshLayout)adapter.getItemView(2).findViewById(R.id.news_viewpager_todayannounce_swiperefreshlayout)).setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadingTodayAnnounce();
            }
        });
        ((SwipeRefreshLayout)adapter.getItemView(3).findViewById(R.id.news_viewpager_recentannounce_swiperefreshlayout)).setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadingRecentAnnounce();
            }
        });
        ((SwipeRefreshLayout)adapter.getItemView(4).findViewById(R.id.news_viewpager_event_swiperefreshlayout)).setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadingEvent();
            }
        });

        RecyclerViewItemClickSupport.addTo((RecyclerView) adapter.getItemView(1).findViewById(R.id.news_viewpager_monitor_recyclerview)).setOnItemClickListener(this);

    }
    private void initOther(){
        loaded = new boolean[]{false,false,false,false,false};
    }

    private void loading(){
        loadingPc();
        loadingMonitor();
        loadingTodayAnnounce();
        loadingRecentAnnounce();
        loadingEvent();
    }
    private void loading(int position){
        switch (position){
            case 0:
                if (!loaded[0]){
                    ((SwipeRefreshLayout)((AdapterViewPagerNews)viewPager.getAdapter()).getItemView(0).findViewById(R.id.news_viewpager_pc_swiperefreshlayout)).setRefreshing(true);
                    loadingPc();
                }
                break;
            case 1:
                if (!loaded[1]){
                    loadingMonitor();
                }
                break;
            case 2:
                if (!loaded[2]){
                    ((SwipeRefreshLayout)((AdapterViewPagerNews)viewPager.getAdapter()).getItemView(2).findViewById(R.id.news_viewpager_todayannounce_swiperefreshlayout)).setRefreshing(true);
                    loadingTodayAnnounce();
                }
                break;
            case 3:
                if (!loaded[3]){
                    ((SwipeRefreshLayout)((AdapterViewPagerNews)viewPager.getAdapter()).getItemView(3).findViewById(R.id.news_viewpager_recentannounce_swiperefreshlayout)).setRefreshing(true);
                    loadingRecentAnnounce();
                }
                break;
            case 4:
                if (!loaded[4]){
                    ((SwipeRefreshLayout)((AdapterViewPagerNews)viewPager.getAdapter()).getItemView(4).findViewById(R.id.news_viewpager_event_swiperefreshlayout)).setRefreshing(true);
                    loadingEvent();
                }
                break;
        }
    }
    private void loadingMonitor(){
        AdapterViewPagerNews adapter = (AdapterViewPagerNews) viewPager.getAdapter();
        RecyclerView recyclerView = adapter.getItemView(1).findViewById(R.id.news_viewpager_monitor_recyclerview);
        ImageView imageView = adapter.getItemView(1).findViewById(R.id.news_viewpager_monitor_monitor);
        String[] monitors = new String[]{"紅27公車站","紅28公車站","操場", "籃球場", "網球場", "五虎崗球場", "商管三樓電梯", "郵局"};
        AdapterRecyclerViewMonitor adapter_recyclerview = new AdapterRecyclerViewMonitor(monitors);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter_recyclerview);
        recyclerView.setNestedScrollingEnabled(false);
        loaded[1] = true;
    }
    private void loadingTodayAnnounce(){
        new FetchPostTodayAnnounce(this).execute();
    }
    private void loadingRecentAnnounce(){
        new FetchPostRecentAnnounce(this).execute();
    }
    private void loadingEvent(){
        new FetchPostEvent(this).execute();
    }
    private void loadingPc(){
        new FetchPostPc(this).execute();
    }


    private static class FetchPostTodayAnnounce extends AsyncTask<Void, Void, ArrayList<BeanTodayAnnounce>>{
        private WeakReference<FragmentNews> reference;

        public FetchPostTodayAnnounce(FragmentNews fragmentNews){
            reference = new WeakReference<FragmentNews>(fragmentNews);
        }

        @Override
        protected ArrayList<BeanTodayAnnounce> doInBackground(Void... voids) {
            return fetchPost();
        }
        @Override
        protected void onPostExecute(ArrayList<BeanTodayAnnounce> announces) {
            super.onPostExecute(announces);

            FragmentNews fragmentNews = reference.get();
            if (fragmentNews==null||fragmentNews.isDetached()) return;

            if (announces!=null){
                updateViewTodayAnnounce(fragmentNews, announces);
                fragmentNews.loaded[2] = true;
            }
            else {
                updateViewFailed(fragmentNews);
            }
        }

        private ArrayList<BeanTodayAnnounce> fetchPost(){
            SecretResource secretResource = new SecretResource();
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(5,TimeUnit.SECONDS)
                    .build();
            Request request = new Request.Builder()
                    .url(secretResource.getURL_TodayAnnounce())
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
                    ArrayList<BeanTodayAnnounce> announces = parseXML(response.body().byteStream());
                    return announces;
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        private ArrayList<BeanTodayAnnounce> parseXML(InputStream xml) throws XmlPullParserException, IOException {
            ArrayList<BeanTodayAnnounce> announces = null;
            BeanTodayAnnounce announce = null;
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(xml, "utf-16");
            int announcType = parser.getEventType();
            while (announcType!=XmlPullParser.END_DOCUMENT){
                String name = parser.getName();
                switch (announcType){
                    case XmlPullParser.START_DOCUMENT:
                        announces = new ArrayList<>();
                        break;
                    case XmlPullParser.START_TAG:
                        if (name.equals("today")) announce = new BeanTodayAnnounce();
                        else if (name.equals("編號")) announce.setId(parser.nextText().trim());
                        else if (name.equals("公告")) announce.setTitle(parser.nextText().trim());
                        else if (name.equals("類別")) announce.setKind(parser.nextText().trim());
                        else if (name.equals("活動開始日期")) announce.setStart(parser.nextText().trim());
                        else if (name.equals("活動結束日期")) announce.setEnd(parser.nextText().trim());
                        break;
                    case XmlPullParser.END_TAG:
                        if (name.equals("today")){
                            announces.add(announce);
                            announce = null;
                        }
                        break;
                    default:
                        break;
                }
                announcType = parser.next();
            }
            return announces;
        }
        private void updateViewTodayAnnounce(FragmentNews fragmentNews, ArrayList<BeanTodayAnnounce> announces){
            SwipeRefreshLayout swipeRefreshLayout = ((AdapterViewPagerNews)fragmentNews.viewPager.getAdapter()).getItemView(2).findViewById(R.id.news_viewpager_todayannounce_swiperefreshlayout);
            RecyclerView recyclerView = ((AdapterViewPagerNews)fragmentNews.viewPager.getAdapter()).getItemView(2).findViewById(R.id.news_viewpager_todayannounce_recyclerview);

            AdapterRecyclerViewTodayAnnounce adapter = new AdapterRecyclerViewTodayAnnounce();
            for (BeanTodayAnnounce announce: announces) adapter.addItem(announce);

            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(fragmentNews.getContext()));

            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
        }
        private void updateViewFailed(FragmentNews fragmentNews){
            SwipeRefreshLayout swipeRefreshLayout = ((AdapterViewPagerNews)fragmentNews.viewPager.getAdapter()).getItemView(2).findViewById(R.id.news_viewpager_todayannounce_swiperefreshlayout);
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);

            Toast.makeText(fragmentNews.getContext(), "讀取本日訊息失敗，請重試", Toast.LENGTH_SHORT).show();
        }
    }
    private static class FetchPostRecentAnnounce extends AsyncTask<Void, Void, ArrayList<BeanRecentAnnounce>>{
        private WeakReference<FragmentNews> reference;

        public FetchPostRecentAnnounce(FragmentNews fragmentNews){
            reference = new WeakReference<FragmentNews>(fragmentNews);
        }

        @Override
        protected ArrayList<BeanRecentAnnounce> doInBackground(Void... voids) {
            return fetchPost();
        }
        @Override
        protected void onPostExecute(ArrayList<BeanRecentAnnounce> announces) {
            super.onPostExecute(announces);

            FragmentNews fragmentNews = reference.get();
            if (fragmentNews.isDetached()||fragmentNews==null) return;

            if (announces!=null){
                updateViewRecentAnnounce(fragmentNews, announces);
                fragmentNews.loaded[3] = true;
            }
            else {
                updateViewFailed(fragmentNews);
            }
        }

        private ArrayList<BeanRecentAnnounce> fetchPost(){
            SecretResource secretResource = new SecretResource();
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(5,TimeUnit.SECONDS)
                    .build();
            Request request = new Request.Builder()
                    .url(secretResource.getURL_RecentAnnounce())
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
                    ArrayList<BeanRecentAnnounce> announces = parseXML(response.body().byteStream());
                    return announces;
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        private ArrayList<BeanRecentAnnounce> parseXML(InputStream xml) throws XmlPullParserException, IOException {
            ArrayList<BeanRecentAnnounce> announces = null;
            BeanRecentAnnounce announce = null;
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(xml, "utf-16");
            int eventType = parser.getEventType();
            while (eventType!=XmlPullParser.END_DOCUMENT){
                String name = parser.getName();
                switch (eventType){
                    case XmlPullParser.START_DOCUMENT:
                        announces = new ArrayList<>();
                        break;
                    case XmlPullParser.START_TAG:
                        if (name.equals("news")) announce = new BeanRecentAnnounce();
                        else if (name.equals("編號")) announce.setId(parser.nextText().trim());
                        else if (name.equals("公告")) announce.setTitle(parser.nextText().trim());
                        else if (name.equals("類別")) announce.setKind(parser.nextText().trim());
                        else if (name.equals("活動開始日期")) announce.setStart(parser.nextText().trim());
                        else if (name.equals("活動結束日期")) announce.setEnd(parser.nextText().trim());
                        break;
                    case XmlPullParser.END_TAG:
                        if (name.equals("news")){
                            announces.add(announce);
                            announce = null;
                        }
                        break;
                    default:
                        break;
                }
                eventType = parser.next();
            }
            return announces;
        }
        private void updateViewRecentAnnounce(FragmentNews fragmentNews, ArrayList<BeanRecentAnnounce> announces){
            SwipeRefreshLayout swipeRefreshLayout = ((AdapterViewPagerNews)fragmentNews.viewPager.getAdapter()).getItemView(3).findViewById(R.id.news_viewpager_recentannounce_swiperefreshlayout);
            RecyclerView recyclerView = ((AdapterViewPagerNews)fragmentNews.viewPager.getAdapter()).getItemView(3).findViewById(R.id.news_viewpager_recentannounce_recyclerview);

            AdapterRecyclerViewRecentAnnounce adapter = new AdapterRecyclerViewRecentAnnounce();
            for (BeanRecentAnnounce announce:announces) adapter.addItem(announce);

            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(fragmentNews.getContext()));

            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
        }
        private void updateViewFailed(FragmentNews fragmentNews){
            SwipeRefreshLayout swipeRefreshLayout = ((AdapterViewPagerNews)fragmentNews.viewPager.getAdapter()).getItemView(3).findViewById(R.id.news_viewpager_recentannounce_swiperefreshlayout);
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);

            Toast.makeText(fragmentNews.getContext(), "獲取近期消息失敗，請重試", Toast.LENGTH_SHORT).show();
        }
    }
    private static class FetchPostEvent extends AsyncTask<Void, Void, ArrayList<BeanEvent>>{
        private WeakReference<FragmentNews> reference;

        public FetchPostEvent(FragmentNews fragmentNews){
            reference = new WeakReference<FragmentNews>(fragmentNews);
        }

        @Override
        protected ArrayList<BeanEvent> doInBackground(Void... voids) {
            return fetchPost();
        }
        @Override
        protected void onPostExecute(ArrayList<BeanEvent> events) {
            super.onPostExecute(events);

            FragmentNews fragmentNews = reference.get();
            if (fragmentNews==null||fragmentNews.isDetached()) return;

            if (events!=null){
                updateViewEvent(fragmentNews, events);
                fragmentNews.loaded[4] = true;
            }
            else updateViewFailed(fragmentNews);
        }

        private ArrayList<BeanEvent> fetchPost(){
            SecretResource secretResource = new SecretResource();
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(5,TimeUnit.SECONDS)
                    .build();
            Request request = new Request.Builder()
                    .url(secretResource.getURL_Event())
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
                    ArrayList<BeanEvent> events = parseXML(response.body().byteStream());
                    return events;
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        private ArrayList<BeanEvent> parseXML(InputStream xml) throws XmlPullParserException, IOException {
            ArrayList<BeanEvent> events = null;
            BeanEvent event = null;
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(xml, "utf-16");
            int eventType = parser.getEventType();
            while (eventType!=XmlPullParser.END_DOCUMENT){
                String name = parser.getName();
                switch (eventType){
                    case XmlPullParser.START_DOCUMENT:
                        events = new ArrayList<>();
                        break;
                    case XmlPullParser.START_TAG:
                        if (name.equals("act")) event = new BeanEvent();
                        else if (name.equals("連結編號")) event.setId(parser.nextText().trim());
                        else if (name.equals("課程名稱")) event.setTitle(parser.nextText().trim());
                        else if (name.equals("開課類別")) event.setKind(parser.nextText().trim());
                        else if(name.equals("上課地點")) event.setRoom(parser.nextText().trim());
                        else if (name.equals("上課起始時間")) event.setClass_start(parser.nextText().trim());
                        else if (name.equals("上課結束時間")) event.setClass_end(parser.nextText().trim());
                        else if (name.equals("開始報名時間")) event.setSign_start(parser.nextText().trim());
                        else if (name.equals("結束報名時間")) event.setSign_end(parser.nextText().trim());
                        else if (name.equals("開課對象")) event.setStatus(parser.nextText().trim());
                        break;
                    case XmlPullParser.END_TAG:
                        if (name.equals("act")){
                            events.add(event);
                            event = null;
                        }
                        break;
                    default:
                        break;
                }
                eventType = parser.next();
            }
            return events;
        }
        private void updateViewEvent(FragmentNews fragmentNews, ArrayList<BeanEvent> events){
            SwipeRefreshLayout swipeRefreshLayout = ((AdapterViewPagerNews)fragmentNews.viewPager.getAdapter()).getItemView(4).findViewById(R.id.news_viewpager_event_swiperefreshlayout);
            RecyclerView recyclerView = ((AdapterViewPagerNews)fragmentNews.viewPager.getAdapter()).getItemView(4).findViewById(R.id.news_viewpager_event_recyclerview);

            AdapterRecyclerViewEvent adapter = new AdapterRecyclerViewEvent();
            for (BeanEvent event:events) adapter.addItem(event);

            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(fragmentNews.getContext()));

            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
        }
        private void updateViewFailed(FragmentNews fragmentNews){
            SwipeRefreshLayout swipeRefreshLayout = ((AdapterViewPagerNews)fragmentNews.viewPager.getAdapter()).getItemView(4).findViewById(R.id.news_viewpager_event_swiperefreshlayout);
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);

            Toast.makeText(fragmentNews.getContext(), "獲取近期活動訊息失敗，請重試", Toast.LENGTH_SHORT).show();
        }
    }
    private static class FetchPostMonitorImg extends AsyncTask<Integer, Void, Bitmap>{
        private WeakReference<FragmentNews> reference;

        public FetchPostMonitorImg(FragmentNews fragmentNews){
            reference = new WeakReference<FragmentNews>(fragmentNews);
        }

        @Override
        protected Bitmap doInBackground(Integer... integers) {
            return fetchPost(integers[0]);
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            FragmentNews fragmentNews = reference.get();
            if (fragmentNews==null||fragmentNews.isDetached()) return;

            if (bitmap!=null) updateViewMonitorImg(fragmentNews, bitmap);
            else updateViewFailed(fragmentNews);
        }

        private Bitmap fetchPost(int position){
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .build();
            Request request = new Request.Builder()
                    .url(getURL(position))
                    .method("GET", null)
                    .build();
            Response response = null;
            try {
                response = okHttpClient.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response!=null&&response.isSuccessful()){
                InputStream inputStream = response.body().byteStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            }
            return null;
        }
        private String getURL(int position){
            SecretResource secretResource = new SecretResource();
            switch (position){
                case 0:
                    return secretResource.getURL_MonitorBusStop27();
                case 1:
                    return secretResource.getURL_MonitorBusStop28();
                case 2:
                    return secretResource.getURL_MonitorPlayground();
                case 3:
                    return secretResource.getURL_MonitorBasketballCourt();
                case 4:
                    return secretResource.getURL_MonitorTennisCourt();
                case 5:
                    return secretResource.getURL_MonitorFiveTiger();
                case 6:
                    return secretResource.getURL_MonitorElevator();
                case 7:
                    return secretResource.getURL_MonitorPostOffice();
                default:
                    return null;

            }
        }
        private void updateViewMonitorImg(FragmentNews fragmentNews, Bitmap bitmap){
            ImageView imageView = ((AdapterViewPagerNews)fragmentNews.viewPager.getAdapter()).getItemView(1).findViewById(R.id.news_viewpager_monitor_monitor);
            imageView.setImageBitmap(bitmap);
        }
        private void updateViewFailed(FragmentNews fragmentNews){
            Toast.makeText(fragmentNews.getContext(), "獲取監視器畫面失敗，請重試", Toast.LENGTH_SHORT).show();
        }
    }
    private static class FetchPostPc extends AsyncTask<Void, Void, ArrayList<String[]>>{
        private WeakReference<FragmentNews> reference;

        public FetchPostPc(FragmentNews fragmentNews){
            reference = new WeakReference<FragmentNews>(fragmentNews);
        }

        @Override
        protected ArrayList<String[]> doInBackground(Void... voids) {
            return fetchPost();
        }
        @Override
        protected void onPostExecute(ArrayList<String[]> strings) {
            super.onPostExecute(strings);

            FragmentNews fragmentNews = reference.get();
            if (fragmentNews==null||fragmentNews.isDetached()) return;

            if (strings!=null){
                updateViewPc(fragmentNews, strings);
                fragmentNews.loaded[0] = true;
            }
        }

        private ArrayList<String[]> fetchPost(){
            SecretResource secretResource = new SecretResource();
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(5,TimeUnit.SECONDS)
                    .build();
            Request request = new Request.Builder()
                    .url(secretResource.getURL_Pc())
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
                    ArrayList<String[]> pc_situation;
                    InputStream inputStream = response.body().byteStream();
                    StringBuffer stringBuffer = new StringBuffer();
                    if(inputStream!=null){
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
                        while(true){
                            String str = bufferedReader.readLine();
                            if(str!=null){
                                stringBuffer.append(str);
                                continue;
                            }
                            break;
                        }
                        String[] ori = stringBuffer.toString().split("<br>");
                        pc_situation = new ArrayList<>();
                        pc_situation.add((ori[0].split("<span id=\"Label1\">")[1]).split(","));
                        for(int i=1;i<ori.length;i++) pc_situation.add(ori[i].split(","));
                        return pc_situation;
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        private void updateViewPc(FragmentNews fragmentNews, ArrayList<String[]> pc_situation){
            AdapterViewPagerNews adapter = (AdapterViewPagerNews) fragmentNews.viewPager.getAdapter();

            SwipeRefreshLayout swipeRefreshLayout =adapter.getItemView(0).findViewById(R.id.news_viewpager_pc_swiperefreshlayout);

            ArrayList<TextRoundProgress> textRoundProgresses = new ArrayList<>();
            textRoundProgresses.add((TextRoundProgress)adapter.getItemView(0).findViewById(R.id.news_viewpager_pc_pc_1));
            textRoundProgresses.add((TextRoundProgress)adapter.getItemView(0).findViewById(R.id.news_viewpager_pc_pc_2));
            textRoundProgresses.add((TextRoundProgress)adapter.getItemView(0).findViewById(R.id.news_viewpager_pc_pc_3));
            textRoundProgresses.add((TextRoundProgress)adapter.getItemView(0).findViewById(R.id.news_viewpager_pc_pc_4));
            textRoundProgresses.add((TextRoundProgress)adapter.getItemView(0).findViewById(R.id.news_viewpager_pc_pc_5));
            textRoundProgresses.add((TextRoundProgress)adapter.getItemView(0).findViewById(R.id.news_viewpager_pc_pc_6));
            textRoundProgresses.add((TextRoundProgress)adapter.getItemView(0).findViewById(R.id.news_viewpager_pc_pc_7));

            for(int i=0;i<textRoundProgresses.size();i++){
                textRoundProgresses.get(i).setText(pc_situation.get(i)[0]);
                textRoundProgresses.get(i).setMax(Integer.valueOf(pc_situation.get(i)[1]));
                new AnimLoadingPc(Integer.valueOf(pc_situation.get(i)[2]), Integer.valueOf(pc_situation.get(i)[1]), textRoundProgresses.get(i)).start();
            }

            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
        }
    }
    private static class AnimLoadingPc extends Thread{
        private int now, max, i;
        private TextRoundProgress textRoundProgress;
        public AnimLoadingPc(int now, int max, TextRoundProgress textRoundProgress){
            this.now = now;
            this.max = max;
            this.textRoundProgress = textRoundProgress;
            this.i = 1;
        }
        @Override
        public void run() {
            super.run();
            if (max!=0){
                while(true){
                    textRoundProgress.setProgress(i);
                    i++;
                    if (i==now+1) break;
                    else {
                        try {
                            sleep(25);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }
    }



}
