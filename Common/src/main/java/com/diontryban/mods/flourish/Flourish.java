package com.diontryban.mods.flourish;

import com.diontryban.mods.ash.api.modloader.ModLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class Flourish {
    public static final String MOD_ID = "flourish";
    public static final String MOD_NAME = "Flourish";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    private static final File MOD_CONFIG_FILE = ModLoader
            .getConfigDir()
            .resolve(MOD_ID + ".json")
            .toFile();

    public static Config config;

    public static void init() {
        config = Config.read(MOD_CONFIG_FILE);
    }
}
