package com.fly.tkuilife.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fly.tkuilife.R;

import java.util.Arrays;
import java.util.List;

public class AdapterRecyclerViewMonitor extends RecyclerView.Adapter {

    private List<String> monitors;

    public AdapterRecyclerViewMonitor(String[] monitors){
        this.monitors = Arrays.asList(monitors);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_news_viewpager_monitor_recyclerview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TextView text = holder.itemView.findViewById(R.id.cell_news_viewpager_monitor_recyclerview_text);
        text.setText(monitors.get(position));
    }

    @Override
    public int getItemCount() {
        return monitors.size();
    }



    class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
