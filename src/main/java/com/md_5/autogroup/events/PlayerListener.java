package com.md_5.autogroup.events;

import com.md_5.autogroup.AutoGroup;
import com.md_5.autogroup.Database;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;

public class PlayerListener extends org.bukkit.event.player.PlayerListener {

    private final AutoGroup plugin;

    public PlayerListener(final AutoGroup plugin) {
        this.plugin = plugin;
    }

    public void registerEvents(final PluginManager pm) {
        pm.registerEvent(Type.PLAYER_QUIT, this, Priority.Normal, plugin);
        pm.registerEvent(Type.PLAYER_JOIN, this, Priority.Normal, plugin);
    }

    @Override
    public void onPlayerQuit(PlayerQuitEvent event) {
        String name = event.getPlayer().getName();
        if (AutoGroup.playerTimes.containsKey(name)) {
            AutoGroup.playerTimes.get(name).setLast((int) (System.currentTimeMillis() / 1000L));
            Database.update(name);
            AutoGroup.playerTimes.remove(name);
        }
    }

    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        Database.load(event.getPlayer().getName());
    }
}
