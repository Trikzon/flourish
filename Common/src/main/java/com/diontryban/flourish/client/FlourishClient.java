package com.diontryban.flourish.client;

import com.diontryban.ash.api.client.gui.screens.ModOptionsScreenRegistry;
import com.diontryban.flourish.Flourish;
import com.diontryban.flourish.client.gui.screens.FlourishOptionsScreen;

public class FlourishClient {
    public static void init() {
        ModOptionsScreenRegistry.registerModOptionsScreen(Flourish.CONFIG, FlourishOptionsScreen::new);
    }
}
