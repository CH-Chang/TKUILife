package com.fly.tkuilife.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.fly.tkuilife.R;
import com.fly.tkuilife.adapter.AdapterRecyclerViewGrade;
import com.fly.tkuilife.bean.BeanGrade;
import com.fly.tkuilife.utils.AESHelper;
import com.fly.tkuilife.utils.KeyStoreHelper;
import com.fly.tkuilife.utils.SecretResource;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FragmentGrade extends Fragment {

    private View view;
    private boolean loaded;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_grade, container, false);
        init();
        return view;
    }
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden){}
        else {
            if (!loaded) loadingGrade();
        }
    }

    private void init(){
        initInteraction();
        initOther();
    }
    private void initInteraction(){
        ((SwipeRefreshLayout)view.findViewById(R.id.grade_swiperefreshlayout)).setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadingGrade();
            }
        });
    }
    private void initOther(){
        loaded = false;
    }

    private void loadingGrade(){
        SharedPreferences common = getContext().getSharedPreferences("common",Context.MODE_PRIVATE);
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.grade_swiperefreshlayout);
        if (common.getBoolean("login",false)) new FetchPostGrade(this).execute();
        else {
            view.findViewById(R.id.grade_content).setVisibility(View.GONE);
            view.findViewById(R.id.grade_error).setVisibility(View.VISIBLE);
            ((TextView)view.findViewById(R.id.grade_message)).setText("請先登入");
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
        }
    }

    private static class FetchPostGrade extends AsyncTask<Void, Void, ArrayList<BeanGrade>>{
        private WeakReference<FragmentGrade> reference;

        public FetchPostGrade(FragmentGrade fragmentGrade){
            reference = new WeakReference<FragmentGrade>(fragmentGrade);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            FragmentGrade fragmentGrade = reference.get();
            if (fragmentGrade==null||fragmentGrade.isDetached()) return;

            updateViewPrepare(fragmentGrade);
        }
        @Override
        protected ArrayList<BeanGrade> doInBackground(Void... voids) {
            return fetchPost(decryptAccount());
        }
        @Override
        protected void onPostExecute(ArrayList<BeanGrade> grades) {
            super.onPostExecute(grades);
            FragmentGrade fragmentGrade = reference.get();
            if (fragmentGrade==null||fragmentGrade.isDetached()) return;

            if (grades==null) updateViewFailed(fragmentGrade);
            else{
                fragmentGrade.loaded = true;

                if (grades.size()==0) updateViewMessenge(fragmentGrade, "查無成績資料");
                else updateViewGrade(fragmentGrade, grades);
            }
        }

        private String[] decryptAccount(){
            FragmentGrade fragmentGrade = reference.get();
            if (fragmentGrade==null||fragmentGrade.isDetached()) return null;

            SharedPreferences account = fragmentGrade.getContext().getSharedPreferences("account",Context.MODE_PRIVATE);


            KeyStoreHelper keyStoreHelper = new KeyStoreHelper(fragmentGrade.getContext());
            AESHelper aesHelper = new AESHelper();

            try {
                String[] secret = new String[]{keyStoreHelper.getKey(), keyStoreHelper.getIv()};
                String[] res = new String[]{
                        aesHelper.decrypt(Base64.decode(account.getString("id",""), Base64.NO_WRAP), secret[0], secret[1]),
                        aesHelper.decrypt(Base64.decode(account.getString("pk",""), Base64.NO_WRAP), secret[0], secret[1])
                };
                return res;
            } catch (UnrecoverableKeyException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            }
            return null;
        }
        private ArrayList<BeanGrade> fetchPost(String[] account){
            if (account!=null){
                SecretResource secretResource = new SecretResource();
                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .connectTimeout(5,TimeUnit.SECONDS)
                        .build();
                FormBody formBody = new FormBody.Builder()
                        .add("uid", account[0])
                        .add("pk", account[1])
                        .build();
                Request request = new Request.Builder()
                        .url(secretResource.getURL_Grade())
                        .post((RequestBody) formBody)
                        .build();
                Response response = null;
                try {
                    response = okHttpClient.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (response!=null&&response.isSuccessful()){
                    try {
                        ArrayList<BeanGrade> grades = parseXML(response.body().byteStream());
                        return grades;
                    } catch (XmlPullParserException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return null;
        }
        private ArrayList<BeanGrade> parseXML(InputStream xml) throws XmlPullParserException, IOException {
            ArrayList<BeanGrade> grades = null;
            BeanGrade grade = null;
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(xml, "utf-16");
            int eventType = parser.getEventType();
            while(eventType!= XmlPullParser.END_DOCUMENT){
                String name;
                switch (eventType){
                    case XmlPullParser.START_DOCUMENT:
                        grades = new ArrayList<>();
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if(name.equals("stu_scoquery_table")) grade = new BeanGrade();
                        else if(name.equals("考試別")) grade.setKind(parser.nextText().trim());
                        else if(name.equals("科目名稱")) grade.setCourse(parser.nextText().trim());
                        else if(name.equals("學分數")) grade.setCredit(parser.nextText().trim());
                        else if(name.equals("成績")) grade.setGrade(parser.nextText().trim());
                        else if(name.equals("錯誤訊息")) grade=null;
                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                        if(name.equals("stu_scoquery_table")){
                            if (grade!=null){
                                grades.add(grade);
                                grade=null;
                            }
                        }
                        break;
                    default:
                        break;
                }
                eventType = parser.next();
            }
            return grades;
        }

        private void updateViewPrepare(FragmentGrade fragmentGrade){
            SwipeRefreshLayout swipeRefreshLayout = fragmentGrade.view.findViewById(R.id.grade_swiperefreshlayout);
            if (!swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(true);
        }
        private void updateViewMessenge(FragmentGrade fragmentGrade,String messenge){
            fragmentGrade.view.findViewById(R.id.grade_content).setVisibility(View.GONE);
            fragmentGrade.view.findViewById(R.id.grade_error).setVisibility(View.VISIBLE);
            ((TextView)fragmentGrade.view.findViewById(R.id.grade_message)).setText(messenge);
            ((SwipeRefreshLayout)fragmentGrade.view.findViewById(R.id.grade_swiperefreshlayout)).setRefreshing(false);
        }
        private void updateViewGrade(FragmentGrade fragmentGrade, ArrayList<BeanGrade> grades){
            fragmentGrade.view.findViewById(R.id.grade_content).setVisibility(View.VISIBLE);
            fragmentGrade.view.findViewById(R.id.grade_error).setVisibility(View.GONE);

            TextView tv_totalcredit, tv_gotcredit, tv_average, tv_title;
            RecyclerView recyclerView;

            recyclerView = fragmentGrade.view.findViewById(R.id.grade_recyclerview);
            tv_average = fragmentGrade.view.findViewById(R.id.grade_average);
            tv_gotcredit = fragmentGrade.view.findViewById(R.id.grade_gotcredit);
            tv_title = fragmentGrade.view.findViewById(R.id.grade_title);
            tv_totalcredit = fragmentGrade.view.findViewById(R.id.grade_totalcredit);

            AdapterRecyclerViewGrade adapter = new AdapterRecyclerViewGrade();
            for (BeanGrade grade:grades) adapter.addItem(grade);

            int totalcredit, gotcredit, sum;
            float average;

            totalcredit = gotcredit = sum = 0;
            average = 0;

            for (int i=0;i<grades.size();i++){
                int grade = Integer.valueOf(grades.get(i).getGrade());
                int credit = Integer.valueOf(grades.get(i).getCredit());
                totalcredit+=credit;
                if(grade>60) gotcredit+=credit;
                sum+=credit*grade;
            }
            average = (float)sum/(float)totalcredit;

            recyclerView.setLayoutManager(new LinearLayoutManager(fragmentGrade.getContext()));
            recyclerView.setAdapter(adapter);

            if (grades.get(0).getKind().equals("期末考")) tv_title.setText("學期成績");
            else tv_title.setText("期中成績");
            tv_average.setText(String.format("%1.2f", average));
            tv_totalcredit.setText(String.format("%02d", totalcredit));
            tv_gotcredit.setText(String.format("%02d", gotcredit));

            ((SwipeRefreshLayout)fragmentGrade.view.findViewById(R.id.grade_swiperefreshlayout)).setRefreshing(false);
        }
        private void updateViewFailed(FragmentGrade fragmentGrade){
            ((SwipeRefreshLayout)fragmentGrade.view.findViewById(R.id.grade_swiperefreshlayout)).setRefreshing(false);

            Toast.makeText(fragmentGrade.getContext(), "查詢成績資料錯誤，請重試", Toast.LENGTH_SHORT).show();
        }
    }

    public void setLoaded(boolean loaded){
        this.loaded = loaded;
    }



}
