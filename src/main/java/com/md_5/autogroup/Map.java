package com.md_5.autogroup;

public class Map {

    private int time; //time in seconds they have been on server
    private int date; //first time they joined
    private int last; //last time they were on
    private String status = "";//what group do they have (used to determine promotion eligibility

    public Map(int time, int date, int last) {
        this.time = time;
        this.date = date;
        this.last = last;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = (int) date;
    }

    public int getLast() {
        return last;
    }

    public void setLast(long last) {
        this.last = (int) last;
    }

  public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
