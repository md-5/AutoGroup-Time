package com.md_5.autogroup.time;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Database {

    private final String connectionString;
    private final String driver;
    private final String createStatement;

    public Database(String connectionString, String driver, String createStatement) {
        this.connectionString = connectionString;
        this.driver = driver;
        this.createStatement = createStatement;
    }

    public void init() throws Exception {
        Class.forName(driver);
        Connection conn = DriverManager.getConnection(connectionString);
        PreparedStatement stat = conn.prepareStatement(createStatement);
        stat.executeUpdate();
        stat.close();
        conn.close();
    }

    public PlayerData load(String player) throws Exception {
        Connection conn = DriverManager.getConnection(connectionString);
        PreparedStatement stat = conn.prepareStatement("SELECT * FROM AutoGroup where name = ?");
        stat.setString(0, player);
        ResultSet rs = stat.executeQuery();
        PlayerData map = null;
        if (rs.next()) {
            map = new PlayerData(player);
            map.setPlayTime(rs.getInt("time"));
            map.setFirstJoin(rs.getInt("date"));
            map.setLastJoin(rs.getInt("last"));
            map.setStatus(rs.getString("status"));
        } else {
            map = add(player);
        }
        rs.close();
        stat.close();
        conn.close();
        return map;
    }

    private PlayerData add(String player) throws Exception {
        Connection conn = DriverManager.getConnection(connectionString);
        PreparedStatement stat = conn.prepareStatement("INSERT INTO AutoGroup ('name', 'date') VALUES (?, ?)");
        stat.setString(0, player);
        stat.setInt(1, AutoGroup.getTime());
        stat.executeUpdate();
        stat.close();
        conn.close();
        return load(player);
    }

    public void update(PlayerData player) throws Exception {
        Connection conn = DriverManager.getConnection(connectionString);
        PreparedStatement stat = conn.prepareStatement("UPDATE AutoGroup SET time = ? , last = ? , status = ? WHERE name = ?");
        stat.setInt(0, player.getPlayTime());
        stat.setInt(1, AutoGroup.getTime());
        stat.setString(2, player.getStatus());
        stat.setString(3, player.name);
        stat.executeUpdate();
        stat.close();
        conn.close();
    }
}
