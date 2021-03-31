package com.fly.tkuilife.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fly.tkuilife.R;
import com.fly.tkuilife.bean.BeanNewsPaperNews;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterRecyclerViewNewsPaperNews extends RecyclerView.Adapter<AdapterRecyclerViewNewsPaperNews.ViewHolder>{

    private ArrayList<BeanNewsPaperNews> newsPapers;

    public AdapterRecyclerViewNewsPaperNews(){
        newsPapers = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_newspaper_viewpager_latest_recyclerview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TextView kind, title;
        ImageView img;
        kind = holder.itemView.findViewById(R.id.cell_newspaper_viewpager_latest_recyclerview_kind);
        title = holder.itemView.findViewById(R.id.cell_newspaper_viewpager_latest_recyclerview_title);
        img = holder.itemView.findViewById(R.id.cell_newspaper_viewpager_latest_recyclerview_img);

        kind.setText(newsPapers.get(position).getKind());
        title.setText(newsPapers.get(position).getTitle());
        Picasso.get().load(newsPapers.get(position).getImg()).into(img);
    }

    @Override
    public int getItemCount() {
        return newsPapers.size();
    }





    public void addItem(BeanNewsPaperNews newsPaper){
        this.newsPapers.add(newsPaper);
    }
    public BeanNewsPaperNews getItem(int position){
        return this.newsPapers.get(position);
    }



    class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
