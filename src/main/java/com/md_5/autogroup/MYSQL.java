package com.md_5.autogroup;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.bukkit.Bukkit;

public final class MYSQL {

    public static boolean load(String id, String player) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://" + Config.url + "/" + Config.dbName, Config.userName, Config.password);
            Statement stat = conn.createStatement();
            ResultSet rs = stat.executeQuery("select * from " + Config.table + " WHERE txn_id='" + id + "'");
            Don map = new Don();
            while (rs.next()) {
                String first_name = rs.getString("first_name");
                String last_name = rs.getString("last_name");
                String payer_email = rs.getString("payer_email");
                String txn_id = rs.getString("txn_id");
                int payment_amount = rs.getInt("payment_amount");
                boolean claimed = rs.getBoolean("claimed");
                map.first_name = first_name;
                map.last_name = last_name;
                map.payer_email = payer_email;
                map.txn_id = txn_id;
                map.payment_amount = payment_amount;
                map.claimed = claimed;

            }
            rs = stat.executeQuery("select * from " + Config.table2 + " WHERE name='" + player + "'");
            while (rs.next()) {
                String name = rs.getString("name");
                int amount = rs.getInt("amount");
                map.name = name;
                map.amount = amount;
            }
            AutoGroup.playerDonations.put(player, map);
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

    public static boolean add(String player) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://" + Config.url + "/" + Config.dbName, Config.userName, Config.password);
            Statement stat = conn.createStatement();
            stat.executeUpdate("insert into " + Config.table2 + " values ('" + player + "', " + AutoGroup.playerDonations.get(player).amount + ")");
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

    public static boolean update(String player, String id) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://" + Config.url + "/" + Config.dbName, Config.userName, Config.password);
            Statement stat = conn.createStatement();
            stat.executeUpdate("update " + Config.table + " set claimed=" + AutoGroup.playerDonations.get(player).claimed
                    + " WHERE txn_id='" + id + "'");
            stat.executeUpdate("update " + Config.table2 + " set amount=" + AutoGroup.playerDonations.get(player).amount
                    + " WHERE name='" + player + "'");
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
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://" + Config.url + "/" + Config.dbName, Config.userName, Config.password);
            Statement stat = conn.createStatement();
            stat.executeUpdate("create table if not exists " + Config.table
                    + "(first_name text, last_name text, payer_email text, txn_id text, payment_amount int, claimed boolean)");
            stat.executeUpdate("create table if not exists " + Config.table2
                    + "(name text, amount int)");
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

    public static boolean loadAmount(String player) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://" + Config.url + "/" + Config.dbName, Config.userName, Config.password);
            Statement stat = conn.createStatement();
            ResultSet rs = stat.executeQuery("select amount from " + Config.table2 + " WHERE name='" + player + "'");
            Don map = new Don();
            while (rs.next()) {
                int amount = rs.getInt("amount");
                map.amount = amount;
            }
            AutoGroup.playerDonations.put(player, map);
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
}
