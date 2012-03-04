package com.md_5.autogroup.time;

import org.bukkit.configuration.file.FileConfiguration;

public class Config {

    public static boolean debug;
    public static int interval;
    public static String command;

    public static void load(AutoGroup instance) {
        FileConfiguration config = instance.getConfig();
        Config.debug = config.getBoolean("debug");
        Config.interval = config.getInt("interval");
        Config.command = config.getString("command");

        for (String s : config.getConfigurationSection("groups").getKeys(false)) {
            AutoGroup.groups.put(s, config.getInt("groups." + s));
        }
    }
}
