package com.trikzon.flora_doubling;

import com.trikzon.flora_doubling.dispenser.BoneMealDispenserBehavior;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tag.Tag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class FloraDoubling implements ModInitializer {
    public static final String MOD_ID = "flora-doubling";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static final File MOD_CONFIG_FILE = new File("./config/" + MOD_ID + ".json");
    public static final Tag<Block> DOUBLING_FLORA_TAG = TagRegistry.block(new Identifier(MOD_ID, "doubling_flora"));
    public static final Tag<Block> SMALL_FLOWERS_TAG = TagRegistry.block(new Identifier("minecraft", "small_flowers"));
    public static final Tag<Block> TALL_FLOWERS_TAG = TagRegistry.block(new Identifier("minecraft", "tall_flowers"));

    public static Config.ConfigBean CONFIG = new Config.ConfigBean();

    @Override
    public void onInitialize() {
        if (MOD_CONFIG_FILE.exists()) {
            Config.ConfigBean config = Config.read();
            if (config != null) {
                CONFIG = config;
            }
        } else {
            Config.write(CONFIG);
        }

        DispenserBlock.registerBehavior(Items.BONE_MEAL, new BoneMealDispenserBehavior());
        UseBlockCallback.EVENT.register(this::onUseBlock);
    }

    private ActionResult onUseBlock(PlayerEntity player, World world, Hand hand, BlockHitResult result) {
        boolean success = grow(player.getStackInHand(hand), world, result.getBlockPos());
        if (!success) return ActionResult.PASS;

        if (!player.isCreative()) {
            player.getStackInHand(hand).decrement(1);
        }
        return ActionResult.SUCCESS;
    }

    private boolean grow(ItemStack stack, World world, BlockPos pos) {
        if (!(stack.getItem() instanceof BoneMealItem)) return false;
        Block block = world.getBlockState(pos).getBlock();
        if (!isTargetFlower(block))
            return false;

        if (world.isClient) {
            BoneMealItem.createParticles(world, pos, world.random.nextInt(14));
        }
        Block.dropStack(world, pos, new ItemStack(block, 1));

        return true;
    }

    public static String getId(Item item) {
        return Registry.ITEM.getId(item).toString();
    }

    public static boolean isTargetFlower(Block block) {
        return ((block.getClass() != WitherRoseBlock.class) || CONFIG.allowWitherRoses) &&
                (DOUBLING_FLORA_TAG.contains(block) && CONFIG.doublingFlora.contains(getId(block.asItem())) ||
                        (SMALL_FLOWERS_TAG.contains(block) && CONFIG.useSmallFlowersTag) ||
                        (TALL_FLOWERS_TAG.contains(block) && CONFIG.useTallFlowersTag));
    }
}
