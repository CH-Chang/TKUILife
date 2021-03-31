package com.fly.tkuilife.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.fly.tkuilife.R;

import java.util.ArrayList;

public class AdapterViewPager extends PagerAdapter {

    private Context context;
    private ArrayList<View> views;
    private LinearLayout indicator;

    public AdapterViewPager(Context context, LinearLayout indicator){

        this.context = context;
        this.indicator = indicator;

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
    public void removeItemView(int position){
        views.remove(position);
    }
    public void removeAllView(){
        views.clear();
    }
    public View getItemView(int position){
        return views.get(position);
    }
    public void initIndicator(){
        indicator.removeAllViews();
        for(int i=0;i<views.size();i++){
            View view = new View(context);
            view.setBackgroundResource(R.drawable.selector_indicator);
            view.setEnabled(false);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(20,20);
            if(i!=0) layoutParams.leftMargin = 10;
            view.setLayoutParams(layoutParams);
            indicator.addView(view);
        }
        indicator.getChildAt(0).setEnabled(true);
    }


}
