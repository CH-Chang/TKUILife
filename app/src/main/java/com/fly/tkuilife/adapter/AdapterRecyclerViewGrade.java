package com.fly.tkuilife.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fly.tkuilife.R;
import com.fly.tkuilife.bean.BeanGrade;

import java.util.ArrayList;

public class AdapterRecyclerViewGrade extends RecyclerView.Adapter<AdapterRecyclerViewGrade.ViewHolder> {

    private ArrayList<BeanGrade> grades;

    public AdapterRecyclerViewGrade(){
        grades = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_grade_recyclerview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TextView course = holder.itemView.findViewById(R.id.cell_grade_recyclerview_course);
        TextView credit = holder.itemView.findViewById(R.id.cell_grade_recyclerview_credit);
        TextView grade = holder.itemView.findViewById(R.id.cell_grade_recyclerview_grade);
        course.setText(grades.get(position).getCourse());
        credit.setText(grades.get(position).getCredit()+"學分");
        int point  = Integer.valueOf(grades.get(position).getGrade());
        grade.setText(String.format("%02d", point));
        if(point>=60) grade.setTextColor(Color.rgb(28,117,2));
        else grade.setTextColor(Color.rgb(252,24,47));
    }

    @Override
    public int getItemCount() {
        return grades.size();
    }

    public void addItem(BeanGrade grade){
        grades.add(grade);
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
