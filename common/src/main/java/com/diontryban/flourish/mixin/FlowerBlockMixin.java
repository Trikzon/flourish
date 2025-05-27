/*
 * This file is part of Flourish.
 * A copy of this program can be found at https://github.com/Trikzon/flourish.
 * Copyright (C) 2023 Dion Tryban
 *
 * Flourish is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Flourish is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Flourish. If not, see <https://www.gnu.org/licenses/>.
 */

package com.diontryban.flourish.mixin;

import com.diontryban.flourish.Flourish;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(FlowerBlock.class)
public abstract class FlowerBlockMixin extends VegetationBlock implements BonemealableBlock {
    protected FlowerBlockMixin(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return state.is(BlockTags.SMALL_FLOWERS)
                && (Flourish.OPTIONS.get().witherRose || state.getBlock() != Blocks.WITHER_ROSE)
                && (Flourish.OPTIONS.get().torchflower || state.getBlock() != Blocks.TORCHFLOWER);
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        if (Flourish.OPTIONS.get().useTallFlowerBehavior) {
            flourish$tallFlowerBehavior(level, pos);
        } else {
            flourish$bedrockEditionBehavior(level, random, pos);
        }
    }

    @Override
    public @NotNull Type getType() {
        if (Flourish.OPTIONS.get().useTallFlowerBehavior) {
            return Type.GROWER;
        } else {
            return Type.NEIGHBOR_SPREADER;
        }
    }

    @Unique
    private void flourish$tallFlowerBehavior(ServerLevel level, BlockPos pos) {
        Block.popResource(level, pos, new ItemStack(this, 1));
    }

    @Unique
    private void flourish$bedrockEditionBehavior(ServerLevel level, RandomSource random, BlockPos pos) {
        final int maxSuccesses = random.nextIntBetweenInclusive(1, 7);
        int successCounter = 0;
        for (int i = 0; i < 64 && successCounter < maxSuccesses; i++) {
            BlockPos newPos = pos;
            for (int j = 0; j < i / 22 + 1; j++) {
                newPos = newPos.offset(
                        random.nextIntBetweenInclusive(-1, 1),
                        0,
                        random.nextIntBetweenInclusive(-1, 1)
                );
            }
            newPos = newPos.offset(0, random.nextIntBetweenInclusive(-1, 1), 0);
            final BlockPos below = newPos.below();
            if (this.mayPlaceOn(level.getBlockState(below), level, below)) {
                if (level.getBlockState(newPos).isAir()) {
                    level.setBlock(newPos, this.defaultBlockState(), 1 | 2);
                    successCounter++;
                }
            }
        }
    }
}
