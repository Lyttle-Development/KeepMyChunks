package com.lyttldev.keepmychunks.types;


public class LocationEntry {
    public double x;
    public double y;
    public double z;
    public String world;
    public LocationEntry(double x, double y, double z, String world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
    }

    public LocationCheckEntry toLocationCheckEntry(boolean enabled) {
        return new LocationCheckEntry(this.x, this.y, this.z, this.world, enabled);
    }

    public LocationEnabledEntry toLocationEnabledEntry(boolean enabled, String player) {
        return new LocationEnabledEntry(this.x, this.y, this.z, this.world, enabled, player);
    }
}