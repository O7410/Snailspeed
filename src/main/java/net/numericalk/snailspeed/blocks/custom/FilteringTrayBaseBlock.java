package net.numericalk.snailspeed.blocks.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
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
import net.numericalk.snailspeed.blocks.SnailBlocks;
import net.numericalk.snailspeed.datagen.SnailItemTagsProvider;

import java.util.stream.Stream;

public class FilteringTrayBaseBlock extends Block {
    public static final IntProperty STAGES = IntProperty.of("stages", 0, 4);

    public static final VoxelShape SHAPE_BASE = Stream.of(
            Block.createCuboidShape(14, 0, 14, 16, 16, 16),
            Block.createCuboidShape(0, 0, 14, 2, 16, 16),
            Block.createCuboidShape(0, 0, 0, 2, 16, 2),
            Block.createCuboidShape(14, 0, 0, 16, 16, 2)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();
    public static final VoxelShape SHAPE_1 = Stream.of(
            Block.createCuboidShape(14, 0, 14, 16, 16, 16),
            Block.createCuboidShape(0, 0, 14, 2, 16, 16),
            Block.createCuboidShape(0, 0, 0, 2, 16, 2),
            Block.createCuboidShape(14, 0, 0, 16, 16, 2),
            Block.createCuboidShape(2, 14, 14, 14, 16, 16)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();
    public static final VoxelShape SHAPE_2 = Stream.of(
            Block.createCuboidShape(14, 0, 14, 16, 16, 16),
            Block.createCuboidShape(0, 0, 14, 2, 16, 16),
            Block.createCuboidShape(0, 0, 0, 2, 16, 2),
            Block.createCuboidShape(14, 0, 0, 16, 16, 2),
            Block.createCuboidShape(14, 14, 2, 16, 16, 14),
            Block.createCuboidShape(2, 14, 14, 14, 16, 16)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();
    public static final VoxelShape SHAPE_3 =Stream.of(
            Block.createCuboidShape(14, 0, 14, 16, 16, 16),
            Block.createCuboidShape(0, 0, 14, 2, 16, 16),
            Block.createCuboidShape(0, 0, 0, 2, 16, 2),
            Block.createCuboidShape(14, 0, 0, 16, 16, 2),
            Block.createCuboidShape(0, 14, 2, 2, 16, 14),
            Block.createCuboidShape(14, 14, 2, 16, 16, 14),
            Block.createCuboidShape(2, 14, 14, 14, 16, 16)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();
    public static final VoxelShape SHAPE_FULL = Stream.of(
            Block.createCuboidShape(14, 0, 14, 16, 16, 16),
            Block.createCuboidShape(0, 0, 14, 2, 16, 16),
            Block.createCuboidShape(0, 0, 0, 2, 16, 2),
            Block.createCuboidShape(14, 0, 0, 16, 16, 2),
            Block.createCuboidShape(0, 14, 2, 2, 16, 14),
            Block.createCuboidShape(14, 14, 2, 16, 16, 14),
            Block.createCuboidShape(2, 14, 14, 14, 16, 16),
            Block.createCuboidShape(2, 14, 0, 14, 16, 2)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

    public FilteringTrayBaseBlock(Settings settings) {
        super(settings);
    }

    private void addDrop(ItemStack item, int count, World world, BlockPos pos) {
        for (int i = 0; i < count; i++) {
            ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), item);
        }
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(STAGES)) {
            case 0 -> SHAPE_BASE;
            case 1 -> SHAPE_1;
            case 2 -> SHAPE_2;
            case 3 -> SHAPE_3;
            default -> SHAPE_FULL;
        };
    }

    @Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (stack.isOf(Items.STICK) && state.get(STAGES) < 4) {
            if (!player.isCreative()) {
                stack.decrement(1);
            }
            world.setBlockState(pos, state.cycle(STAGES));
            return ActionResult.SUCCESS;
        }
        if (stack.isIn(SnailItemTagsProvider.STRING) && state.get(STAGES) == 4) {
            if (!player.isCreative()) {
                stack.decrement(1);
            }
            world.setBlockState(pos, SnailBlocks.FILTERING_TRAY.getDefaultState().with(FilteringTrayBlock.HAS_FILTER, false));
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(STAGES);
    }
}
