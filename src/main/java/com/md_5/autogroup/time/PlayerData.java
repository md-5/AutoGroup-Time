package com.md_5.autogroup.time;

import org.bukkit.Bukkit;

public class PlayerData
{
	private int playTime;
	private int firstJoin;
	private int lastJoin;
	public String status;
	private String name;

	public PlayerData(String name)
	{
		this.name = name;
	}

	public int getPlayTime() {
		return this.playTime;
	}

	public void setPlayTime(int playTime) {
		this.playTime = playTime;
	}

	public int getFirstJoin() {
		return this.firstJoin;
	}

	public void setFirstJoin(int firstJoin) {
		this.firstJoin = firstJoin;
	}

	public int getLastJoin() {
		return this.lastJoin;
	}

	public void setLastJoin(int lastJoin) {
		this.lastJoin = lastJoin;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
		AutoGroup.logger.info(String.format("Trying to promote %1$s to group " + status, new Object[] { this.name }));
		Bukkit.getServer().broadcastMessage(org.bukkit.ChatColor.GOLD + this.name + " has reached " + status + " status!");
		Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), String.format(AutoGroup.command, new Object[] { this.name, status }));
	}

	public String getName() {
		return this.name;
	}
} 