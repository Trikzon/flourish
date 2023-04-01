package com.diontryban.flourish;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

public class FlourishQuilt implements ModInitializer {
    @Override
    public void onInitialize(ModContainer mod) {
        Flourish.init();
    }
}
