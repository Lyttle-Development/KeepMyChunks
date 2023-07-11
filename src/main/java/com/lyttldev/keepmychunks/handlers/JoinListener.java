package com.lyttldev.keepmychunks.handlers;

import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class JoinListener implements Listener {
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
//        Bukkit.getLogger().info("Wow! hello" + event.getPlayer().getLocation());
    }
}
