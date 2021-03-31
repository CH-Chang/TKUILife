package com.fly.tkuilife.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fly.tkuilife.R;
import com.fly.tkuilife.bean.BeanCourse;

import java.util.ArrayList;

public class AdapterRecyclerViewCurriculum extends RecyclerView.Adapter<AdapterRecyclerViewCurriculum.ViewHolder> {

    private ArrayList<BeanCourse> courses;

    public AdapterRecyclerViewCurriculum(){
        courses = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_home_body_curriculum_viewpager_recyclerview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TextView session, course, teacher, room, seatnum;
        session = holder.itemView.findViewById(R.id.cell_home_body_curriculum_viewpager_recyclerview_session);
        course = holder.itemView.findViewById(R.id.cell_home_body_curriculum_viewpager_recyclerview_course);
        teacher = holder.itemView.findViewById(R.id.cell_home_body_curriculum_viewpager_recyclerview_teacher);
        room = holder.itemView.findViewById(R.id.cell_home_body_curriculum_viewpager_recyclerview_room);
        seatnum = holder.itemView.findViewById(R.id.cell_home_body_curriculum_viewpager_recyclerview_seatnum);
        session.setText(String.format("%02d", courses.get(position).getSession()));
        course.setText(courses.get(position).getCourse());
        teacher.setText(courses.get(position).getTeacher());
        room.setText(courses.get(position).getRoom());
        seatnum.setText(String.format("%02d", courses.get(position).getSeatnum()));
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    public void clearAllItem(){
        courses.clear();
    }

    public void addCourse(BeanCourse course){
        courses.add(course);
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
