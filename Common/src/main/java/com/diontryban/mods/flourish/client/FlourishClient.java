package com.diontryban.mods.flourish.client;

import com.diontryban.mods.ash.api.client.gui.screens.ModOptionsScreenRegistry;
import com.diontryban.mods.flourish.Flourish;
import com.diontryban.mods.flourish.client.gui.screens.FlourishOptionsScreen;

public class FlourishClient {
    public static void init() {
        ModOptionsScreenRegistry.registerModOptionsScreen(Flourish.CONFIG, FlourishOptionsScreen::new);
    }
}
