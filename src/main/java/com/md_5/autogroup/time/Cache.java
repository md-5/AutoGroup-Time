package com.md_5.autogroup.time;

import java.util.HashMap;

public class Cache {

    private static final HashMap<String, Map> players = new HashMap<String, Map>();

    public static Map get(final String player) {
        if (players.get(player) == null) {
            players.put(player, AutoGroup.database.load(player));
        }
        return players.get(player);
    }

    public static void expire(final String player) {
        players.remove(player);
    }
}
