package net.numericalk.snailspeed.items.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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

import java.util.Map;

public class BarkSpudItem extends Item {
    private static final Map<Block, Block> LOG_TO_TAPPED_LOG = Map.ofEntries(
            Map.entry(Blocks.SPRUCE_LOG, SnailBlocks.TAPPED_SPRUCE_LOG),
            Map.entry(Blocks.BIRCH_LOG, SnailBlocks.TAPPED_BIRCH_LOG),
            Map.entry(Blocks.PALE_OAK_LOG, SnailBlocks.TAPPED_PALE_OAK_LOG)
    );

    //Bark Blud😂😂😂😂
    public BarkSpudItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos pos = context.getBlockPos();
        BlockState state = context.getWorld().getBlockState(pos);
        World world = context.getWorld();
        ItemStack stack = context.getStack();
        PlayerEntity player = context.getPlayer();
        Block tappedLog = LOG_TO_TAPPED_LOG.get(state.getBlock());
        if (tappedLog == null) return ActionResult.PASS;
        world.setBlockState(pos, tappedLog.getStateWithProperties(state));
        world.playSound(player, pos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1f, 1f);
        stack.damage(1, player);
        return ActionResult.SUCCESS;
    }
}
