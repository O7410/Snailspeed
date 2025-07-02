package net.numericalk.snailspeed.utils;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.numericalk.snailspeed.items.SnailItems;

public class SnailLogStripping {
    public static void logStripping() {
        WoodBlocksToBarkEntry[] barkDrops = {
                new WoodBlocksToBarkEntry(Blocks.OAK_LOG, Blocks.STRIPPED_OAK_LOG, Blocks.OAK_WOOD, Blocks.STRIPPED_OAK_WOOD, SnailItems.OAK_LOG_BARK),
                new WoodBlocksToBarkEntry(Blocks.SPRUCE_LOG, Blocks.STRIPPED_SPRUCE_LOG, Blocks.SPRUCE_WOOD, Blocks.STRIPPED_SPRUCE_WOOD, SnailItems.SPRUCE_LOG_BARK),
                new WoodBlocksToBarkEntry(Blocks.BIRCH_LOG, Blocks.STRIPPED_BIRCH_LOG, Blocks.BIRCH_WOOD, Blocks.STRIPPED_BIRCH_WOOD, SnailItems.BIRCH_LOG_BARK),
                new WoodBlocksToBarkEntry(Blocks.JUNGLE_LOG, Blocks.STRIPPED_JUNGLE_LOG, Blocks.JUNGLE_WOOD, Blocks.STRIPPED_JUNGLE_WOOD, SnailItems.JUNGLE_LOG_BARK),
                new WoodBlocksToBarkEntry(Blocks.ACACIA_LOG, Blocks.STRIPPED_ACACIA_LOG, Blocks.ACACIA_WOOD, Blocks.STRIPPED_ACACIA_WOOD, SnailItems.ACACIA_LOG_BARK),
                new WoodBlocksToBarkEntry(Blocks.DARK_OAK_LOG, Blocks.STRIPPED_DARK_OAK_LOG, Blocks.DARK_OAK_WOOD, Blocks.STRIPPED_DARK_OAK_WOOD, SnailItems.DARK_OAK_LOG_BARK),
                new WoodBlocksToBarkEntry(Blocks.MANGROVE_LOG, Blocks.STRIPPED_MANGROVE_LOG, Blocks.MANGROVE_WOOD, Blocks.STRIPPED_MANGROVE_WOOD, SnailItems.MANGROVE_LOG_BARK),
                new WoodBlocksToBarkEntry(Blocks.CHERRY_LOG, Blocks.STRIPPED_CHERRY_LOG, Blocks.CHERRY_WOOD, Blocks.STRIPPED_CHERRY_WOOD, SnailItems.CHERRY_LOG_BARK),
                new WoodBlocksToBarkEntry(Blocks.PALE_OAK_LOG, Blocks.STRIPPED_PALE_OAK_LOG, Blocks.PALE_OAK_WOOD, Blocks.STRIPPED_PALE_OAK_WOOD, SnailItems.PALE_OAK_LOG_BARK),
                new WoodBlocksToBarkEntry(Blocks.CRIMSON_STEM, Blocks.STRIPPED_CRIMSON_STEM, Blocks.CRIMSON_HYPHAE, Blocks.STRIPPED_CRIMSON_HYPHAE, SnailItems.CRIMSON_STEM_BARK),
                new WoodBlocksToBarkEntry(Blocks.WARPED_STEM, Blocks.STRIPPED_WARPED_STEM, Blocks.WARPED_HYPHAE, Blocks.STRIPPED_WARPED_HYPHAE, SnailItems.WARPED_STEM_BARK)
        };

        UseBlockCallback.EVENT.register((playerEntity, world, hand, blockHitResult) -> {
            BlockPos pos = blockHitResult.getBlockPos();
            BlockState state = world.getBlockState(pos);
            ItemStack stack = playerEntity.getStackInHand(hand);

            for (WoodBlocksToBarkEntry entry : barkDrops) {
                Block barkedLog = entry.barkedLog;
                Block strippedLog = entry.strippedLog;
                Block barkedWood = entry.barkedWood;
                Block strippedWood = entry.strippedWood;
                Item barkDrop = entry.barkDrop;
                if (!stack.isIn(ItemTags.AXES)) continue;
                if (state.isOf(barkedLog)) {
                    if (world instanceof ServerWorld serverWorld) {
                        ItemStack drop = new ItemStack(barkDrop, 1);
                        Block.dropStack(serverWorld, pos, drop);
                        world.setBlockState(pos, strippedLog.getStateWithProperties(state));
                        stack.damage(1, playerEntity, playerEntity.getPreferredEquipmentSlot(stack));
                    }
                    world.playSound(null, pos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1.0f, 1.0f);

                    return ActionResult.SUCCESS;
                }
                if (state.isOf(barkedWood)) {
                    if (world instanceof ServerWorld serverWorld) {
                        ItemStack drop = new ItemStack(barkDrop, 2);
                        Block.dropStack(serverWorld, pos, drop);
                        world.setBlockState(pos, strippedWood.getStateWithProperties(state));
                        stack.damage(1, playerEntity, playerEntity.getPreferredEquipmentSlot(stack));
                    }
                    world.playSound(null, pos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1.0f, 1.0f);

                    return ActionResult.SUCCESS;
                }
                return ActionResult.PASS;
            }
            return ActionResult.PASS;
        });
    }

    private record WoodBlocksToBarkEntry(Block barkedLog, Block strippedLog, Block barkedWood, Block strippedWood, Item barkDrop) {}
}
