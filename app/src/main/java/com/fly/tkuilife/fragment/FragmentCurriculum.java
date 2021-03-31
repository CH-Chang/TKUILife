package com.fly.tkuilife.fragment;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.fly.tkuilife.R;
import com.fly.tkuilife.adapter.AdapterRecyclerViewCourseChg;
import com.fly.tkuilife.adapter.AdapterRecyclerViewExam;
import com.fly.tkuilife.adapter.AdapterViewPagerCurriculum;
import com.fly.tkuilife.bean.BeanCourseChg;
import com.fly.tkuilife.bean.BeanExam;
import com.google.android.material.tabs.TabLayout;

public class FragmentCurriculum extends Fragment implements TabLayout.OnTabSelectedListener {

    private View view;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private AdapterViewPagerCurriculum adapter;

    private SQLiteDatabase db;
    private Cursor cursor;

    private boolean[] loaded;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_curriculum, container, false);
        init();
        return view;
    }
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden){}
        else {
            loading(tabLayout.getSelectedTabPosition());
        }
    }
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        loading(tab.getPosition());
    }
    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }
    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }

    private void init(){
        initView();
        initDataBases();
        initOther();
        initViewPager();
        initTabLayout();
        initInteraction();
    }
    private void initView(){
        tabLayout = (TabLayout) view.findViewById(R.id.curriculum_tablayout);
        viewPager = (ViewPager) view.findViewById(R.id.curriculum_viewpager);
    }
    private void initViewPager(){
        adapter = new AdapterViewPagerCurriculum();
        adapter.addItemView(LayoutInflater.from(getContext()).inflate(R.layout.layout_curriculum_viewpager_timetable, null));
        adapter.addItemView(LayoutInflater.from(getContext()).inflate(R.layout.layout_curriculum_viewpager_exam, null));
        adapter.addItemView(LayoutInflater.from(getContext()).inflate(R.layout.layout_curriculum_viewpager_coursechg, null));
        viewPager.setAdapter(adapter);
    }
    private void initTabLayout(){
        String[] tab = {"學期課表", "考試小表", "課程異動"};
        for(int i=0;i<tab.length;i++) tabLayout.addTab(tabLayout.newTab());
        tabLayout.setupWithViewPager(viewPager, false);
        for (int i=0;i<tab.length;i++) tabLayout.getTabAt(i).setText(tab[i]);
    }
    private void initDataBases(){
        db = getContext().openOrCreateDatabase("curriculum", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS CURRICULUM (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "NAME VARCHAR(255) NOT NULL, " +
                "TEACHER VARCHAR(50) NOT NULL, " +
                "ROOM VARCHAR(50) NOT NULL, " +
                "SEATNUM INTEGER NOT NULL, " +
                "WEEK INTEGER NOT NULL, " +
                "SESSION INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS COURSECHG(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "DEPARTMENT VARCHAR(80) NOT NULL, " +
                "CURSE VARCHAR(255) NOT NULL, " +
                "TEACHER VARCHAR(50) NOT NULL, " +
                "BEF VARCHAR(255) NOT NULL, " +
                "AFT VARCHAR(255) NOT NULL, " +
                "START DATE NOT NULL, " +
                "TYPE INTEGER NOT NULL, " +
                "ID VARCHAR(50) NOT NULL" +
                ")");
        db.execSQL("CREATE TABLE IF NOT EXISTS EXAM(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "EXAMINEE VARCHAR(50) NOT NULL, " +
                "COURSE VARCHAR(255) NOT NULL, " +
                "REQUIRED INTEGER NOT NULL, " +
                "CREDIT INTEGER NOT NULL, " +
                "SEATNUM INTEGER NOT NULL, " +
                "KIND VARCHAR(50) NOT NULL, " +
                "TYPE VARCHAR(50) NOT NULL, " +
                "DAYTIME DATE, " +
                "SESSION INTEGER, " +
                "ROOM VARCHAR(50), " +
                "EXAMNUM INTEGER, " +
                "EXAMTIME INTEGER NOT NULL)");

    }
    private void initInteraction(){
        tabLayout.addOnTabSelectedListener(this);
        ((SwipeRefreshLayout)adapter.getItemView(0).findViewById(R.id.curriculum_viewpager_timetable_swiperefreshlayout)).setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadingTimeTable();
            }
        });
        ((SwipeRefreshLayout)adapter.getItemView(1).findViewById(R.id.curriculum_viewpager_exam_swiperefreshlayout)).setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadingExam();
            }
        });
        ((SwipeRefreshLayout)adapter.getItemView(2).findViewById(R.id.curriculum_viewpager_coursechg_swiperefreshlayout)).setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadingCourseChg();
            }
        });
    }
    private void initOther(){
        loaded = new boolean[]{false, false, false};
    }

    private void loading(){
        loadingTimeTable();
        loadingExam();
        loadingCourseChg();
    }
    private void loading(int position){
        switch (position){
            case 0:
                if (!loaded[0]) {
                    ((SwipeRefreshLayout) adapter.getItemView(0).findViewById(R.id.curriculum_viewpager_timetable_swiperefreshlayout)).setRefreshing(true);
                    loadingTimeTable();
                }
                break;
            case 1:
                if (!loaded[1]) {
                    ((SwipeRefreshLayout) adapter.getItemView(1).findViewById(R.id.curriculum_viewpager_exam_swiperefreshlayout)).setRefreshing(true);
                    loadingExam();
                }
                break;
            case 2:
                if (!loaded[2]) {
                    ((SwipeRefreshLayout) adapter.getItemView(2).findViewById(R.id.curriculum_viewpager_coursechg_swiperefreshlayout)).setRefreshing(true);
                    loadingCourseChg();
                }
                break;
        }
    }
    private void loadingCourseChg(){

        RecyclerView recyclerView = adapter.getItemView(2).findViewById(R.id.curriculum_viepager_coursechg_recyclerview);

        AdapterRecyclerViewCourseChg adapter_recyclerview = new AdapterRecyclerViewCourseChg();

        cursor = db.rawQuery("SELECT * FROM COURSECHG", null);
        cursor.moveToFirst();
        for(int i=0;i<cursor.getCount();i++){
            adapter_recyclerview.addItem(
                    new BeanCourseChg(
                            cursor.getInt(7),
                            cursor.getString(2),
                            cursor.getString(3),
                            cursor.getString(1),
                            cursor.getString(4),
                            cursor.getString(5),
                            cursor.getString(6),
                            cursor.getString(8)
                    )
            );
            cursor.moveToNext();
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter_recyclerview);

        SwipeRefreshLayout swipeRefreshLayout = adapter.getItemView(2).findViewById(R.id.curriculum_viewpager_coursechg_swiperefreshlayout);
        if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);

        loaded[2] = true;
    }
    private void loadingExam(){
        RecyclerView recyclerView = (RecyclerView) adapter.getItemView(1).findViewById(R.id.curriculum_viepager_exam_recyclerview);

         AdapterRecyclerViewExam adapter_recyclerview = new AdapterRecyclerViewExam();

        cursor = db.rawQuery("SELECT * FROM EXAM ORDER BY DAYTIME ASC", null);
        cursor.moveToFirst();
        for(int i=0;i<cursor.getCount();i++){
            adapter_recyclerview.addItem(
                    new BeanExam(
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3),
                            cursor.getString(4),
                            cursor.getString(8),
                            cursor.getString(10),
                            cursor.getString(7),
                            cursor.getString(6),
                            cursor.getInt(12),
                            cursor.getInt(9),
                            cursor.getInt(5),
                            cursor.getInt(11)
                    )
            );
            cursor.moveToNext();
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter_recyclerview);

        SwipeRefreshLayout swipeRefreshLayout = adapter.getItemView(1).findViewById(R.id.curriculum_viewpager_exam_swiperefreshlayout);
        if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);

        loaded[1] = true;
    }
    private void loadingTimeTable(){
        clearCurriculum();

        for(int i=1;i<=7;i++){
            cursor = db.rawQuery("SELECT * FROM CURRICULUM WHERE WEEK="+String.valueOf(i), null);
            cursor.moveToFirst();

            if(cursor.getCount()==0) continue;

            String name = cursor.getString(1);
            String teacher = cursor.getString(2);
            String room = cursor.getString(3);
            int start = cursor.getInt(6);
            int session = 1;

            for(int j=1;j<cursor.getCount();j++){
                cursor.moveToNext();
                if((!name.equals(cursor.getString(1)))||(!teacher.equals(cursor.getString(2)))||(!room.equals(cursor.getString(3)))){
                    addClass(i, start, start+session-1, teacher, name, room);
                    name = cursor.getString(1);
                    room = cursor.getString(3);
                    teacher = cursor.getString(2);
                    start = cursor.getInt(6);
                    session=1;
                }
                else session++;
                if(j==cursor.getCount()-1){
                    addClass(i, start, cursor.getInt(6), teacher, name, cursor.getString(3));
                }
            }
        }

        SwipeRefreshLayout swipeRefreshLayout = adapter.getItemView(0).findViewById(R.id.curriculum_viewpager_timetable_swiperefreshlayout);
        if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);

        loaded[0] = true;
    }

    private void clearCurriculum(){
        ((RelativeLayout) adapter.getItemView(0).findViewById(R.id.curriculum_viewpager_timetable_week1)).removeAllViews();
        ((RelativeLayout) adapter.getItemView(0).findViewById(R.id.curriculum_viewpager_timetable_week2)).removeAllViews();
        ((RelativeLayout) adapter.getItemView(0).findViewById(R.id.curriculum_viewpager_timetable_week3)).removeAllViews();
        ((RelativeLayout) adapter.getItemView(0).findViewById(R.id.curriculum_viewpager_timetable_week4)).removeAllViews();
        ((RelativeLayout) adapter.getItemView(0).findViewById(R.id.curriculum_viewpager_timetable_week5)).removeAllViews();
        ((RelativeLayout) adapter.getItemView(0).findViewById(R.id.curriculum_viewpager_timetable_week6)).removeAllViews();
        ((RelativeLayout) adapter.getItemView(0).findViewById(R.id.curriculum_viewpager_timetable_week7)).removeAllViews();
    }
    private void addClass(int week, int start, int end, String teacher, String name, String room){
        RelativeLayout layout = null;
        switch (week){
            case 1:
                layout = (RelativeLayout) adapter.getItemView(0).findViewById(R.id.curriculum_viewpager_timetable_week1);
                break;
            case 2:
                layout = (RelativeLayout) adapter.getItemView(0).findViewById(R.id.curriculum_viewpager_timetable_week2);
                break;
            case 3:
                layout = (RelativeLayout) adapter.getItemView(0).findViewById(R.id.curriculum_viewpager_timetable_week3);
                break;
            case 4:
                layout = (RelativeLayout) adapter.getItemView(0).findViewById(R.id.curriculum_viewpager_timetable_week4);
                break;
            case 5:
                layout = (RelativeLayout) adapter.getItemView(0).findViewById(R.id.curriculum_viewpager_timetable_week5);
                break;
            case 6:
                layout = (RelativeLayout) adapter.getItemView(0).findViewById(R.id.curriculum_viewpager_timetable_week6);
                break;
            case 7:
                layout = (RelativeLayout) adapter.getItemView(0).findViewById(R.id.curriculum_viewpager_timetable_week7);
                break;
        }

        LinearLayout layoutClass = createClassLayout(start, end, teacher, name, room);
        layout.addView(layoutClass);
    }
    private LinearLayout createClassLayout(int start, int end, String teacher, String name, String room){
        float height = 100 * getContext().getResources().getDisplayMetrics().density;


        LinearLayout layout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.cell_curriculum_viewpager_timetable, null);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int)height*(end-start+1));
        layoutParams.topMargin = (start-1)*(int)height;

        layout.setLayoutParams(layoutParams);

        ((TextView) layout.findViewById(R.id.cell_curriculum_viewpager_timetable_teacher)).setText(teacher);
        ((TextView) layout.findViewById(R.id.cell_curriculum_viewpager_timetable_room)).setText(room);
        ((TextView) layout.findViewById(R.id.cell_curriculum_viewpager_timetable_name)).setText(name);


        return layout;
    }


    public void setLoaded(boolean loaded){
        for (int i=0;i<this.loaded.length;i++) this.loaded[i] = loaded;
    }
}
