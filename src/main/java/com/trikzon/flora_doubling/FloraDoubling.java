package com.trikzon.flora_doubling;

import com.trikzon.flora_doubling.mixin.DispenserBlockAccessor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.Tag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Map;
import java.util.Random;

public class FloraDoubling implements ModInitializer {
    public static final String MOD_ID = "flora-doubling";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static final File MOD_CONFIG_FILE = new File("./config/" + MOD_ID + ".json");
    public static final Tag<Block> DOUBLING_FLORA_TAG = TagRegistry.block(new Identifier(MOD_ID, "doubling_flora"));

    public static Config.ConfigBean CONFIG = new Config.ConfigBean();

    @Override
    public void onInitialize() {
        if (MOD_CONFIG_FILE.exists()) {
            Config.ConfigBean config = Config.read();
            if (config != null) {
                CONFIG = config;
            }
        } else {
            Config.write(CONFIG, true);
        }

        UseBlockCallback.EVENT.register(this::onUseBlock);
        registerDispenserBehaviors();
    }

    private ActionResult onUseBlock(PlayerEntity player, World world, Hand hand, BlockHitResult result) {
        boolean success = grow(player.getStackInHand(hand), world, result.getBlockPos(), true);
        if (!success) return ActionResult.PASS;

        if (!player.isCreative()) {
            player.getStackInHand(hand).decrement(1);
        }
        return ActionResult.SUCCESS;
    }

    private void registerDispenserBehaviors() {
        if (!CONFIG.dispenser) return;

        Map<Item, DispenserBehavior> behaviors = ((DispenserBlockAccessor) Blocks.DISPENSER).getBEHAVIORS();

        // Overwriting the behavior linked to Items.BONE_MEAL
        DispenserBehavior behavior = behaviors.get(Items.BONE_MEAL);
        DispenserBlock.registerBehavior(Items.BONE_MEAL, ((pointer, stack) -> {
            ItemStack preResult = behavior.dispense(pointer, stack);
            if (!preResult.equals(stack) || stack.getCount() != preResult.getCount()) {
                return preResult;
            }

            BlockPos pos = pointer.getBlockPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
            Block block = pointer.getWorld().getBlockState(pos).getBlock();
            if (DOUBLING_FLORA_TAG.contains(block) || CONFIG.doublingFlora.contains(getId(block.asItem()))) {
                boolean success = grow(stack, pointer.getWorld(), pos, false);
                if (success) {
                    stack.decrement(1);
                }
            }
            return stack;
        }));
    }

    private boolean grow(ItemStack stack, World world, BlockPos pos, boolean particlesOnClient) {
        if (!(stack.getItem() instanceof BoneMealItem)) return false;
        Block block = world.getBlockState(pos).getBlock();
        if (!(DOUBLING_FLORA_TAG.contains(block) || CONFIG.doublingFlora.contains(getId(block.asItem()))))
            return false;

        if (world.isClient && particlesOnClient) {
            BoneMealItem.createParticles(world, pos, world.random.nextInt(14));
        } else if (!world.isClient && !particlesOnClient) {
            createParticles((ServerWorld)world, pos, world.random.nextInt(14));
        }
        Block.dropStack(world, pos, new ItemStack(block, 1));

        return true;
    }

    public static String getId(Item item) {
        return Registry.ITEM.getId(item).toString();
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
                    (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D,
                    1, 0.0D, 0.0D, 0.0D, 1.0
            );

            for (int i = 0; i < count; ++i) {
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
}
