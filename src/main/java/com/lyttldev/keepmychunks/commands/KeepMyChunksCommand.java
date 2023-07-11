package com.lyttldev.keepmychunks.commands;

import com.lyttldev.keepmychunks.KeepMyChunks;
import com.lyttldev.keepmychunks.utils.Locations;
import com.lyttldev.keepmychunks.utils.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class KeepMyChunksCommand implements CommandExecutor, TabExecutor {
    KeepMyChunks plugin;

    public KeepMyChunksCommand(KeepMyChunks plugin) {
        this.plugin = plugin;
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
        double z = player.getLocation().getZ();
        String world = player.getWorld().getName();

        if (arguments[0].equals("load")) {
            Locations.add(x, z, world);
            Message.send(player, "Chunk added to config.");
            return true;
        }

        if (arguments[0].equals("unload")) {
            Locations.remove(x, z, world);
            Message.send(player, "Chunk removed from config.");
            return true;
        }

        Message.send(player, "Hello " + player.getName() + "!");

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] arguments) {
        if (arguments.length == 1) {
            return Arrays.asList("load", "unload");
        }

        return Arrays.asList();
    }
}
