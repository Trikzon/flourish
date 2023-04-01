package com.diontryban.mods.flourish.client;

import com.diontryban.mods.flourish.Flourish;
import net.fabricmc.api.ClientModInitializer;

public class FlourishClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Flourish.init();
        FlourishClient.init();
    }
}
