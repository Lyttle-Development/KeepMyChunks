package com.lyttldev.keepmychunks.modules;

import com.lyttldev.keepmychunks.types.ConfigEntry;
import com.lyttldev.keepmychunks.types.LocationCheck;
import com.lyttldev.keepmychunks.utils.Locations;
import org.bukkit.Bukkit;

public class CheckLocations {
    public static Runnable check = CheckLocations::checkLocations;

    protected static float checkRange = 100;

    private static void checkLocations() {
        java.util.Collection<? extends org.bukkit.entity.Player> players = Bukkit.getOnlinePlayers();

        for (org.bukkit.entity.Player player : players) {
            org.bukkit.Location location = player.getLocation();
            double x = location.x();
            double y = location.y();
            double z = location.z();
            LocationCheck result = checkIfLocationInRange(x, z);
            if (result.isInRange) {
                Bukkit.getLogger().info("Player " + player.getName() + " is in chunk, X:" + result.x + ", Z: " + result.z);
            }

            Bukkit.getLogger().info("X: " + x + ", Y: " + y + ", Z: " + z);
        }
    }

    private static LocationCheck checkIfLocationInRange(double x, double z) {
        for (ConfigEntry location : Locations.data) {
            float range = checkRange / 2;
            boolean playerXInRange =
                    // Check if player is in range of positive x
                    (x >= location.x && x <= location.x + range) ||
                            // Check if player is in range of negative z
                            (x <= location.x && x >= location.x - range);

            boolean playerZInRange =
                    // Check if player is in range of positive x
                    (z >= location.z && z <= location.z + range) ||
                            // Check if player is in range of negative z
                            (z <= location.z && z >= location.z - range);

            if (playerXInRange && playerZInRange) {
                return new LocationCheck(true, location.x, location.z);
            }
        }
        return new LocationCheck(false, 0, 0);
    }
}
