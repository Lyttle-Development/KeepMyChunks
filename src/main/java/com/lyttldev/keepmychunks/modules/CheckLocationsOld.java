package com.lyttldev.keepmychunks.modules;

import com.lyttldev.keepmychunks.types.LocationCheck;
import com.lyttldev.keepmychunks.types.LocationCheckEntry;
import com.lyttldev.keepmychunks.types.LocationEntry;
import com.lyttldev.keepmychunks.utils.Locations;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CheckLocationsOld {
    public static Map<String, LocationCheckEntry> checkLocationsCache = new HashMap<String, LocationCheckEntry>();
    public static Map<String, LocationCheckEntry> checkLocationsUpdated = new HashMap<String, LocationCheckEntry>();
    public static Map<String, LocationCheckEntry> checkLocationsChanged = new HashMap<String, LocationCheckEntry>();
    public static Runnable check = CheckLocationsOld::checkLocations;

    protected static float checkRange = 50;
    protected static float detectRange = 500;

    private static void checkLocations() {
        if (checkLocationsCache.size() == 0 || checkLocationsUpdated.size() == 0) {
            for (LocationEntry locationEntry : Locations.data) {
                setWorld(locationEntry, false);
            }
        }
        // set checkLocationsCache to checkLocationsUpdated
        checkLocationsCache.putAll(checkLocationsUpdated);

        Bukkit.getLogger().info("=========================================");
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        if (players.size() == 0) {
            try {
                Bukkit.getLogger().info("No players online, releasing all chunks!");
//                Console.run("keepchunks:keepchunks releaseall");
            } catch (Exception e) {
                Bukkit.getLogger().info("Error: " + e);
            }
            return;
        }

//        Bukkit.getLogger().info("checkLocationsCache 1: " + checkLocationsCache.size());
//        Bukkit.getLogger().info("checkLocationsUpdated 1: " + checkLocationsUpdated.size());

        for (Player player : players) {
            Location location = player.getLocation();
            LocationCheck result = checkIfLocationInRange(location);
            if (result.isInRange) {
                Bukkit.getLogger().info("Player " + player.getName() + " is in chunk, X:" + result.x + ", Z: " + result.z);
            }

//            Bukkit.getLogger().info("X: " + location.x() + ", Y: " + location.y() + ", Z: " + location.z());
        }

//        Bukkit.getLogger().info("checkLocationsCache 2: " + checkLocationsCache.size());
//        Bukkit.getLogger().info("checkLocationsUpdated 2: " + checkLocationsUpdated.size());

        checkChangedLocations();
        Bukkit.getLogger().info("checkLocationsChanged: " + checkLocationsChanged.size());
        getNearbyChunks();
        Bukkit.getLogger().info("checkLocationsChanged: " + checkLocationsChanged.size());
        activateChanges();
    }

    private static LocationCheck checkIfLocationInRange(Location location) {
//        Bukkit.getLogger().info("Checking if location is in range.");
        double x = location.x();
        double z = location.z();

        boolean result = false;
        double resultX = 0;
        double resultY = 0;
        double resultZ = 0;

        for (LocationEntry locationEntry : Locations.data) {

            Location locationLocation = new Location(Bukkit.getWorld(locationEntry.world), locationEntry.x, locationEntry.y, locationEntry.z);
            Material block = Bukkit.getWorld(locationEntry.world).getBlockAt(locationLocation).getType();
//            Bukkit.getLogger().info("Block: " + block);
            if (block != Material.BEACON) {
                Locations.remove(locationEntry.x, locationEntry.y, locationEntry.z, locationEntry.world);
                continue;
            }

            boolean playerXInRange =
                    // Check if player is in range of positive x
                    (x >= locationEntry.x && x <= locationEntry.x + checkRange) ||
                    // Check if player is in range of negative z
                    (x <= locationEntry.x && x >= locationEntry.x - checkRange);

            boolean playerZInRange =
                    // Check if player is in range of positive x
                    (z >= locationEntry.z && z <= locationEntry.z + checkRange) ||
                    // Check if player is in range of negative z
                    (z <= locationEntry.z && z >= locationEntry.z - checkRange);

            if (playerXInRange && playerZInRange) {
                setWorld(locationEntry, true);
                result = true;
                resultX = locationEntry.x;
                resultY = locationEntry.y;
                resultZ = locationEntry.z;
                continue;
            }
            setWorld(locationEntry, false);
        }
        return new LocationCheck(result, resultX, resultY, resultZ);
    }

    public static void setWorld(LocationEntry locationEntry, boolean enabled) {
        LocationCheckEntry newLocation = new LocationCheckEntry(locationEntry.x, locationEntry.y, locationEntry.z, locationEntry.world, enabled);
        String key = getKey(locationEntry);
        checkLocationsUpdated.put(key, newLocation);
    }

    public static void checkChangedLocations() {
        for (LocationEntry locationEntry : Locations.data) {
            try {
                String key = getKey(locationEntry);
                LocationCheckEntry locationCheckEntryCache = checkLocationsCache.get(key);
                LocationCheckEntry locationCheckEntry = checkLocationsUpdated.get(key);

//                Bukkit.getLogger().info(locationCheckEntry.x + locationCheckEntry.y + locationCheckEntry.z + " locationCheckEntryCache: " + locationCheckEntryCache.enabled);
//                Bukkit.getLogger().info(locationCheckEntryCache.x + locationCheckEntryCache.y + locationCheckEntryCache.z + " locationCheckEntry: " + locationCheckEntry.enabled);

                if (locationCheckEntryCache.enabled == locationCheckEntry.enabled) {
                    continue;
                }

                if (locationCheckEntry.enabled) {
                    checkLocationsChanged.put(key, locationCheckEntry);
                    Bukkit.getLogger().info("Player entered chunk, X:" + locationCheckEntry.x + ", Z: " + locationCheckEntry.z + "(" + key + ")");
                }

                if (!locationCheckEntry.enabled) {
                    checkLocationsChanged.put(key, locationCheckEntry);
                    Bukkit.getLogger().info("Player left chunk, X:" + locationCheckEntry.x + ", Z: " + locationCheckEntry.z + "(" + key + ")");
                }
            } catch (Exception e) {
                Bukkit.getLogger().info("Error: " + e);
            }
        }
    }

    public static String getKey(LocationEntry locationEntry) {
        return locationEntry.x + "|" + locationEntry.y + "|" + locationEntry.z + "|" + locationEntry.world;
    }

    public static void getNearbyChunks() {
        boolean changes = false;
        Collection<LocationCheckEntry> locations = checkLocationsChanged.values();
        for (LocationCheckEntry loc : locations) {
            for (LocationCheckEntry otherLoc : checkLocationsUpdated.values()) {

                boolean inXRange =
                        // Check if player is in range of positive x
                        (otherLoc.x >= loc.x && otherLoc.x <= loc.x + detectRange) ||
                        // Check if player is in range of negative z
                        (otherLoc.x <= loc.x && otherLoc.x >= loc.x - detectRange);

                boolean inZRange =
                        // Check if player is in range of positive x
                        (otherLoc.z >= loc.z && otherLoc.z <= loc.z + detectRange) ||
                        // Check if player is in range of negative z
                        (otherLoc.z <= loc.z && otherLoc.z >= loc.z - detectRange);

                Bukkit.getLogger().info("inXRange: " + inXRange + ", inZRange: " + inZRange);

                if (inXRange && inZRange) {
                    boolean oldEnabled = otherLoc.enabled;
                    LocationEntry newLocation = new LocationEntry(otherLoc.x, otherLoc.y, otherLoc.z, otherLoc.world);
                    otherLoc.enabled = loc.enabled || otherLoc.enabled;
                    checkLocationsChanged.put(getKey(newLocation), otherLoc);
                    if (oldEnabled != otherLoc.enabled) {
                        changes = true;
                    }
                }
            }
        }
        if (locations.size() != checkLocationsChanged.size() || changes) {
            getNearbyChunks();
        }
    }

    public static void activateChanges() {
        if (checkLocationsChanged.size() == 0) {
            return;
        }
        Bukkit.getLogger().info("Activating chunk changes");
        for (LocationCheckEntry loc : checkLocationsChanged.values()) {
            String coordinates = Math.round(loc.x - 50d) + " " + Math.round(loc.z - 50d) + " " + Math.round(loc.x + 50d) + " " + Math.round(loc.z + 50d) + " " + loc.world;

            Bukkit.getLogger().info("coordinates: " + coordinates);
            try {
            if (loc.enabled) {
                Bukkit.getLogger().info("Keeping chunks in region, X: " + loc.x + ", Z: " + loc.z);
//                Console.run("keepchunks:keepchunks keepregion coords " + coordinates);
            } else {
                Bukkit.getLogger().info("Releasing chunks in region, X: " + loc.x + ", Z: " + loc.z);
//                Console.run("keepchunks:keepchunks releaseregion coords " + coordinates);
            }
            } catch (Exception e) {
                Bukkit.getLogger().info("Error: " + e);
            }
        }
        checkLocationsChanged.clear();
        Bukkit.getLogger().info("Chunk changes activated");
    }
}
