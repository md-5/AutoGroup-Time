package com.md_5.autogroup;

import java.util.Calendar;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.md_5.autogroup.events.PlayerListener;
import com.md_5.autogroup.events.WorldListener;

public class AutoGroup extends JavaPlugin{
	
	public static final Logger logger = Bukkit.getServer().getLogger();
    public static HashMap<String, Map> playerTimes = new HashMap<String, Map>();
    public static HashMap<String, Object> groupConfig = new HashMap<String, Object>();
    static FileConfiguration config;

    public void onEnable() {

        PluginManager pm = this.getServer().getPluginManager();

        WorldListener worldListener = new WorldListener(this);
        worldListener.registerEvents(pm);

        PlayerListener playerListener = new PlayerListener(this);
        playerListener.registerEvents(pm);

        config = getConfig();
        config.options().copyDefaults(true);
        saveConfig();
        Config.debug = config.getBoolean("debug", Config.debug);
        Config.interval = config.getInt("interval", Config.interval);
        Config.connectionType = config.getString("connectionType", Config.connectionType);
        Config.url = config.getString("mysqlURL", Config.url);
        Config.dbName = config.getString("dbName", Config.dbName);
        Config.userName = config.getString("username", Config.userName);
        Config.password = config.getString("password", Config.password);
        Config.command = config.getString("command", Config.command);

        groupConfig.putAll(config.getConfigurationSection("groups").getValues(false));
        
        Database.init();

        if (!groupConfig.containsValue(0)) {
            getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Ticker(), Config.interval * 20, Config.interval * 20);
        }
        else 
        	logger.info("AutoGroup: A group with no time has been detected. No promotions shall be made.");
        logger.info(String.format("AutoGroup v%1$s by md_5 enabled", this.getDescription().getVersion()));
    }

    public void onDisable() {

        Database.save();

        getServer().getScheduler().cancelTasks(this);

        logger.info(String.format("Autogroup v%1$s by md_5 disabled", this.getDescription().getVersion()));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            return onPlayerCommand((Player) sender, command, label, args);
        } else {
            return onConsoleCommand(sender, command, label, args);
        }
    }

    public boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("playtime") && !groupConfig.isEmpty()) {
        	int groupTime=0;
            String status = "";
            switch (args.length) {
                case 0:
                    if (!playerTimes.containsKey(player.getName())) {
                        player.sendMessage("That player does not exist!");
                        return true;
                    }
                    player.sendMessage(ChatColor.GOLD + "You joined the server on " + ChatColor.DARK_GREEN
                    		+ formattedDate(playerTimes.get(player.getName()).getDate()) + ChatColor.GOLD);
                    player.sendMessage(ChatColor.GOLD + "which was " + ChatColor.DARK_GREEN
                            + elapsedDate((playerTimes.get(player.getName()).getLast() - playerTimes.get(player.getName()).getDate()))
                            + ChatColor.GOLD + " ago");;
                    

                    for (String s : groupConfig.keySet()){
                    	groupTime=Integer.parseInt(groupConfig.get(s).toString());
                    	for (String t : groupConfig.keySet()){
                    		if (playerTimes.get(player.getName()).getTime() <= Integer.parseInt(groupConfig.get(t).toString())
                    				&& groupTime >= Integer.parseInt(groupConfig.get(t).toString())){
                    			groupTime=Integer.parseInt(groupConfig.get(t).toString());
                    			status = t;
                    		}
                    	}
                   
                    player.sendMessage(ChatColor.DARK_GREEN + formattedSeconds(groupTime - playerTimes.get(player.getName()).getTime()) 
                    		+ ChatColor.GOLD + " before you reach the rank of " + ChatColor.RED + status);
                    player.sendMessage(ChatColor.GOLD + "You have played for " + ChatColor.DARK_GREEN
                    		+ formattedSeconds(playerTimes.get(player.getName()).getTime()) + ChatColor.GOLD + " in total");
                    break;
                    }
                case 1:
                    if (player.hasPermission("autogroup.playtime.others") && args.length > 0) {
                        if (!playerTimes.containsKey(args[0])) {
                            player.sendMessage("That player does not exist!");
                            return true;
                        }
                        player.sendMessage(ChatColor.GOLD + args[0] + " joined the server on "+ ChatColor.DARK_GREEN
                        		+ formattedDate(playerTimes.get(args[0]).getDate()) + ChatColor.GOLD); 
                        		player.sendMessage(ChatColor.GOLD + "which was " + ChatColor.DARK_GREEN
                                + elapsedDate((playerTimes.get(args[0]).getLast() - playerTimes.get(args[0]).getDate())) 
                                + ChatColor.GOLD + " ago");
                       
                        for (String s : groupConfig.keySet()){
                        	groupTime=Integer.parseInt(groupConfig.get(s).toString());
                        	for (String t : groupConfig.keySet()){
                        		if (playerTimes.get(args[0]).getTime() <= Integer.parseInt(groupConfig.get(t).toString())
                        				&& groupTime >= Integer.parseInt(groupConfig.get(t).toString())){
                        			groupTime=Integer.parseInt(groupConfig.get(t).toString());
                        			status = t;
                        		}
                        	}
                        }

                        	player.sendMessage(ChatColor.GOLD + args[0] + " must play "+ ChatColor.DARK_GREEN + formattedSeconds(groupTime - playerTimes.get(args[0]).getTime())
                        			+ ChatColor.GOLD + " before they reach the rank of " + ChatColor.RED + status);
                        player.sendMessage(ChatColor.GOLD + args[0] + " has played for " + ChatColor.DARK_GREEN
                                + formattedSeconds(playerTimes.get(player.getName()).getTime()) + ChatColor.GOLD + " in total");
                    } else if (!player.hasPermission("autogroup.playtime.others")) {
                        player.sendMessage("You can only view your own time. Run this command without arguments");
                    }
                    break;
            }
            return true;
        }
        return true;
    }

    public boolean onConsoleCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(String.format("AutoGroup v%1$s by md_5", this.getDescription().getVersion()));
        sender.sendMessage("AutoGroup: No other console functionality is available at this time");
        return true;
    }
    
