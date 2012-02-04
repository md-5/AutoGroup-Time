package com.md_5.autogroup.time;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Promote {

    public static void checkPromote(String player) {
        int groupTime = 0;
        String groupName = "";

        if (AutoGroup.groupTimes.size() == 1) {
            groupName = AutoGroup.groupNames.get(0);
            groupTime = AutoGroup.groupTimes.get(0);
        } else {
            for (int i = 0; i < AutoGroup.groupTimes.size(); i++) {
                if (AutoGroup.groupTimes.get(i) <= AutoGroup.playerTimes.get(player).getTime()) {
                    groupName = AutoGroup.groupNames.get(i);
                    groupTime = AutoGroup.groupTimes.get(i);
                }
            }
        }
        if (groupTime != 0 && !groupName.isEmpty() && !groupName.equals(AutoGroup.playerTimes.get(player).getStatus())) {
            promotePlayer(player, groupName);
        }

        AutoGroup.database.update(player);
    }

    public static void promotePlayer(String player, String group) {
        if (Bukkit.getServer().getPlayer(player).hasPermission("autogroup.norank")) {
            return;
        }
        AutoGroup.logger.info(String.format("AutoGroup: Trying to promote %1$s to group " + group, player));
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            p.sendMessage(ChatColor.LIGHT_PURPLE + "AutoGroup: Please welcome " + player + " to group " + group);
        }
        AutoGroup.playerTimes.get(player).setStatus(group);
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), String.format(Config.command, player, group));
    }
}