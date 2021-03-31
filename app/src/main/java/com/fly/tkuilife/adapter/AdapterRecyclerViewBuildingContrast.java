package com.fly.tkuilife.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fly.tkuilife.R;
import com.fly.tkuilife.bean.BeanBuildingContrast;

import java.util.ArrayList;

public class AdapterRecyclerViewBuildingContrast extends RecyclerView.Adapter<AdapterRecyclerViewBuildingContrast.ViewHolder> {

    private ArrayList<BeanBuildingContrast> buildings;

    public AdapterRecyclerViewBuildingContrast(){
        buildings = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_contrast_viewpager_buildingcontrast_recyclerview, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TextView id, building;
        id = holder.itemView.findViewById(R.id.cell_contrast_viewpager_buildingcontrast_recyclerview_id);
        building = holder.itemView.findViewById(R.id.cell_contrast_viewpager_buildingcontrast_recyclerview_building);
        id.setText(this.buildings.get(position).getId());
        building.setText(this.buildings.get(position).getName());

        holder.itemView.setTag(position);
    }
    @Override
    public int getItemCount() {
        return buildings.size();
    }

    public void addItem(BeanBuildingContrast building){
        this.buildings.add(building);
    }
    public BeanBuildingContrast getItem(int position){
        return this.buildings.get(position);
    }


    class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
