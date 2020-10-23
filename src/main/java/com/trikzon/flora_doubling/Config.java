package com.trikzon.flora_doubling;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Config {
    public static class ConfigBean {
        public ArrayList<String> doublingFlora = new ArrayList<>();
        public boolean dispenser = true;
        public boolean useSmallFlowersTag = true;
        public boolean useTallFlowersTag = true;
    }

    public static ConfigBean read() {
        try (FileReader file = new FileReader(FloraDoubling.MOD_CONFIG_FILE)) {
            Gson gson = new Gson();
            return gson.fromJson(file, ConfigBean.class);
        }
        catch (IOException e) {
            FloraDoubling.LOGGER.error("Failed to read from config file.");
            return null;
        }
    }

    public static void write(ConfigBean config, boolean withDefaults) {
        if (!FloraDoubling.MOD_CONFIG_FILE.getParentFile().exists() &&
            !FloraDoubling.MOD_CONFIG_FILE.getParentFile().mkdirs()) {
            FloraDoubling.LOGGER.error("Failed to write the config file as parent directories couldn't be made.");
        }
        try (FileWriter file = new FileWriter(FloraDoubling.MOD_CONFIG_FILE)) {
            if (withDefaults) {
                setDefaults(config);
            }
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            file.write(gson.toJson(config));
            file.flush();
        }
        catch (IOException e) {
            FloraDoubling.LOGGER.error("Failed to write the config file.");
        }
    }

    private static void setDefaults(ConfigBean config) {
    }
}
