package net.numericalk.snailspeed.items.custom;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.numericalk.snailspeed.blocks.SnailBlocks;

public class CircularSawItem extends Item {
    public CircularSawItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity player = context.getPlayer();
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);
        ItemStack stack = player.getMainHandStack();

        if (state.isIn(BlockTags.WOODEN_SLABS)) {
            world.setBlockState(pos, SnailBlocks.SAW_TABLE.getDefaultState());
            stack.decrement(1);
            world.playSound(player, pos, SoundEvents.BLOCK_GRINDSTONE_USE, SoundCategory.BLOCKS, 1f, 1f);
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }
}
