package com.md_5.autogroup.time;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.joda.time.DateTime;

public class Ticker implements Runnable {

    public void run() {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            PlayerData p = AutoGroup.database.load(player.getName());
            if (Config.promotionType.equalsIgnoreCase("seconds")) {
                AutoGroup.playerTimes.get(name).setTime(AutoGroup.playerTimes.get(name).getTime() + Config.interval);
            } else if (Config.promotionType.equalsIgnoreCase("days")) {
                DateTime now = new DateTime(System.currentTimeMillis());
                DateTime cal = new DateTime(AutoGroup.playerTimes.get(name).getLast() * 1000L);
                if (cal.dayOfYear() != now.dayOfYear() || cal.year() != now.year()) {
                    AutoGroup.playerTimes.get(name).setTime(AutoGroup.playerTimes.get(name).getTime() + 1);
                }
            }
            AutoGroup.playerTimes.get(name).setLast((int) (System.currentTimeMillis() / 1000L));
            Promote.checkPromote(name);
        }
    }
}
