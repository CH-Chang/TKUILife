package com.fly.tkuilife.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fly.tkuilife.adapter.AdapterViewPager;

public class ViewPager extends androidx.viewpager.widget.ViewPager {
    public ViewPager(@NonNull Context context) {
        super(context);
    }
    public ViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(getAdapter()!=null){
            int height = 0;
            for(int i = 0; i<((AdapterViewPager)getAdapter()).getCount(); i++){

                View view = ((AdapterViewPager)getAdapter()).getItemView(i);
                view.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0,MeasureSpec.UNSPECIFIED));
                int view_height = view.getMeasuredHeight();
                if(height<view_height) height=view_height;
            }
            height+=getPaddingTop();
            height+=getPaddingBottom();
            if(height!=0) heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
