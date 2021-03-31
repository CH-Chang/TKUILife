package com.fly.tkuilife.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;

public class AdapterViewPagerContrast extends PagerAdapter {

    private ArrayList<View> views;

    public AdapterViewPagerContrast(){
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
        container.addView(this.views.get(position));
        return this.views.get(position);
    }
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView(this.views.get(position));
        super.destroyItem(container, position, object);
    }

    public void addItemView(View view){
        this.views.add(view);
    }
    public View getItemView(int position){
        return this.views.get(position);
    }
}
