package com.diontryban.mods.flourish.mixin;

import com.diontryban.mods.flourish.Flourish;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FlowerBlock.class)
public abstract class FlowerBlockMixin extends BushBlock implements BonemealableBlock {
    protected FlowerBlockMixin(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean isClient) {
        return state.is(BlockTags.SMALL_FLOWERS) && (Flourish.config.witherRose || state.getBlock() != Blocks.WITHER_ROSE);
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        if (Flourish.config.useTallFlowerBehavior) {
            tallFlowerBehavior(level, pos);
        } else {
            bedrockEditionBehavior(level, random, pos);
        }
    }

    private void tallFlowerBehavior(ServerLevel level, BlockPos pos) {
        Block.popResource(level, pos, new ItemStack(this, 1));
    }

    private void bedrockEditionBehavior(ServerLevel level, RandomSource random, BlockPos pos) {
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
                    addGrowthParticles(level, newPos, random.nextInt(14));
                    successCounter++;
                }
            }
        }
    }

    // Copied from BoneMealItem and modified to work from the server side.
    private static void addGrowthParticles(ServerLevel pLevel, BlockPos pPos, int pData) {
        if (pData == 0) {
            pData = 15;
        }

        BlockState blockstate = pLevel.getBlockState(pPos);
        double d0 = 0.5D;
        double d1;
        if (blockstate.is(Blocks.WATER)) {
            pData *= 3;
            d1 = 1.0D;
            d0 = 3.0D;
        } else if (blockstate.isSolidRender(pLevel, pPos)) {
            pPos = pPos.above();
            pData *= 3;
            d0 = 3.0D;
            d1 = 1.0D;
        } else {
            d1 = blockstate.getShape(pLevel, pPos).max(Direction.Axis.Y);
        }

        pLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, (double)pPos.getX() + 0.5D, (double)pPos.getY() + 0.5D, (double)pPos.getZ() + 0.5D, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        RandomSource randomsource = pLevel.getRandom();

        for(int i = 0; i < pData; ++i) {
            double d2 = randomsource.nextGaussian() * 0.02D;
            double d3 = randomsource.nextGaussian() * 0.02D;
            double d4 = randomsource.nextGaussian() * 0.02D;
            double d9 = randomsource.nextGaussian() * 0.02D;
            double d5 = 0.5D - d0;
            double d6 = (double)pPos.getX() + d5 + randomsource.nextDouble() * d0 * 2.0D;
            double d7 = (double)pPos.getY() + randomsource.nextDouble() * d1;
            double d8 = (double)pPos.getZ() + d5 + randomsource.nextDouble() * d0 * 2.0D;
            if (!pLevel.getBlockState((new BlockPos(d6, d7, d8)).below()).isAir()) {
                pLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, d6, d7, d8, 1, d2, d3, d4, d9);
            }
        }
    }
}
