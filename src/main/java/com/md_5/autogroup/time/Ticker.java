package com.md_5.autogroup.time;

import java.util.Map;
import org.bukkit.entity.Player;

public class Ticker implements Runnable
{
	public void run()
	{
		for (Player player : org.bukkit.Bukkit.getServer().getOnlinePlayers()) {
			PlayerData p = AutoGroup.database.load(player.getName());
			p.setPlayTime(p.getPlayTime() + AutoGroup.interval);
			p.setLastJoin(AutoGroup.getTime());

			int groupTime = 0;
			String groupName = new String();

			for (Map.Entry<String, Integer> group : AutoGroup.groups.entrySet()) {
				if ((((Integer)group.getValue()).intValue() <= p.getPlayTime()) && (((Integer)group.getValue()).intValue() > groupTime)) {
					groupName = (String)group.getKey();
					groupTime = ((Integer)group.getValue()).intValue();
				}
			}

			if ((groupTime != 0) && (!groupName.equals(p.getStatus())) && (!player.hasPermission("autogroup.norank"))) {
				p.setStatus(groupName);
			}
			AutoGroup.database.update(p);
		}
	}
} 