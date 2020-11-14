package com.trikzon.flora_doubling;

import net.minecraft.block.Block;
import net.minecraft.block.WitherRoseBlock;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
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
    public static final Tag<Block> DOUBLING_FLORA_TAG = new BlockTags.Wrapper(new ResourceLocation(MOD_ID, "doubling_flora"));
    public static final Tag<Block> SMALL_FLOWERS_TAG = BlockTags.SMALL_FLOWERS;
    public static final Tag<Block> TALL_FLOWERS_TAG = BlockTags.TALL_FLOWERS;

    public static Config.ConfigBean CONFIG = new Config.ConfigBean();

    public FloraDoubling() {
        if (MOD_CONFIG_FILE.exists()) {
            Config.ConfigBean config = Config.read();
            if (config != null) {
                CONFIG = config;
            }
        } else {
            Config.write(CONFIG);
        }

        MinecraftForge.EVENT_BUS.addListener(this::onBoneMeal);
    }

    private void onBoneMeal(BonemealEvent event) {
        if (!(!CONFIG.dispenser && event.getPlayer() instanceof FakePlayer)) {
            boolean success = grow(event.getStack(), event.getWorld(), event.getPos());
            if (success) event.setResult(Event.Result.ALLOW);
        }
    }

    private boolean grow(ItemStack stack, World world, BlockPos pos) {
        if (!(stack.getItem() instanceof BoneMealItem)) return false;
        Block block = world.getBlockState(pos).getBlock();

        boolean success = isTargetFlower(block);
        if (success) Block.spawnAsEntity(world, pos, new ItemStack(block, 1));

        return success;
    }

    public static boolean isTargetFlower(Block block) {
        return ((block.getClass() != WitherRoseBlock.class) || CONFIG.allowWitherRoses) &&
                (DOUBLING_FLORA_TAG.contains(block) || CONFIG.doublingFlora.contains(block.getRegistryName().toString()) ||
                        (SMALL_FLOWERS_TAG.contains(block) && CONFIG.useSmallFlowersTag) ||
                        (TALL_FLOWERS_TAG.contains(block) && CONFIG.useTallFlowersTag));
    }
}
