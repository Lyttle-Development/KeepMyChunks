package com.lyttldev.keepmychunks.utils;

import com.lyttldev.keepmychunks.KeepMyChunks;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

public class Console {
    public static KeepMyChunks plugin;

    public static void init(KeepMyChunks plugin) {
        Console.plugin = plugin;
    }

    public static void run(String command) {
        if (command == null || command.isEmpty()) return;
        ConsoleCommandSender console = Bukkit.getConsoleSender();
        Bukkit.getScheduler().callSyncMethod( plugin, () -> Bukkit.dispatchCommand( console, command ) );
    }
}
