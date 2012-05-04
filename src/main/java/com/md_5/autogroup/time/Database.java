package com.md_5.autogroup.time;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Database
{
	private final String connectionString;
	private final String driver;
	private final String createStatement;

	public Database()
	{
		this.connectionString = "jdbc:sqlite:plugins/AutoGroup/users.db";
		this.driver = "org.sqlite.JDBC";
		this.createStatement = "CREATE TABLE IF NOT EXISTS `AutoGroup` (`name` TEXT PRIMARY KEY NOT NULL,`time` INTEGER NOT NULL DEFAULT 0,`date` INTEGER NOT NULL DEFAULT CURRENT_TIMESTAMP, `last` INTEGER NOT NULL DEFAULT CURRENT_TIMESTAMP, `status` TEXT)";
	}





	public Database init()
	{
		try
		{
			Class.forName(this.driver);
			Connection conn = java.sql.DriverManager.getConnection(this.connectionString);
			PreparedStatement stat = conn.prepareStatement(this.createStatement);
			stat.executeUpdate();
			stat.close();
			conn.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return this;
	}

	public PlayerData load(String player) {
		try {
			Connection conn = java.sql.DriverManager.getConnection(this.connectionString);
			PreparedStatement stat = conn.prepareStatement("SELECT * FROM AutoGroup where name = ?");
			stat.setString(1, player);
			ResultSet rs = stat.executeQuery();
			PlayerData info = null;
			if (rs.next()) {
				info = new PlayerData(player);
				info.setPlayTime(rs.getInt("time"));
				info.setFirstJoin(rs.getInt("date"));
				info.setLastJoin(rs.getInt("last"));
				info.status = rs.getString("status");
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
			Connection conn = java.sql.DriverManager.getConnection(this.connectionString);
			PreparedStatement stat = conn.prepareStatement("INSERT INTO AutoGroup ('name') VALUES (?)");
			stat.setString(1, player);
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
			Connection conn = java.sql.DriverManager.getConnection(this.connectionString);
			PreparedStatement stat = conn.prepareStatement("UPDATE AutoGroup SET time = ? , last = CURRENT_TIMESTAMP , status = ? WHERE name = ?");
			stat.setInt(1, player.getPlayTime());
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