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
import com.fly.tkuilife.bean.BeanEvent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AdapterRecyclerViewEvent extends RecyclerView.Adapter<AdapterRecyclerViewEvent.ViewHolder> {

    private ArrayList<BeanEvent> events;
    private SimpleDateFormat formator;

    public AdapterRecyclerViewEvent(){
        events = new ArrayList<>();
        formator = new SimpleDateFormat("", Locale.UK);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_news_viewpager_event_recyclerview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TextView title = holder.itemView.findViewById(R.id.cell_news_viewpager_event_recyclerview_title);
        TextView kind = holder.itemView.findViewById(R.id.cell_news_viewpager_event_recyclerview_kind);
        TextView id = holder.itemView.findViewById(R.id.cell_news_viewpager_event_recyclerview_id);
        TextView room = holder.itemView.findViewById(R.id.cell_news_viewpager_event_recyclerview_room);
        TextView status = holder.itemView.findViewById(R.id.cell_news_viewpager_event_recyclerview_status);
        TextView classtime = holder.itemView.findViewById(R.id.cell_news_viewpager_event_recyclerview_classtime);
        TextView signtime = holder.itemView.findViewById(R.id.cell_news_viewpager_event_recyclerview_signtime);
        title.setText(events.get(position).getTitle());
        kind.setText(events.get(position).getKind());
        id.setText(events.get(position).getId());
        room.setText(events.get(position).getRoom());
        status.setText(events.get(position).getStatus());
        formator.applyPattern("yyyy-MM-dd'T'HH:mm:ssZ");
        Date class_start, class_end, sign_start, sign_end;
        class_start = class_end = sign_start = sign_end = null;
        try {
            class_start = formator.parse(events.get(position).getClass_start());
            class_end = formator.parse(events.get(position).getClass_end());
            sign_start = formator.parse(events.get(position).getSign_start());
            sign_end = formator.parse(events.get(position).getSign_end());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        formator.applyLocalizedPattern("yyyy/MM/dd HH:mm");
        classtime.setText(formator.format(class_start)+" 至 "+formator.format(class_end));
        signtime.setText(formator.format(sign_start)+" 至 "+formator.format(sign_end));
        holder.body.setAlpha(1);
        holder.body.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public void addItem(BeanEvent event){
        events.add(event);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private LinearLayout header, body;
        private ValueAnimator openValueAnimator, closeValueAnimator;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            header = (LinearLayout) itemView.findViewById(R.id.cell_news_viewpager_event_recyclerview_header);
            body = (LinearLayout) itemView.findViewById(R.id.cell_news_viewpager_event_recyclerview_body);
            openValueAnimator = null;
            closeValueAnimator = null;
            header.setOnClickListener(this);
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
