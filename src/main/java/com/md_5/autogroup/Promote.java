package com.md_5.autogroup;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Promote {
	

    public static void checkPromote(String player) {
        int groupTime=0;
        String status = "";

        for (String s : AutoGroup.groupConfig.keySet()){
        	groupTime=Integer.parseInt(AutoGroup.groupConfig.get(s).toString());
        	for (String t : AutoGroup.groupConfig.keySet()){
        		if (AutoGroup.playerTimes.get(player).getTime() >= Integer.parseInt(AutoGroup.groupConfig.get(t).toString())
        				&& groupTime <= Integer.parseInt(AutoGroup.groupConfig.get(t).toString())){
        			groupTime=Integer.parseInt(AutoGroup.groupConfig.get(t).toString());
        			status = t;
        		}
        	}
        
        }
        if (groupTime!=0 && status != "" && status != AutoGroup.playerTimes.get(player).getStatus())
        	promotePlayer(player, status);
        
        Database.update(player);
        return;

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
        return;
    }
}
//AutoGroup.logger.info(String.format("Player %1$s has been playing for %2$d seconds", player, AutoGroup.playerTimes.get(player).time));
//AutoGroup.logger.info(String.format("Player %1$s has been loyal", player));