package com.fly.tkuilife.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fly.tkuilife.R;
import com.fly.tkuilife.bean.BeanTimetableContrast;

import java.util.ArrayList;

public class AdapterRecyclerViewTimetableContrast extends RecyclerView.Adapter<AdapterRecyclerViewTimetableContrast.ViewHolder> {

    private ArrayList<BeanTimetableContrast> timetables;

    public AdapterRecyclerViewTimetableContrast() {
        this.timetables = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_contrast_viewpager_timetablecontrast_recyclerview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TextView time, session;
        time = holder.itemView.findViewById(R.id.cell_contrast_viewpager_timetablecontrast_recyclerview_time);
        session = holder.itemView.findViewById(R.id.cell_contrast_viewpager_timetablecontrast_recyclerview_session);

        time.setText(this.timetables.get(position).getTime());
        session.setText(this.timetables.get(position).getSession());
    }

    @Override
    public int getItemCount() {
        return this.timetables.size();
    }

    public void addItem(BeanTimetableContrast timetable){
        this.timetables.add(timetable);
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
