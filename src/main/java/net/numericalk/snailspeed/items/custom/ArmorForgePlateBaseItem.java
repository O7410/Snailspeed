package net.numericalk.snailspeed.items.custom;

import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.numericalk.snailspeed.blocks.SnailBlocks;

public class ArmorForgePlateBaseItem extends Item {
    public ArmorForgePlateBaseItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity player = context.getPlayer();
        ItemStack stack = context.getPlayer().getMainHandStack();
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = context.getWorld().getBlockState(pos);
        if (state.isIn(ConventionalBlockTags.STRIPPED_LOGS)) {
            world.setBlockState(pos, SnailBlocks.ARMOR_FORGE.getDefaultState());
            stack.decrement(1);
            world.playSound(player, pos, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 1f, 1f);

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }
}
