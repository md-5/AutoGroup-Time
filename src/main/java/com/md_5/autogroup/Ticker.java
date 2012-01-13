package com.md_5.autogroup;

import java.util.Calendar;

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
            if (Config.promotionType.equalsIgnoreCase("seconds")){
            	AutoGroup.playerTimes.get(name).setTime(AutoGroup.playerTimes.get(name).getTime() + Config.interval);
            } else if (Config.promotionType.equalsIgnoreCase("days")){
            	Calendar now=Calendar.getInstance();
            	now.setTimeInMillis(System.currentTimeMillis());
            	Calendar cal=Calendar.getInstance();
            	cal.setTimeInMillis(AutoGroup.playerTimes.get(name).getLast() * 1000L);
            	if (cal.get(Calendar.DAY_OF_YEAR) != now.get(Calendar.DAY_OF_YEAR) ||
            			cal.get(Calendar.YEAR) != now.get(Calendar.YEAR)){
            		AutoGroup.playerTimes.get(name).setTime(AutoGroup.playerTimes.get(name).getTime() + 1);
            	}
            }
            AutoGroup.playerTimes.get(name).setLast((int) (System.currentTimeMillis() / 1000L));
            Promote.checkPromote(name);
        }
    }
}
