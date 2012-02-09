package com.md_5.autogroup.time;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Database {

    private final String connectionString;
    private final String driver;
    private final String createStatement;

    public Database(final String connectionString, final String driver, final String createStatement) {
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

    public Map load(final String player) throws Exception {
        Connection conn = DriverManager.getConnection(connectionString);
        PreparedStatement stat = conn.prepareStatement("SELECT * FROM AutoGroup where name = ?");
        stat.setString(0, player);
        ResultSet rs = stat.executeQuery();
        Map map = new Map();
        while (rs.next()) {
            map.setPlayTime(rs.getInt("time"));
            map.setFirstJoin(rs.getInt("date"));
            map.setLastJoin(rs.getInt("last"));
            map.setStatus(rs.getString("status"));
        }
        rs.close();
        stat.close();
        conn.close();
        return map;
    }

    public void add(final String player) throws Exception {
        // TODO call from load
        Connection conn = DriverManager.getConnection(connectionString);
        PreparedStatement stat = conn.prepareStatement("INSERT INTO AutoGroup ('name', 'date') VALUES (?, ?)");
        stat.setString(0, player);
        stat.setInt(1, AutoGroup.getTime());
        stat.executeUpdate();
        stat.close();
        conn.close();
    }

    public void update(final String player, final int playTime, final String status) throws Exception {
        Connection conn = DriverManager.getConnection(connectionString);
        PreparedStatement stat = conn.prepareStatement("UPDATE AutoGroup SET time = ? , last = ? , status = ? WHERE name = ?");
        stat.setInt(0, playTime);
        stat.setInt(1, AutoGroup.getTime());
        stat.setString(2, status);
        stat.setString(3, player);
        stat.executeUpdate();
        stat.close();
        conn.close();
        Cache.expire(player);
    }
}
