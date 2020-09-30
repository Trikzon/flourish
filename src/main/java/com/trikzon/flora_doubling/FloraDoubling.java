package com.trikzon.flora_doubling;

import net.minecraft.block.Block;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(FloraDoubling.MOD_ID)
public class FloraDoubling {
    public static final String MOD_ID = "flora-doubling";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static final File MOD_CONFIG_FILE = new File("./config/" + MOD_ID + ".json");
    public static final ITag.INamedTag<Block> DOUBLING_FLORA_TAG = BlockTags.makeWrapperTag(MOD_ID + ":doubling_flora");

    public static Config.ConfigBean CONFIG = new Config.ConfigBean();

    public FloraDoubling() {
        if (MOD_CONFIG_FILE.exists()) {
            Config.ConfigBean config = Config.read();
            if (config != null) {
                CONFIG = config;
            }
        } else {
            Config.write(CONFIG, true);
        }

        MinecraftForge.EVENT_BUS.addListener(this::onBoneMeal);
    }

    private void onBoneMeal(BonemealEvent event) {
        boolean success = grow(event.getStack(), event.getWorld(), event.getPos());
        if (success) event.setResult(Event.Result.ALLOW);
    }

    private boolean grow(ItemStack stack, World world, BlockPos pos) {
        if (!(stack.getItem() instanceof BoneMealItem)) return false;
        Block block = world.getBlockState(pos).getBlock();

        boolean success = DOUBLING_FLORA_TAG.contains(block) || CONFIG.doublingFlora.contains(block.getRegistryName().toString());
        if (success) Block.spawnAsEntity(world, pos, new ItemStack(block, 1));

        return success;
    }
}
