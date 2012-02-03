package com.md_5.autogroup.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.plugin.PluginManager;

import com.md_5.autogroup.AutoGroup;
import com.md_5.autogroup.Database;

public class WorldListener implements Listener {

    private final AutoGroup plugin;

    public WorldListener(final AutoGroup plugin) {
        this.plugin = plugin;
    }

    public void registerEvents(final PluginManager pm) {
        pm.registerEvents(this, plugin);
    }

    @EventHandler
    public void onWorldSave(WorldSaveEvent event) {
        Database.save();
    }
}
