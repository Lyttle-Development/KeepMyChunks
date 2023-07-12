package com.lyttldev.keepmychunks;

import com.lyttldev.keepmychunks.commands.*;
import com.lyttldev.keepmychunks.handlers.*;
import com.lyttldev.keepmychunks.utils.*;
import org.bukkit.plugin.java.JavaPlugin;

public final class KeepMyChunks extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Console.init(this);
        Locations.init();
        Schedules.init();

        // Commands
        new KeepMyChunksCommand(this);

        // Listeners
        new BlockPlaceListener(this);
    }
}
