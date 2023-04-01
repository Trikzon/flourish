package com.diontryban.mods.flourish.client.gui.screens;

import com.diontryban.mods.ash.api.client.gui.screens.ModOptionsScreen;
import com.diontryban.mods.ash.api.options.ModOptionsManager;
import com.diontryban.mods.flourish.Flourish;
import com.diontryban.mods.flourish.options.FlourishOptions;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class FlourishOptionsScreen extends ModOptionsScreen<FlourishOptions> {
    public FlourishOptionsScreen(ModOptionsManager<FlourishOptions> config, Screen parent) {
        super(Component.literal(Flourish.MOD_NAME), config, parent);
    }

    @Override
    protected void addOptions() {
        this.list.addBig(OptionInstance.createBoolean(
                "flourish.options.wither_rose",
                value -> Tooltip.create(Component.translatable("flourish.options.wither_rose.tooltip")),
                options.get().witherRose,
                value -> options.get().witherRose = value
        ));
        this.list.addBig(OptionInstance.createBoolean(
                "flourish.options.use_tall_flower_behavior",
                value -> Tooltip.create(Component.translatable("flourish.options.use_tall_flower_behavior.tooltip")),
                options.get().useTallFlowerBehavior,
                value -> options.get().useTallFlowerBehavior = value
        ));
    }
}
