/*
 * Copyright 2020 Trikzon
 *
 * Flora Doubling is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * https://www.gnu.org/licenses/
 */
package com.trikzon.flora_doubling.fabric;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.trikzon.flora_doubling.Config;
import com.trikzon.flora_doubling.fabric.mixin.DispenserBlockAccessor;
import com.trikzon.flora_doubling.fabric.mixin.DispenserBlockAccessor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.Tag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class FloraDoubling implements ModInitializer
{
    public static final String MOD_ID = "flora-doubling";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    private static final File MOD_CONFIG_FILE = new File("./config/" + MOD_ID + ".json");
    public static Config.ConfigBean CONFIG = new Config.ConfigBean();

    public static final Tag<Block> DOUBLING_FLORA = TagRegistry.block(new Identifier(MOD_ID, "doubling_flora"));

    public static String getId(Item item)
    {
        return Registry.ITEM.getId(item).toString();
    }

    // Copied from BoneMealItem but edited to create the particles from the server instead of the client
    // This is so that dispensers will emit particles when growing flowers
    public static void createParticles(ServerWorld world, BlockPos pos, int count, Random random) {
        if (count == 0) {
            count = 15;
        }

        BlockState blockState = world.getBlockState(pos);
        if (!blockState.isAir()) {
            double d = 0.5D;
            double g;
            if (blockState.isOf(Blocks.WATER)) {
                count *= 3;
                g = 1.0D;
                d = 3.0D;
            } else if (blockState.isOpaqueFullCube(world, pos)) {
                pos = pos.up();
                count *= 3;
                d = 3.0D;
                g = 1.0D;
            } else {
                g = blockState.getOutlineShape(world, pos).getMax(Direction.Axis.Y);
            }

            world.spawnParticles(ParticleTypes.HAPPY_VILLAGER, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, 1, 0.0D, 0.0D, 0.0D, 1.0);

            for(int i = 0; i < count; ++i) {
                double h = random.nextGaussian() * 0.02D;
                double j = random.nextGaussian() * 0.02D;
                double k = random.nextGaussian() * 0.02D;
                double l = 0.5D - d;
                double m = (double)pos.getX() + l + random.nextDouble() * d * 2.0D;
                double n = (double)pos.getY() + random.nextDouble() * g;
                double o = (double)pos.getZ() + l + random.nextDouble() * d * 2.0D;
                if (!world.getBlockState((new BlockPos(m, n, o)).down()).isAir()) {
                    world.spawnParticles(ParticleTypes.HAPPY_VILLAGER, m, n, o, 1, h, j, k, 1.0);
                }
            }

        }
    }

    private static boolean grow(ItemStack stack, World world, BlockPos pos, boolean runsOnClient)
    {
        if (!(stack.getItem() instanceof BoneMealItem))
        {
            return false;
        }
        Block block = world.getBlockState(pos).getBlock();
        if (!(DOUBLING_FLORA.contains(block) || CONFIG.doublingFlora.contains(getId(block.asItem()))))
        {
            return false;
        }

        if (world.isClient && runsOnClient)
        {
            BoneMealItem.createParticles(world, pos, world.random.nextInt(14));
        }
        else if (!world.isClient && !runsOnClient)
        {
            createParticles((ServerWorld) world, pos, world.random.nextInt(14), world.random);
        }
        Block.dropStack(world, pos, new ItemStack(block, 1));

        return true;
    }

    private void registerDispenserBehaviors()
    {
        if (!CONFIG.dispenser) return;

        Map<Item, DispenserBehavior> behaviors = ((DispenserBlockAccessor) Blocks.DISPENSER).getBEHAVIORS();

        // Tiny hack to get default behavior to compare so I don't run it if I'm not meant to.
        DispenserBehavior defaultBehavior = behaviors.get(Items.AIR);
        if (!(defaultBehavior instanceof ItemDispenserBehavior)) return;

        DispenserBehavior behavior = behaviors.get(Items.BONE_MEAL);

        DispenserBlock.registerBehavior(Items.BONE_MEAL, (pointer, stack) -> {
            ItemStack preResult = behavior.dispense(pointer, stack);
            if (!preResult.equals(stack) || stack.getCount() != preResult.getCount())
            {
                return preResult;
            }

            BlockPos pos = pointer.getBlockPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
            Block block = pointer.getWorld().getBlockState(pos).getBlock();
            if (DOUBLING_FLORA.contains(block) || CONFIG.doublingFlora.contains(getId(block.asItem())))
            {
                boolean success = grow(stack, pointer.getWorld(), pos, false);
                if (success)
                {
                    stack.decrement(1);
                }
            }
            return stack;
        });
    }

    @Override
    public void onInitialize()
    {
        Config config = new Config(MOD_CONFIG_FILE, LOGGER);
        if (MOD_CONFIG_FILE.exists()) {
            Config.ConfigBean c = config.readConfigFile();
            if (c != null) CONFIG = c;
        } else {
            config.writeConfigFile(CONFIG, true);
        }

        UseBlockCallback.EVENT.register((playerEntity, world, hand, blockHitResult) -> {
            boolean success = grow(playerEntity.getStackInHand(hand), world, blockHitResult.getBlockPos(), true);
            if (!success) return ActionResult.PASS;

            if (!playerEntity.isCreative())
            {
                playerEntity.getStackInHand(hand).decrement(1);
            }
            return ActionResult.SUCCESS;
        });
        registerDispenserBehaviors();
    }
}
