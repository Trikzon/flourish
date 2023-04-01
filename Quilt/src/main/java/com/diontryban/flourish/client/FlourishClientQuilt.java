package com.diontryban.flourish.client;

import com.diontryban.flourish.Flourish;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

public class FlourishClientQuilt implements ClientModInitializer {
    @Override
    public void onInitializeClient(ModContainer mod) {
        Flourish.init();
        FlourishClient.init();
    }
}
