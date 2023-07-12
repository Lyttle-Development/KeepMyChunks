package com.lyttldev.keepmychunks.utils;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.lyttldev.keepmychunks.KeepMyChunks;
import com.lyttldev.keepmychunks.types.LocationEntry;
import org.bukkit.Bukkit;

import java.io.*;
import java.nio.charset.StandardCharsets;


public class Locations {
    private static final String configPath = KeepMyChunks.getPlugin(KeepMyChunks.class).getDataFolder() + "\\locations.json";
    private static final Gson gson = new Gson();

    // set empty array as default value
    public static LocationEntry[] data;

    public static void init() {
        read();
    }
    private static boolean noLocationOnInit = false;

    public static void add(double x, double y, double z, String world) {
        LocationEntry[] newData = new LocationEntry[data.length + 1];
        System.arraycopy(data, 0, newData, 0, data.length);
        newData[data.length] = new LocationEntry(x, y, z, world);
        data = newData;
        write(gson.toJson(data));
    }

    public static void remove(double x, double y, double z, String world) {
        for (int i = 0; i < data.length; i++) {
            if (data[i].x == x && data[i].z == z && data[i].y == y && data[i].world.equals(world)) {
                LocationEntry[] newData = new LocationEntry[data.length - 1];
                System.arraycopy(data, 0, newData, 0, i);
                System.arraycopy(data, i + 1, newData, i, data.length - i - 1);
                data = newData;
                write(gson.toJson(data));
                return;
            }
        }
    }

    private static void write(String myData) {
        Bukkit.getLogger().info(configPath);
        File file = new File(configPath);


        // exists(): Tests whether the file or directory denoted by this abstract pathname exists.
        if (!file.exists()) {

            try {
                File directory = new File(file.getParent());
                if (!directory.exists()) {

                    // mkdirs(): Creates the directory named by this abstract pathname, including any necessary but nonexistent parent directories.
                    // Note that if this operation fails it may have succeeded in creating some of the necessary parent directories.
                    directory.mkdirs();
                }

                // createNewFile(): Atomically creates a new, empty file named by this abstract pathname if and only if a file with this name does not yet exist.
                // The check for the existence of the file and the creation of the file if it does not exist are a single operation
                // that is atomic with respect to all other filesystem activities that might affect the file.
                file.createNewFile();
            } catch (IOException e) {
                Logger.info("Exception Occurred: " + e.toString());
            }
        }

        try {

            // Convenience class for writing character files
            FileWriter fileWriter;
            fileWriter = new FileWriter(file.getAbsoluteFile(), false);

            // Writes text to a character-output stream
            BufferedWriter bufferWriter = new BufferedWriter(fileWriter);
            bufferWriter.write(myData.toString());
            bufferWriter.close();

            Logger.info("Company data saved at file location: " + configPath + " Data: " + myData);
        } catch (IOException e) {

            Logger.info("Hmm.. Got an error while saving Company data to file " + e.toString());
        }

    }

    // Read From File Utility
    private static void read() {

        // File: An abstract representation of file and directory pathnames.
        // User interfaces and operating systems use system-dependent pathname strings to name files and directories.
        File crunchifyFile = new File(configPath);

        if (!crunchifyFile.exists())
            Logger.info("File doesn't exist");

        InputStreamReader isReader;
        try {
            isReader = new InputStreamReader(new FileInputStream(crunchifyFile), StandardCharsets.UTF_8);

            JsonReader myReader = new JsonReader(isReader);
            LocationEntry[] locations = gson.fromJson(myReader, LocationEntry[].class);

            Logger.info("Locations: " + locations.toString());
            data = locations;
        } catch (Exception e) {
            Logger.info("error load cache from file " + e.toString());
            write("[]");

            if (!noLocationOnInit) {
                noLocationOnInit = true;
                read();
            }
        }

        Logger.info("\nCompany Data loaded successfully from file " + configPath);

    }
}
