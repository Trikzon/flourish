package com.diontryban.flourish.client;

import com.diontryban.ash.api.modloader.forge.ForgeModLoader;
import com.diontryban.flourish.Flourish;
import net.minecraftforge.fml.ModLoadingContext;

public class FlourishClientForge {
    public FlourishClientForge() {
        ForgeModLoader.registerMod(Flourish.MOD_ID, ModLoadingContext.get());
        FlourishClient.init();
    }
}
