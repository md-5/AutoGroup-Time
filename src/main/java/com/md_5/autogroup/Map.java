package com.md_5.autogroup;

public class Map {

    private int time;
    private int date;
    private int last;
    private String status = "";

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

    public void setDate(int date) {
        this.date = date;
    }

    public int getLast() {
        return last;
    }

    public void setLast(int last) {
        this.last = last;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
