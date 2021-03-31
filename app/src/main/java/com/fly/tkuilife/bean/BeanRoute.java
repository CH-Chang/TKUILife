package com.fly.tkuilife.bean;

public class BeanRoute {
    private String route, time, city, station;
    private int icon, direction;

    public BeanRoute(String route, String station, String time, String city, int icon, int direction) {
        this.route = route;
        this.station = station;
        this.time = time;
        this.city = city;
        this.icon = icon;
        this.direction = direction;
    }

    public String getRoute() {
        return route;
    }
    public void setRoute(String route) {
        this.route = route;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public int getIcon() {
        return icon;
    }
    public void setIcon(int icon) {
        this.icon = icon;
    }
    public int getDirection() {
        return direction;
    }
    public void setDirection(int direction) {
        this.direction = direction;
    }
    public String getStation() {
        return station;
    }
    public void setStation(String station) {
        this.station = station;
    }
}
