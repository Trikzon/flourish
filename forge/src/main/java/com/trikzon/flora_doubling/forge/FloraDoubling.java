package com.trikzon.flora_doubling.forge;

import com.trikzon.flora_doubling.Config;
import net.minecraft.block.Block;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
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
    private static final File MOD_CONFIG_FILE = new File("./config/" + MOD_ID + ".json");
    public static Config.ConfigBean CONFIG = new Config.ConfigBean();

    public static final ITag.INamedTag<Block> DOUBLING_FLORA = BlockTags.makeWrapperTag(MOD_ID + ":doubling_flora");

    private static boolean grow(ItemStack stack, World world, BlockPos pos)
    {
        if (!(stack.getItem() instanceof BoneMealItem))
        {
            return false;
        }
        Block block = world.getBlockState(pos).getBlock();
        Block.spawnAsEntity(world, pos, new ItemStack(block, 1));
        return block.isIn(DOUBLING_FLORA) || CONFIG.doublingFlora.contains(block.getRegistryName().toString());
    }

    public void onBoneMeal(BonemealEvent event)
    {
        if (!(event.getStack().getItem() instanceof BoneMealItem)) return;

        Block block = event.getWorld().getBlockState(event.getPos()).getBlock();
        if (block.isIn(DOUBLING_FLORA) || CONFIG.doublingFlora.contains(block.getRegistryName().toString()))
        {
            Block.spawnAsEntity(event.getWorld(), event.getPos(), new ItemStack(block, 1));
            event.setResult(Event.Result.ALLOW);
        }
    }

    public FloraDoubling()
    {
        Config config = new Config(MOD_CONFIG_FILE, LOGGER);
        if (MOD_CONFIG_FILE.exists())
        {
            Config.ConfigBean c = config.readConfigFile();
            if (c != null) CONFIG = c;
        }
        else
        {
            config.writeConfigFile(CONFIG, true);
        }

        MinecraftForge.EVENT_BUS.addListener(this::onBoneMeal);
    }
}
