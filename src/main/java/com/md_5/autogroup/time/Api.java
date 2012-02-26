package com.md_5.autogroup.time;

public class Api {

    private final Database engine;
    private final String connectionString;
    private final String driver;
    private final String createStatement;

    public Api() {
        if (Config.connectionType.equalsIgnoreCase("mysql")) {
            connectionString = "jdbc:mysql://" + Config.url + ":" + 3306 + "/" + Config.dbName + "?user=" + Config.userName + "&password=" + Config.password;
            driver = "com.mysql.jdbc.Driver";
            createStatement = "";
        } else {
            connectionString = "jdbc:sqlite:plugins/AutoGroup/users.db";
            driver = "org.sqlite.JDBC";
            createStatement = "create table if not exists AutoGroup (`name` text NOT NULL, `time` int(10) unsigned NOT NULL DEFAULT '0', `date` "
                    + "int(10) unsigned NOT NULL DEFAULT '0', `last` int(10) unsigned NOT NULL DEFAULT '0', `status` text, PRIMARY KEY (`name`(20)))";
        }
        engine = new Database(connectionString, driver, createStatement);
    }

    public void init() {
        try {
            engine.init();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public PlayerData load(String player) {
        try {
            return engine.load(player);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void update(PlayerData player) {
        try {
            engine.update(player);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
