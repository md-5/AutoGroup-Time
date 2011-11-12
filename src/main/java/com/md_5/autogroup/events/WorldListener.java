package com.md_5.autogroup.events;

import com.md_5.autogroup.AutoGroup;
import com.md_5.autogroup.Database;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.plugin.PluginManager;

public class WorldListener extends org.bukkit.event.world.WorldListener {

    private final AutoGroup plugin;

    public WorldListener(final AutoGroup plugin) {
        this.plugin = plugin;
    }

    public void registerEvents(final PluginManager pm) {
        pm.registerEvent(Type.WORLD_SAVE, this, Priority.Normal, plugin);
    }

    @Override
    public void onWorldSave(WorldSaveEvent event) {
        Database.save();
    }
}
