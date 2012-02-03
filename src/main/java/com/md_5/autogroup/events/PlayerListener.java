package com.md_5.autogroup.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;

import com.md_5.autogroup.AutoGroup;
import com.md_5.autogroup.Database;

public class PlayerListener implements Listener {

    private final AutoGroup plugin;

    public PlayerListener(final AutoGroup plugin) {
        this.plugin = plugin;
    }

    public void registerEvents(final PluginManager pm) {
        pm.registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String name = event.getPlayer().getName();
        if (AutoGroup.playerTimes.containsKey(name)) {
            AutoGroup.playerTimes.get(name).setLast((int) (System.currentTimeMillis() / 1000L));
            Database.update(name);
            AutoGroup.playerTimes.remove(name);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Database.load(event.getPlayer().getName());
    }
}
