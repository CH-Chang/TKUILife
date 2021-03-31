package com.fly.tkuilife.utils;


import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fly.tkuilife.R;

public class RecyclerViewItemClickSupport {

    private final RecyclerView recyclerView;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (onItemClickListener!=null){
                RecyclerView.ViewHolder holder = recyclerView.getChildViewHolder(v);
                onItemClickListener.onItemClick(recyclerView, holder.getAdapterPosition(), v);
            }
        }
    };
    private View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            if (onItemLongClickListener!=null){
                RecyclerView.ViewHolder holder = recyclerView.getChildViewHolder(v);
                onItemLongClickListener.onItemLongClick(recyclerView, holder.getAdapterPosition(), v);
            }
            return false;
        }
    };
    private RecyclerView.OnChildAttachStateChangeListener onChildAttachStateChangeListener = new RecyclerView.OnChildAttachStateChangeListener() {
        @Override
        public void onChildViewAttachedToWindow(@NonNull View view) {
            if (onItemClickListener!=null) view.setOnClickListener(onClickListener);
            if (onItemLongClickListener!=null) view.setOnLongClickListener(onLongClickListener);
        }
        @Override
        public void onChildViewDetachedFromWindow(@NonNull View view) { }
    };

    private RecyclerViewItemClickSupport(RecyclerView recyclerView){
        this.recyclerView = recyclerView;
        this.recyclerView.setTag("");
        this.recyclerView.addOnChildAttachStateChangeListener(onChildAttachStateChangeListener);
    }

    public static RecyclerViewItemClickSupport addTo(RecyclerView recyclerView){
        RecyclerViewItemClickSupport recyclerViewItemClickSupport = (RecyclerViewItemClickSupport) recyclerView.getTag();
        if (recyclerViewItemClickSupport==null) recyclerViewItemClickSupport = new RecyclerViewItemClickSupport(recyclerView);
        return recyclerViewItemClickSupport;
    }

    public static RecyclerViewItemClickSupport removeFrom(RecyclerView recyclerView){
        RecyclerViewItemClickSupport recyclerViewItemClickSupport = (RecyclerViewItemClickSupport) recyclerView.getTag(R.id.recyclerview_item_click_support);
        if (recyclerViewItemClickSupport!=null) recyclerViewItemClickSupport.detach(recyclerView);
        return recyclerViewItemClickSupport;
    }

    public RecyclerViewItemClickSupport setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
        return this;
    }

    public RecyclerViewItemClickSupport setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener){
        this.onItemLongClickListener = onItemLongClickListener;
        return this;
    }

    private void detach(RecyclerView recyclerView){
        recyclerView.removeOnChildAttachStateChangeListener(onChildAttachStateChangeListener);
        recyclerView.setTag(R.id.recyclerview_item_click_support, null);
    }



    public interface OnItemClickListener{
        void onItemClick(RecyclerView recyclerView, int position, View view);
    }
    public interface OnItemLongClickListener{
        void onItemLongClick(RecyclerView recyclerView, int position, View view);
    }
}
