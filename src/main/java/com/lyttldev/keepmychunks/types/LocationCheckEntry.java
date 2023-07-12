package com.lyttldev.keepmychunks.types;


public class LocationCheckEntry {
    public double x;
    public double y;
    public double z;
    public String world;
    public boolean enabled;
    public LocationCheckEntry(double x, double y, double z, String world, boolean enabled) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.enabled = enabled;
    }

    public LocationEntry toLocationEntry() {
        return new LocationEntry(this.x, this.y, this.z, this.world);
    }

    public LocationEnabledEntry toLocationEnabledEntry(String player) {
        return new LocationEnabledEntry(this.x, this.y, this.z, this.world, this.enabled, player);
    }
}