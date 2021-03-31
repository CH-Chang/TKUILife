package com.fly.tkuilife.bean;

public class BeanCourse {
    private int session, seatnum;
    private String course, teacher, room;

    public BeanCourse(int session, int seatnum, String course, String teacher, String room) {
        this.session = session;
        this.seatnum = seatnum;
        this.course = course;
        this.teacher = teacher;
        this.room = room;
    }

    public int getSession() {
        return session;
    }

    public void setSession(int session) {
        this.session = session;
    }

    public int getSeatnum() {
        return seatnum;
    }

    public void setSeatnum(int seatnum) {
        this.seatnum = seatnum;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }
}
