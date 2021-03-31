package com.fly.tkuilife.bean;

public class BeanExam {
    private String examinee, course, required, credit, date, room, type, kind;
    private int session, seatnum, examnum, time;

    public BeanExam() {
    }

    public BeanExam(String examinee, String course, String required, String credit, String date, String room, String type, String kind, int time, int session, int seatnum, int examnum) {
        this.examinee = examinee;
        this.course = course;
        this.required = required;
        this.credit = credit;
        this.date = date;
        this.room = room;
        this.type = type;
        this.kind = kind;
        this.time = time;
        this.session = session;
        this.seatnum = seatnum;
        this.examnum = examnum;
    }

    public String getExaminee() {
        return examinee;
    }
    public void setExaminee(String examinee) {
        this.examinee = examinee.trim();
    }
    public String getCourse() {
        return course;
    }
    public void setCourse(String course) {
        this.course = course;
    }
    public String getRequired() {
        return required;
    }
    public void setRequired(String required) {
        this.required = required;
    }
    public String getCredit() {
        return credit;
    }
    public void setCredit(String credit) {
        this.credit = credit;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getRoom() {
        return room;
    }
    public void setRoom(String room) {
        this.room = room.replaceAll(" ","");
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getKind() {
        return kind;
    }
    public void setKind(String kind) {
        this.kind = kind;
    }
    public String getSession() {
        return String.valueOf(session);
    }
    public void setSession(String session) {
        this.session = Integer.valueOf(session);
    }
    public String getTime() {
        return String.valueOf(time);
    }
    public void setTime(String time) {
        this.time = Integer.valueOf(time);
    }
    public String getSeatnum() {
        return String.valueOf(seatnum);
    }
    public void setSeatnum(String seatnum) {
        this.seatnum = Integer.valueOf(seatnum);
    }
    public String getExamnum() {
        return String.valueOf(examnum);
    }
    public void setExamnum(String examnum) {
        if(!examnum.equals("")) this.examnum = Integer.valueOf(examnum);
        else this.examnum = -1;
    }
}