public String formattedSeconds(int seconds){
    	
    	
    	int year;
    	int month;
    	int day;
    	int hour;
    	int minute;
    	
		if (seconds >= 31556926){
    		year= (int) (seconds/31556926);
    		seconds = (int) (seconds % 31556926);
    	}
    	else 
    		year=0;
		if (seconds >= 2629743.83){
    		month = (int) (seconds / 2629743.83);
    		seconds = (int) (seconds % 2629743.83);
    	}
    	else 
    		month=0;
		if (seconds >= 86400){
    		day=(int) (seconds / 86400);
    		seconds = (int) (seconds % 86400);
    	}
    	else
    		day=0;
		if (seconds >= 3600){
    		hour=(int) (seconds / 3600);
    		seconds = (int) (seconds % 3600);
    	}
    	else
    		hour=0;
		if (seconds >= 60){
    		minute=(int) (seconds/60);
    		seconds = (int) (seconds % 60);
    	}
    	else minute=0;
		String formatted= seconds + "s";
		if (minute>0){
			formatted= minute + "m " + formatted;
			if(hour > 0){
				formatted = hour + "h " + formatted;
				if (day > 0){
					formatted = day + "d " + formatted;
					if (month > 0){
						formatted = month + "m " + formatted;
						if (year > 0){
							formatted = year + "y " + formatted;
						}
					}
				}
			}
		}
    	return formatted;
    	
    }

	public String formattedDate(long seconds){
		String months[] = {
				"Jan", "Feb", "Mar", "Apr",
				"May", "Jun", "Jul", "Aug",
				"Sep", "Oct", "Nov", "Dec"}; 
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(seconds);
		return months[cal.get(Calendar.MONTH)] + " " + cal.get(Calendar.DATE) + ", " + cal.get(Calendar.YEAR) + ", "
			+ cal.get(Calendar.HOUR_OF_DAY)	+ ":" + cal.get(Calendar.MINUTE);
	}
	 public String elapsedDate(long seconds){
		 	Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(seconds);
			String formatted= cal.get(Calendar.SECOND) + "s";
			if (cal.get(Calendar.MINUTE)>0){
				formatted= cal.get(Calendar.MINUTE) + "m " + formatted;
				if(cal.get(Calendar.HOUR_OF_DAY) > 0){
					formatted = cal.get(Calendar.HOUR_OF_DAY) + "h " + formatted;
					if (cal.get(Calendar.DATE) > 0){
						formatted = cal.get(Calendar.DATE) + "d " + formatted;
						if (cal.get(Calendar.MONTH) + 1 > 0 ){
							formatted = cal.get(Calendar.MONTH) + 1 + "m " + formatted;
							if (cal.get(Calendar.YEAR) > 0){
								formatted = cal.get(Calendar.YEAR) + "y " + formatted;
							}
						}
					}
				}
			}
	    	return formatted;
	 }
	}
