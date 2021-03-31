package com.fly.tkuilife.bean;

public class BeanTimetableContrast {
    private String session, time;

    public BeanTimetableContrast(String session, String time) {
        this.session = session;
        this.time = time;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
