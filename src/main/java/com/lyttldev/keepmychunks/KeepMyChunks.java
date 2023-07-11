package com.lyttldev.keepmychunks;

import com.lyttldev.keepmychunks.commands.*;
import com.lyttldev.keepmychunks.utils.*;
import org.bukkit.plugin.java.JavaPlugin;

public final class KeepMyChunks extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Locations.init();
        Schedules.init();

        // Commands
        new KeepMyChunksCommand(this);
    }
}
