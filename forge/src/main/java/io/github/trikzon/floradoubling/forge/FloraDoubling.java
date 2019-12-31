/* ===========================================================================
 * Copyright 2019 Trikzon
 *
 * Flora-Doubling is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * File: FloraDoubling.java
 * Date: 2019-12-30
 * Revision:
 * Author: Trikzon
 * =========================================================================== */
package io.github.trikzon.floradoubling.forge;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(FloraDoubling.MOD_ID)
public class FloraDoubling {

    public static final String MOD_ID = "floradoubling";

    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public FloraDoubling() {
        MinecraftForge.EVENT_BUS.register(FloraDoubling.class);
        MinecraftForge.EVENT_BUS.register(Config.class);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);
        Config.loadConfig(Config.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve(MOD_ID + "-common.toml"));
    }

    @SubscribeEvent
    public static void onUseBlock(PlayerInteractEvent.RightClickBlock event) {

        PlayerEntity player = event.getPlayer();
        Hand hand = event.getHand();

        ItemStack stackInHand = player.getHeldItem(hand);
        if (stackInHand.getItem() != Items.BONE_MEAL) return;

        World world = event.getWorld();
        BlockPos pos = event.getPos();
        Block block = world.getBlockState(pos).getBlock();
        if (!(block instanceof FlowerBlock)) return;

        if (!Config.DOUBLE_WITHER_ROSE.get() && block == Blocks.WITHER_ROSE) return;

        if (!player.isCreative()) {
            stackInHand.shrink(1);
        }
        if (world.isRemote) {
            BoneMealItem.spawnBonemealParticles(world, pos, world.rand.nextInt(12));
        }
        Block.spawnAsEntity(world, pos, new ItemStack(block, 1));
    }
}
