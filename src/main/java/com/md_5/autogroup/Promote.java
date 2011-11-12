package com.md_5.autogroup;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Promote {

    public static void checkPromote(String player) {
        if ((AutoGroup.playerTimes.get(player).last - AutoGroup.playerTimes.get(player).date) >= Config.loyalty
                && Config.loyalty != 0) {
            if (!AutoGroup.playerTimes.get(player).status.equalsIgnoreCase(Config.loyalGroup)) {
                promotePlayer(player, Config.loyalGroup);
            }
        }
        if ((AutoGroup.playerTimes.get(player).time >= Config.addict1Time)
                && AutoGroup.playerTimes.get(player).time < Config.addict2Time && Config.addict1Time != 0) {
            if (!AutoGroup.playerTimes.get(player).status.equalsIgnoreCase(Config.addictGroup1)) {
                promotePlayer(player, Config.addictGroup1);
            }
        }
        if ((AutoGroup.playerTimes.get(player).time >= Config.addict2Time)
                && AutoGroup.playerTimes.get(player).time < Config.addict3Time && Config.addict2Time != 0) {
            if (!AutoGroup.playerTimes.get(player).status.equalsIgnoreCase(Config.addictGroup2)) {
                promotePlayer(player, Config.addictGroup2);
            }
        }
        if ((AutoGroup.playerTimes.get(player).time >= Config.addict3Time)
                && Config.addict3Time != 0) {
            if (!AutoGroup.playerTimes.get(player).status.equalsIgnoreCase(Config.addictGroup3)) {
                promotePlayer(player, Config.addictGroup3);
            }
        }
        Database.update(player);
        return;

    }

    public static void promotePlayer(String player, String group) {
        AutoGroup.logger.info(String.format("AutoGroup: Trying to promote %1$s to group " + group, player));
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            p.sendMessage(ChatColor.LIGHT_PURPLE + "AutoGroup: Please welcome " + player + " to group " + group);
        }
        AutoGroup.playerTimes.get(player).status = group;
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), String.format(Config.command, player, group));
        return;
    }
}
//AutoGroup.logger.info(String.format("Player %1$s has been playing for %2$d seconds", player, AutoGroup.playerTimes.get(player).time));
//AutoGroup.logger.info(String.format("Player %1$s has been loyal", player));