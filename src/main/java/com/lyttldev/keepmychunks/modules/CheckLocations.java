package com.lyttldev.keepmychunks.modules;

import com.lyttldev.keepmychunks.types.*;
import com.lyttldev.keepmychunks.utils.*;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class CheckLocations {
    // The runnable that will be called by the scheduler.
    public static Runnable check = CheckLocations::checkLocations;

    // Variables
    public static Map<String, String> playersInKeepChunk = new ConcurrentHashMap<>();
    public static Map<String, LocationCheckEntry> keepLocationsInPlayerRange = new ConcurrentHashMap<>();
    public static Map<String, LocationCheckEntry> keepLocations = new ConcurrentHashMap<>();
    public static Map<String, LocationEnabledEntry> enabledKeepLocations = new ConcurrentHashMap<>();


    // Range to keep chunks loaded:
    protected static float checkRange = 3;

    // Range to detect other keep location loaders:
    protected static float detectRange = 10;

    // Amount in tokens:
    protected static int tokensPerLocationAmount = 1;

    // Time in minutes:
    protected static int tokensPerLocationTime = 20;

    /**
     * Main function
     */
    private static void checkLocations() {
        try {
            Bukkit.getLogger().info("=========================================");

            // Reset all cache's back to empty.
            cleanup();

            // Check all keep locations, and deleted removed keep locations.
            checkKeepLocations();

            // Get player locations
            Collection<Location> playerLocations = getPlayerLocations();

            // Get all keep locations where a player is located.
            getPlayerKeepLocations(playerLocations);
            Bukkit.getLogger().info("keepLocationsInPlayerRange: " + keepLocationsInPlayerRange.size());

            // Get all keep locations that are nearby another keep chunk, where a player is located.
            getAllNearbyKeepLocations();
            Bukkit.getLogger().info("keepLocations: " + keepLocations.size());

            cleanupEnabledKeepLocations();

            //
            notifyPlayersInsideKeepLocations();
            Bukkit.getLogger().info("playersInKeepChunk: " + playersInKeepChunk.size());
        } catch (Exception e) {
            Bukkit.getLogger().info("Error: " + e);
        }
    }

    /**
     * Cleanup all cache's back to empty.
     */
    private static void cleanup() {
        keepLocationsInPlayerRange.clear();
//        playersInKeepChunk.clear();
        keepLocations.clear();
    }

    private static void checkKeepLocations() {
        for (LocationEntry keepLocation : Locations.data) {
            Location bukkitKeepLocation = new Location(Bukkit.getWorld(keepLocation.world), keepLocation.x, keepLocation.y, keepLocation.z);
            Material block = Bukkit.getWorld(keepLocation.world).getBlockAt(bukkitKeepLocation).getType();
            if (block != Material.BEACON) {
                Locations.remove(keepLocation.x, keepLocation.y, keepLocation.z, keepLocation.world);
            }
        }
    }

    private static String getLocationHashMapKey(LocationEntry locationEntry) {
        return locationEntry.x + "|" + locationEntry.y + "|" + locationEntry.z + "|" + locationEntry.world;
    }

    private static Collection<Location> getPlayerLocations() {
        // Get online players
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();

        // Map players to their locations
        return players.stream().map(Player::getLocation).collect(Collectors.toList());
    }

    private static void getPlayerKeepLocations(Collection<Location> playerLocations) {
        for (LocationEntry keepLocation : Locations.data) {
            // Check if player is in range of keepLocation
            for (Location location : playerLocations) {
                if (isLocationInRange(location, keepLocation, checkRange)) {
                    String key = getLocationHashMapKey(keepLocation);
                    boolean isEnabled = enabledKeepLocations.containsKey(key);
                    keepLocationsInPlayerRange.put(key, keepLocation.toLocationCheckEntry(isEnabled));
                }
            }
        }
    }

    private static boolean isLocationInRange(Location location, LocationEntry keepLocation, float range) {
        boolean isXInRange =
                // Check if player is in range of positive x
                (location.x() >= keepLocation.x && location.x() <= keepLocation.x + range) ||
                // Check if player is in range of negative z
                (location.x() <= keepLocation.x && location.x() >= keepLocation.x - range);

        boolean isZInRange =
                // Check if player is in range of positive x
                (location.z() >= keepLocation.z && location.z() <= keepLocation.z + range) ||
                // Check if player is in range of negative z
                (location.z() <= keepLocation.z && location.z() >= keepLocation.z - range);

        return isXInRange && isZInRange;
    }

    private static void getAllNearbyKeepLocations() {
        keepLocations = getNearbyKeepLocations(keepLocationsInPlayerRange);
    }

    private static Map<String, LocationCheckEntry> getByOneNearbyKeepLocations(LocationCheckEntry keepLocation) {
        Map<String, LocationCheckEntry> locations = new ConcurrentHashMap<>();
        String key = getLocationHashMapKey(keepLocation.toLocationEntry());
        locations.put(key, keepLocation);
        return getNearbyKeepLocations(locations);
    }

    private static Map<String, LocationCheckEntry> getNearbyKeepLocations(Map<String, LocationCheckEntry> locations) {
        try {
            boolean checkAgain = false;
            // Check if player is in range of keepLocation
            for (LocationCheckEntry checkLocation : locations.values()) {
                Location keepBukkitLocation = new Location(Bukkit.getWorld(checkLocation.world), checkLocation.x, checkLocation.y, checkLocation.z);
                for (LocationEntry keepLocation : Locations.data) {
                    if (isLocationInRange(keepBukkitLocation, keepLocation, detectRange)) {
                        String key = getLocationHashMapKey(keepLocation);
                        boolean isEnabled = enabledKeepLocations.containsKey(key);
                        LocationCheckEntry newLocation = keepLocation.toLocationCheckEntry(isEnabled);
                        LocationCheckEntry newLocationEntry = locations.get(key);
                        if (newLocationEntry == null) {
                            locations.put(key, newLocation);
                            checkAgain = true;
                        }
                    }
                }
            }
            if (checkAgain) {
                return getNearbyKeepLocations(locations);
            }
            return locations;
        } catch (Exception e) {
            return locations;
        }
    }

    private static void cleanupEnabledKeepLocations() {
        String[] playersLeft = new String[0];
        for (LocationEnabledEntry keepLocation : enabledKeepLocations.values()) {
            String key = getLocationHashMapKey(keepLocation.toLocationEntry());
            LocationCheckEntry keepLocationsStillActive = keepLocations.get(key);

            if (keepLocationsStillActive == null) {
                Player player = Bukkit.getPlayer(keepLocation.player);
                enabledKeepLocations.remove(key);
                if (player != null && !ArrayUtils.contains(playersLeft, player.getName())) {
                    playersLeft = ArrayUtils.add(playersLeft, player.getName());

                    String messageLine1 = "You just &4&lleft&7 a region with active chunk loaders.";
                    String messageLine2 = "\nThe chunk loaders were &a&ldisabled&7 automatically!";
                    Message.sendBulk(player, messageLine1 + messageLine2);
                }
            }
        }
    }

    private static void notifyPlayersInsideKeepLocations() {
        for (LocationEnabledEntry loc : enabledKeepLocations.values()) {
            Bukkit.getLogger().info("enabledKeepLocation: " + loc.enabled + ", " + loc.x + ", " + loc.y + ", " + loc.z + ", " + loc.world);
        }

        try {
            // Get online players
            Collection<? extends Player> players = Bukkit.getOnlinePlayers();

            // Map players to their locations
            for (Player player : players) {
                boolean removePlayer = true;
                boolean enabled = false;
                String name = player.getName();
                for (LocationEntry keepLocation : Locations.data) {
                    String key = getLocationHashMapKey(keepLocation);
                    LocationCheckEntry keepLocationInPlayerRange = keepLocationsInPlayerRange.get(key);
                    if (keepLocationInPlayerRange != null && keepLocationInPlayerRange.enabled) {
                        enabled = true;
                    }
                    String result = playersInKeepChunk.get(name);

                    if (isLocationInRange(player.getLocation(), keepLocation, checkRange)) {
                        removePlayer = false;
                        if (result != null || enabled) {
                            continue;
                        }
                        playersInKeepChunk.put(name, key);
                        int loaderSize = getByOneNearbyKeepLocations(keepLocation.toLocationCheckEntry(false)).size();
                        String messageLine1 = "You just &2&lentered&7 a region with &l" + loaderSize + "&r&7 beacon chunk loaders.";
                        String messageLine2 = "\nYou can start the chunk loading by running: &a&o/kmc start&r&7";
                        String messageLine3 = "\nWhen active, each beacon consumes &c" + tokensPerLocationAmount + " tokens&7 per &b" + tokensPerLocationTime +  " minutes&7.";
                        String messageLine4 = "\nWhen activating this yourself. &lYou will be paying for it!&r&7";
                        String messageLine5 = "\nTo stop the chunk loading, run: &a&o/kmc stop&r&7 or leave the region.";
                        Message.sendBulk(player, messageLine1 + messageLine2 + messageLine3 + messageLine4 + messageLine5);
                    }
                }
                if (removePlayer) {
                    playersInKeepChunk.remove(name);
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().info("Error: " + e.getMessage());
        }
    }

    public static boolean startKeepingLocation(Player player) {
        for (LocationEntry keepLocation : Locations.data) {
            String key = getLocationHashMapKey(keepLocation);
            LocationCheckEntry enabledKeepLocation = keepLocations.get(key);
            Bukkit.getLogger().info("AAAA" +  key + " - " + ((enabledKeepLocation == null) ? "null" : "not null"));
            if (enabledKeepLocation != null && enabledKeepLocation.enabled) {
                continue;
            }

            if (isLocationInRange(player.getLocation(), keepLocation, checkRange)) {
                Map<String, LocationCheckEntry> locations = getByOneNearbyKeepLocations(keepLocation.toLocationCheckEntry(false));
                Bukkit.getLogger().info("SIZE" + locations.size());
                for (LocationCheckEntry location : locations.values()) {
                    Bukkit.getLogger().info("XYZ" + location.x + " " + location.y + " " + location.z);
                    String locationKey = getLocationHashMapKey(location.toLocationEntry());
                    location.enabled = true;
                    enabledKeepLocations.put(locationKey, location.toLocationEnabledEntry(player.getName()));
                }
                return true;
            }
        }
        return false;
    }

    public static boolean stopKeepingLocation(Player player) {
        // remove all entries with player = player.getName() from enabledKeepLocations
        Map<String, LocationEnabledEntry> playerEnabledLocations = enabledKeepLocations.entrySet().stream()
                .filter(entry -> entry.getValue().player.equals(player.getName()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        for (LocationEnabledEntry playerEnabledLocation : playerEnabledLocations.values()) {
            String key = getLocationHashMapKey(playerEnabledLocation.toLocationEntry());
            enabledKeepLocations.remove(key);
        }

        return true;
    }
}
