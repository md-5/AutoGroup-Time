package com.md_5.autogroup.time;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Logger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class AutoGroup extends JavaPlugin {

    public static Logger logger;
    public static Database database;
    public static HashMap<String, Integer> groups = new HashMap<String, Integer>();
    static FileConfiguration config;

    @Override
    public void onEnable() {
        logger = getLogger();
        if (!new File(getDataFolder(), "config.yml").exists()) {
            saveDefaultConfig();
        }
        Config.load(this);
        database = new Database();
        database.init();
        getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Ticker(), Config.interval * 20, Config.interval * 20);
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
    }

    public static int getTime() {
        return (int) (System.currentTimeMillis() / 1000L);
    }
}
