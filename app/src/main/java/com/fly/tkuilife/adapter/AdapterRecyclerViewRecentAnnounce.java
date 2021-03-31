package com.fly.tkuilife.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fly.tkuilife.R;
import com.fly.tkuilife.bean.BeanRecentAnnounce;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AdapterRecyclerViewRecentAnnounce extends RecyclerView.Adapter<AdapterRecyclerViewRecentAnnounce.ViewHolder> {

    private ArrayList<BeanRecentAnnounce> announces;
    private SimpleDateFormat formator;

    public AdapterRecyclerViewRecentAnnounce(){
        announces = new ArrayList<>();
        formator = new SimpleDateFormat("");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_news_viewpager_recentannounce_recyclerview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TextView kind, title, time, id;
        title = holder.itemView.findViewById(R.id.cell_news_viewpager_recentannounce_recyclerview_title);
        kind = holder.itemView.findViewById(R.id.cell_news_viewpager_recentannounce_recyclerview_kind);
        time = holder.itemView.findViewById(R.id.cell_news_viewpager_recentannounce_recyclerview_time);
        id = holder.itemView.findViewById(R.id.cell_news_viewpager_recentannounce_recyclerview_id);
        kind.setText(String.valueOf(announces.get(position).getKind().charAt(0)));
        title.setText(announces.get(position).getTitle());
        id.setText(announces.get(position).getId());
        formator.applyPattern("yyyyMMdd");
        Date start = null, end = null;
        try {
            start = formator.parse(announces.get(position).getStart());
            end = formator.parse(announces.get(position).getEnd());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        formator.applyPattern("yyyy'/'MM'/'dd");
        time.setText(formator.format(start)+" 至 "+formator.format(end));
    }

    @Override
    public int getItemCount() {
        return announces.size();
    }

    public void addItem(BeanRecentAnnounce announce){
        announces.add(announce);
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
