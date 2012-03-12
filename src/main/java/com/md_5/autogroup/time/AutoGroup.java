package com.md_5.autogroup.time;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Logger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class AutoGroup extends JavaPlugin {

    public static Logger logger;
    public static Database database;
    public static HashMap<String, Integer> groups = new HashMap<String, Integer>();
    public static int interval;
    public static String command;

    @Override
    public void onEnable() {
        logger = getLogger();
        if (!new File(getDataFolder(), "config.yml").exists()) {
            saveDefaultConfig();
        }
        FileConfiguration conf = getConfig();
        conf.options().copyDefaults(true);
        saveConfig();
        interval = conf.getInt("interval");
        command = conf.getString("command");
        for (String s : conf.getConfigurationSection("groups").getKeys(false)) {
            AutoGroup.groups.put(s, conf.getInt("groups." + s));
        }
        database = new Database().init();
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Ticker(), interval * 20, interval * 20);
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
    }

    public static int getTime() {
        return (int) (System.currentTimeMillis() / 1000L);
    }
}
