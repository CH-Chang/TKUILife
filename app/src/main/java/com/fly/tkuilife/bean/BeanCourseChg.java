package com.fly.tkuilife.bean;

public class BeanCourseChg {
    private int kind;
    private String course, teacher, department, before, after, start, id;

    public BeanCourseChg(int kind, String course, String teacher, String department, String before, String after, String start, String id) {
        this.kind = kind;
        this.course = course;
        this.teacher = teacher;
        this.department = department;
        this.before = before;
        this.after = after;
        this.start = start;
        this.id = id;
    }

    public int getKind() {
        return kind;
    }

    public void setKind(int kind) {
        this.kind = kind;
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

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getBefore() {
        return before;
    }

    public void setBefore(String before) {
        this.before = before;
    }

    public String getAfter() {
        return after;
    }

    public void setAfter(String after) {
        this.after = after;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
