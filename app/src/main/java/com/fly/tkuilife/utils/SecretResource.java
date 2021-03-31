package com.fly.tkuilife.utils;

public class SecretResource {

    static {
        System.loadLibrary("SecretResource");
    }

    public native String getAPIKey_Weather();
    public native String getAPIKey_Transportation();
    public native String getAppKey_Teansportation();
    public native String getAES256Key_TKU();
    public native String getAES256Iv_TKU();
    public native String getURL_Curriculum();
    public native String getURL_Exam();
    public native String getURL_CourseChangeTemporary();
    public native String getURL_CourseChangePermanent();
    public native String getURL_Grade();
    public native String getURL_Transportation();
    public native String getURL_Weather();
    public native String getURL_TodayAnnounce();
    public native String getURL_RecentAnnounce();
    public native String getURL_Event();
    public native String getURL_Pc();
    public native String getURL_MonitorBusStop27();
    public native String getURL_MonitorBusStop28();
    public native String getURL_MonitorPlayground();
    public native String getURL_MonitorBasketballCourt();
    public native String getURL_MonitorTennisCourt();
    public native String getURL_MonitorFiveTiger();
    public native String getURL_MonitorElevator();
    public native String getURL_MonitorPostOffice();
    public native String getURL_MapBuilding();
    public native String getURL_MapFood();
    public native String getURL_MapBus();
    public native String getURL_CalendarFirstSemester();
    public native String getURL_CalendarSecondSemester();
    public native String getURL_EResourcesNews();
    public native String getURL_LibraryOpen();
    public native String getURL_NewsPaperLatest();
    public native String getURL_NewsPaperLatestImg();
    public native String getURL_NewsPaperNews();
    public native String getURL_NewsPaperGallery();
    public native String getURL_BusStationList();
    public native String getURL_BusLocation();


}
