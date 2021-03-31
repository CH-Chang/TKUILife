package com.fly.tkuilife.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.fly.tkuilife.R;
import com.fly.tkuilife.utils.SecretResource;
import com.fly.tkuilife.view.MarqueeTextView;
import com.squareup.picasso.Picasso;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ActivityNewsPaperReader extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private Toolbar toolbar;
    private TextView title, article, kind;
    private LinearLayout content;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        loading();
    }
    @Override
    public void onRefresh() {
        loading();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_newspaperreader_toolbar, menu);

        Drawable icon = menu.findItem(R.id.newspaper_toolbar_share).getIcon();
        icon.setTint(Color.WHITE);
        menu.findItem(R.id.newspaper_toolbar_share).setIcon(icon);

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.newspaper_toolbar_share:
                startActivity(Intent.createChooser(new Intent(Intent.ACTION_SEND).putExtra(Intent.EXTRA_TEXT, "淡江時報 - " + title.getText()+"\nhttp://tkutimes.tku.edu.tw/dtl.aspx?no="+getIntent().getStringExtra("id")).setType("text/plain"), "分享內容"));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init(){
        initView();
        initActionBar();
        initInteraction();
    }
    private void initView(){
        setContentView(R.layout.activity_newspaperreader);
        toolbar = findViewById(R.id.newspaperreader_toolbar);
        title = findViewById(R.id.newspaperreader_title);
        article = findViewById(R.id.newspaperreader_article);
        kind = findViewById(R.id.newspaperreader_kind);
        swipeRefreshLayout = findViewById(R.id.newspaperreader_swiperrefreshlayout);
        content = findViewById(R.id.newspaperreader_content);
    }
    private void initActionBar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    private void initInteraction(){
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    private void loading(){
        new FetchPost(this).execute(getIntent().getStringExtra("id"));
    }



    private static class FetchPost extends AsyncTask<String, Void, ArrayList<String[]>>{
        private WeakReference<ActivityNewsPaperReader> reference;

        public FetchPost(ActivityNewsPaperReader activityNewsPaperReader){
            reference = new WeakReference<ActivityNewsPaperReader>(activityNewsPaperReader);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            ActivityNewsPaperReader activityNewsPaperReader = reference.get();
            if (activityNewsPaperReader==null||activityNewsPaperReader.isDestroyed()) return;

            updateViewPrepare(activityNewsPaperReader);
        }
        @Override
        protected ArrayList<String[]> doInBackground(String... strings) {
            return fetchPost(strings[0]);
        }
        @Override
        protected void onPostExecute(ArrayList<String[]> strings) {
            super.onPostExecute(strings);

            ActivityNewsPaperReader activityNewsPaperReader = reference.get();
            if (activityNewsPaperReader==null||activityNewsPaperReader.isDestroyed()) return;

            if (strings!=null) updateViewSuccessful(activityNewsPaperReader, strings);
            else updateViewFailed(activityNewsPaperReader);
        }


        private ArrayList<String[]> fetchPost(String id){
            SecretResource secretResource = new SecretResource();
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .build();
            Request request = new Request.Builder()
                    .url(secretResource.getURL_NewsPaperNews()+id+".xml")
                    .method("GET", null)
                    .build();
            Response response = null;
            try {
                response = okHttpClient.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response!=null&&response.isSuccessful()){
                try {
                    return parseXML(response.body().byteStream());
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        private ArrayList<String[]> parseXML(InputStream xml) throws XmlPullParserException, IOException {
            SecretResource secretResource = new SecretResource();
            ArrayList<String[]> newsPapers = null;
            String[] newsPaper = null;
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(xml, "utf-16");
            int eventType = parser.getEventType();
            while (eventType!=XmlPullParser.END_DOCUMENT) {
                String name = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        newsPapers = new ArrayList<>();
                        break;
                    case XmlPullParser.START_TAG:
                        if (name.equals("cht")) newsPaper = new String[]{null, null};
                        else if (name.equals("標題")) newsPaper[0] = parser.nextText().trim();
                        else if (name.equals("內文")) newsPaper[1] = parser.nextText().trim();
                        else if (name.equals("圖片路徑")) newsPaper[0] = secretResource.getURL_NewsPaperLatestImg()+parser.nextText().trim();
                        else if (name.equals("圖片內文")) newsPaper[1] = parser.nextText().trim();
                        break;
                    case XmlPullParser.END_TAG:
                        if (name.equals("cht")) {
                            if (newsPaper != null) {
                                newsPapers.add(newsPaper);
                                newsPaper = null;
                            }
                        }
                        break;
                }
                eventType = parser.next();
            }
            return newsPapers;
        }

        private void updateViewPrepare(ActivityNewsPaperReader activityNewsPaperReader){
            if (!activityNewsPaperReader.swipeRefreshLayout.isRefreshing()) activityNewsPaperReader.swipeRefreshLayout.setRefreshing(true);
        }
        private void updateViewSuccessful(ActivityNewsPaperReader activityNewsPaperReader, ArrayList<String[]> newsPapers){
            activityNewsPaperReader.kind.setText(activityNewsPaperReader.getIntent().getStringExtra("kind"));
            activityNewsPaperReader.getSupportActionBar().setTitle(newsPapers.get(0)[0]);
            activityNewsPaperReader.title.setText(newsPapers.get(0)[0]);
            activityNewsPaperReader.article.setText(newsPapers.get(0)[1]);

            for (int i=1;i<newsPapers.size();i++){
                ImageView imageView = new ImageView(activityNewsPaperReader);
                LinearLayout.LayoutParams layoutParams_imageview = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams_imageview.setMargins(0,50,0,0);
                Picasso.get().load(newsPapers.get(i)[0]).into(imageView);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setAdjustViewBounds(true);
                imageView.setPadding(10,10,10,10);
                imageView.setLayoutParams(layoutParams_imageview);

                MarqueeTextView textView = new MarqueeTextView(activityNewsPaperReader);
                textView.setText(newsPapers.get(i)[1]);
                textView.setSingleLine(true);
                textView.setFocusable(true);
                textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                textView.setTextColor(activityNewsPaperReader.getColor(R.color.colorTextPrimary));
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                LinearLayout.LayoutParams layoutParams_textview = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                textView.setLayoutParams(layoutParams_textview);

                activityNewsPaperReader.content.addView(imageView);
                activityNewsPaperReader.content.addView(textView);
            }

            if (activityNewsPaperReader.swipeRefreshLayout.isRefreshing()) activityNewsPaperReader.swipeRefreshLayout.setRefreshing(false);
        }
        private void updateViewFailed(ActivityNewsPaperReader activityNewsPaperReader){
            if (activityNewsPaperReader.swipeRefreshLayout.isRefreshing()) activityNewsPaperReader.swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(activityNewsPaperReader, "獲取內容錯誤，請重試", Toast.LENGTH_SHORT).show();
        }
    }
}
