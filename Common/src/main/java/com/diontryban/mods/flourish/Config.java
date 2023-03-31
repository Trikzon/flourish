package com.diontryban.mods.flourish;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Config {
    public int version = 1;
    @SerializedName("wither_rose")
    public boolean witherRose = false;
    @SerializedName("use_tall_flower_behavior")
    public boolean useTallFlowerBehavior = false;

    public static Config read(File file) {
        if (!file.exists()) {
            return new Config().write(file);
        }

        try (FileReader reader = new FileReader(file)) {
            Gson gson = new Gson();
            Config config = gson.fromJson(reader, Config.class);

            if (config.version < 1) {
                Flourish.LOG.info("Found deprecated config file. Updating.");
                return new Config().write(file);
            }
            return config;
        } catch (IOException e) {
            Flourish.LOG.error("Failed to read from config file.");
            return new Config();
        }
    }

    public Config write(File file) {
        try (FileWriter writer = new FileWriter(file)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            writer.write(gson.toJson(this));
            writer.flush();
        } catch (IOException e) {
            Flourish.LOG.error("Failed to write to config file.");
        }
        return this;
    }
}
