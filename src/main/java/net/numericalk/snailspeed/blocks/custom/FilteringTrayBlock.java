package net.numericalk.snailspeed.blocks.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.numericalk.snailspeed.blocks.entity.SnailBlockEntities;
import net.numericalk.snailspeed.blocks.entity.custom.FilteringTrayBlockEntity;
import net.numericalk.snailspeed.datagen.SnailItemTagsProvider;
import net.numericalk.snailspeed.items.SnailItems;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class FilteringTrayBlock extends BlockWithEntity implements BlockEntityProvider {
    private static final MapCodec<FilteringTrayBlock> CODEC = FilteringTrayBlock.createCodec(FilteringTrayBlock::new);
    public static final VoxelShape SHAPE_UNFILTERED = Stream.of(
            Block.createCuboidShape(14, 0, 14, 16, 16, 16),
            Block.createCuboidShape(0, 0, 14, 2, 16, 16),
            Block.createCuboidShape(0, 0, 0, 2, 16, 2),
            Block.createCuboidShape(14, 0, 0, 16, 16, 2),
            Block.createCuboidShape(0, 14, 2, 2, 16, 14),
            Block.createCuboidShape(14, 14, 2, 16, 16, 14),
            Block.createCuboidShape(2, 14, 14, 14, 16, 16),
            Block.createCuboidShape(2, 14, 0, 14, 16, 2)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();
    public static final VoxelShape SHAPE_FILTERED = Stream.of(
            Block.createCuboidShape(14, 0, 14, 16, 16, 16),
            Block.createCuboidShape(0, 0, 14, 2, 16, 16),
            Block.createCuboidShape(0, 0, 0, 2, 16, 2),
            Block.createCuboidShape(14, 0, 0, 16, 16, 2),
            Block.createCuboidShape(0, 14, 2, 2, 16, 14),
            Block.createCuboidShape(14, 14, 2, 16, 16, 14),
            Block.createCuboidShape(2, 14, 14, 14, 16, 16),
            Block.createCuboidShape(2, 14, 0, 14, 16, 2),
            Block.createCuboidShape(2, 14.25, 2, 14, 14.25, 14)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();


    public static final BooleanProperty HAS_FILTER = BooleanProperty.of("has_filter");

    public FilteringTrayBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            if (world.getBlockEntity(pos) instanceof FilteringTrayBlockEntity blockEntity) {
                ItemScatterer.spawn(world, pos, blockEntity);
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!(world.getBlockEntity(pos) instanceof FilteringTrayBlockEntity filteringTrayBlockEntity)) return ActionResult.PASS;
        if (canInsertFilter(stack, state)) {
            applyFilter(player, world, pos, stack, state, filteringTrayBlockEntity);
            return ActionResult.SUCCESS;
        }
        if (canTakeFilter(stack, state, filteringTrayBlockEntity)) {
            takeFilter(player, world, pos, stack, state, filteringTrayBlockEntity);
            return ActionResult.SUCCESS;
        }
        if (canInsertItem(stack, state)) {
            for (int i = 0; i < 4; i++) {
                if (!filteringTrayBlockEntity.getStack(i).isEmpty()) continue;
                world.updateListeners(pos, state, state, 3);
                filteringTrayBlockEntity.setStack(i, stack.copyWithCount(1));
                if (!player.isCreative()) {
                    stack.decrement(1);
                }
                world.playSound(player, pos, SoundEvents.ENTITY_ITEM_FRAME_ADD_ITEM, SoundCategory.BLOCKS, 1f, 1f);
                return ActionResult.SUCCESS;
            }
            return ActionResult.SUCCESS;
        }
        if (canTakeItem(stack, state)) {
            for (int i = 3; i > -1; i--) {
                if (filteringTrayBlockEntity.getStack(i).isEmpty()) continue;
                world.updateListeners(pos, state, state,  3);
                world.playSound(player, pos, SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM, SoundCategory.BLOCKS, 1f, 1f);
                player.giveOrDropStack(filteringTrayBlockEntity.getStack(i));
                filteringTrayBlockEntity.setStack(i, SnailItems.AIR.getDefaultStack());
                return ActionResult.SUCCESS;
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    private boolean canTakeItem(ItemStack stack, BlockState state) {
        return stack.isEmpty();
    }

    private void takeFilter(PlayerEntity player, World world, BlockPos pos, ItemStack stack, BlockState state, FilteringTrayBlockEntity filteringTrayBlockEntity) {
        if (!filteringTrayBlockEntity.getStack(4).isEmpty()) {
            world.setBlockState(pos, state.with(HAS_FILTER, false));
            world.updateListeners(pos, state, state,  3);
            world.playSound(player, pos, SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM, SoundCategory.BLOCKS, 1f, 1f);
            player.giveOrDropStack(filteringTrayBlockEntity.getStack(4));
            filteringTrayBlockEntity.setStack(4, SnailItems.AIR.getDefaultStack());
        }
    }

    private boolean canTakeFilter(ItemStack stack, BlockState state, FilteringTrayBlockEntity filteringTrayBlockEntity) {
        return stack.isEmpty() && state.get(HAS_FILTER) &&
                (filteringTrayBlockEntity.getStack(0).isEmpty()) &&
                (filteringTrayBlockEntity.getStack(1).isEmpty()) &&
                (filteringTrayBlockEntity.getStack(2).isEmpty()) &&
                (filteringTrayBlockEntity.getStack(3).isEmpty());
    }

    private boolean canInsertItem(ItemStack stack, BlockState state) {
        return !stack.isEmpty() && state.get(HAS_FILTER);
    }

    private void applyFilter(PlayerEntity player, World world, BlockPos pos, ItemStack stack, BlockState state, FilteringTrayBlockEntity filteringTrayBlockEntity) {
        if (filteringTrayBlockEntity.getStack(4).isEmpty()) {
            world.updateListeners(pos, state, state, 3);
            filteringTrayBlockEntity.setStack(4, stack.copyWithCount(1));
            if (!player.isCreative()) {
                stack.decrement(1);
            }
            world.playSound(player, pos, SoundEvents.ENTITY_ITEM_FRAME_ADD_ITEM, SoundCategory.BLOCKS, 1f, 1f);
        }
    }

    private boolean canInsertFilter(ItemStack stack, BlockState state) {
        return stack.isIn(SnailItemTagsProvider.FILTERS) && !state.get(HAS_FILTER);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HAS_FILTER);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new FilteringTrayBlockEntity(pos, state);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (state.get(HAS_FILTER)) {
            return SHAPE_FILTERED;
        }
        return SHAPE_UNFILTERED;
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(HAS_FILTER, false);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, SnailBlockEntities.FILTERING_TRAY,
                (world1, pos, state1, blockEntity) ->
                        blockEntity.tick(world1, pos, state1));
    }
}
