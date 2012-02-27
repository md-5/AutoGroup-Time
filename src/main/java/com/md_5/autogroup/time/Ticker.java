package com.md_5.autogroup.time;

import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Ticker implements Runnable {

    public void run() {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            PlayerData p = AutoGroup.database.load(player.getName());
            p.setPlayTime(p.getPlayTime() + Config.interval);
            p.setLastJoin(AutoGroup.getTime());

            int groupTime = 0;
            String groupName = new String();

            for (Map.Entry<String, Integer> group : AutoGroup.groups.entrySet()) {
                if (group.getValue() <= p.getPlayTime()) {
                    groupName = group.getKey();
                    groupTime = group.getValue();
                    break;
                }
            }

            if (groupTime != 0 && !groupName.isEmpty() && !groupName.equals(p.getStatus()) && !Bukkit.getServer().getPlayer(p.name).hasPermission("autogroup.norank")) {
                p.setStatus(groupName);
            }
            AutoGroup.database.update(p);
        }
    }
}
