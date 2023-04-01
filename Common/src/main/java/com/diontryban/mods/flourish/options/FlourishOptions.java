package com.diontryban.mods.flourish.options;

import com.diontryban.mods.ash.api.options.ModOptions;
import com.google.gson.annotations.SerializedName;

public class FlourishOptions extends ModOptions {
    @SerializedName("wither_rose")
    public boolean witherRose = false;
    @SerializedName("use_tall_flower_behavior")
    public boolean useTallFlowerBehavior = false;

    @Override
    protected int getVersion() {
        return 1;
    }
}
