package com.trikzon.flora_doubling.dispenser;

import com.trikzon.flora_doubling.FloraDoubling;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Random;

public class BoneMealDispenserBehavior implements DispenserBehavior {

    // Copied from DispenserBehavior#registerDefaults
    private static class Default extends FallibleItemDispenserBehavior {
        @Override
        protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
            this.success = true;
            World world = pointer.getWorld();
            BlockPos blockPos = pointer.getBlockPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
            if (!BoneMealItem.useOnFertilizable(stack, world, blockPos) && !BoneMealItem.useOnGround(stack, world, blockPos, (Direction)null)) {
                this.success = false;
            } else if (!world.isClient) {
                world.playLevelEvent(2005, blockPos, 0);
            }

            return stack;
        }
    }

    private static class Modified extends ItemDispenserBehavior {
        @Override
        protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
            BlockPos pos = pointer.getBlockPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
            World world = pointer.getWorld();
            Block block = world.getBlockState(pos).getBlock();

            Block.dropStack(world, pos, new ItemStack(block, 1));
            createParticles((ServerWorld) world, pos, world.random.nextInt(14));
            stack.decrement(1);
            return stack;
        }

        // Copied from BoneMealItem but edited to create the particles from the server instead of the client.
        // This is so that dispensers will emit particles when growing flowers.
        private void createParticles(ServerWorld world, BlockPos pos, int count) {
            if (count == 0) {
                count = 15;
            }

            Random random = world.random;
            BlockState blockState = world.getBlockState(pos);
            if (!blockState.isAir()) {
                for(int i = 0; i < count; ++i) {
                    double x = ((float)pos.getX() + random.nextFloat());
                    double y = (double)pos.getY() + (double)random.nextFloat() * blockState.getOutlineShape(world, pos).getMaximum(Direction.Axis.Y);
                    double z = ((float)pos.getZ() + random.nextFloat());
                    double deltaX = random.nextGaussian() * 0.02D;
                    double deltaY = random.nextGaussian() * 0.02D;
                    double deltaZ = random.nextGaussian() * 0.02D;
                    world.spawnParticles(ParticleTypes.HAPPY_VILLAGER, x, y, z, 1, deltaX, deltaY, deltaZ, 1.0);
                }
            }
        }
    }

    @Override
    public ItemStack dispense(BlockPointer pointer, ItemStack stack) {
        BlockPos pos = pointer.getBlockPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
        World world = pointer.getWorld();
        Block block = world.getBlockState(pos).getBlock();

        if (FloraDoubling.CONFIG.dispenser && FloraDoubling.isTargetFlower(block) && !world.isClient) {
            return new Modified().dispense(pointer, stack);
        } else {
            return new Default().dispense(pointer, stack);
        }
    }
}
