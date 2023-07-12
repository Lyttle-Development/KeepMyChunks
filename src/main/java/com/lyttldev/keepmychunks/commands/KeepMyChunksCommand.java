package com.lyttldev.keepmychunks.commands;

import com.lyttldev.keepmychunks.KeepMyChunks;
import com.lyttldev.keepmychunks.modules.CheckLocations;
import com.lyttldev.keepmychunks.utils.Locations;
import com.lyttldev.keepmychunks.utils.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class KeepMyChunksCommand implements CommandExecutor, TabExecutor {
    public KeepMyChunksCommand(KeepMyChunks plugin) {
        plugin.getCommand("keepmychunks").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String [] arguments) {
        if (!(sender instanceof Player)) {
            Message.send(sender, "This command can only be run by a player.");
            return true;
        }

        Player player = (Player) sender;
        double x = player.getLocation().getX();
        double y = player.getLocation().getY();
        double z = player.getLocation().getZ();
        String world = player.getWorld().getName();

        if (arguments[0].equals("start")) {
            boolean result = CheckLocations.startKeepingLocation(player);
            if (!result) {
                Message.send(player, "You are already keeping chunks loaded!");
                return true;
            }
            Message.send(player, "Chunk loaders have been &a&lenabled&r&7!");
            return true;
        }

        if (arguments[0].equals("stop")) {
            boolean result = CheckLocations.stopKeepingLocation(player);
            if (!result) {
                Message.send(player, "You are not keeping chunks loaded!");
                return true;
            }
            Message.send(player, "Chunk loaders have been &c&ldisabled&r&7!");
            return true;
        }

        Message.send(player, "Hello " + player.getName() + "!");

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] arguments) {
        if (arguments.length == 1) {
            return Arrays.asList("start", "stop");
        }

        return Arrays.asList();
    }
}
