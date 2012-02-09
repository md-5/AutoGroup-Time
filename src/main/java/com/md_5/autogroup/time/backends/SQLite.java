package com.md_5.autogroup.time.backends;

import com.md_5.autogroup.time.AutoGroup;
import com.md_5.autogroup.time.Map;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SQLite implements Database {

    private String connectionString = "jdbc:sqlite:plugins/AutoGroup/users.db";

    public String getConnectionString() {
        return connectionString;
    }

    public void init() throws Exception {
        Connection conn = DriverManager.getConnection(connectionString);
        Statement stat = conn.createStatement();
        stat.executeUpdate("create table if not exists AutoGroup (`name` text NOT NULL, `time` int(10) unsigned NOT NULL DEFAULT '0', `date` "
                + "int(10) unsigned NOT NULL DEFAULT '0', `last` int(10) unsigned NOT NULL DEFAULT '0', `status` text, PRIMARY KEY (`name`(20)))");
        stat.close();
        conn.close();
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            load(player.getName());
        }
    }

    public void load(String player) throws Exception {
        Connection conn = DriverManager.getConnection(connectionString);
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

    public void add(String player) throws Exception {
        Connection conn = DriverManager.getConnection(connectionString);
        Statement stat = conn.createStatement();
        stat.executeUpdate("insert into AutoGroup values ('" + player + "',0," + AutoGroup.playerTimes.get(player).getDate() + ", 0, '')");
        stat.close();
        conn.close();
    }

    public void update(String player) throws Exception {
        Connection conn = DriverManager.getConnection(connectionString);
        Statement stat = conn.createStatement();
        stat.executeUpdate("update AutoGroup set time=" + AutoGroup.playerTimes.get(player).getTime()
                + ", last=" + AutoGroup.playerTimes.get(player).getLast() + ", status='"
                + AutoGroup.playerTimes.get(player).getStatus()
                + "' WHERE name='" + player + "'");
        stat.close();
        conn.close();
    }

    public void save() throws Exception {
        Connection conn = DriverManager.getConnection(connectionString);
        Statement stat = conn.createStatement();
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            String name = player.getName();
            AutoGroup.playerTimes.get(name).setLast((int) (System.currentTimeMillis() / 1000L));
            update(name);
        }
        for (String player : AutoGroup.playerTimes.keySet()) {
            update(player);
        }
        stat.close();
        conn.close();
    }
}
