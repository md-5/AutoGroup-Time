package com.md_5.autogroup.time;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Database {

    private final String connectionString;
    private final String driver;
    private final String createStatement;

    public Database() {
        this.connectionString = "jdbc:sqlite:plugins/AutoGroup/users.db";
        this.driver = "org.sqlite.JDBC";
        this.createStatement = "CREATE TABLE IF NOT EXISTS AutoGroup (`name` text NOT NULL, `time` int(10) unsigned NOT NULL DEFAULT '0', `date` "
                + "int(10) unsigned NOT NULL DEFAULT '0', `last` int(10) unsigned NOT NULL DEFAULT '0', `status` text, PRIMARY KEY (`name`(20)))";
    }

    public void init() {
        try {
            Class.forName(driver);
            Connection conn = DriverManager.getConnection(connectionString);
            PreparedStatement stat = conn.prepareStatement(createStatement);
            stat.executeUpdate();
            stat.close();
            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public PlayerData load(String player) {
        try {
            Connection conn = DriverManager.getConnection(connectionString);
            PreparedStatement stat = conn.prepareStatement("SELECT * FROM AutoGroup where name = ?");
            stat.setString(0, player);
            ResultSet rs = stat.executeQuery();
            PlayerData info = null;
            if (rs.next()) {
                info = new PlayerData(player);
                info.setPlayTime(rs.getInt("time"));
                info.setFirstJoin(rs.getInt("date"));
                info.setLastJoin(rs.getInt("last"));
                info.setStatus(rs.getString("status"));
            } else {
                info = add(player);
            }
            rs.close();
            stat.close();
            conn.close();
            return info;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private PlayerData add(String player) {
        try {
            Connection conn = DriverManager.getConnection(connectionString);
            PreparedStatement stat = conn.prepareStatement("INSERT INTO AutoGroup ('name', 'date') VALUES (?, ?)");
            stat.setString(0, player);
            stat.setInt(1, AutoGroup.getTime());
            stat.executeUpdate();
            stat.close();
            conn.close();
            return load(player);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void update(PlayerData player) {
        try {
            Connection conn = DriverManager.getConnection(connectionString);
            PreparedStatement stat = conn.prepareStatement("UPDATE AutoGroup SET time = ? , last = ? , status = ? WHERE name = ?");
            stat.setInt(0, player.getPlayTime());
            stat.setInt(1, AutoGroup.getTime());
            stat.setString(2, player.getStatus());
            stat.setString(3, player.getName());
            stat.executeUpdate();
            stat.close();
            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
