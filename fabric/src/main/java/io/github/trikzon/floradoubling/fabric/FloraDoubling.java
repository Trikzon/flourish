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
package io.github.trikzon.floradoubling.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerBlock;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;

public class FloraDoubling implements ModInitializer {

    public static final String MOD_ID = "floradoubling";

    @Override
    public void onInitialize() {
        Config.initConfig();

        UseBlockCallback.EVENT.register((playerEntity, world, hand, blockHitResult) -> {
            ItemStack stackInHand = playerEntity.getStackInHand(hand);
            if (stackInHand.getItem() != Items.BONE_MEAL) return ActionResult.PASS;

            Block block = world.getBlockState(blockHitResult.getBlockPos()).getBlock();
            if (!(block instanceof FlowerBlock)) return ActionResult.PASS;

            if (!(Config.CONFIG.doubleWitherRose) && block == Blocks.WITHER_ROSE) return ActionResult.PASS;

            if (!playerEntity.isCreative()) {
                stackInHand.decrement(1);
            }
            if (world.isClient) {
                BoneMealItem.createParticles(world, blockHitResult.getBlockPos(), world.random.nextInt(12));
            }
            Block.dropStack(world, blockHitResult.getBlockPos(), new ItemStack(block, 1));

            return ActionResult.SUCCESS;
        });
    }
}
