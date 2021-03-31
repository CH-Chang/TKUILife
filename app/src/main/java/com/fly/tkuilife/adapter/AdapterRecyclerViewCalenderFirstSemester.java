package com.fly.tkuilife.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fly.tkuilife.R;
import com.fly.tkuilife.bean.BeanCalendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class AdapterRecyclerViewCalenderFirstSemester extends RecyclerView.Adapter<AdapterRecyclerViewCalenderFirstSemester.ViewHolder> {

    private ArrayList<BeanCalendar> calendars;
    private SimpleDateFormat formator_input, formator_output;

    public AdapterRecyclerViewCalenderFirstSemester(){
        calendars = new ArrayList<>();
        formator_input = new SimpleDateFormat("yyyy'-'MM'-'dd");
        formator_output = new SimpleDateFormat("yyyy'/'MM'/'dd EEE");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_calendar_viewpager_firstsemester_recyclerview, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TextView week, event, time;
        week = holder.itemView.findViewById(R.id.cell_calendar_viewpager_firstsemester_recyclerview_week);
        event = holder.itemView.findViewById(R.id.cell_calendar_viewpager_firstsemester_recyclerview_event);
        time = holder.itemView.findViewById(R.id.cell_calendar_viewpager_firstsemester_recyclerview_time);
        event.setText(calendars.get(position).getEvent());
        week.setText(calendars.get(position).getWeek());
        if (calendars.get(position).getEndtime().equals("")){
            try {
                time.setText(formator_output.format(formator_input.parse(calendars.get(position).getStarttime())));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                time.setText(formator_output.format(formator_input.parse(calendars.get(position).getStarttime())) + " 至 " + formator_output.format(formator_input.parse(calendars.get(position).getEndtime())));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public int getItemCount() {
        return calendars.size();
    }

    public void addItem(BeanCalendar calendar){
        this.calendars.add(calendar);
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
