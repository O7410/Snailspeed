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
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.numericalk.snailspeed.blocks.entity.SnailBlockEntities;
import net.numericalk.snailspeed.blocks.entity.custom.BrickOvenBlockEntity;
import net.numericalk.snailspeed.datagen.SnailItemTagsProvider;
import net.numericalk.snailspeed.items.SnailItems;
import org.jetbrains.annotations.Nullable;

public class BrickOvenBlock extends BlockWithEntity implements BlockEntityProvider {

    public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;
    public static final IntProperty LIT = IntProperty.of("lit", 0, 3);
    private static final MapCodec<BrickOvenBlock> CODEC = BrickOvenBlock.createCodec(BrickOvenBlock::new);

    public BrickOvenBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BrickOvenBlockEntity(pos, state);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if(state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if(blockEntity instanceof BrickOvenBlockEntity) {
                ItemScatterer.spawn(world, pos, ((BrickOvenBlockEntity) blockEntity));
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        Vec3d hitPos = hit.getPos();
        double relativeY = hitPos.y - pos.getY();

        if (world.getBlockEntity(pos) instanceof BrickOvenBlockEntity brickOvenBlockEntity) {
            if (relativeY > 0.5) {
                if (canPutItem(stack)) {
                    for (int i = 0; i < 5; i++) {
                        if (brickOvenBlockEntity.getStack(i).isEmpty()) {
                            world.updateListeners(pos, state, state, 0);
                            brickOvenBlockEntity.setStack(i, stack.copyWithCount(1));
                            if (!player.isCreative()) {
                                stack.decrement(1);
                            }
                            world.playSound(player, pos, SoundEvents.ENTITY_ITEM_FRAME_ADD_ITEM, SoundCategory.BLOCKS, 1f, 1f);
                            return ActionResult.SUCCESS;
                        }
                    }
                }
                if (canTakeItem(stack)) {
                    for (int i = 0; i < 5; i++) {
                        if (!brickOvenBlockEntity.getStack(i).isEmpty()) {
                            world.updateListeners(pos, state, state, 0);
                            world.playSound(player, pos, SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM, SoundCategory.BLOCKS, 1f, 1f);
                            player.giveOrDropStack(brickOvenBlockEntity.getStack(i));
                            brickOvenBlockEntity.setStack(i, SnailItems.AIR.getDefaultStack());
                            return ActionResult.SUCCESS;
                        }
                    }
                }
            } else {
                if (brickOvenBlockEntity.getStack(5).isEmpty() && isFuel(stack)) {
                    world.updateListeners(pos, state, state, 0);
                    brickOvenBlockEntity.setStack(5, stack.copyWithCount(1));
                    if (!player.isCreative()) {
                        stack.decrement(1);
                    }
                    world.playSound(player, pos, SoundEvents.ENTITY_ITEM_FRAME_ADD_ITEM, SoundCategory.BLOCKS, 1f, 1f);
                    return ActionResult.SUCCESS;
                }
                if (canLitOvenWith(Items.FLINT_AND_STEEL, stack, state)) {
                    litOvenWith(SoundEvents.ITEM_FLINTANDSTEEL_USE, stack, player, state, world, pos);
                    return ActionResult.SUCCESS;
                } else if (canLitOvenWith(Items.FIRE_CHARGE, stack, state) ||
                            canLitOvenWith(SnailItems.BURNING_TINDER, stack, state)) {
                    litOvenWith(SoundEvents.ITEM_FIRECHARGE_USE, stack, player, state, world, pos);
                    return ActionResult.SUCCESS;
                } else if (canLitBlueFire(SnailItems.SOUL, stack, state, world, pos)) {
                    litBlueFire(stack, player, state, world, pos);
                    return ActionResult.SUCCESS;
                }

                if (canFeedFire(stack, state, player, pos, world)) {
                    feedFire(world, pos, player, stack);
                    return ActionResult.SUCCESS;
                }
            }
        }
        return ActionResult.PASS;
    }

    private void litBlueFire(ItemStack stack, PlayerEntity player, BlockState state, World world, BlockPos pos) {
        world.setBlockState(pos, state.with(LIT, 3));
        if (stack.isDamageable() && !player.isCreative()) {
            stack.damage(1, player);
        } else if (!player.isCreative()) {
            stack.decrement(1);
        }
        world.playSound(player, pos, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 1f, 1f);
    }

    private boolean canLitBlueFire(Item soul, ItemStack stack, BlockState state, World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof BrickOvenBlockEntity brickOvenBlockEntity) {
            return stack.isOf(soul) && state.get(LIT) == 2 && (brickOvenBlockEntity.getFireTime() >= 20 * 60 * 3);
        }
        return false;
    }

    private boolean canPutItem(ItemStack stack) {
        return !stack.isEmpty();
    }

    private boolean canTakeItem(ItemStack stack) {
        return stack.isEmpty();
    }

    private void litOvenWith(SoundEvent soundEvent, ItemStack stack, PlayerEntity player, BlockState state, World world, BlockPos pos) {
        world.setBlockState(pos, state.with(LIT, 2));
        if (stack.isDamageable() && !player.isCreative()) {
            stack.damage(1, player);
        } else if (!player.isCreative()) {
            stack.decrement(1);
        }
        world.playSound(player, pos, soundEvent, SoundCategory.BLOCKS, 1f, 1f);
    }

    private boolean canLitOvenWith(Item item, ItemStack stack, BlockState state) {
        return stack.isOf(item) && state.get(LIT) == 1;
    }

    private void feedFire(World world, BlockPos pos, PlayerEntity player, ItemStack stack) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof BrickOvenBlockEntity brickOvenBlockEntity) {
            if (stack.isIn(SnailItemTagsProvider.CAMPFIRE_FUEL)) {
                brickOvenBlockEntity.calculatedAddedFireTime(SnailItemTagsProvider.CAMPFIRE_FUEL);
            } else if (stack.isIn(SnailItemTagsProvider.OVEN_FUEL)) {
                brickOvenBlockEntity.calculatedAddedFireTime(SnailItemTagsProvider.OVEN_FUEL);
            }
            if (!player.isCreative()) {
                stack.decrement(1);
            }
        }
    }
    private boolean canFeedFire(ItemStack stack, BlockState state, PlayerEntity player, BlockPos pos, World world) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof BrickOvenBlockEntity brickOvenBlockEntity)) return false;

        return (stack.isIn(SnailItemTagsProvider.CAMPFIRE_FUEL) || stack.isIn(SnailItemTagsProvider.OVEN_FUEL))
                && state.get(LIT) >= 2
                && !player.isSneaking()
                && brickOvenBlockEntity.getFireTime() < brickOvenBlockEntity.getMaxFireTime();
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, SnailBlockEntities.BRICK_OVEN,
                (world1, pos, state1, blockEntity) ->
                        blockEntity.tick(world1, pos, state1));
    }

    private boolean isFuel(ItemStack stack) {
        return stack.isIn(SnailItemTagsProvider.CAMPFIRE_FUEL) || stack.isIn(SnailItemTagsProvider.OVEN_FUEL);
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState()
                .with(FACING, ctx.getHorizontalPlayerFacing().getOpposite())
                .with(LIT, 0);
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }
    public static int getLuminance(BlockState state) {
        return switch (state.get(LIT)) {
            case 2 -> 15;
            case 3 -> 10;
            default -> 0;
        };
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT);
    }
}
