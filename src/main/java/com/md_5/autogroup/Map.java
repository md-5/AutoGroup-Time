package com.md_5.autogroup;

public class Map {

    private int time; //time in seconds they have been on server
    private long date; //first time they joined
    private long last; //last time they were on
    private String status = "";//what group do they have (not used right now

    public Map(int time, long date, int last) {
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

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getLast() {
        return last;
    }

    public void setLast(long last) {
        this.last = last;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
