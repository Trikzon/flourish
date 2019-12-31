package io.github.trikzon.floradoubling.fabric;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

public class Config {

    public static ConfigObject CONFIG;

    public static final File MOD_CONFIG_FILE = new File("./config/" + FloraDoubling.MOD_ID + ".json");

    public static void initConfig() {
        Gson gson = new Gson();
        try {
            CONFIG = gson.fromJson(new FileReader(MOD_CONFIG_FILE), ConfigObject.class);
        } catch (FileNotFoundException e) {
            CONFIG = save();
        }
    }

    private static ConfigObject save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        ConfigObject defaultConfig = new ConfigObject(false);
        String config = gson.toJson(defaultConfig);

        try (FileWriter file = new FileWriter(MOD_CONFIG_FILE)) {
            file.write(config);
            file.flush();
        } catch (IOException e) {
            File dir = new File("./config/");
            dir.mkdir();
            save();
        }

        return defaultConfig;
    }

    static class ConfigObject {

        public boolean doubleWitherRose;

        public ConfigObject(boolean doubleWitherRose) {
            this.doubleWitherRose = doubleWitherRose;
        }
    }
}