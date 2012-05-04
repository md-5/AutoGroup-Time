package com.md_5.autogroup.time;

import java.io.File;
import java.util.HashMap;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class AutoGroup extends JavaPlugin{
	public static java.util.logging.Logger logger;
	public static Database database;
	public static HashMap<String, Integer> groups = new HashMap<String, Integer>();
	public static int interval;
	public static String command;
	public int time;

	public void onEnable(){
		logger = getLogger();
		if (!new File(getDataFolder(), "config.yml").exists()) {
			saveDefaultConfig();
		}
		FileConfiguration conf = getConfig();
		interval = conf.getInt("interval");
		command = conf.getString("command");
		for (String s : conf.getConfigurationSection("groups").getKeys(false)) {
			groups.put(s, Integer.valueOf(conf.getInt("groups." + s)));
		}
		database = new Database().init();
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Ticker(), interval * 20, interval * 20);
	}

	public void onDisable(){
		getServer().getScheduler().cancelTasks(this);
	}

	public static int getTime() {
		return (int)(System.currentTimeMillis() / 1000L);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String string, String[] args){

		if ((cmd.getName().equalsIgnoreCase("playtime")) && (args.length < 1)){
			
			if (!(sender instanceof Player)){
				sender.sendMessage("This command can only be used in-game.");
				return true;
			}

			Player player = (Player)sender;
			PlayerData pd = database.load(player.getName());

			int secs = pd.getPlayTime();

			int hours = secs / 3600;
			int remainder = secs % 3600;
			int minutes = remainder / 60;
			int seconds = remainder % 60;

			String disHour = (hours < 10 ? "0" : "") + hours;
			String disMinu = (minutes < 10 ? "0" : "") + minutes;
			String disSec = (seconds < 10 ? "0" : "") + seconds;

			player.sendMessage(org.bukkit.ChatColor.GOLD + "You have been playing for " + disHour + ":" + disMinu + " hour(s)");

			return true;
		}

		if ((cmd.getName().equalsIgnoreCase("playtime")) && (args.length >= 1)){
			
//			if(!(sender.hasPermission("autogroup.playtime.others"))) {
//				sender.sendMessage("You cannot check the playtime of others");
//				return true;
//			}
			
			Player player = getServer().getPlayer(args[0]);
			PlayerData pd = database.load(player.getName());

			int secs = pd.getPlayTime();

			int hours = secs / 3600;
			int remainder = secs % 3600;
			int minutes = remainder / 60;
			int seconds = remainder % 60;

			String disHour = (hours < 10 ? "0" : "") + hours;
			String disMinu = (minutes < 10 ? "0" : "") + minutes;
			String disSec = (seconds < 10 ? "0" : "") + seconds;

			sender.sendMessage(org.bukkit.ChatColor.GOLD + player.getName() + " has been playing for " + disHour + ":" + disMinu + " hour(s)");

			return true;
		}

		return true;
	}
} 