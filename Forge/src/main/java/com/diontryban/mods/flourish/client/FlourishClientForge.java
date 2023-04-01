package com.diontryban.mods.flourish.client;

import com.diontryban.mods.ash.api.modloader.forge.ForgeModLoader;
import com.diontryban.mods.flourish.Flourish;
import net.minecraftforge.fml.ModLoadingContext;

public class FlourishClientForge {
    public FlourishClientForge() {
        ForgeModLoader.registerMod(Flourish.MOD_ID, ModLoadingContext.get());
        FlourishClient.init();
    }
}
