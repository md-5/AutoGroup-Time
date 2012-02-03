package com.md_5.autogroup.time;

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
}
