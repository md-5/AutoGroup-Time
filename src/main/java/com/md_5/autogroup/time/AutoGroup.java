package com.md_5.autogroup.time;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class AutoGroup extends JavaPlugin implements Listener {

    public static AutoGroup instance;
    public static Logger logger;
    public static Api database;
    public static HashMap<String, Map> playerTimes = new HashMap<String, Map>();
    public static ArrayList<String> groupNames = new ArrayList<String>(3);
    public static ArrayList<Integer> groupTimes = new ArrayList<Integer>(3);
    static FileConfiguration config;

    @Override
    public void onEnable() {
        getDataFolder().mkdirs();
        instance = this;
        logger = getLogger();
        getServer().getPluginManager().registerEvents(this, this);
        config = getConfig();
        if (!config.contains("groups")) {
            config.options().copyDefaults(true);
        } else {
            config.options().copyDefaults(false);
        }
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

        for (String s : config.getConfigurationSection("groups").getKeys(false)) {
            groupTimes.add(config.getInt("groups." + s));
            groupNames.add(s);
        }
        Collections.sort(groupTimes);
        for (String s : config.getConfigurationSection("groups").getKeys(false)) {
            groupNames.set(groupTimes.indexOf(config.getInt("groups." + s)), s);
        }

        new Api();
        database.init();

        if (!groupTimes.contains(0)) {
            getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Ticker(), Config.interval * 20, Config.interval * 20);
        } else {
            logger.info("A group with no time has been detected. No promotions shall be made.");
        }
    }

    @Override
    public void onDisable() {
        database.save();
        getServer().getScheduler().cancelTasks(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("playtime") && !groupTimes.isEmpty()) {
            int groupTime = 0;
            String groupName = "";
            switch (args.length) {
                case 0:
                    if (!playerTimes.containsKey(sender.getName())) {
                        sender.sendMessage("That player does not exist!");
                        return true;
                    }
                    sender.sendMessage(ChatColor.GOLD + "You joined the server on " + ChatColor.DARK_GREEN
                            + formattedDate(playerTimes.get(sender.getName()).getDate()) + ChatColor.GOLD);
                    sender.sendMessage(ChatColor.GOLD + "which was " + ChatColor.DARK_GREEN
                            + elapsedTime(playerTimes.get(sender.getName()).getDate(), ((int) (System.currentTimeMillis() / 1000L)))
                            + ChatColor.GOLD + " ago");

                    if (groupTimes.size() == 1) {
                        groupName = groupNames.get(0);
                        groupTime = groupTimes.get(0);
                    } else {
                        for (int i = groupTimes.size() - 1; i > -1; i--) {
                            if (groupTimes.get(i) >= playerTimes.get(sender.getName()).getTime()) {
                                groupName = groupNames.get(i);
                                groupTime = groupTimes.get(i);
                            }
                        }
                    }


                    if (Config.promotionType.equalsIgnoreCase("seconds")) {
                        if (groupName.isEmpty()) {
                            sender.sendMessage(ChatColor.GOLD + "You have already achieved the highest rank.");
                        } else {
                            sender.sendMessage(ChatColor.DARK_GREEN + elapsedTime(playerTimes.get(sender.getName()).getTime(), groupTime)
                                    + ChatColor.GOLD + " before you reach the rank of " + ChatColor.RED + groupName);
                        }
                        sender.sendMessage(ChatColor.GOLD + "You have played for " + ChatColor.DARK_GREEN
                                + elapsedTime(0, playerTimes.get(sender.getName()).getTime()) + ChatColor.GOLD + " in total");
                    } else if (Config.promotionType.equalsIgnoreCase("days")) {
                        sender.sendMessage(ChatColor.DARK_GREEN + "" + (groupTime - playerTimes.get(sender.getName()).getTime())
                                + ChatColor.GOLD + " day(s) before you reach the rank of " + ChatColor.RED + groupName);
                        sender.sendMessage(ChatColor.GOLD + "You have logged into this server " + ChatColor.DARK_GREEN
                                + playerTimes.get(sender.getName()).getTime() + ChatColor.GOLD + " day(s)");
                    }
                    break;

                case 1:
                    if (sender.hasPermission("autogroup.playtime.others")) {
                        if (!playerTimes.containsKey(args[0])) {
                            sender.sendMessage("That player does not exist!");
                            return true;
                        }
                        sender.sendMessage(ChatColor.RED + args[0] + ChatColor.GOLD + " joined the server on " + ChatColor.DARK_GREEN
                                + formattedDate(playerTimes.get(args[0]).getDate()) + ChatColor.GOLD);
                        sender.sendMessage(ChatColor.GOLD + "which was " + ChatColor.DARK_GREEN
                                + elapsedTime(playerTimes.get(args[0]).getDate(), ((int) (System.currentTimeMillis() / 1000L)))
                                + ChatColor.GOLD + " ago");

                        if (groupTimes.size() == 1) {
                            groupName = groupNames.get(0);
                            groupTime = groupTimes.get(0);
                        } else {
                            for (int i = groupTimes.size() - 1; i > -1; i--) {
                                if (groupTimes.get(i) >= playerTimes.get(args[0]).getTime()) {
                                    groupName = groupNames.get(i);
                                    groupTime = groupTimes.get(i);
                                }
                            }
                        }

                        if (Config.promotionType.equalsIgnoreCase("seconds")) {
                            if (groupName.isEmpty()) {
                                sender.sendMessage(ChatColor.GOLD + args[0] + " has already achieved the highest rank.");
                            } else {
                                sender.sendMessage(ChatColor.RED + args[0] + ChatColor.GOLD + " must play " + ChatColor.DARK_GREEN + elapsedTime(playerTimes.get(args[0]).getTime(), groupTime));
                            }
                            sender.sendMessage(ChatColor.GOLD + "before they reach the rank of " + ChatColor.RED + groupName);
                            sender.sendMessage(ChatColor.GOLD + args[0] + " has played for " + ChatColor.DARK_GREEN
                                    + elapsedTime(0, playerTimes.get(sender.getName()).getTime()) + ChatColor.GOLD + " in total");
                        } else if (Config.promotionType.equalsIgnoreCase("days")) {
                            if (groupName.isEmpty()) {
                                sender.sendMessage(ChatColor.GOLD + args[0] + " has already achieved the highest rank.");
                            } else {
                                sender.sendMessage(ChatColor.DARK_GREEN + "" + (groupTime - playerTimes.get(sender.getName()).getTime())
                                        + ChatColor.GOLD + " day(s) before " + args[0] + " reach the rank of " + ChatColor.RED + groupName);
                            }
                            sender.sendMessage(ChatColor.GOLD + args[0] + " has logged into this server " + ChatColor.DARK_GREEN
                                    + playerTimes.get(sender.getName()).getTime() + ChatColor.GOLD + " day(s)");
                        }
                    } else if (!sender.hasPermission("autogroup.playtime.others")) {
                        sender.sendMessage("You have no permission to see other people's playtime.");
                    }
                    break;

                case 2:
                    if (sender.hasPermission("autogroup.playtime.settime")) {
                        if (!playerTimes.containsKey(args[0])) {
                            sender.sendMessage("That player does not exist, or is not online!");
                            return true;
                        }
                        try {
                            playerTimes.get(args[0]).setTime(Integer.parseInt(args[1]));
                            sender.sendMessage(ChatColor.RED + args[0] + ChatColor.GOLD + "'s time has been set to " + " " + elapsedTime(0, Integer.parseInt(args[1])));
                        } catch (NumberFormatException ex) {
                            sender.sendMessage("That is not a valid number");
                        }
                    } else if (!sender.hasPermission("autogroup.playtime.settime")) {
                        sender.sendMessage("You have no permission to set other people's playtime.");
                    }
                    break;
            }
            return true;
        }
        return true;
    }

    public String formattedDate(int seconds) {
        DateTime cal = new DateTime(seconds * 1000L);
        DateTimeFormatter fmt = DateTimeFormat.forPattern("MMM dd, yyyy, hh:mm");
        return cal.toString(fmt);
    }

    public String elapsedTime(int before, int after) {
        Period period = new Period(before * 1000L, after * 1000L);

        String formatted = period.getSeconds() + "s";

        if (period.getMinutes() > 0 || period.getHours() > 0 || (period.getDays() + period.getWeeks() * 7) > 0 || period.getMonths() > 0 || period.getYears() > 0) {
            formatted = period.getMinutes() + "m " + formatted;
            if (period.getHours() > 0 || period.getDays() + period.getWeeks() * 7 > 0 || period.getMonths() > 0 || period.getYears() > 0) {
                formatted = period.getHours() + "h " + formatted;
                if ((period.getDays() + period.getWeeks() * 7) > 0 || period.getMonths() > 0 || period.getYears() > 0) {
                    formatted = (period.getDays() + (period.getWeeks() * 7)) + "d " + formatted;
                    if (period.getMonths() > 0 || period.getYears() > 0) {
                        formatted = period.getMonths() + "m " + formatted;
                        if (period.getYears() > 0) {
                            formatted = period.getYears() + "y " + formatted;
                        }
                    }
                }
            }
        }

        return formatted;

    }

    @EventHandler
    public void onWorldSave(WorldSaveEvent event) {
        database.save();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String name = event.getPlayer().getName();
        if (AutoGroup.playerTimes.containsKey(name)) {
            AutoGroup.playerTimes.get(name).setLast((int) (System.currentTimeMillis() / 1000L));
            database.update(name);
            AutoGroup.playerTimes.remove(name);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        database.load(event.getPlayer().getName());
    }
}
