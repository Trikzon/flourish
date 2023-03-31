package com.diontryban.mods.flourish;

import net.fabricmc.api.ModInitializer;

public class FlourishFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Flourish.init();
    }
}
