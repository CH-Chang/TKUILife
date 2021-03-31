package com.fly.tkuilife.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.fly.tkuilife.R;
import com.fly.tkuilife.utils.SecretResource;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ActivityMap extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {

    private Toolbar toolbar;
    private SupportMapFragment fragment_map;
    private GoogleMap map;
    private ImageButton building, food, bus;
    private Dialog loading;

    private ArrayList<Marker> markers_bus, markers_building, markers_food;
    private boolean bus_open, food_open, building_open;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        loading();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.map_building:
                building_open = !building_open;
                break;
            case R.id.map_food:
                food_open = !food_open;
                break;
            case R.id.map_bus:
                bus_open = !bus_open;
                break;
        }
        syncKind();
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
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(25.174769,121.449187),17));
    }

    private void init(){
        initInstantation();
        initView();
        initMap();
        initActionBar();
        initLoadingDialog();
        initOther();
        initInteraction();
    }
    private void initView(){
        setContentView(R.layout.activtiy_map);
        toolbar = (Toolbar) findViewById(R.id.map_toolbar);
        building = (ImageButton) findViewById(R.id.map_building);
        bus = (ImageButton) findViewById(R.id.map_bus);
        food = (ImageButton) findViewById(R.id.map_food);
    }
    private void initLoadingDialog(){
        loading = new Dialog(this, R.style.loadingDialog);
        loading.setContentView(R.layout.layout_loadingdialog);
        loading.setCancelable(false);
    }
    private void initActionBar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    private void initInteraction(){
        building.setOnClickListener(this);
        food.setOnClickListener(this);
        bus.setOnClickListener(this);
    }
    private void initMap(){
        fragment_map = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_map);
        fragment_map.getMapAsync(this);
    }
    private void initInstantation(){
        markers_building = new ArrayList<>();
        markers_bus = new ArrayList<>();
        markers_food = new ArrayList<>();
    }
    private void initOther(){
        building_open = true;
        food_open = true;
        bus_open = true;
    }

    private void loading(){
        loading.show();
        new FetchPost(this).execute();
    }

    private void syncKind(){
        if (building_open){
            building.setColorFilter(Color.rgb(197,131,44));
            for (Marker marker:markers_building) marker.setVisible(true);
        }
        else {
            building.setColorFilter(Color.rgb(153,154,161));
            for (Marker marker:markers_building) marker.setVisible(false);
        }
        if (food_open){
            food.setColorFilter(Color.rgb(197,131,44));
            for (Marker marker:markers_food) marker.setVisible(true);
        }
        else {
            food.setColorFilter(Color.rgb(153,154,161));
            for (Marker marker:markers_food) marker.setVisible(false);
        }
        if (bus_open){
            bus.setColorFilter(Color.rgb(197,131,44));
            for (Marker marker:markers_bus) marker.setVisible(true);
        }
        else {
            bus.setColorFilter(Color.rgb(153,154,161));
            for (Marker marker:markers_bus) marker.setVisible(false);
        }
    }

    private static class FetchPost extends AsyncTask<Void, Void, JSONArray[]>{
        private WeakReference<ActivityMap> reference;

        public FetchPost(ActivityMap activityMap){
            reference = new WeakReference<ActivityMap>(activityMap);
        }

        @Override
        protected JSONArray[] doInBackground(Void... voids) {
            JSONArray jsonArray[] = new JSONArray[]{null, null, null};

            jsonArray[0] = fetchPostBuilding();
            jsonArray[1] = fetchPostFood();
            jsonArray[2] = fetchPostBus();

            return jsonArray;
        }
        @Override
        protected void onPostExecute(JSONArray[] jsonArrays) {
            super.onPostExecute(jsonArrays);
            ActivityMap activityMap = reference.get();
            if (activityMap==null||activityMap.isDestroyed()) return;

            if (jsonArrays[0]!=null&&jsonArrays[1]!=null&&jsonArrays[2]!=null){
                addMarkers(activityMap, jsonArrays);
                updateViewSuccessful(activityMap);
            }
            else {
                updateViewFailed(activityMap);
            }
        }

        private JSONArray fetchPostFood(){
            SecretResource secretResource = new SecretResource();
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .build();
            Request request = new Request.Builder()
                    .url(secretResource.getURL_MapFood())
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
        private JSONArray fetchPostBuilding(){
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
        private JSONArray fetchPostBus(){
            SecretResource secretResource = new SecretResource();
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .build();
            Request request = new Request.Builder()
                    .url(secretResource.getURL_MapBus())
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

        private void addMarkers(ActivityMap activityMap, JSONArray jsonArrays[]){
            addMarkersBuilding(activityMap, jsonArrays[0]);
            addMarkersFood(activityMap, jsonArrays[1]);
            addMarkersBus(activityMap, jsonArrays[2]);
        }
        private void addMarkersBuilding(ActivityMap activityMap, JSONArray jsonArray){
            try {
                for (int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String name = jsonObject.getString("id") + " " + jsonObject.getString("ch_name");
                    LatLng latLng = new LatLng(jsonObject.getDouble("latitude"), jsonObject.getDouble("longitude"));
                    activityMap.markers_building.add(activityMap.map.addMarker(new MarkerOptions().position(latLng).title(name).icon(bitmapDescriptorFromVector(activityMap, R.drawable.ic_marker_building))));
                }
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
        private void addMarkersFood(ActivityMap activityMap, JSONArray jsonArray){
            try {
                for (int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String name = jsonObject.getString("ch_name");
                    LatLng latLng = new LatLng(jsonObject.getDouble("latitude"), jsonObject.getDouble("longitude"));
                    activityMap.markers_food.add(activityMap.map.addMarker(new MarkerOptions().position(latLng).title(name).icon(bitmapDescriptorFromVector(activityMap, R.drawable.ic_marker_food))));
                }
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
        private void addMarkersBus(ActivityMap activityMap, JSONArray jsonArray){
            try {
                for (int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String name = jsonObject.getString("ch_name");
                    LatLng latLng = new LatLng(jsonObject.getDouble("latitude"), jsonObject.getDouble("longitude"));
                    activityMap.markers_bus.add(activityMap.map.addMarker(new MarkerOptions().position(latLng).title(name).icon(bitmapDescriptorFromVector(activityMap, R.drawable.ic_marker_bus))));
                }
            } catch (JSONException e){
                e.printStackTrace();
            }
        }

        private void updateViewSuccessful(ActivityMap activityMap){
            activityMap.syncKind();
            activityMap.loading.dismiss();
        }
        private void updateViewFailed(ActivityMap activityMap){
            Toast.makeText(activityMap, "獲取地圖資料錯誤", Toast.LENGTH_SHORT).show();
            activityMap.finish();
        }
        private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorResId) {
            Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
            vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
            Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            vectorDrawable.draw(canvas);
            return BitmapDescriptorFactory.fromBitmap(bitmap);
        }
    }

}
