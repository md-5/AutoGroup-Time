package com.md_5.autogroup;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Ticker implements Runnable {

    public void run() {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            String name = player.getName();
            if (!AutoGroup.playerTimes.containsKey(name)) {
                AutoGroup.playerTimes.put(name, new Map(0, System.currentTimeMillis(), 0));
                Database.add(name);
            }
            AutoGroup.playerTimes.get(name).setTime(AutoGroup.playerTimes.get(name).getTime() + Config.interval);
            AutoGroup.playerTimes.get(name).setLast(System.currentTimeMillis());
            Promote.checkPromote(name);
        }
    }
}
