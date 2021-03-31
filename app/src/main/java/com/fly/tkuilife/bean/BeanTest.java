package com.fly.tkuilife.bean;

import com.tickaroo.tikxml.annotation.PropertyElement;
import com.tickaroo.tikxml.annotation.Xml;

@Xml(name = "cal1")
public class BeanTest {

    @PropertyElement(name = "週次")
    String week;
    @PropertyElement(name = "日期")
    String day;
    @PropertyElement(name = "星期")
    String weeken;
    @PropertyElement(name = "事項")
    String title;

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getWeeken() {
        return weeken;
    }

    public void setWeeken(String weeken) {
        this.weeken = weeken;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
