package com.trikzon.flora_doubling.dispenser;

import com.trikzon.flora_doubling.FloraDoubling;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Random;

public class BoneMealDispenserBehavior implements DispenserBehavior {

    private static class Default extends ItemDispenserBehavior {
    }

    private static class Modified extends ItemDispenserBehavior {
        @Override
        protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
            BlockPos pos = pointer.getBlockPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
            World world = pointer.getWorld();
            Block block = world.getBlockState(pos).getBlock();

            if (FloraDoubling.isTargetFlower(block) && !world.isClient) {
                Block.dropStack(world, pos, new ItemStack(block, 1));
                createParticles((ServerWorld) world, pos, world.random.nextInt(14));
            }
            return stack;
        }

        // Copied from BoneMealItem but edited to create the particles from the server instead of the client.
        // This is so that dispensers will emit particles when growing flowers.
        private void createParticles(ServerWorld world, BlockPos pos, int count) {
            Random random = world.random;
            BlockState blockState = world.getBlockState(pos);

            if (!blockState.isAir()) {
                if (count == 0) count = 15;
                double d = 0.5D;
                double g;
                if (blockState.isOf(Blocks.WATER)) {
                    count *= 3;
                    g = 1.0D;
                    d = 3.0D;
                } else if (blockState.isOpaqueFullCube(world, pos)) {
                    pos = pos.up();
                    count *= 4;
                    d = 3.0D;
                    g = 1.0D;
                } else {
                    g = blockState.getOutlineShape(world, pos).getMax(Direction.Axis.Y);
                }
                world.spawnParticles(
                        ParticleTypes.HAPPY_VILLAGER,
                        (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D,
                        1, 0.0D, 0.0D, 0.0D, 1.0
                );

                for (int i = 0; i < count; ++i) {
                    double h = random.nextGaussian() * 0.02D;
                    double j = random.nextGaussian() * 0.02D;
                    double k = random.nextGaussian() * 0.02D;
                    double l = 0.5D - d;
                    double m = (double) pos.getX() + l + random.nextDouble() * d * 2.0D;
                    double n = (double) pos.getY() + random.nextDouble() * g;
                    double o = (double) pos.getZ() + l + random.nextDouble() * d * 2.0D;
                    if (!world.getBlockState((new BlockPos(m, n, o)).down()).isAir()) {
                        world.spawnParticles(ParticleTypes.HAPPY_VILLAGER, m, n, o, 1, h, j, k, 1.0);
                    }
                }
            }
        }
    }

    @Override
    public ItemStack dispense(BlockPointer pointer, ItemStack stack) {
        if (FloraDoubling.CONFIG.dispenser) {
            return new Modified().dispense(pointer, stack);
        } else {
            return new Default().dispense(pointer, stack);
        }
    }
}
