package com.md_5.autogroup.time;

import java.util.Collections;
import org.bukkit.configuration.file.FileConfiguration;

public class Config {

    public static boolean debug = true;
    public static int interval = 60;
    public static String connectionType = "sqlite";
    public static String url = "127.0.0.1";
    public static String dbName = "AutoGroup";
    public static String userName = "root";
    public static String password = "AutoGroup";
    public static String table = "donators";
    public static String table2 = "players";
    public static String command = "permissions player setgroup %1$s %2$s";
    public static String promotionType = "seconds";

    static {
        FileConfiguration config = AutoGroup.instance.getConfig();
        if (!config.contains("groups")) {
            config.options().copyDefaults(true);
        } else {
            config.options().copyDefaults(false);
        }
        AutoGroup.instance.saveConfig();
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
            AutoGroup.groupTimes.add(config.getInt("groups." + s));
            AutoGroup.groupNames.add(s);
        }
        Collections.sort(AutoGroup.groupTimes);
        for (String s : config.getConfigurationSection("groups").getKeys(false)) {
            AutoGroup.groupNames.set(AutoGroup.groupTimes.indexOf(config.getInt("groups." + s)), s);
        }
    }
}
