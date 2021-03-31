package com.fly.tkuilife.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fly.tkuilife.R;
import com.fly.tkuilife.adapter.AdapterRecyclerViewContact;
import com.fly.tkuilife.bean.BeanContact;
import com.fly.tkuilife.utils.RecyclerViewItemClickSupport;

public class ActivityContact extends AppCompatActivity implements AdapterView.OnItemSelectedListener, RecyclerViewItemClickSupport.OnItemClickListener {

    private Toolbar toolbar;
    private Spinner spinner;
    private RecyclerView recyclerView;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        loading();
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.contact_spinner:
                loadingRecyclerview(position);
                break;
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
    @Override
    public void onItemClick(RecyclerView recyclerView, int position, View view) {
        switch (recyclerView.getId()){
            case R.id.contact_recyclerview:
                AdapterRecyclerViewContact adapter = (AdapterRecyclerViewContact)recyclerView.getAdapter();
                String telephone = adapter.getItem(position).getTelephone().replace("-","");
                String extension = adapter.getItem(position).getExtension();
                if (!extension.equals("")) Toast.makeText(this,"請撥打分機 - " + extension, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+telephone)));
                break;
        }
    }

    private void init(){
        initView();
        initActionBar();
        initRecyclerView();
        initInteraction();
    }
    private void initView(){
        setContentView(R.layout.activity_contact);
        toolbar = findViewById(R.id.contact_toolbar);
        spinner = findViewById(R.id.contact_spinner);
        recyclerView = findViewById(R.id.contact_recyclerview);
    }
    private void initActionBar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }
    private void initInteraction(){
        spinner.setOnItemSelectedListener(this);
        RecyclerViewItemClickSupport.addTo(recyclerView).setOnItemClickListener(this);
    }
    private void initRecyclerView(){
        recyclerView.setAdapter(new AdapterRecyclerViewContact());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    private void loading(){
        loadingSpinner();
    }
    private void loadingSpinner(){
        String office[] = {"蘭陽副校長室","秘書處","文錙藝術中心","品質保證稽核處","校務研究中心","教務處","學生事務處","總務處","環境保護及安全衛生中心","人力資源處","財務處","覺生紀念圖書館","資訊處","校友服務暨資源發展處","國際暨兩岸事務處","研究發展處","文學院","理學院","工學院","商管學院","外國語文學院","國際事務學院","教育學院","全球發展學院","網路校園","推廣教育處","體育事務處","軍訓室"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, office);
        spinner.setAdapter(adapter);
    }
    private void loadingRecyclerview(int position){
        switch (position){
            case 0:
                loadingOfficeofVicePresidentforLanyangCampus();
                break;
            case 1:
                loadingOfficeoftheSecretariat();
                break;
            case 2:
                loadingCarrieChangFineArtsCenter();
                break;
            case 3:
                loadingOfficeofQualityAssuranceandAudit();
                break;
            case 4:
                loadingCenterforInstitutionalResearch();
                break;
            case 5:
                loadingOfficeofStudentAffairs();
                break;
            case 6:
                loadingOfficeofGeneralAffairs();
                break;
            case 7:
                loadingCenterforEnvironmentalProtectionSafetyandHealth();
                break;
            case 8:
                loadingOfficeofHumanResources();
                break;
            case 9:
                loadingOfficeofFinance();
                break;
            case 10:
                loadingChuehShengMemorialLibrary();
                break;
            case 11:
                loadingOfficeofInformationServices();
                break;
            case 12:
                loadingOfficeofAlumniServicesandResourcesDevelopment();
                break;
            case 13:
                loadingOICSA();
                break;
            case 14:
                loadingOfficeofResearchandDevelopment();
                break;
            case 15:
                loadingCollegeofLiberalArts();
                break;
            case 16:
                loadingCollegeofScience();
                break;
            case 17:
                loadingCollegeofEngineer();
                break;
            case 18:
                loadingCollegeofBusinessandManagement();
                break;
            case 19:
                loadingCollegeofForeignLanguagesandLiteratures();
                break;
            case 20:
                loadingCollegeofInternationalAffairs();
                break;
            case 21:
                loadingCollegeofEducation();
                break;
            case 22:
                loadingCollegeofGlobalDevelopment();
                break;
            case 23:
                loadingInternetCampus();
                break;
            case 24:
                loadingOfficeofContinuingEducation();
                break;
            case 25:
                loadingOfficeofPhysicalEducation();
                break;
            case 26:
                loadingOfficeofMilitaryEducationandTraining();
                break;
            case 27:
                loadingSaftyCampus();
                break;

        }
    }
    private void loadingOfficeofVicePresidentforLanyangCampus(){
        AdapterRecyclerViewContact adapter = (AdapterRecyclerViewContact) recyclerView.getAdapter();

        adapter.clearAllItem();
        adapter.addItem(new BeanContact("副校長室","03-9873088","7001","蘭陽副校長室"));
        adapter.addItem(new BeanContact("教育業務","02-26215656","7002","蘭陽副校長室"));
        adapter.addItem(new BeanContact("教育業務","02-26215656","7003","蘭陽副校長室"));
        adapter.addItem(new BeanContact("學務業務","02-26215656","7004","蘭陽副校長室"));
        adapter.addItem(new BeanContact("學務業務","02-26215656","7015","蘭陽副校長室"));
        adapter.addItem(new BeanContact("體育業務","02-26215656","7057","蘭陽副校長室"));
        adapter.addItem(new BeanContact("總務業務","02-26215656","7007","蘭陽副校長室"));
        adapter.addItem(new BeanContact("總務業務","02-26215656","7018","蘭陽副校長室"));
        adapter.addItem(new BeanContact("財務業務","02-26215656","7013","蘭陽副校長室"));
        adapter.addItem(new BeanContact("圖書業務","02-26215656","7009","蘭陽副校長室"));
        adapter.addItem(new BeanContact("圖書業務","02-26215656","7044","蘭陽副校長室"));
        adapter.addItem(new BeanContact("資訊業務","02-26215656","7011","蘭陽副校長室"));
        adapter.addItem(new BeanContact("資訊業務","02-26215656","7012","蘭陽副校長室"));
        adapter.addItem(new BeanContact("軍訓業務","02-26215656","7006","蘭陽副校長室"));
        adapter.addItem(new BeanContact("軍訓業務","02-26215656","7053","蘭陽副校長室"));
        adapter.notifyDataSetChanged();
    }
    private void loadingOfficeoftheSecretariat(){
        AdapterRecyclerViewContact adapter = (AdapterRecyclerViewContact) recyclerView.getAdapter();

        adapter.clearAllItem();
        adapter.addItem(new BeanContact("劉艾華","02-26232764","","秘書處 秘書長"));
        adapter.addItem(new BeanContact("劉桂香","02-26215656","2354","秘書處 組員"));
        adapter.addItem(new BeanContact("王春貴","02-26215656","2345","秘書處 文書組 組長"));
        adapter.addItem(new BeanContact("陳君祺","02-26215656","2039","秘書處 文書組 組員"));
        adapter.addItem(new BeanContact("黃懿嫃","02-26215656","2374","秘書處 文書組 組員"));
        adapter.addItem(new BeanContact("黃瓊玉","02-26215656","2101","秘書處 文書組 約聘人員"));
        adapter.addItem(new BeanContact("馬雨沛","02-26215656","2040","秘書處 淡江時報委員會 社長"));
        adapter.addItem(new BeanContact("潘紹愷","02-26215656","2517","秘書處 淡江時報委員會 專員"));
        adapter.addItem(new BeanContact("張瑟玉","02-26215656","2799","秘書處 淡江時報委員會 書記"));

        adapter.notifyDataSetChanged();
    }
    private void loadingCarrieChangFineArtsCenter(){
        AdapterRecyclerViewContact adapter = (AdapterRecyclerViewContact) recyclerView.getAdapter();

        adapter.clearAllItem();
        adapter.addItem(new BeanContact("葉如真","02-26215656","2192","文錙藝術中心 展覽廳 行政助理"));
        adapter.addItem(new BeanContact("黃維綱","02-26215656","2618","文錙藝術中心 海事博物館 行政助理"));
        adapter.addItem(new BeanContact("杜美先","02-26215656","3033","文錙藝術中心 書法研究室 業務"));
        adapter.addItem(new BeanContact("海事博物館","02-26215656","2619","文錙藝術中心 海事博物館"));
        adapter.addItem(new BeanContact("海事博物館","02-26215656","2650","文錙藝術中心 海事博物館"));

        adapter.notifyDataSetChanged();
    }
    private void loadingOfficeofQualityAssuranceandAudit(){
        AdapterRecyclerViewContact adapter = (AdapterRecyclerViewContact) recyclerView.getAdapter();

        adapter.clearAllItem();
        adapter.addItem(new BeanContact("張德文","02-26215656","2315","品質保證稽核處 稽核長"));
        adapter.addItem(new BeanContact("孔令娟","02-26215656","3569","品質保證稽核處 秘書"));
        adapter.addItem(new BeanContact("黃祖楨","02-26215656","2043","品質保證稽核處 專員"));
        adapter.addItem(new BeanContact("李文揚","02-26215656","2097","品質保證稽核處 約聘專任研究助理"));
        adapter.addItem(new BeanContact("林素月","02-26215656","2349","品質保證稽核處 約聘專任研究助理"));
        adapter.addItem(new BeanContact("李思儀","02-26215656","2097","品質保證稽核處 約聘人員"));
        adapter.addItem(new BeanContact("陳怡廷","02-26215656","2349","品質保證稽核處 約聘人員"));
        adapter.addItem(new BeanContact("李佳怡","02-26215656","2423","品質保證稽核處 約聘人員"));
        adapter.addItem(new BeanContact("黃雅琪","02-26215656","3569","品質保證稽核處 約聘人員"));
        adapter.addItem(new BeanContact("曾盈雨","02-26215656","2717","品質保證稽核處 約聘人員"));
        adapter.addItem(new BeanContact("林建利","02-26215656","2717","品質保證稽核處 約聘人員"));
        adapter.addItem(new BeanContact("高蘊柔","02-26215656","2717","品質保證稽核處 約聘人員"));

        adapter.notifyDataSetChanged();
    }
    private void loadingCenterforInstitutionalResearch(){
        AdapterRecyclerViewContact adapter = (AdapterRecyclerViewContact) recyclerView.getAdapter();

        adapter.clearAllItem();
        adapter.addItem(new BeanContact("張德文","02-26215656","2315","校務研究中心 主任"));
        adapter.addItem(new BeanContact("邱妤甄","02-26215656","2146","校務研究中心 專任助理"));
        adapter.addItem(new BeanContact("曾筱淩","02-26215656","2146","校務研究中心 專任助理"));
        adapter.addItem(new BeanContact("林素月","02-26215656","2349","校務研究中心 研究助理"));
        adapter.addItem(new BeanContact("李文揚","02-26215656","2097","校務研究中心 研究助理"));

        adapter.notifyDataSetChanged();
    }
    private void loadingOfficeofAcademicAffairs(){
        AdapterRecyclerViewContact adapter = (AdapterRecyclerViewContact) recyclerView.getAdapter();

        adapter.clearAllItem();
        adapter.addItem(new BeanContact("簡文慧","02-26215656","2359","教務處 註冊組 組長"));
        adapter.addItem(new BeanContact("邱妤甄","02-26215656","2146","校務研究中心 專任助理"));
        adapter.addItem(new BeanContact("曾筱淩","02-26215656","2146","校務研究中心 專任助理"));
        adapter.addItem(new BeanContact("林素月","02-26215656","2349","校務研究中心 研究助理"));
        adapter.addItem(new BeanContact("李文揚","02-26215656","2097","校務研究中心 研究主理"));

        adapter.notifyDataSetChanged();
    }
    private void loadingOfficeofStudentAffairs(){
        AdapterRecyclerViewContact adapter = (AdapterRecyclerViewContact) recyclerView.getAdapter();

        adapter.clearAllItem();
        adapter.addItem(new BeanContact("生活輔導組","02-26215656","2214","學生事務處"));
        adapter.addItem(new BeanContact("生活輔導組","02-26215656","2217","學生事務處"));
        adapter.addItem(new BeanContact("生活輔導組","02-26215656","2817","學生事務處"));
        adapter.addItem(new BeanContact("課外活動輔導組","02-26215656","2220","學生事務處"));
        adapter.addItem(new BeanContact("課外活動輔導組","02-26215656","2224","學生事務處"));
        adapter.addItem(new BeanContact("課外活動輔導組","02-26215656","2226","學生事務處"));
        adapter.addItem(new BeanContact("學生學習發展組","02-26215656","3531","學生事務處"));
        adapter.addItem(new BeanContact("學生學習發展組","02-26215656","2160","學生事務處"));
        adapter.addItem(new BeanContact("衛生保健組","02-26215656","2257","學生事務處"));
        adapter.addItem(new BeanContact("衛生保健組","02-26215656","2373","學生事務處"));
        adapter.addItem(new BeanContact("諮商暨職涯輔導組","02-26215656","2221","學生事務處"));
        adapter.addItem(new BeanContact("諮商暨職涯輔導組","02-26215656","2270","學生事務處"));
        adapter.addItem(new BeanContact("諮商暨職涯輔導組","02-26215656","2491","學生事務處"));
        adapter.addItem(new BeanContact("住宿輔導組","02-26215656","2154","學生事務處"));
        adapter.addItem(new BeanContact("住宿輔導組","02-26215656","2395","學生事務處"));
        adapter.addItem(new BeanContact("住宿輔導組","02-26215656","2396","學生事務處"));
        adapter.addItem(new BeanContact("原住民族學生資源中心","02-26215656","3636","學生事務處"));
        adapter.addItem(new BeanContact("學生事務處","02-26215656","2048","學生事務處"));
        adapter.addItem(new BeanContact("學生事務處","02-26215656","2398","學生事務處"));
        adapter.addItem(new BeanContact("學生事務處","02-26215656","2399","學生事務處"));
        adapter.addItem(new BeanContact("學生事務處","02-26215656","2379","學生事務處"));


        adapter.notifyDataSetChanged();
    }
    private void loadingOfficeofGeneralAffairs(){
        AdapterRecyclerViewContact adapter = (AdapterRecyclerViewContact) recyclerView.getAdapter();

        adapter.clearAllItem();
        adapter.addItem(new BeanContact("事物整備組","02-26215656","2231","總務處"));
        adapter.addItem(new BeanContact("事物整備組","02-26215656","2376","總務處"));
        adapter.addItem(new BeanContact("事物整備組","02-26215656","2498","總務處"));
        adapter.addItem(new BeanContact("節能與空間組","02-26215656","2235","總務處"));
        adapter.addItem(new BeanContact("節能與空間組","02-26215656","2236","總務處"));
        adapter.addItem(new BeanContact("資產組","02-26215656","2229","總務處"));
        adapter.addItem(new BeanContact("資產組","02-26215656","2030","總務處"));
        adapter.addItem(new BeanContact("資產組","02-26215656","2230","總務處"));
        adapter.addItem(new BeanContact("總務組","02-26215656","8602","總務處"));
        adapter.addItem(new BeanContact("總務組","02-26215656","8603","總務處"));
        adapter.addItem(new BeanContact("總務組","02-26215656","8606","總務處"));
        adapter.addItem(new BeanContact("出納組","02-26215656","2259","總務處"));
        adapter.addItem(new BeanContact("出納組","02-26215656","8501","總務處"));
        adapter.addItem(new BeanContact("總務處","02-26215656","2228","總務處"));
        adapter.addItem(new BeanContact("總務處","02-26215656","2522","總務處"));


        adapter.notifyDataSetChanged();
    }
    private void loadingCenterforEnvironmentalProtectionSafetyandHealth(){
        AdapterRecyclerViewContact adapter = (AdapterRecyclerViewContact) recyclerView.getAdapter();

        adapter.clearAllItem();
        adapter.addItem(new BeanContact("環境保護及安全衛生中心","02-26215656","2005","環境保護及安全衛生中心"));
        adapter.addItem(new BeanContact("環境保護及安全衛生中心","02-26215656","2276","環境保護及安全衛生中心"));
        adapter.addItem(new BeanContact("環境保護及安全衛生中心","02-26215656","3648","環境保護及安全衛生中心"));


        adapter.notifyDataSetChanged();
    }
    private void loadingOfficeofHumanResources(){
        AdapterRecyclerViewContact adapter = (AdapterRecyclerViewContact) recyclerView.getAdapter();

        adapter.clearAllItem();
        adapter.addItem(new BeanContact("管理企劃組","02-26215656","2238","人力資源處"));
        adapter.addItem(new BeanContact("管理企劃組","02-26215656","2291","人力資源處"));
        adapter.addItem(new BeanContact("管理企劃組","02-26215656","3058","人力資源處"));
        adapter.addItem(new BeanContact("職能福利組","02-26215656","2239","人力資源處"));
        adapter.addItem(new BeanContact("職能福利組","02-26215656","2264","人力資源處"));
        adapter.addItem(new BeanContact("職能福利組","02-26215656","3059","人力資源處"));
        adapter.addItem(new BeanContact("人力資源處","02-26215656","2296","人力資源處"));
        adapter.addItem(new BeanContact("人力資源處","02-26215656","2240","人力資源處"));
        adapter.addItem(new BeanContact("人力資源處","02-26215656","2237","人力資源處"));

        adapter.notifyDataSetChanged();
    }
    private void loadingOfficeofFinance(){
        AdapterRecyclerViewContact adapter = (AdapterRecyclerViewContact) recyclerView.getAdapter();

        adapter.clearAllItem();
        adapter.addItem(new BeanContact("會計組","02-26215656","2066","財務處"));
        adapter.addItem(new BeanContact("會計組","02-26215656","2067","財務處"));
        adapter.addItem(new BeanContact("預算組","02-26215656","2062","財務處"));
        adapter.addItem(new BeanContact("預算組","02-26215656","2063","財務處"));
        adapter.addItem(new BeanContact("預算組","02-26215656","2068","財務處"));
        adapter.addItem(new BeanContact("預算組","02-26215656","2069","財務處"));
        adapter.addItem(new BeanContact("財務處","02-26215656","2355","財務處"));
        adapter.addItem(new BeanContact("財務處","02-26215656","2060","財務處"));
        adapter.addItem(new BeanContact("財務處","02-26215656","2243","財務處"));


        adapter.notifyDataSetChanged();
    }
    private void loadingChuehShengMemorialLibrary(){
        AdapterRecyclerViewContact adapter = (AdapterRecyclerViewContact) recyclerView.getAdapter();

        adapter.clearAllItem();
        adapter.addItem(new BeanContact("採編組","02-26215656","2284","覺生紀念圖書館"));
        adapter.addItem(new BeanContact("採編組","02-26215656","2294","覺生紀念圖書館"));
        adapter.addItem(new BeanContact("採編組","02-26215656","2364","覺生紀念圖書館"));
        adapter.addItem(new BeanContact("典藏閱覽組","02-26215656","2286","覺生紀念圖書館"));
        adapter.addItem(new BeanContact("典藏閱覽組","02-26215656","2312","覺生紀念圖書館"));
        adapter.addItem(new BeanContact("典藏閱覽組","02-26215656","2123","覺生紀念圖書館"));
        adapter.addItem(new BeanContact("典藏閱覽組","02-26215656","2483","覺生紀念圖書館"));
        adapter.addItem(new BeanContact("參考服務組","02-26215656","2321","覺生紀念圖書館"));
        adapter.addItem(new BeanContact("參考服務組","02-26215656","2651","覺生紀念圖書館"));
        adapter.addItem(new BeanContact("參考服務組","02-26215656","2652","覺生紀念圖書館"));
        adapter.addItem(new BeanContact("數位資訊組","02-26215656","2285","覺生紀念圖書館"));
        adapter.addItem(new BeanContact("數位資訊組","02-26215656","2486","覺生紀念圖書館"));
        adapter.addItem(new BeanContact("覺生紀念圖書館","02-26215656","2287","覺生紀念圖書館"));
        adapter.addItem(new BeanContact("覺生紀念圖書館","02-26215656","2282","覺生紀念圖書館"));
        adapter.addItem(new BeanContact("覺生紀念圖書館","02-26215656","2187","覺生紀念圖書館"));


        adapter.notifyDataSetChanged();
    }
    private void loadingOfficeofInformationServices(){
        AdapterRecyclerViewContact adapter = (AdapterRecyclerViewContact) recyclerView.getAdapter();

        adapter.clearAllItem();
        adapter.addItem(new BeanContact("專案發展組","02-26215656","2808","資訊處"));
        adapter.addItem(new BeanContact("專案發展組","02-26215656","2607","資訊處"));
        adapter.addItem(new BeanContact("專案發展組","02-26215656","2809","資訊處"));
        adapter.addItem(new BeanContact("教學支援組","02-26215656","2662","資訊處"));
        adapter.addItem(new BeanContact("教學支援組","02-26215656","8311","資訊處"));
        adapter.addItem(new BeanContact("校務資訊組","02-26215656","2585","資訊處"));
        adapter.addItem(new BeanContact("校務資訊組","02-26215656","2683","資訊處"));
        adapter.addItem(new BeanContact("遠距教學發展中心","02-26215656","2490","資訊處"));
        adapter.addItem(new BeanContact("遠距教學發展中心","02-26215656","2488","資訊處"));
        adapter.addItem(new BeanContact("遠距教學發展中心","02-26215656","2158","資訊處"));
        adapter.addItem(new BeanContact("遠距教學發展中心","02-26215656","2310","資訊處"));
        adapter.addItem(new BeanContact("網路管理組","02-26215656","2628","資訊處"));
        adapter.addItem(new BeanContact("網路管理組","02-26215656","2473","資訊處"));
        adapter.addItem(new BeanContact("數位設計組","02-26215656","2580","資訊處"));
        adapter.addItem(new BeanContact("數位設計組","02-26215656","2469","資訊處"));

        adapter.notifyDataSetChanged();
    }
    private void loadingOfficeofAlumniServicesandResourcesDevelopment(){
        AdapterRecyclerViewContact adapter = (AdapterRecyclerViewContact) recyclerView.getAdapter();

        adapter.clearAllItem();
        adapter.addItem(new BeanContact("校友服務暨資源發展處","02-26215656","8121","校友服務暨資源發展處"));
        adapter.addItem(new BeanContact("校友服務暨資源發展處","02-26215656","8122","校友服務暨資源發展處"));
        adapter.addItem(new BeanContact("校友服務暨資源發展處","02-26215656","8123","校友服務暨資源發展處"));


        adapter.notifyDataSetChanged();
    }
    private void loadingOICSA(){
        AdapterRecyclerViewContact adapter = (AdapterRecyclerViewContact) recyclerView.getAdapter();

        adapter.clearAllItem();
        adapter.addItem(new BeanContact("國際暨兩岸交流組","02-26215656","2002","國際暨兩岸事務處"));
        adapter.addItem(new BeanContact("國際暨兩岸交流組","02-26215656","2003","國際暨兩岸事務處"));
        adapter.addItem(new BeanContact("境外生輔導組","02-26215656","2218","國際暨兩岸事務處"));
        adapter.addItem(new BeanContact("境外生輔導組","02-26215656","2818","國際暨兩岸事務處"));
        adapter.addItem(new BeanContact("國際暨兩岸事務處","02-26209929","","國際暨兩岸事務處"));
        adapter.addItem(new BeanContact("國際暨兩岸事務處","02-26215656","2002","國際暨兩岸事務處"));
        adapter.addItem(new BeanContact("國際暨兩岸事務處","02-26215656","2325","國際暨兩岸事務處"));

        adapter.notifyDataSetChanged();
    }
    private void loadingOfficeofResearchandDevelopment(){
        AdapterRecyclerViewContact adapter = (AdapterRecyclerViewContact) recyclerView.getAdapter();

        adapter.clearAllItem();
        adapter.addItem(new BeanContact("研究暨產學組","02-26215656","2562","研究發展處"));
        adapter.addItem(new BeanContact("研究暨產學組","02-26215656","2120","研究發展處"));
        adapter.addItem(new BeanContact("研究暨產學組","02-26215656","2127","研究發展處"));
        adapter.addItem(new BeanContact("建邦中小企業創新育成中心","02-26215656","2666","研究發展處"));
        adapter.addItem(new BeanContact("建邦中小企業創新育成中心","02-26215656","2427","研究發展處"));
        adapter.addItem(new BeanContact("視障資源中心","02-26215656","2201","研究發展處"));
        adapter.addItem(new BeanContact("視障資源中心","02-26215656","2647","研究發展處"));
        adapter.addItem(new BeanContact("水資源管理與政策研究中心","02-26215656","2766","研究發展處"));
        adapter.addItem(new BeanContact("風工程研究中心","02-26215656","2049","研究發展處"));
        adapter.addItem(new BeanContact("數位語言研究中心","02-26215656","2905","研究發展處"));
        adapter.addItem(new BeanContact("數位語言研究中心","02-26215656","2913","研究發展處"));
        adapter.addItem(new BeanContact("工程法律研究發展中心","02-26215656","8701","研究發展處"));
        adapter.addItem(new BeanContact("工程法律研究發展中心","02-26215656","3219","研究發展處"));
        adapter.addItem(new BeanContact("智慧自動化與機器人中心","02-26215656","2730","研究發展處"));
        adapter.addItem(new BeanContact("運輸與物流研究中心","02-26215656","2597","研究發展處"));
        adapter.addItem(new BeanContact("村上春樹研究中心","02-26215656","3590","研究發展處"));
        adapter.addItem(new BeanContact("臨床醫學資訊系統發展與應用研究中心","02-26215656","3631","研究發展處"));
        adapter.addItem(new BeanContact("水資源資訊研究中心","02-26215656","3269","研究發展處"));
        adapter.addItem(new BeanContact("海洋輯水下科技研究中心","02-26215656","3646","研究發展處"));
        adapter.addItem(new BeanContact("研究發展處","02-26215656","2100","研究發展處"));


        adapter.notifyDataSetChanged();
    }
    private void loadingCollegeofLiberalArts(){
        AdapterRecyclerViewContact adapter = (AdapterRecyclerViewContact) recyclerView.getAdapter();

        adapter.clearAllItem();
        adapter.addItem(new BeanContact("中國文學學系","02-26215656","2330","文學院"));
        adapter.addItem(new BeanContact("中國文學學系","02-26215656","2331","文學院"));
        adapter.addItem(new BeanContact("歷史學系","02-26215656","2327","文學院"));
        adapter.addItem(new BeanContact("歷史學系","02-26215656","2328","文學院"));
        adapter.addItem(new BeanContact("資訊與圖書館學系","02-26215656","2814","文學院"));
        adapter.addItem(new BeanContact("資訊與圖書館學系","02-26215656","2335","文學院"));
        adapter.addItem(new BeanContact("資訊與圖書館學系","02-26215656","2382","文學院"));
        adapter.addItem(new BeanContact("大眾傳播學系","02-26215656","2305","文學院"));
        adapter.addItem(new BeanContact("大眾傳播學系","02-26215656","2556","文學院"));
        adapter.addItem(new BeanContact("資訊傳播學系","02-26215656","2266","文學院"));
        adapter.addItem(new BeanContact("資訊傳播學系","02-26215656","2267","文學院"));
        adapter.addItem(new BeanContact("漢學研究中心","02-26215656","2302","文學院"));
        adapter.addItem(new BeanContact("文學院","02-26215656","2300","文學院"));
        adapter.addItem(new BeanContact("文學院","02-26215656","2302","文學院"));


        adapter.notifyDataSetChanged();
    }
    private void loadingCollegeofScience(){
        AdapterRecyclerViewContact adapter = (AdapterRecyclerViewContact) recyclerView.getAdapter();

        adapter.clearAllItem();
        adapter.addItem(new BeanContact("數學學系","02-26215656","2501","理學院"));
        adapter.addItem(new BeanContact("數學學系","02-26215656","2502","理學院"));
        adapter.addItem(new BeanContact("物理學系","02-26215656","2521","理學院"));
        adapter.addItem(new BeanContact("物理學系","02-26215656","2578","理學院"));
        adapter.addItem(new BeanContact("化學學系","02-26215656","2531","理學院"));
        adapter.addItem(new BeanContact("化學學系","02-26215656","2533","理學院"));
        adapter.addItem(new BeanContact("尖端材料科學學士學位學程","02-26215656","3603","理學院"));
        adapter.addItem(new BeanContact("尖端材料科學學士學位學程","02-26215656","3604","理學院"));
        adapter.addItem(new BeanContact("生命開學開發中心","02-26215656","3038","理學院"));
        adapter.addItem(new BeanContact("科學教育中心","02-26215656","3165","理學院"));
        adapter.addItem(new BeanContact("X光科學研究中心","02-26215656","2521","理學院"));
        adapter.addItem(new BeanContact("理學院","02-26215656","2500","理學院"));
        adapter.addItem(new BeanContact("理學院","02-26215656","2534","理學院"));

        adapter.notifyDataSetChanged();
    }
    private void loadingCollegeofEngineer(){
        AdapterRecyclerViewContact adapter = (AdapterRecyclerViewContact) recyclerView.getAdapter();

        adapter.clearAllItem();
        adapter.addItem(new BeanContact("建築學系","02-26215656","2570","工學院"));
        adapter.addItem(new BeanContact("建築學系","02-26215656","2610","工學院"));
        adapter.addItem(new BeanContact("土木工程學系","02-26215656","2571","工學院"));
        adapter.addItem(new BeanContact("土木工程學系","02-26215656","2611","工學院"));
        adapter.addItem(new BeanContact("水資源及環境工程學系","02-26215656","2572","工學院"));
        adapter.addItem(new BeanContact("水資源及環境工程學系","02-26215656","2612","工學院"));
        adapter.addItem(new BeanContact("機械與機電工程學系","02-26215656","2573","工學院"));
        adapter.addItem(new BeanContact("機械與機電工程學系","02-26215656","2613","工學院"));
        adapter.addItem(new BeanContact("化學工程與材料工程學系","02-26215656","2574","工學院"));
        adapter.addItem(new BeanContact("化學工程與材料工程學系","02-26215656","2614","工學院"));
        adapter.addItem(new BeanContact("電機工程學系","02-26215656","2575","工學院"));
        adapter.addItem(new BeanContact("電機工程學系","02-26215656","2615","工學院"));
        adapter.addItem(new BeanContact("資訊工程學系","02-26215656","2576","工學院"));
        adapter.addItem(new BeanContact("資訊工程學系","02-26215656","2616","工學院"));
        adapter.addItem(new BeanContact("航空太空工程學系","02-26215656","2577","工學院"));
        adapter.addItem(new BeanContact("航空太空工程學系","02-26215656","2617","工學院"));
        adapter.addItem(new BeanContact("工學院機器人博士學位學程","02-26215656","2615","工學院"));
        adapter.addItem(new BeanContact("無人機應用研究中心","02-26215656","2577","工學院"));
        adapter.addItem(new BeanContact("無人機應用研究中心","02-26215656","2617","工學院"));
        adapter.addItem(new BeanContact("物聯網與大數據研究中心","02-26215656","3645","工學院"));
        adapter.addItem(new BeanContact("水處理科技研究中心","02-26215656","2602","工學院"));
        adapter.addItem(new BeanContact("工學院","02-26215656","2600","工學院"));
        adapter.addItem(new BeanContact("工學院","02-26215656","2602","工學院"));


        adapter.notifyDataSetChanged();
    }
    private void loadingCollegeofBusinessandManagement(){
        AdapterRecyclerViewContact adapter = (AdapterRecyclerViewContact) recyclerView.getAdapter();

        adapter.clearAllItem();
        adapter.addItem(new BeanContact("國際企業學系","02-26215656","2567","商管學院"));
        adapter.addItem(new BeanContact("國際企業學系","02-26215656","2569","商管學院"));
        adapter.addItem(new BeanContact("國際企業學系","02-26215656","3008","商管學院"));
        adapter.addItem(new BeanContact("財務金融學系","02-26215656","2591","商管學院"));
        adapter.addItem(new BeanContact("財務金融學系","02-26215656","2592","商管學院"));
        adapter.addItem(new BeanContact("財務金融學系","02-26215656","2047","商管學院"));
        adapter.addItem(new BeanContact("風險管理與保險學系","02-26215656","2563","商管學院"));
        adapter.addItem(new BeanContact("風險管理與保險學系","02-26215656","2564","商管學院"));
        adapter.addItem(new BeanContact("產業經濟學系","02-26215656","2566","商管學院"));
        adapter.addItem(new BeanContact("產業經濟學系","02-26215656","2596","商管學院"));
        adapter.addItem(new BeanContact("經濟學系","02-26215656","2565","商管學院"));
        adapter.addItem(new BeanContact("經濟學系","02-26215656","2595","商管學院"));
        adapter.addItem(new BeanContact("企業管理學系","02-26215656","2623","商管學院"));
        adapter.addItem(new BeanContact("企業管理學系","02-26215656","2678","商管學院"));
        adapter.addItem(new BeanContact("會計學系","02-26215656","2589","商管學院"));
        adapter.addItem(new BeanContact("會計學系","02-26215656","2594","商管學院"));
        adapter.addItem(new BeanContact("會計學系","02-26215656","2718","商管學院"));
        adapter.addItem(new BeanContact("統計學系","02-26215656","2046","商管學院"));
        adapter.addItem(new BeanContact("統計學系","02-26215656","2632","商管學院"));
        adapter.addItem(new BeanContact("統計學系","02-26215656","2677","商管學院"));
        adapter.addItem(new BeanContact("資訊管理學系","02-26215656","2645","商管學院"));
        adapter.addItem(new BeanContact("資訊管理學系","02-26215656","2648","商管學院"));
        adapter.addItem(new BeanContact("運輸管理學系","02-26215656","2597","商管學院"));
        adapter.addItem(new BeanContact("運輸管理學系","02-26215656","2598","商管學院"));
        adapter.addItem(new BeanContact("運輸管理學系","02-26215656","2833","商管學院"));
        adapter.addItem(new BeanContact("公共行政學系","02-26215656","2544","商管學院"));
        adapter.addItem(new BeanContact("公共行政學系","02-26215656","2554","商管學院"));
        adapter.addItem(new BeanContact("管理科學學系","02-26215656","2185","商管學院"));
        adapter.addItem(new BeanContact("管理科學學系","02-26215656","2186","商管學院"));
        adapter.addItem(new BeanContact("商管碩士在職專班","02-23942670","","商管學院"));
        adapter.addItem(new BeanContact("商管碩士在職專班","02-26215656","8511","商管學院"));
        adapter.addItem(new BeanContact("商管碩士在職專班","02-26215656","8572","商管學院"));
        adapter.addItem(new BeanContact("商館聯合AACSB認證辦公室","02-26215656","3084","商管學院"));
        adapter.addItem(new BeanContact("全球財務管理全英語學士學位學程","02-26215656","3338","商管學院"));
        adapter.addItem(new BeanContact("商管學院經營管理權英語碩士學位學程","02-26215656","2186","商管學院"));
        adapter.addItem(new BeanContact("淡江大學暨澳洲昆士蘭理工大學財金全英語雙碩士學位學程","02-26215656","2595","商管學院"));
        adapter.addItem(new BeanContact("商管學院大數據分析與商業智慧碩士學位學程","02-26215656","2046","商管學院"));
        adapter.addItem(new BeanContact("商管學院產業金融暨經營管理博士學位學程","02-26215656","2591","商管學院"));
        adapter.addItem(new BeanContact("商管學院數位商務與經濟碩士學位學程","02-26215656","2566","商管學院"));
        adapter.addItem(new BeanContact("兩岸金融研究中心","02-26215656","2971","商管學院"));
        adapter.addItem(new BeanContact("統計調查研究中心","02-23414678","","商管學院"));
        adapter.addItem(new BeanContact("資訊科技使用行為研究中心","02-26215656","2645","商管學院"));
        adapter.addItem(new BeanContact("資訊科技使用行為研究中心","02-26215656","2648","商管學院"));
        adapter.addItem(new BeanContact("商管學院","02-26215656","2590","商管學院"));
        adapter.addItem(new BeanContact("商管學院","02-26215656","3629","商管學院"));

        adapter.notifyDataSetChanged();
    }
    private void loadingCollegeofForeignLanguagesandLiteratures(){
        AdapterRecyclerViewContact adapter = (AdapterRecyclerViewContact) recyclerView.getAdapter();

        adapter.clearAllItem();
        adapter.addItem(new BeanContact("英文學系","02-26215656","2342","外國語文學院"));
        adapter.addItem(new BeanContact("英文學系","02-26215656","2343","外國語文學院"));
        adapter.addItem(new BeanContact("西班牙語文學系","02-26215656","2336","外國語文學院"));
        adapter.addItem(new BeanContact("西班牙語文學系","02-26215656","2337","外國語文學院"));
        adapter.addItem(new BeanContact("法國語文學系","02-26215656","2338","外國語文學院"));
        adapter.addItem(new BeanContact("法國語文學系","02-26215656","2339","外國語文學院"));
        adapter.addItem(new BeanContact("德國語文學系","02-26215656","2332","外國語文學院"));
        adapter.addItem(new BeanContact("德國語文學系","02-26215656","2333","外國語文學院"));
        adapter.addItem(new BeanContact("日本語文學系","02-26215656","2340","外國語文學院"));
        adapter.addItem(new BeanContact("俄國語文學系","02-26215656","2711","外國語文學院"));
        adapter.addItem(new BeanContact("外國語文學院","02-26215656","2551","外國語文學院"));
        adapter.addItem(new BeanContact("外國語文學院","02-26215656","2558","外國語文學院"));

        adapter.notifyDataSetChanged();
    }
    private void loadingCollegeofInternationalAffairs(){
        AdapterRecyclerViewContact adapter = (AdapterRecyclerViewContact) recyclerView.getAdapter();

        adapter.clearAllItem();
        adapter.addItem(new BeanContact("歐洲研究所","02-26215656","2702","國際事物學院"));
        adapter.addItem(new BeanContact("歐洲研究所","02-26215656","2703","國際事物學院"));
        adapter.addItem(new BeanContact("拉丁美洲研究所","02-26215656","2706","國際事物學院"));
        adapter.addItem(new BeanContact("拉丁美洲研究所","02-26215656","2707","國際事物學院"));
        adapter.addItem(new BeanContact("國際事務與戰略研究所","02-26215656","2175","國際事物學院"));
        adapter.addItem(new BeanContact("國際事務與戰略研究所","02-26215656","2176","國際事物學院"));
        adapter.addItem(new BeanContact("日本政經研究所","02-26215656","2708","國際事物學院"));
        adapter.addItem(new BeanContact("日本政經研究所","02-26215656","2709","國際事物學院"));
        adapter.addItem(new BeanContact("中國大陸研究所","02-26215656","2712","國際事物學院"));
        adapter.addItem(new BeanContact("中國大陸研究所","02-26215656","2173","國際事物學院"));
        adapter.addItem(new BeanContact("台灣與亞太研究全英語碩士學位學程","02-26215656","2706","國際事物學院"));
        adapter.addItem(new BeanContact("台灣與亞太研究全英語碩士學位學程","02-26215656","2707","國際事物學院"));
        adapter.addItem(new BeanContact("外交與國際關係學系全英語學士班","02-26215656","3000","國際事物學院"));
        adapter.addItem(new BeanContact("外交與國際關係學系全英語學士班","02-26215656","2700","國際事物學院"));
        adapter.addItem(new BeanContact("整合戰略科技中心","02-26215656","2710","國際事物學院"));
        adapter.addItem(new BeanContact("整合戰略科技中心","02-26215656","2176","國際事物學院"));
        adapter.addItem(new BeanContact("日本研究中心","02-26215656","2708","國際事物學院"));
        adapter.addItem(new BeanContact("歐洲聯盟研究中心","02-26215656","3077","國際事物學院"));
        adapter.addItem(new BeanContact("東協研究中心","02-26215656","3624","國際事物學院"));
        adapter.addItem(new BeanContact("兩岸金融研究中心","02-26215656","3291","國際事物學院"));
        adapter.addItem(new BeanContact("新南向與一帶一路研究中心","02-26215656","3069","國際事物學院"));
        adapter.addItem(new BeanContact("新南向與一帶一路研究中心","02-26215656","3291","國際事物學院"));
        adapter.addItem(new BeanContact("國際事務學院","02-26215656","2700","國際事物學院"));
        adapter.addItem(new BeanContact("國際事務學院","02-26215656","2701","國際事物學院"));


        adapter.notifyDataSetChanged();
    }
    private void loadingCollegeofEducation(){
        AdapterRecyclerViewContact adapter = (AdapterRecyclerViewContact) recyclerView.getAdapter();

        adapter.clearAllItem();
        adapter.addItem(new BeanContact("教育科技學系","02-26215656","2113","教育學院"));
        adapter.addItem(new BeanContact("教育科技學系","02-26215656","2535","教育學院"));
        adapter.addItem(new BeanContact("教育政策與領導研究所","02-26215656","2114","教育學院"));
        adapter.addItem(new BeanContact("教育政策與領導研究所","02-26215656","2115","教育學院"));
        adapter.addItem(new BeanContact("教育心理與諮商研究所","02-26215656","3002","教育學院"));
        adapter.addItem(new BeanContact("教育心理與諮商研究所","02-26215656","3003","教育學院"));
        adapter.addItem(new BeanContact("未來學研究所","02-26215656","2150","教育學院"));
        adapter.addItem(new BeanContact("未來學研究所","02-26215656","3001","教育學院"));
        adapter.addItem(new BeanContact("課程與教學研究所","02-26215656","3022","教育學院"));
        adapter.addItem(new BeanContact("課程與教學研究所","02-26215656","3032","教育學院"));
        adapter.addItem(new BeanContact("教育領導與科技管理博士班","02-26215656","2112","教育學院"));
        adapter.addItem(new BeanContact("師資培育中心","02-26215656","2122","教育學院"));
        adapter.addItem(new BeanContact("師資培育中心","02-26215656","2124","教育學院"));
        adapter.addItem(new BeanContact("策略遠見研究中心","02-26215656","3199","教育學院"));
        adapter.addItem(new BeanContact("策略遠見研究中心","02-26215656","3001","教育學院"));
        adapter.addItem(new BeanContact("教育學院","02-26215656","2111","教育學院"));
        adapter.addItem(new BeanContact("教育學院","02-26215656","2112","教育學院"));


        adapter.notifyDataSetChanged();
    }
    private void loadingCollegeofGlobalDevelopment(){
        AdapterRecyclerViewContact adapter = (AdapterRecyclerViewContact) recyclerView.getAdapter();

        adapter.clearAllItem();
        adapter.addItem(new BeanContact("資訊創新與科技學系","02-26215656","7039","全球發展學院"));
        adapter.addItem(new BeanContact("國際觀光管理學系全英語學士班","02-26215656","7128","全球發展學院"));
        adapter.addItem(new BeanContact("英美語言文化學系全英語學士班","02-26215656","7128","全球發展學院"));
        adapter.addItem(new BeanContact("全球政治經濟學系全英語學士班","02-26215656","7039","全球發展學院"));
        adapter.addItem(new BeanContact("全球發展學院","02-26215656","7020","全球發展學院"));
        adapter.addItem(new BeanContact("全球發展學院","02-26215656","7029","全球發展學院"));


        adapter.notifyDataSetChanged();
    }
    private void loadingInternetCampus(){
        AdapterRecyclerViewContact adapter = (AdapterRecyclerViewContact) recyclerView.getAdapter();

        adapter.clearAllItem();
        adapter.addItem(new BeanContact("拉丁美洲研究所亞太研究數位學習碩士在職專班","02-26215656","2706","網路校園"));
        adapter.addItem(new BeanContact("拉丁美洲研究所亞太研究數位學習碩士在職專班","02-26215656","2707","網路校園"));
        adapter.addItem(new BeanContact("教育科技學系數位學習碩士在職專班","02-26215656","2113","網路校園"));
        adapter.addItem(new BeanContact("數位出版與典藏數位學習碩士在職專班","02-26215656","2382","網路校園"));


        adapter.notifyDataSetChanged();
    }
    private void loadingOfficeofContinuingEducation(){
        AdapterRecyclerViewContact adapter = (AdapterRecyclerViewContact) recyclerView.getAdapter();

        adapter.clearAllItem();
        adapter.addItem(new BeanContact("進修中心","02-26215656","8329","推廣事務處"));
        adapter.addItem(new BeanContact("進修中心","02-26215656","8831","推廣事務處"));
        adapter.addItem(new BeanContact("推廣中心","02-26215656","8328","推廣事務處"));
        adapter.addItem(new BeanContact("推廣中心","02-26215656","8835","推廣事務處"));
        adapter.addItem(new BeanContact("日語中心","02-26215656","8855","推廣事務處"));
        adapter.addItem(new BeanContact("華語中心","02-26215656","8328","推廣事務處"));
        adapter.addItem(new BeanContact("華語中心","02-26215656","8835","推廣事務處"));
        adapter.addItem(new BeanContact("證照中心","02-26215656","8343","推廣事務處"));
        adapter.addItem(new BeanContact("證照中心","02-26215656","8867","推廣事務處"));
        adapter.addItem(new BeanContact("推廣教育處","02-26215656","8811","推廣事務處"));
        adapter.addItem(new BeanContact("推廣教育處","02-26215656","8822","推廣事務處"));
        adapter.addItem(new BeanContact("推廣教育處","02-26215656","8823","推廣事務處"));


        adapter.notifyDataSetChanged();
    }
    private void loadingOfficeofPhysicalEducation(){
        AdapterRecyclerViewContact adapter = (AdapterRecyclerViewContact) recyclerView.getAdapter();

        adapter.clearAllItem();
        adapter.addItem(new BeanContact("體育教學與活動組","02-26215656","2174","體育事務處"));
        adapter.addItem(new BeanContact("體育教學與活動組","02-26215656","2151","體育事務處"));
        adapter.addItem(new BeanContact("體育事務處","02-26215656","2172","體育事務處"));
        adapter.addItem(new BeanContact("體育事務處","02-26215656","2173","體育事務處"));


        adapter.notifyDataSetChanged();
    }
    private void loadingOfficeofMilitaryEducationandTraining(){
        AdapterRecyclerViewContact adapter = (AdapterRecyclerViewContact) recyclerView.getAdapter();

        adapter.clearAllItem();
        adapter.addItem(new BeanContact("行政組","02-26215656","2009","軍訓室"));
        adapter.addItem(new BeanContact("教學組","02-26215656","2255","軍訓室"));
        adapter.addItem(new BeanContact("服務組","02-26215656","2216","軍訓室"));
        adapter.addItem(new BeanContact("蘭陽校園組","02-26215656","7006","軍訓室"));
        adapter.addItem(new BeanContact("蘭陽校園組","02-26215656","7056","軍訓室"));
        adapter.addItem(new BeanContact("蘭陽校園組","02-26215656","7059","軍訓室"));
        adapter.addItem(new BeanContact("基北宜區資源中心","02-26215656","2255","軍訓室"));
        adapter.addItem(new BeanContact("軍訓處","02-26215656","2213","軍訓室"));

        adapter.notifyDataSetChanged();
    }
    private void loadingSaftyCampus(){
        AdapterRecyclerViewContact adapter = (AdapterRecyclerViewContact) recyclerView.getAdapter();

        adapter.clearAllItem();
        adapter.addItem(new BeanContact("勤務管制室","02-26215656","2110",""));
        adapter.addItem(new BeanContact("勤務管制室","02-26215656","2119",""));
        adapter.addItem(new BeanContact("本校值日教官室","02-26215656","2256",""));
        adapter.addItem(new BeanContact("本校值日教官室","02-26222173","",""));
        adapter.addItem(new BeanContact("諮輔組(淡水)","02-26215656","2221",""));
        adapter.addItem(new BeanContact("諮輔組(淡水)","02-26215656","2491",""));
        adapter.addItem(new BeanContact("諮輔組(蘭陽)","02-26215656","7015",""));
        adapter.addItem(new BeanContact("性別教育委員會","02-26215656","3056",""));
        adapter.addItem(new BeanContact("性侵害、性騷擾或性霸凌諮詢與申請調查專線","02-26232424","",""));
        adapter.addItem(new BeanContact("衛生保健組(淡水)","02-26222173","2373",""));
        adapter.addItem(new BeanContact("衛生保健組(淡水)","02-26222173","2372",""));
        adapter.addItem(new BeanContact("衛生保健組(台北)","02-23219482","",""));
        adapter.addItem(new BeanContact("馬偕醫院(淡水)","02-28094661","",""));
        adapter.addItem(new BeanContact("馬偕醫院(台北)","02-25433535","",""));
        adapter.addItem(new BeanContact("公祥醫院","02-25433535","",""));
        adapter.addItem(new BeanContact("警察局淡水分局","02-26212069","",""));
        adapter.addItem(new BeanContact("淡水警分局刑事組","02-26212443","",""));
        adapter.addItem(new BeanContact("淡水中正派出所","02-26212753","",""));
        adapter.addItem(new BeanContact("淡水中正派出所","02-26212752","",""));
        adapter.addItem(new BeanContact("淡水水碓派出所","02-26212491","",""));
        adapter.addItem(new BeanContact("竹圍派出所","02-28095050","",""));
        adapter.addItem(new BeanContact("淡水鎮調解委員會","02-26282616","",""));
        adapter.addItem(new BeanContact("淡水鎮調解委員會","02-26282619","",""));
        adapter.addItem(new BeanContact("教育部反霸凌專線","0800-200885","",""));
        adapter.addItem(new BeanContact("全過自殺防治專線","0800-788995","",""));
        adapter.addItem(new BeanContact("全國性騷擾防治電話","113","",""));
        adapter.addItem(new BeanContact("內政部反詐騙專線","165","",""));



        adapter.notifyDataSetChanged();
    }
}
