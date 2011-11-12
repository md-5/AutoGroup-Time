package com.md_5.autogroup;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Ticker implements Runnable {

    public void run() {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            String name = player.getName();
            if (!AutoGroup.playerTimes.containsKey(name)) {
                AutoGroup.playerTimes.put(name, new Map(0, (int) (System.currentTimeMillis() / 1000L), 0));
                Database.add(name);
            }
            AutoGroup.playerTimes.get(name).time += Config.interval;
            AutoGroup.playerTimes.get(name).last = (int) (System.currentTimeMillis() / 1000L);
            Promote.checkPromote(name);
        }
    }
}
