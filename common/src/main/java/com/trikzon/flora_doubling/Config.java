package com.trikzon.flora_doubling;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Config {
    private final File location;
    private final Logger logger;

    public Config(File location, Logger logger) {
        this.location = location;
        this.logger = logger;
    }

    public static class ConfigBean {
        public ArrayList<String> doublingFlora = new ArrayList<>();
        public boolean dispenser = true;
    }

    public ConfigBean readConfigFile() {
        try (FileReader file = new FileReader(location)) {
            Gson gson = new Gson();
            return gson.fromJson(file, ConfigBean.class);
        }
        catch (IOException e) {
            logger.error("Failed to read from config file.");
            return null;
        }
    }

    private void setDefaultConfig(ConfigBean config) {
        config.doublingFlora.add("minecraft:dandelion");
        config.doublingFlora.add("minecraft:poppy");
        config.doublingFlora.add("minecraft:blue_orchid");
        config.doublingFlora.add("minecraft:allium");
        config.doublingFlora.add("minecraft:azure_bluet");
        config.doublingFlora.add("minecraft:red_tulip");
        config.doublingFlora.add("minecraft:orange_tulip");
        config.doublingFlora.add("minecraft:white_tulip");
        config.doublingFlora.add("minecraft:pink_tulip");
        config.doublingFlora.add("minecraft:oxeye_daisy");
        config.doublingFlora.add("minecraft:cornflower");
        config.doublingFlora.add("minecraft:lily_of_the_valley");
        config.doublingFlora.add("minecraft:wither_rose");
    }

    public void writeConfigFile(ConfigBean config, boolean withDefaults) {
        if (!location.getParentFile().exists() && !location.getParentFile().mkdirs()) {
            logger.error("Failed to write the config file as parent directories couldn't be made.");
            return;
        }
        try (FileWriter file = new FileWriter(location)) {
            if (withDefaults) {
                setDefaultConfig(config);
            }
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            file.write(gson.toJson(config));
            file.flush();
        }
        catch (IOException e) {
            logger.error("Failed to write the config file.");
        }
    }
}
