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
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:plugins/AutoGroup/users.db");
            Statement stat = conn.createStatement();
            ResultSet rs = stat.executeQuery("select * from players WHERE name='" + player + "'");
            while (rs.next()) {
                Map map = new Map(rs.getInt("time"), rs.getInt("date"), rs.getInt("last"));
                map.status = rs.getString("status");
                AutoGroup.playerTimes.put(rs.getString("name"), map);
            }
            stat.close();
            rs.close();
            conn.close();
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
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:plugins/AutoGroup/users.db");
            Statement stat = conn.createStatement();
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                String name = player.getName();
                AutoGroup.playerTimes.put(name, new Map(AutoGroup.playerTimes.get(name).time,
                        AutoGroup.playerTimes.get(name).date, (int) (System.currentTimeMillis() / 1000L)));
                Database.update(name);
            }
            for (String player : AutoGroup.playerTimes.keySet()) {
                update(player);
            }
            stat.close();
            conn.close();
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
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:plugins/AutoGroup/users.db");
            Statement stat = conn.createStatement();
            stat.executeUpdate("insert into players values ('" + player + "',0," + AutoGroup.playerTimes.get(player).date + ", 0, '')");
            stat.close();
            conn.close();
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
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:plugins/AutoGroup/users.db");
            Statement stat = conn.createStatement();
            stat.executeUpdate("update players set time=" + AutoGroup.playerTimes.get(player).time
                    + ", last=" + AutoGroup.playerTimes.get(player).last + ", status='"
                    + AutoGroup.playerTimes.get(player).status
                    + "' WHERE name='" + player + "'");
            stat.close();
            conn.close();
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
            Bukkit.getServer().getPluginManager().getPlugin("AutoGroup").getDataFolder().mkdirs();
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:plugins/AutoGroup/users.db");
            Statement stat = conn.createStatement();
            stat.executeUpdate("create table if not exists players (name text, time numeric, date numeric, last numeric, status text)");
            stat.close();
            conn.close();
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                load(player.getName());
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
}
