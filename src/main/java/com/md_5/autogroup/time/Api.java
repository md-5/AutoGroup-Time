package com.md_5.autogroup.time;

import com.md_5.autogroup.time.backends.Database;
import com.md_5.autogroup.time.backends.MySQL;
import com.md_5.autogroup.time.backends.SQLite;

public class Api implements Database {

    private Database engine;

    public Api() {
        if (Config.connectionType.equalsIgnoreCase("mysql")) {
            engine = new MySQL();
        } else {
            engine = new SQLite();
        }
    }

    public void init() {
        try {
            engine.init();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void load(String player) {
        try {
            engine.load(player);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void add(String player) {
        try {
            engine.add(player);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void update(String player) {
        try {
            engine.update(player);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void save() {
        try {
            engine.save();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
