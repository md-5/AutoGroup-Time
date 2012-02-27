package com.md_5.autogroup.time;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class PlayerData {

    private int playTime; // time in seconds they have been on server
    private int firstJoin; // first time they joined
    private int lastJoin; // last time they were on
    private String status; // what group do they have (used to determine promotion eligibility
    private String name; // This players name

    public PlayerData(String name) {
        this.name = name;
    }

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
        AutoGroup.logger.info(String.format("Trying to promote %1$s to group " + status, name));
        Bukkit.getServer().broadcastMessage(ChatColor.LIGHT_PURPLE + "AutoGroup: Please welcome " + name + " to group " + status);
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), String.format(Config.command, name, status));
    }

    public String getName() {
        return name;
    }
}
