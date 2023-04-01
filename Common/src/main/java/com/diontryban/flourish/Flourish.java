package com.diontryban.flourish;

import com.diontryban.ash.api.options.ModOptionsManager;
import com.diontryban.flourish.options.FlourishOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Flourish {
    public static final String MOD_ID = "flourish";
    public static final String MOD_NAME = "Flourish";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);
    public static final ModOptionsManager<FlourishOptions> CONFIG = new ModOptionsManager<>(MOD_ID, FlourishOptions.class);
    public static void init() {

    }
}
