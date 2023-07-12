package com.lyttldev.keepmychunks.types;


public class LocationEnabledEntry {
    public double x;
    public double y;
    public double z;
    public String world;
    public boolean enabled;
    public String player;
    public LocationEnabledEntry(double x, double y, double z, String world, boolean enabled, String player) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.enabled = enabled;
        this.player = player;
    }

    public LocationEntry toLocationEntry() {
        return new LocationEntry(this.x, this.y, this.z, this.world);
    }

    public LocationCheckEntry toLocationCheckEntry() {
        return new LocationCheckEntry(this.x, this.y, this.z, this.world, this.enabled);
    }
}