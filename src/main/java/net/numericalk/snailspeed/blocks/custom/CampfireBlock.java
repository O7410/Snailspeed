package net.numericalk.snailspeed.blocks.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.block.WireOrientation;
import net.numericalk.snailspeed.blocks.entity.SnailBlockEntities;
import net.numericalk.snailspeed.blocks.entity.custom.CampfireBlockEntity;
import net.numericalk.snailspeed.datagen.SnailItemTagsProvider;
import net.numericalk.snailspeed.items.SnailItems;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class CampfireBlock extends BlockWithEntity implements BlockEntityProvider {

    public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;

    public static final int STAGES_BASE = 1;
    public static final int STAGES_ONE_STICK = 2;
    public static final int STAGES_TWO_STICKS = 3;
    public static final int STAGES_THREE_STICKS = 4;
    public static final int STAGES_FULL_STICK = 5;
    public static final int STAGES_BURNT = 6;
    public static final IntProperty STAGES = IntProperty.of("stages", 1, 6);

    public static final int LIT_UNLIT = 1;
    public static final int LIT_SMALL = 2;
    public static final int LIT_MEDIUM = 3;
    public static final int LIT_LARGE = 4;
    public static final IntProperty LIT = IntProperty.of("lit", 1, 4);

    public static final int COOKING_NO_SUPPORT = 1;
    public static final int COOKING_ONE_SUPPORT = 2;
    public static final int COOKING_TWO_SUPPORTS = 3;
    public static final int COOKING_FULL_SUPPORT = 4;
    public static final IntProperty COOKING = IntProperty.of("cooking", 1, 4);

    private static final VoxelShape SHAPE_BASE = Stream.of(
            Block.createCuboidShape(6, 0, 3, 10, 1, 13),
            Block.createCuboidShape(10, 0, 4, 12, 1, 12),
            Block.createCuboidShape(3, 0, 6, 4, 1, 10),
            Block.createCuboidShape(4, 0, 4, 6, 1, 12),
            Block.createCuboidShape(12, 0, 6, 13, 1, 10)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();
    private static final VoxelShape SHAPE_FULL =
            Block.createCuboidShape(2, 0, 2, 14, 11, 14);
    private static final VoxelShape SHAPE_FULL_1_X = Stream.of(
            Block.createCuboidShape(7, 0, 0, 9, 16, 1),
            Block.createCuboidShape(2, 0, 2, 14, 11, 14)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();
    private static final VoxelShape SHAPE_FULL_2_X = Stream.of(
            Block.createCuboidShape(7, 0, 0, 9, 16, 1),
            Block.createCuboidShape(7, 0, 15, 9, 16, 16),
            Block.createCuboidShape(2, 0, 2, 14, 11, 14)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();
    private static final VoxelShape SHAPE_FULL_3_X = Stream.of(
            Block.createCuboidShape(7, 0, 0, 9, 16, 1),
            Block.createCuboidShape(7, 0, 15, 9, 16, 16),
            Block.createCuboidShape(7, 15, 1, 9, 16, 15),
            Block.createCuboidShape(2, 0, 2, 14, 11, 14)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();
    private static final VoxelShape SHAPE_FULL_1_Z = Stream.of(
            Block.createCuboidShape(15, 0, 7, 16, 16, 9),
            Block.createCuboidShape(2, 0, 2, 14, 11, 14)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();
    private static final VoxelShape SHAPE_FULL_2_Z = Stream.of(
            Block.createCuboidShape(0, 0, 7, 1, 16, 9),
            Block.createCuboidShape(15, 0, 7, 16, 16, 9),
            Block.createCuboidShape(2, 0, 2, 14, 11, 14)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();
    private static final VoxelShape SHAPE_FULL_3_Z = Stream.of(
            Block.createCuboidShape(0, 0, 7, 1, 16, 9),
            Block.createCuboidShape(15, 0, 7, 16, 16, 9),
            Block.createCuboidShape(1, 15, 7, 15, 16, 9),
            Block.createCuboidShape(2, 0, 2, 14, 11, 14)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();
    private static final MapCodec<CampfireBlock> CODEC = CampfireBlock.createCodec(CampfireBlock::new);

    public CampfireBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    public static int getLuminance(BlockState state) {
        return switch (state.get(LIT)) {
            case LIT_UNLIT -> 0;
            case LIT_SMALL -> 7;
            case LIT_MEDIUM -> 11;
            case LIT_LARGE -> 15;
            default -> 7;
        };
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (state.get(STAGES) == 1) {
            return SHAPE_BASE;
        }
        return switch (state.get(COOKING)) {
            case COOKING_ONE_SUPPORT -> switch (state.get(FACING)) {
                case Direction.WEST, Direction.EAST -> SHAPE_FULL_1_X;
                default -> SHAPE_FULL_1_Z;
            };
            case COOKING_TWO_SUPPORTS -> switch (state.get(FACING)) {
                case Direction.WEST, Direction.EAST -> SHAPE_FULL_2_X;
                default -> SHAPE_FULL_2_Z;
            };
            case COOKING_FULL_SUPPORT -> switch (state.get(FACING)) {
                case Direction.WEST, Direction.EAST -> SHAPE_FULL_3_X;
                default -> SHAPE_FULL_3_Z;
            };
            default -> SHAPE_FULL;
        };
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CampfireBlockEntity(pos, state);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            if (world.getBlockEntity(pos) instanceof CampfireBlockEntity campfireBlockEntity) {
                ItemScatterer.spawn(world, pos, campfireBlockEntity);
                world.updateComparators(pos, this);
            }
            switch (state.get(STAGES)) {
                case STAGES_ONE_STICK -> dropItem(world, pos, Items.STICK, 0);
                case STAGES_TWO_STICKS -> dropItem(world, pos, Items.STICK, 1);
                case STAGES_THREE_STICKS -> dropItem(world, pos, Items.STICK, 2);
                case STAGES_FULL_STICK -> dropItem(world, pos, Items.STICK, 3);
                case STAGES_BURNT -> dropItem(world, pos, Items.CHARCOAL, 1);
                default -> dropItem(world, pos, SnailItems.AIR, 1);
            }
            switch (state.get(COOKING)) {
                case COOKING_ONE_SUPPORT -> dropItem(world, pos, Items.STICK, 1);
                case COOKING_TWO_SUPPORTS -> dropItem(world, pos, Items.STICK, 2);
                case COOKING_FULL_SUPPORT -> dropItem(world, pos, Items.STICK, 3);
                default -> dropItem(world, pos, SnailItems.AIR, 1);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    private void dropItem(World world, BlockPos pos, Item item, int count) {
        for (int i = 0; i <= count; i++) {
            ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), item.getDefaultStack());
        }
    }

    @Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (canPlaceStickFor(STAGES, STAGES_FULL_STICK, state, stack)) {
            placeStickFor(STAGES, stack, state, world, pos, player);
            return ActionResult.SUCCESS;
        } else if (canPlaceStickFor(COOKING, COOKING_FULL_SUPPORT, STAGES, STAGES_BURNT, state, stack)) {
            placeStickFor(COOKING, stack, state, world, pos, player);
            return ActionResult.SUCCESS;
        }

        if (canLitCampfireWith(Items.FLINT_AND_STEEL, stack, state, world, pos)) {
            litCampfireWith(SoundEvents.ITEM_FLINTANDSTEEL_USE, stack, player, state, world, pos);
            return ActionResult.SUCCESS;
        } else if (canLitCampfireWith(Items.FIRE_CHARGE, stack, state, world, pos)
                || canLitCampfireWith(SnailItems.BURNING_TINDER, stack, state, world, pos)) {
            litCampfireWith(SoundEvents.ITEM_FIRECHARGE_USE, stack, player, state, world, pos);
            return ActionResult.SUCCESS;
        }

        if (canFeedFire(stack, state, player, pos, world)) {
            feedFire(world, pos, player, stack);
            return ActionResult.SUCCESS;
        }

        if (world.getBlockEntity(pos) instanceof CampfireBlockEntity campfireBlockEntity) {
            if (canPutItem(stack, state)) {
                for (int i = 0; i < 3; i++) {
                    if (campfireBlockEntity.getStack(i).isEmpty()) {
                        campfireBlockEntity.setStack(i, stack.copyWithCount(1));
                        if (!player.isCreative()) {
                            stack.decrement(1);
                        }
                        world.playSound(player, pos, SoundEvents.ENTITY_ITEM_FRAME_ADD_ITEM, SoundCategory.BLOCKS, 1f, 1f);

                        world.updateListeners(pos, state, state, 0);
                        return ActionResult.SUCCESS;
                    }
                }
            }
            if (canTakeItem(stack, state)) {
                for (int i = 0; i < 3; i++) {
                    if (!campfireBlockEntity.getStack(i).isEmpty()) {
                        world.updateListeners(pos, state, state, 0);
                        world.playSound(player, pos, SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM, SoundCategory.BLOCKS, 1f, 1f);
                        player.giveOrDropStack(campfireBlockEntity.getStack(i));
                        campfireBlockEntity.setStack(i, SnailItems.AIR.getDefaultStack());
                        return ActionResult.SUCCESS;
                    }
                }
            }
        }
        return ActionResult.PASS;
    }

    public boolean isSkyVisible(World world, BlockPos pos) {
        int worldHeight = world.getHeight();

        for (int y = pos.getY() + 1; y < worldHeight; y++) {
            BlockPos abovePos = new BlockPos(pos.getX(), y, pos.getZ());
            if (!world.isAir(abovePos)) {
                return false;
            }
        }

        return true;
    }

    private boolean canTakeItem(ItemStack stack, BlockState state) {
        return stack.isEmpty() && state.get(COOKING) == COOKING_FULL_SUPPORT;
    }

    private boolean canPutItem(ItemStack stack, BlockState state) {
        return !stack.isEmpty() && state.get(COOKING) == COOKING_FULL_SUPPORT;
    }

    private void feedFire(World world, BlockPos pos, PlayerEntity player, ItemStack stack) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof CampfireBlockEntity campfireBlockEntity) {
            campfireBlockEntity.calculateAddedFireTime();
        }
        if (!player.isCreative()) {
            stack.decrement(1);
        }
    }

    private boolean canFeedFire(ItemStack stack, BlockState state, PlayerEntity player, BlockPos pos, World world) {
        if (!(world.getBlockEntity(pos) instanceof CampfireBlockEntity campfireBlockEntity)) return false;

        return stack.isIn(SnailItemTagsProvider.CAMPFIRE_FUEL)
                && state.get(LIT) >= LIT_SMALL
                && !player.isSneaking()
                && campfireBlockEntity.getFireDegradeTime() < campfireBlockEntity.getFireDegradeTimeLimit();
    }

    private void litCampfireWith(SoundEvent soundEvent, ItemStack stack, PlayerEntity player, BlockState state, World world, BlockPos pos) {
        world.setBlockState(pos, state.cycle(LIT));
        world.playSound(player, pos, soundEvent, SoundCategory.BLOCKS, 1f, 1f);
        if (stack.isDamageable() && !player.isCreative()) {
            stack.damage(1, player);
        } else if (!player.isCreative()) {
            stack.decrement(1);
        }
    }

    private boolean canLitCampfireWith(Item item, ItemStack stack, BlockState state, World world, BlockPos pos) {
        return stack.isOf(item) && state.get(LIT).equals(LIT_UNLIT) && rainFireHandler(world, pos) && state.get(STAGES) == STAGES_FULL_STICK;
    }

    private boolean rainFireHandler(World world, BlockPos pos) {
        if (!world.isRaining() && !world.isThundering()) {
            return true;
        } else if (!isSkyVisible(world, pos) && (world.isRaining() || world.isThundering())) {
            return true;
        }
        return false;
    }

    private void placeStickFor(IntProperty blockState, ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player) {
        world.setBlockState(pos, state.cycle(blockState));
        if (!player.isCreative()) {
            stack.decrement(1);
        }
    }

    private boolean canPlaceStickFor(IntProperty blockState, int maxBlockState, BlockState state, ItemStack stack) {
        if (stack.isOf(Items.STICK)) {
            return state.get(blockState) < maxBlockState;
        }
        return false;
    }

    private boolean canPlaceStickFor(IntProperty blockState, int maxBlockState, IntProperty blockState2, int maxBlockState2, BlockState state, ItemStack stack) {
        if (stack.isOf(Items.STICK)) {
            return state.get(blockState) < maxBlockState && state.get(blockState2) < maxBlockState2;
        }
        return false;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, SnailBlockEntities.CAMPFIRE,
                (world1, pos, state1, blockEntity) ->
                blockEntity.tick(world1, pos, state1));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(STAGES, LIT, COOKING, FACING);
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        super.neighborUpdate(state, world, pos, sourceBlock, wireOrientation, notify);
        checkSupport(state, world, pos);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        checkSupport(state, world, pos);
    }

    private void checkSupport(BlockState state, World world, BlockPos pos) {
        if (!world.getBlockState(pos.down()).isSideSolidFullSquare(world, pos.down(), net.minecraft.util.math.Direction.UP)) {
            world.breakBlock(pos, true);
        }
    }

    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return world.getBlockState(pos.down()).isSideSolidFullSquare(world, pos.down(), net.minecraft.util.math.Direction.UP);
    }
}
