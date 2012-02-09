package com.md_5.autogroup.time;

public class Map {

    private int playTime; // time in seconds they have been on server
    private int firstJoin; // first time they joined
    private int lastJoin; // last time they were on
    private String status; // what group do they have (used to determine promotion eligibility

    public int getPlayTime() {
        return playTime;
    }

    public void setPlayTime(int playTime) {
        this.playTime = playTime;
    }

    public int getFirstJoin() {
        return firstJoin;
    }

    public void setFirstJoin(int firstJoin) {
        this.firstJoin = firstJoin;
    }

    public int getLastJoin() {
        return lastJoin;
    }

    public void setLastJoin(int lastJoin) {
        this.lastJoin = lastJoin;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
