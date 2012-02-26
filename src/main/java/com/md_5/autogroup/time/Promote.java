package com.md_5.autogroup.time;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Promote {

    public static void checkPromote(String player) {
        int groupTime = 0;
        String groupName = new String();
        PlayerData info = AutoGroup.database.load(player);
        for (int i = 0; i < AutoGroup.groupTimes.size(); i++) {
            if (AutoGroup.groupTimes.get(i) <= info.getPlayTime()) {
                groupName = AutoGroup.groupNames.get(i);
                groupTime = AutoGroup.groupTimes.get(i);
            }
        }
        if (groupTime != 0 && !groupName.isEmpty() && !groupName.equals(info.getStatus()) && !Bukkit.getServer().getPlayer(player).hasPermission("autogroup.norank")) {
            promotePlayer(info, groupName);
        }
    }

    public static void promotePlayer(PlayerData player, String group) {
        AutoGroup.logger.info(String.format("Trying to promote %1$s to group " + group, player.name));
        Bukkit.getServer().broadcastMessage(ChatColor.LIGHT_PURPLE + "AutoGroup: Please welcome " + player.name + " to group " + group);
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), String.format(Config.command, player.name, group));
        player.setStatus(group);
        AutoGroup.database.update(player);
    }
}
