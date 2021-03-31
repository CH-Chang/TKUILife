package com.fly.tkuilife.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.fly.tkuilife.R;
import com.fly.tkuilife.adapter.AdapterRecyclerViewBus;
import com.fly.tkuilife.adapter.AdapterViewPagerBus;
import com.fly.tkuilife.bean.BeanStation;
import com.fly.tkuilife.utils.HmacHelper;
import com.fly.tkuilife.utils.SecretResource;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.http.RealResponseBody;
import okio.GzipSource;
import okio.Okio;

public class ActivityBus extends AppCompatActivity {

    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TextView time, title;

    private LoadingStatus loadingStatus;

    private Handler handler;

    private Dialog loading;

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
    protected void onDestroy() {
        if (loadingStatus!=null) loadingStatus.cancel();
        loading.dismiss();
        super.onDestroy();
    }

    private void init(){
        initView();
        initActionBar();
        initViewPager();
        initTabLayout();
        initHandler();
        initOther();
        initLoadingDialog();
    }
    private void initView(){
        setContentView(R.layout.activity_bus);
        toolbar = findViewById(R.id.bus_toolbar);
        tabLayout = findViewById(R.id.bus_tablayout);
        viewPager = findViewById(R.id.bus_viewpager);
        title = findViewById(R.id.bus_title);
        time = findViewById(R.id.bus_time);
    }
    private void initActionBar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }
    private void initViewPager(){
        AdapterViewPagerBus adapter = new AdapterViewPagerBus();

        adapter.addItemView(LayoutInflater.from(this).inflate(R.layout.layout_bus_viewpager_view, null));
        adapter.addItemView(LayoutInflater.from(this).inflate(R.layout.layout_bus_viewpager_view, null));

        viewPager.setAdapter(adapter);
    }
    private void initTabLayout(){
        for (int i=0;i<2;i++) tabLayout.addTab(tabLayout.newTab());
        tabLayout.setupWithViewPager(viewPager, false);
        tabLayout.getTabAt(0).setText("去程");
        tabLayout.getTabAt(1).setText("回程");
    }
    private void initOther(){
        loadingStatus = null;
    }
    private void initHandler(){
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                if (msg.what==0){
                    time.setText(msg.arg1+" 秒前更新");
                }
                return false;
            }
        });
    }
    private void initLoadingDialog(){
        loading = new Dialog(this, R.style.loadingDialog);
        loading.setContentView(R.layout.layout_loadingdialog);
        loading.setCancelable(false);
    }

    private void loading(){
        loadingActionBar();
        loadingDirection();
        loadingStationList();
    }
    private void loadingActionBar(){
        title.setText(getIntent().getStringExtra("route"));
    }
    private void loadingDirection(){
        Intent intent = getIntent();
        tabLayout.selectTab(tabLayout.getTabAt(intent.getIntExtra("direction",0)));
    }
    private void loadingStationList(){
        Intent intent = getIntent();
        new FetchPostStationList(this).execute(new String[]{intent.getStringExtra("route"), intent.getStringExtra("city")});
    }
    private void loadingStatus(){
        Intent intent = getIntent();
        new FetchPostStatus(this).execute(new String[]{intent.getStringExtra("route"), intent.getStringExtra("city")});
    }

    private static class LoadingStatus extends Thread{
        private WeakReference<ActivityBus> reference;
        private int secs;
        private boolean flag;

        public LoadingStatus(ActivityBus activityBus){
            reference = new WeakReference<ActivityBus>(activityBus);
            secs = 1;
            flag = true;
        }
        @Override
        public void run() {
            super.run();
            while (flag){
                try {
                    ActivityBus activityBus = reference.get();
                    if (activityBus==null||activityBus.isDestroyed()) break;

                    if (secs==30) secs=1;
                    if (secs==1) activityBus.loadingStatus();


                    Message message = new Message();
                    message.what = 0;
                    message.arg1 = secs;
                    activityBus.handler.sendMessage(message);
                    secs++;
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        public void cancel(){
            flag = false;
            interrupt();
        }
    }
    private static class FetchPostStationList extends AsyncTask<String[], Void, JSONArray>{
        private WeakReference<ActivityBus> reference;
        private SecretResource secretResource;
        private OkHttpClient okHttpClient;
        private HmacHelper hmacHelper;

        public FetchPostStationList(ActivityBus activityBus){
            reference = new WeakReference<ActivityBus>(activityBus);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            ActivityBus activityBus = reference.get();
            if (activityBus==null||activityBus.isDestroyed()) return;

            updateViewPrepare(activityBus);
        }
        @Override
        protected JSONArray doInBackground(String[]... strings) {
            initInstantation();
            return fetchPost(strings[0]);
        }
        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            super.onPostExecute(jsonArray);

            ActivityBus activityBus = reference.get();
            if (activityBus==null||activityBus.isDestroyed()) return;

            if (jsonArray!=null) updateViewSuccessful(activityBus, jsonArray);
            else updateViewFailed(activityBus);
        }

        private void initInstantation(){
            secretResource = new SecretResource();
            hmacHelper = new HmacHelper();
            okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .build();
        }

        private JSONArray fetchPost(String[] strings){
            String hmac[] = getHmac();
            String url = getURL(strings);
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
                    return new JSONArray(response.body().string());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        private String getURL(String[] strings){
            String url = null;
            try {
                url = secretResource.getURL_BusStationList()
                        + URLEncoder.encode(strings[1], "UTF-8") + "/"
                        + "?$filter="
                        + "RouteName%2FZh_tw%20eq%20'" + URLEncoder.encode(strings[0], "UTF-8") + "'"
                        + "&$orderby=" + "Direction"
                        + "&$format=" + "JSON";
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return url;
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

        private void updateViewPrepare(ActivityBus activityBus){
            activityBus.loading.show();
        }
        private void updateViewSuccessful(ActivityBus activityBus, JSONArray jsonArray){
            RecyclerView recyclerView_outbound = ((AdapterViewPagerBus)activityBus.viewPager.getAdapter()).getItemView(0).findViewById(R.id.bus_viewpager_view_recyclerview);
            RecyclerView recyclerView_return = ((AdapterViewPagerBus)activityBus.viewPager.getAdapter()).getItemView(1).findViewById(R.id.bus_viewpager_view_recyclerview);

            AdapterRecyclerViewBus outbound = new AdapterRecyclerViewBus();
            AdapterRecyclerViewBus returntrip = new AdapterRecyclerViewBus();
            for (int i=0;i<jsonArray.length();i++){
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    JSONArray stops = jsonObject.getJSONArray("Stops");
                    if (jsonObject.getInt("Direction")==0) {
                        for (int j=0;j<stops.length();j++){
                            outbound.addItem(new BeanStation(stops.getJSONObject(j).getJSONObject("StopName").getString("Zh_tw"),"讀取中"));
                        }
                    }
                    else {
                        for (int j=0;j<stops.length();j++){
                            returntrip.addItem(new BeanStation(stops.getJSONObject(j).getJSONObject("StopName").getString("Zh_tw"), "讀取中"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            activityBus.tabLayout.getTabAt(0).setText("往 "+outbound.getItem(outbound.getItemCount()-1).getStationname());
            activityBus.tabLayout.getTabAt(1).setText("往 "+returntrip.getItem(returntrip.getItemCount()-1).getStationname());

            recyclerView_outbound.setLayoutManager(new LinearLayoutManager(activityBus));
            recyclerView_return.setLayoutManager(new LinearLayoutManager(activityBus));
            recyclerView_outbound.setAdapter(outbound);
            recyclerView_return.setAdapter(returntrip);

            activityBus.loadingStatus = new LoadingStatus(activityBus);
            activityBus.loadingStatus.start();

            activityBus.loading.dismiss();
        }
        private void updateViewFailed(ActivityBus activityBus){
            activityBus.loading.dismiss();
            Toast.makeText(activityBus, "讀取站牌資料錯誤", Toast.LENGTH_SHORT).show();
        }
    }
    private static class FetchPostStatus extends AsyncTask<String[], Void, JSONArray[]>{
        private WeakReference<ActivityBus> reference;
        private OkHttpClient okHttpClient;
        private SecretResource secretResource;
        private HmacHelper hmacHelper;

        public FetchPostStatus(ActivityBus activityBus){
            reference = new WeakReference<ActivityBus>(activityBus);
        }


        @Override
        protected JSONArray[] doInBackground(String[]... strings) {
            initInstantation();
            JSONArray[] jsonArrays = new JSONArray[]{null, null};
            jsonArrays[0] = fetchPostEstimateTime(strings[0]);
            jsonArrays[1] = fetchPostBusLocation(strings[0]);
            return jsonArrays;
        }
        @Override
        protected void onPostExecute(JSONArray[] jsonArrays) {
            super.onPostExecute(jsonArrays);

            ActivityBus activityBus = reference.get();
            if (activityBus==null||activityBus.isDestroyed()) return;

            if (jsonArrays[0]!=null&&jsonArrays[1]!=null) updateViewSuccessful(activityBus, jsonArrays);
            else updateViewFailed(activityBus);
        }

        private void initInstantation(){
            secretResource = new SecretResource();
            hmacHelper = new HmacHelper();
            okHttpClient = new OkHttpClient.Builder().
                    connectTimeout(5,TimeUnit.SECONDS).
                    build();
        }

        private JSONArray fetchPostEstimateTime(String[] strings){
            String url = getURLEstimateTime(strings);
            String[] hmac = getHmac();
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
                    return new JSONArray(response.body().string());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        private JSONArray fetchPostBusLocation(String[] strings){
            String url = getURLBusLocation(strings);
            String[] hmac = getHmac();
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
                    return new JSONArray(response.body().string());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        private String getURLEstimateTime(String[] strings){
            String url = null;
            try {
                url = secretResource.getURL_Transportation()
                        + URLEncoder.encode(strings[1],"UTF-8") + "/"
                        + URLEncoder.encode(strings[0], "UTF-8")
                        + "?$filter=RouteName%2FZh_tw%20eq%20'" + URLEncoder.encode(strings[0], "UTF-8") + "'"
                        + "&$orderby=" + "Direction"
                        + "&$format=" + "JSON";
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return url;
        }
        private String getURLBusLocation(String[] strings){
            try {
                String url = secretResource.getURL_BusLocation()
                        + URLEncoder.encode(strings[1],"UTF-8") + "/"
                        + URLEncoder.encode(strings[0], "UTF-8")
                        + "?$filter=RouteName%2FZh_tw%20eq%20'" + URLEncoder.encode(strings[0], "UTF-8") + "'"
                        + "&$format=" + "JSON";
                return url;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return null;
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

        private void updateViewSuccessful(ActivityBus activityBus, JSONArray[] jsonArrays){
            RecyclerView recyclerView_outbound = ((AdapterViewPagerBus)activityBus.viewPager.getAdapter()).getItemView(0).findViewById(R.id.bus_viewpager_view_recyclerview);
            RecyclerView recyclerView_return = ((AdapterViewPagerBus)activityBus.viewPager.getAdapter()).getItemView(1).findViewById(R.id.bus_viewpager_view_recyclerview);
            AdapterRecyclerViewBus adapter_outbound = (AdapterRecyclerViewBus)recyclerView_outbound.getAdapter();
            AdapterRecyclerViewBus adapter_return = (AdapterRecyclerViewBus)recyclerView_return.getAdapter();
            for (int i=0;i<jsonArrays[0].length();i++){
                try {
                    JSONObject jsonObject = jsonArrays[0].getJSONObject(i);
                    String stopname = jsonObject.getJSONObject("StopName").getString("Zh_tw");
                    int stopstatus = jsonObject.getInt("StopStatus");
                    int direction = jsonObject.getInt("Direction");
                    if (direction==0){
                        for (int j=0;j<adapter_outbound.getItemCount();j++){
                            if (adapter_outbound.getItem(j).getStationname().equals(stopname)){
                                switch (stopstatus){
                                    case 0:
                                        int estimatetime = jsonObject.getInt("EstimateTime");
                                        int min = estimatetime/60;
                                        if (min!=0) adapter_outbound.getItem(j).setEstimatetime(String.valueOf(min)+"分鐘");
                                        else adapter_outbound.getItem(j).setEstimatetime("進站中");
                                        break;
                                    case 1:
                                        adapter_outbound.getItem(j).setEstimatetime("未發車");
                                        break;
                                    case 2:
                                        adapter_outbound.getItem(j).setEstimatetime("交管不停");
                                        break;
                                    case 3:
                                        adapter_outbound.getItem(j).setEstimatetime("末班已過");
                                        break;
                                    case 4:
                                        adapter_outbound.getItem(j).setEstimatetime("未營運");
                                        break;
                                }
                            }

                        }
                    }
                    else {
                        for (int j=0;j<adapter_return.getItemCount();j++){
                            if (adapter_return.getItem(j).getStationname().equals(stopname)){
                                switch (stopstatus){
                                    case 0:
                                        int estimatetime = jsonObject.getInt("EstimateTime");
                                        int min = estimatetime/60;
                                        if (min!=0) adapter_return.getItem(j).setEstimatetime(String.valueOf(min)+"分鐘");
                                        else adapter_return.getItem(j).setEstimatetime("進站中");
                                        break;
                                    case 1:
                                        adapter_return.getItem(j).setEstimatetime("未發車");
                                        break;
                                    case 2:
                                        adapter_return.getItem(j).setEstimatetime("交管不停");
                                        break;
                                    case 3:
                                        adapter_return.getItem(j).setEstimatetime("末班已過");
                                        break;
                                    case 4:
                                        adapter_return.getItem(j).setEstimatetime("未營運");
                                        break;
                                }
                            }

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            adapter_outbound.clearBusItem();
            adapter_return.clearBusItem();
            for (int i=0;i<jsonArrays[1].length();i++){
                try {
                    JSONObject jsonObject = jsonArrays[1].getJSONObject(i);
                    int stopposition = jsonObject.getInt("StopSequence")-1;
                    int direction = jsonObject.getInt("Direction");
                    int dutystatus = jsonObject.getInt("DutyStatus");
                    if (dutystatus!=2){
                        String busitem = jsonObject.getString("PlateNumb");
                        if (direction==0) adapter_outbound.getItem(stopposition).getBusitem().add(busitem);
                        else adapter_return.getItem(stopposition).getBusitem().add(busitem);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            adapter_outbound.notifyDataSetChanged();
            adapter_return.notifyDataSetChanged();
        }
        private void updateViewFailed(ActivityBus activityBus){
            Toast.makeText(activityBus, "刷新公車即時到站資料失敗", Toast.LENGTH_SHORT).show();
        }
    }
}
