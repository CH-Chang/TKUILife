package com.fly.tkuilife.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;

public class AdapterViewPagerNews extends PagerAdapter {

    private ArrayList<View> views;

    public AdapterViewPagerNews(){
        views = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return views.size();
    }
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        container.addView(views.get(position));
        return views.get(position);
    }
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView(views.get(position));
    }
    @Override
    public int getItemPosition(@NonNull Object object) {
        return views.indexOf(object);
    }

    public void addItemView(View view){
        views.add(view);
    }
    public View getItemView(int position){
        return views.get(position);
    }
}
