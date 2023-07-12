package com.lyttldev.keepmychunks.utils;

import com.lyttldev.keepmychunks.modules.CheckLocations;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Schedules {
    public static void init() {
        createSchedule(CheckLocations.check, 15);
    }

    private static void createSchedule(Runnable runnable, int seconds) {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(runnable, 0, seconds, TimeUnit.SECONDS);
    }
}
