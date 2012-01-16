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
        if (!config.contains("groups"))
        	config.options().copyDefaults(true);
        else
        	config.options().copyDefaults(false);
        saveConfig();
        Config.debug = config.getBoolean("debug", Config.debug);
        Config.interval = config.getInt("interval", Config.interval);
        Config.connectionType = config.getString("connectionType", Config.connectionType);
        Config.url = config.getString("mysqlURL", Config.url);
        Config.dbName = config.getString("dbName", Config.dbName);
        Config.userName = config.getString("username", Config.userName);
        Config.password = config.getString("password", Config.password);
        Config.command = config.getString("command", Config.command);
        Config.promotionType = config.getString("promotionType", Config.promotionType);

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
                            + elapsedTime(playerTimes.get(player.getName()).getDate() , ((int) (System.currentTimeMillis() / 1000L)))
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
                    if (Config.promotionType.equalsIgnoreCase("seconds")){
                    	if (status == ""){
                    		player.sendMessage(ChatColor.GOLD  + "You have already achieved the highest rank.");
                    	}else
                    		player.sendMessage(ChatColor.DARK_GREEN + elapsedTime(playerTimes.get(player.getName()).getTime() , groupTime) 
                    				+ ChatColor.GOLD + " before you reach the rank of " + ChatColor.RED + status);
                    	player.sendMessage(ChatColor.GOLD + "You have played for " + ChatColor.DARK_GREEN
                    			+ elapsedTime(0 , playerTimes.get(player.getName()).getTime()) + ChatColor.GOLD + " in total");
                    }else if(Config.promotionType.equalsIgnoreCase("days")){
                    	player.sendMessage(ChatColor.DARK_GREEN + "" + (groupTime - playerTimes.get(player.getName()).getTime())
                    			+ ChatColor.GOLD + " day(s) before you reach the rank of " + ChatColor.RED + status);
                    	player.sendMessage(ChatColor.GOLD + "You have logged into this server " + ChatColor.DARK_GREEN
                    			+ playerTimes.get(player.getName()).getTime() + ChatColor.GOLD + " day(s)");
                    }
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
                                + elapsedTime(playerTimes.get(args[0]).getDate() , ((int) (System.currentTimeMillis() / 1000L))) 
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
                        if (Config.promotionType.equalsIgnoreCase("seconds")){
                        	if (status == ""){
                        		player.sendMessage(ChatColor.GOLD + args[0] + " has already achieved the highest rank.");
                        	}else
                        		player.sendMessage(ChatColor.GOLD + args[0] + " must play "+ ChatColor.DARK_GREEN + elapsedTime(playerTimes.get(args[0]).getTime() , groupTime)
                        				+ ChatColor.GOLD + " before they reach the rank of " + ChatColor.RED + status);
                        	player.sendMessage(ChatColor.GOLD + args[0] + " has played for " + ChatColor.DARK_GREEN
                        			+ elapsedTime(0 , playerTimes.get(player.getName()).getTime()) + ChatColor.GOLD + " in total");
                        }
                        else if(Config.promotionType.equalsIgnoreCase("days")){
                        	if (status == null){
                        		player.sendMessage(ChatColor.GOLD + args[0] + " has already achieved the highest rank.");
                        	}else
                        		player.sendMessage(ChatColor.DARK_GREEN + "" + (groupTime - playerTimes.get(player.getName()).getTime())
                        				+ ChatColor.GOLD + " day(s) before " + args[0] +  " reach the rank of " + ChatColor.RED + status);
                        	player.sendMessage(ChatColor.GOLD + args[0] + " has logged into this server " + ChatColor.DARK_GREEN
                        			+ playerTimes.get(player.getName()).getTime() + ChatColor.GOLD + " day(s)");
                        }
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
    

	public String formattedDate(int seconds){
		String months[] = {
				"Jan", "Feb", "Mar", "Apr",
				"May", "Jun", "Jul", "Aug",
				"Sep", "Oct", "Nov", "Dec"}; 
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(seconds * 1000L);
		return months[cal.get(Calendar.MONTH)] + " " + cal.get(Calendar.DATE) + ", " + cal.get(Calendar.YEAR) + ", "
			+ cal.get(Calendar.HOUR_OF_DAY)	+ ":" + cal.get(Calendar.MINUTE);
	}
	 public String elapsedTime(int before, int after){
		 	Calendar beforeCal = Calendar.getInstance();
			beforeCal.setTimeInMillis(before * 1000L);
			Calendar afterCal = Calendar.getInstance();
			afterCal.setTimeInMillis(after * 1000L);
			
			int year = Math.abs(afterCal.get(Calendar.YEAR) - beforeCal.get(Calendar.YEAR));
	    	int month = Math.abs(afterCal.get(Calendar.MONTH) - beforeCal.get(Calendar.MONTH));
	    	int date = Math.abs(afterCal.get(Calendar.DATE) - beforeCal.get(Calendar.DATE));
	    	int hour = Math.abs(afterCal.get(Calendar.HOUR_OF_DAY) - beforeCal.get(Calendar.HOUR_OF_DAY));
	    	int minute = Math.abs(afterCal.get(Calendar.MINUTE) - beforeCal.get(Calendar.MINUTE));
	    	
			String formatted= Math.abs(afterCal.get(Calendar.SECOND) - beforeCal.get(Calendar.SECOND)) + "s";
			
			if (minute > 0 || hour > 0 || date > 0 || month > 0 || year > 0){
				formatted= minute + "m " + formatted;
				if(hour > 0 || date > 0 || month > 0 || year > 0){
					formatted = hour + "h " + formatted;
					if (date > 0 || month > 0 || year > 0){
						formatted = date + "d " + formatted;
						if (month > 0 || year > 0){
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
	

	}
