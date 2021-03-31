package com.fly.tkuilife.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fly.tkuilife.R;
import com.fly.tkuilife.bean.BeanRoute;
import com.fly.tkuilife.bean.BeanStation;

import java.util.ArrayList;

public class AdapterRecyclerViewTransportation extends RecyclerView.Adapter<AdapterRecyclerViewTransportation.ViewHolder> {

    private ArrayList<BeanRoute> routes;

    public AdapterRecyclerViewTransportation(){
        this.routes = new ArrayList<>();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_home_body_transportation_viewpager_recyclerview, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TextView route = holder.itemView.findViewById(R.id.cell_home_body_transportation_viewpager_recyclerview_route);
        TextView time = holder.itemView.findViewById(R.id.cell_home_body_transportation_viewpager_recyclerview_time);
        TextView station = holder.itemView.findViewById(R.id.cell_home_body_transportation_viewpager_recyclerview_station);
        route.setText(routes.get(position).getRoute());
        station.setText(routes.get(position).getStation());
        time.setText(routes.get(position).getTime());
    }
    @Override
    public int getItemCount() {
        return routes.size();
    }

    public void addItem(BeanRoute route){
        routes.add(route);
    }
    public BeanRoute getItem(int position){
        return routes.get(position);
    }





    class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
