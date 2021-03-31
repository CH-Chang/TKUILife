package com.fly.tkuilife.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fly.tkuilife.R;
import com.fly.tkuilife.bean.BeanEResourcesNews;
import com.fly.tkuilife.view.MarqueeTextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class AdapterRecyclerViewEResourcesNews extends RecyclerView.Adapter<AdapterRecyclerViewEResourcesNews.ViewHolder> {

    private ArrayList<BeanEResourcesNews> resources;
    private SimpleDateFormat formator_input, formator_output;

    public AdapterRecyclerViewEResourcesNews(){
        resources = new ArrayList<>();
        formator_input = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss Z", Locale.US);
        formator_output = new SimpleDateFormat("MM'/'dd'\n'EEE");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_library_viewpager_eresourcesnews_recyclerview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MarqueeTextView event, descirption;
        TextView week;
        event = holder.itemView.findViewById(R.id.cell_library_viewpager_eresourcesnews_recyclerview_event);
        descirption = holder.itemView.findViewById(R.id.cell_library_viewpager_eresourcesnews_recyclerview_description);
        week = holder.itemView.findViewById(R.id.cell_library_viewpager_eresourcesnews_recyclerview_week);

        event.setText(this.resources.get(position).getTitle());
        if (!this.resources.get(position).getDecription().equals("")) descirption.setText(this.resources.get(position).getDecription());
        else descirption.setText("無詳細資訊說明");
        try {
            week.setText(formator_output.format(formator_input.parse(this.resources.get(position).getDate())));
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return resources.size();
    }

    public void addItem(BeanEResourcesNews resource){
        this.resources.add(resource);
    }
    public BeanEResourcesNews getItem(int position){
        return this.resources.get(position);
    }
    public void clearAllItem(){
        this.resources.clear();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
