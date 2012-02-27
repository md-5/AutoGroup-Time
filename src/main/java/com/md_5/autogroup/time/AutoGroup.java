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

    public static int getTime() {
        return (int) (System.currentTimeMillis() / 1000L);
    }
}
