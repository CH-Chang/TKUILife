package com.fly.tkuilife.bean;

import java.util.ArrayList;

public class BeanStation {
    private String stationname, estimatetime;
    private ArrayList<String> busitem;

    public BeanStation(String stationname, String estimatetime) {
        this.stationname = stationname;
        this.estimatetime = estimatetime;
        this.busitem = new ArrayList<>();
    }

    public String getStationname() {
        return stationname;
    }

    public void setStationname(String stationname) {
        this.stationname = stationname;
    }

    public String getEstimatetime() {
        return estimatetime;
    }

    public void setEstimatetime(String estimatetime) {
        this.estimatetime = estimatetime;
    }

    public ArrayList<String> getBusitem() {
        return busitem;
    }

    public void setBusitem(ArrayList<String> busitem) {
        this.busitem = busitem;
    }

    public void clearBusitem(){
        this.busitem.clear();
    }
}
