package com.lyttldev.keepmychunks.handlers;

import com.lyttldev.keepmychunks.KeepMyChunks;
import com.lyttldev.keepmychunks.utils.Console;
import com.lyttldev.keepmychunks.utils.Locations;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener implements Listener {
    public BlockPlaceListener(KeepMyChunks plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getBlock().getType() != Material.BEACON) {
            return;
        }

        Bukkit.getLogger().info("Block placed, Adding it to the list.");
        Location location = event.getBlock().getLocation();
        Locations.add(location.getX(), location.getY(), location.getZ(), location.getWorld().getName());
    }
}
