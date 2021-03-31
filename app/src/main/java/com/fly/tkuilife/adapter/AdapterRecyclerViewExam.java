package com.fly.tkuilife.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fly.tkuilife.R;
import com.fly.tkuilife.bean.BeanExam;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AdapterRecyclerViewExam extends RecyclerView.Adapter<AdapterRecyclerViewExam.ViewHolder> {

    private ArrayList<BeanExam> exams;
    private SimpleDateFormat formator = new SimpleDateFormat("");

    public AdapterRecyclerViewExam(){
        exams = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_curriculum_viewpager_exam_recyclerview, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TextView week = (TextView) holder.itemView.findViewById(R.id.cell_curriculum_viewpager_exam_recyclerview_week);
        TextView date = (TextView) holder.itemView.findViewById(R.id.cell_curriculum_viewpager_exam_recyclerview_date);
        TextView course = (TextView) holder.itemView.findViewById(R.id.cell_curriculum_viewpager_exam_recyclerview_course);
        TextView time = (TextView) holder.itemView.findViewById(R.id.cell_curriculum_viewpager_exam_recyclerview_time);
        TextView duration = (TextView) holder.itemView.findViewById(R.id.cell_curriculum_viewpager_exam_recyclerview_duration);
        TextView room = (TextView) holder.itemView.findViewById(R.id.cell_curriculum_viewpager_exam_recyclerview_room);
        TextView seatnum = (TextView) holder.itemView.findViewById(R.id.cell_curriculum_viewpager_exam_recyclerview_seatnum);
        TextView examnum = (TextView) holder.itemView.findViewById(R.id.cell_curriculum_viewpager_exam_recyclerview_examnum);
        LinearLayout body = (LinearLayout) holder.itemView.findViewById(R.id.cell_curriculum_viewpager_exam_recyclerview_body);

        body.setVisibility(View.GONE);
        body.setAlpha(1);

        if(exams.get(position).getDate()!=null){

            try {
                formator.applyPattern("yyyy-MM-dd");
                Date examdate = formator.parse(exams.get(position).getDate());
                formator.applyPattern("EEE");
                week.setText(formator.format(examdate));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            date.setText(exams.get(position).getDate().replace("-","/"));
            course.setText(exams.get(position).getCourse());
            duration.setText(exams.get(position).getTime()+"分鐘");
            room.setText(exams.get(position).getRoom());
            seatnum.setText(exams.get(position).getSeatnum()+"號");
            examnum.setText(exams.get(position).getExamnum()+"號");
            int session = Integer.valueOf(exams.get(position).getSession());
            int examtime = Integer.valueOf(exams.get(position).getTime());
            time.setText("第"+exams.get(position).getSession()+"節 - "+String.format("%02d",session*2+6)+":20 至 "+String.format("%02d",(session*2+6)+((examtime+20)/60))+":"+String.format("%02d", (examtime+20)%60));
        }
        else {
            course.setText(exams.get(position).getCourse());
            week.setText(exams.get(position).getType().substring(0,2));
            time.setText(exams.get(position).getType());
            date.setText("-");
            duration.setText("-");
            seatnum.setText(exams.get(position).getSeatnum()+"號");
            room.setText("-");
            examnum.setText("-");
        }


    }
    @Override
    public int getItemCount() {
        return exams.size();
    }

    public void addItem(BeanExam exam){
        exams.add(exam);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private LinearLayout header, body;
        private ValueAnimator openValueAnimator, closeValueAnimator;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            header = itemView.findViewById(R.id.cell_curriculum_viewpager_exam_recyclerview_header);
            body = itemView.findViewById(R.id.cell_curriculum_viewpager_exam_recyclerview_body);
            header.setOnClickListener(this);
            openValueAnimator = null;
            closeValueAnimator = null;
        }

        @Override
        public void onClick(View view) {
            if(body.getVisibility()==View.VISIBLE) animClose(body);
            else animOpen(body);
        }

        private void animClose(final View view){
            int height = measureHeight(view);
            view.setAlpha(1);
            if(closeValueAnimator==null){
                closeValueAnimator = createDropAnim(view, height, 0);
                closeValueAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(View.GONE);
                    }
                });
            }
            closeValueAnimator.start();
        }

        private void animOpen(final View view){
            view.setVisibility(View.VISIBLE);
            view.setAlpha(0);
            if(openValueAnimator==null) openValueAnimator = createDropAnim(view, 0, measureHeight(view));
            openValueAnimator.start();
        }

        private ValueAnimator createDropAnim(final View view, final int start, final int end){
            ValueAnimator valueAnimator = ValueAnimator.ofInt(start, end);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int value = (int) valueAnimator.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                    layoutParams.height=value;

                    float alpha = ((float)value)/end;
                    view.setAlpha(alpha);

                    view.setLayoutParams(layoutParams);
                }
            });
            return valueAnimator;
        }

        private int measureHeight(View view){
            view.measure(0,0);
            return view.getMeasuredHeight();
        }
    }
}
