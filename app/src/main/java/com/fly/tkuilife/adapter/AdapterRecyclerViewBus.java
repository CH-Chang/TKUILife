package com.fly.tkuilife.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fly.tkuilife.R;
import com.fly.tkuilife.bean.BeanStation;

import java.util.ArrayList;

public class AdapterRecyclerViewBus extends RecyclerView.Adapter<AdapterRecyclerViewBus.ViewHolder> {

    private ArrayList<BeanStation> stations;

    public AdapterRecyclerViewBus(){
        stations = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_bus_viewpager_view_recyclerview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TextView estimatetime, stationname, bus;

        estimatetime = holder.itemView.findViewById(R.id.cell_bus_viewpager_view_recyclerview_estimatetime);
        stationname = holder.itemView.findViewById(R.id.cell_bus_viewpager_view_recyclerview_station);
        bus = holder.itemView.findViewById(R.id.cell_bus_viewpager_view_recyclerview_bus);

        stationname.setText(this.stations.get(position).getStationname());
        estimatetime.setText(this.stations.get(position).getEstimatetime());
        if (this.stations.get(position).getEstimatetime().equals("進站中")) estimatetime.setTextColor(Color.RED);
        else estimatetime.setTextColor(holder.itemView.getContext().getColor(R.color.colorTextPrimary));

        ArrayList<String> busitem = this.stations.get(position).getBusitem();

        String text_bus="";
        for (int i=0;i<busitem.size();i++){
            if (i==0) text_bus+=busitem.get(i);
            else text_bus+=" / "+busitem.get(i);
        }
        if (text_bus.equals("")){
            bus.setVisibility(View.INVISIBLE);
        }
        else {
            bus.setText(text_bus);
            bus.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public int getItemCount() {
        return stations.size();
    }

    public void addItem(BeanStation station){
        this.stations.add(station);
    }
    public BeanStation getItem(int position){
        return this.stations.get(position);
    }
    public void clearBusItem(){
        for (BeanStation station:stations) station.clearBusitem();
    }


    class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
