package com.lyttldev.keepmychunks.types;

public class LocationCheck {
    public boolean isInRange;
    public double x;
    public double y;
    public double z;
    public LocationCheck(boolean isInRange, double x, double y, double z)
    {
        this.isInRange = isInRange;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}