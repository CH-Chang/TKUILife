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
import com.fly.tkuilife.bean.BeanCourseChg;

import java.util.ArrayList;

public class AdapterRecyclerViewCourseChg extends RecyclerView.Adapter<AdapterRecyclerViewCourseChg.ViewHolder>{

    private ArrayList<BeanCourseChg> courseChgs;

    public AdapterRecyclerViewCourseChg(){
        courseChgs = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_curriculum_viewpager_coursechg_recyclerview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TextView course, kind, detail, before, after, start, id;
        course = (TextView) holder.itemView.findViewById(R.id.cell_curriculum_viewpager_coursechg_recyclerview_course);
        kind = (TextView) holder.itemView.findViewById(R.id.cell_curriculum_viewpager_coursechg_recyclerview_kind);
        detail = (TextView) holder.itemView.findViewById(R.id.cell_curriculum_viewpager_coursechg_recyclerview_detail);
        before = (TextView) holder.itemView.findViewById(R.id.cell_curriculum_viewpager_coursechg_recyclerview_before);
        after = (TextView) holder.itemView.findViewById(R.id.cell_curriculum_viewpager_coursechg_recyclerview_after);
        start = (TextView) holder.itemView.findViewById(R.id.cell_curriculum_viewpager_coursechg_recyclerview_start);
        id = (TextView) holder.itemView.findViewById(R.id.cell_curriculum_viewpager_coursechg_recyclerview_id);

        holder.body.setVisibility(View.GONE);
        holder.body.setAlpha(1);

        course.setText(courseChgs.get(position).getCourse());
        detail.setText(courseChgs.get(position).getDepartment()+" "+courseChgs.get(position).getTeacher());
        before.setText(courseChgs.get(position).getBefore());
        after.setText(courseChgs.get(position).getAfter());
        start.setText(courseChgs.get(position).getStart().replace("-","/"));
        id.setText(courseChgs.get(position).getId());
        if (courseChgs.get(position).getKind()==0) kind.setText("學期");
        else kind.setText("暫時");
    }

    @Override
    public int getItemCount() {
        return courseChgs.size();
    }

    public void addItem(BeanCourseChg courseChg){
        courseChgs.add(courseChg);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private LinearLayout header, body;
        private ValueAnimator openValueAnimator, closeValueAnimator;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            header = (LinearLayout) itemView.findViewById(R.id.cell_curriculum_viewpager_coursechg_recyclerview_header);
            body = (LinearLayout) itemView.findViewById(R.id.cell_curriculum_viewpager_coursechg_recyclerview_body);
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
