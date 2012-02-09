package com.md_5.autogroup.time;

public class Api {

    private final Database engine;
    private final String connectionString;
    private final String driver;
    private final String createStatement;

    public Api() {
        if (Config.connectionType.equalsIgnoreCase("mysql")) {
            connectionString = "jdbc:mysql://" + Config.url + ":" + 3306 + "/" + Config.dbName + "?user=" + Config.userName + "&password=" + Config.password;
            driver = "org.mysql";
            createStatement = "";
        } else {
            connectionString = "create table if not exists AutoGroup (`name` text NOT NULL, `time` int(10) unsigned NOT NULL DEFAULT '0', `date` "
                    + "int(10) unsigned NOT NULL DEFAULT '0', `last` int(10) unsigned NOT NULL DEFAULT '0', `status` text, PRIMARY KEY (`name`(20)))";
            driver = "org.sqlite";
            createStatement = "jdbc:sqlite:plugins/AutoGroup/users.db";
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

    public Map load(final String player) {
        try {
            return engine.load(player);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void add(final String player) {
        try {
            engine.add(player);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void update(final String player, final int playTime, final String status) {
        try {
            engine.update(player, playTime, status);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
