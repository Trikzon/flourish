package com.trikzon.flora_doubling.dispenser;

import com.trikzon.flora_doubling.FloraDoubling;
import net.minecraft.block.Block;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;

public class BoneMealDispenserBehavior implements DispenserBehavior {

    private static class Default extends ItemDispenserBehavior {}

    private static class Modified extends ItemDispenserBehavior {
        @Override
        protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
            BlockPos pos = pointer.getBlockPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
            Block block = pointer.getWorld().getBlockState(pos).getBlock();

            if (FloraDoubling.DOUBLING_FLORA_TAG.contains(block) || FloraDoubling.CONFIG.doublingFlora.contains(FloraDoubling.getId(block.asItem()))) {
                Block.dropStack(pointer.getWorld(), pos, new ItemStack(block, 1));
            }
            return stack;
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
