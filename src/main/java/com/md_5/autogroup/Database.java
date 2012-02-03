package com.md_5.autogroup;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class Database {

    public static boolean load(String player) {
        try {
        	if (Config.connectionType.equalsIgnoreCase("sqlite")){
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:plugins/AutoGroup/users.db");
            Statement stat = conn.createStatement();
            ResultSet rs = stat.executeQuery("select * from AutoGroup WHERE name='" + player + "'");
            while (rs.next()) {
                Map map = new Map(rs.getInt("time"), rs.getInt("date"), rs.getInt("last"));
                map.setStatus(rs.getString("status"));
                AutoGroup.playerTimes.put(rs.getString("name"), map);
            }
            stat.close();
            rs.close();
            conn.close();
        	}
        	else if(Config.connectionType.equalsIgnoreCase("mysql")){
        		Class.forName("com.mysql.jdbc.Driver");
                Connection conn = DriverManager.getConnection("jdbc:mysql://" + Config.url + "/" + Config.dbName, Config.userName, Config.password);
                Statement stat = conn.createStatement();
                ResultSet rs = stat.executeQuery("select * from AutoGroup WHERE name='" + player + "'");
                while (rs.next()) {
                    Map map = new Map(rs.getInt("time"), rs.getInt("date"), rs.getInt("last"));
                    map.setStatus(rs.getString("status"));
                    AutoGroup.playerTimes.put(rs.getString("name"), map);
        	}
        	}
        } catch (ClassNotFoundException e) {
            Errors.classNotFound();
        } catch (SQLException e) {
            if (Config.debug) {
                e.printStackTrace();
           } else {
                Errors.SQLException();
           }
        }
        return true;
    }

    public static boolean save() {
        try {
        	if (Config.connectionType.equalsIgnoreCase("sqlite")){
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:plugins/AutoGroup/users.db");
            Statement stat = conn.createStatement();
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                String name = player.getName();
                AutoGroup.playerTimes.get(name).setLast((int) (System.currentTimeMillis() / 1000L));
                Database.update(name);
            }
            for (String player : AutoGroup.playerTimes.keySet()) {
                update(player);
            }
            stat.close();
            conn.close();
        	}
        	else if(Config.connectionType.equalsIgnoreCase("mysql")){
        		Class.forName("com.mysql.jdbc.Driver");
                Connection conn = DriverManager.getConnection("jdbc:mysql://" + Config.url + "/" + Config.dbName, Config.userName, Config.password);
                Statement stat = conn.createStatement();
                for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                    String name = player.getName();
                    AutoGroup.playerTimes.get(name).setLast((int) (System.currentTimeMillis() / 1000L));
                    Database.update(name);
                }
                for (String player : AutoGroup.playerTimes.keySet()) {
                    update(player);
                }
                stat.close();
                conn.close();
        	}
        } catch (ClassNotFoundException e) {
            Errors.classNotFound();
        } catch (SQLException e) {
            if (Config.debug) {
                e.printStackTrace();
            } else {
                Errors.SQLException();
            }
        }
        return true;
    }

    public static boolean add(String player) {
        try {
        	if (Config.connectionType.equalsIgnoreCase("sqlite")){
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:plugins/AutoGroup/users.db");
            Statement stat = conn.createStatement();
            stat.executeUpdate("insert into AutoGroup values ('" + player + "',0," + AutoGroup.playerTimes.get(player).getDate() + ", 0, '')");
            stat.close();
            conn.close();
        	}
        	else if(Config.connectionType.equalsIgnoreCase("mysql")){
        		Class.forName("com.mysql.jdbc.Driver");
                Connection conn = DriverManager.getConnection("jdbc:mysql://" + Config.url + "/" + Config.dbName, Config.userName, Config.password);
                Statement stat = conn.createStatement();
                stat.executeUpdate("insert into AutoGroup values ('" + player + "',0," + AutoGroup.playerTimes.get(player).getDate() + ", 0, '')");
                stat.close();
                conn.close();
        	}
        } catch (ClassNotFoundException e) {
            Errors.classNotFound();
        } catch (SQLException e) {
            if (Config.debug) {
                e.printStackTrace();
            } else {
                Errors.SQLException();
            }
        }
        return true;
    }

    public static boolean update(String player) {
        try {
        	if (Config.connectionType.equalsIgnoreCase("sqlite")){
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:plugins/AutoGroup/users.db");
            Statement stat = conn.createStatement();
            stat.executeUpdate("update AutoGroup set time=" + AutoGroup.playerTimes.get(player).getTime()
                    + ", last=" + AutoGroup.playerTimes.get(player).getLast() + ", status='"
                    + AutoGroup.playerTimes.get(player).getStatus()
                    + "' WHERE name='" + player + "'");
            stat.close();
            conn.close();
        	}
        	else if(Config.connectionType.equalsIgnoreCase("mysql")){
        		Class.forName("com.mysql.jdbc.Driver");
                Connection conn = DriverManager.getConnection("jdbc:mysql://" + Config.url + "/" + Config.dbName, Config.userName, Config.password);
                Statement stat = conn.createStatement();
                stat.executeUpdate("update AutoGroup set time=" + AutoGroup.playerTimes.get(player).getTime()
                        + ", last=" + AutoGroup.playerTimes.get(player).getLast() + ", status='"
                        + AutoGroup.playerTimes.get(player).getStatus()
                        + "' WHERE name='" + player + "'");
                stat.close();
                conn.close();
        	}
        } catch (ClassNotFoundException e) {
            Errors.classNotFound();
        } catch (SQLException e) {
            if (Config.debug) {
                e.printStackTrace();
            } else {
                Errors.SQLException();
            }
        }
        return true;
    }

    public static boolean init() {
        try {
        	if (Config.connectionType.equalsIgnoreCase("sqlite")){
            Bukkit.getServer().getPluginManager().getPlugin("AutoGroup").getDataFolder().mkdirs();
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:plugins/AutoGroup/users.db");
            Statement stat = conn.createStatement();
            stat.executeUpdate("create table if not exists AutoGroup (`name` text NOT NULL, `time` int(10) unsigned NOT NULL DEFAULT '0', `date` "
            		+ "int(10) unsigned NOT NULL DEFAULT '0', `last` int(10) unsigned NOT NULL DEFAULT '0', `status` text, PRIMARY KEY (`name`(20)))");
            stat.close();
            conn.close();
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                load(player.getName());
            }
        	}
        	else if(Config.connectionType.equalsIgnoreCase("mysql")){
        		Class.forName("com.mysql.jdbc.Driver");
                Connection conn = DriverManager.getConnection("jdbc:mysql://" + Config.url + "/" + Config.dbName, Config.userName, Config.password);
                Statement stat = conn.createStatement();
                stat.executeUpdate("create table if not exists AutoGroup (`name` text NOT NULL, `time` int(10) unsigned NOT NULL DEFAULT '0', `date` "
                		+ "int(10) unsigned NOT NULL DEFAULT '0', `last` int(10) unsigned NOT NULL DEFAULT '0', `status` text, PRIMARY KEY (`name`(20)))");
                stat.close();
                conn.close();
                for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                    load(player.getName());
                }
        	}
        	else
        		AutoGroup.logger.info("AutoGroup: Invalid connection type specified in config.");
        } catch (ClassNotFoundException e) {
            Errors.classNotFound();
        } catch (SQLException e) {
            if (Config.debug) {
                e.printStackTrace();
            } else {
                Errors.SQLException();
            }
        }
        return true;
    }
}
