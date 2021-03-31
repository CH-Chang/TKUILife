package com.fly.tkuilife.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fly.tkuilife.R;
import com.fly.tkuilife.bean.BeanNewsPaperPhoto;
import com.fly.tkuilife.view.RoundImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterRecyclerViewNewsPaperPhoto extends RecyclerView.Adapter<AdapterRecyclerViewNewsPaperPhoto.ViewHolder> {

    private ArrayList<BeanNewsPaperPhoto> photos;

    public AdapterRecyclerViewNewsPaperPhoto(){
        photos = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_newspaper_viewpager_gallery_recyclerview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TextView period, title, photographer;
        RoundImageView img;

        period = holder.itemView.findViewById(R.id.cell_newspaper_viewpager_gallery_recyclerview_period);
        title = holder.itemView.findViewById(R.id.cell_newspaper_viewpager_gallery_recyclerview_title);
        photographer = holder.itemView.findViewById(R.id.cell_newspaper_viewpager_gallery_recyclerview_photographer);
        img = holder.itemView.findViewById(R.id.cell_newspaper_viewpager_gallery_recyclerview_img);

        period.setText("淡江時報"+this.photos.get(position).getPeriod()+"期"+" "+this.photos.get(position).getPubdate());
        title.setText(this.photos.get(position).getTitle());
        photographer.setText(this.photos.get(position).getPhotographer()+"攝");
        Picasso.get().load(this.photos.get(position).getUrl()).into(img);
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public void addItem(BeanNewsPaperPhoto photo){
        this.photos.add(photo);
    }



    class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
