package com.diontryban.mods.flourish;

import com.diontryban.mods.flourish.client.FlourishClientForge;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod(Flourish.MOD_ID)
public class FlourishForge {
    public FlourishForge() {
        Flourish.init();
        DistExecutor.safeCallWhenOn(Dist.CLIENT, () -> FlourishClientForge::new);
    }
}
