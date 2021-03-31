package com.fly.tkuilife.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fly.tkuilife.R;
import com.fly.tkuilife.bean.BeanLibraryOpen;

import java.util.ArrayList;

public class AdapterRecyclerViewLibraryOpen extends RecyclerView.Adapter<AdapterRecyclerViewLibraryOpen.ViewHolder> {

    private ArrayList<BeanLibraryOpen> opens;

    public AdapterRecyclerViewLibraryOpen() {
        opens = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_library_viewpager_libraryopen_recyclerview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TextView term, title, period;
        term = holder.itemView.findViewById(R.id.cell_library_viewpager_libraryopen_recyclerview_term);
        title = holder.itemView.findViewById(R.id.cell_library_viewpager_libraryopen_recyclerview_title);
        period = holder.itemView.findViewById(R.id.cell_library_viewpager_libraryopen_recyclerview_period);

        term.setText(this.opens.get(position).getTerm().substring(0,2));
        String text_title = "", text_period = "";
        if (this.opens.get(position).getBuilding()!=null) text_title+=this.opens.get(position).getBuilding()+" ";
        if (this.opens.get(position).getHour()!=null) text_title+=this.opens.get(position).getHour();
        if (this.opens.get(position).getPeriod()!=null) text_period+=this.opens.get(position).getPeriod()+" ";
        if (this.opens.get(position).getWeekday()!=null) text_period+=this.opens.get(position).getWeekday();
        title.setText(text_title);
        period.setText(text_period);
    }

    @Override
    public int getItemCount() {
        return opens.size();
    }
    public void addItem(BeanLibraryOpen open){
        this.opens.add(open);
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
