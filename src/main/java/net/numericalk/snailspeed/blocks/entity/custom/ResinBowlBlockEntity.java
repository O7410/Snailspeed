package net.numericalk.snailspeed.blocks.entity.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.numericalk.snailspeed.blocks.SnailBlocks;
import net.numericalk.snailspeed.blocks.custom.ResinBowlBlock;
import net.numericalk.snailspeed.blocks.entity.SnailBlockEntities;
import net.numericalk.snailspeed.datagen.SnailBlockTagsProvider;
import org.jetbrains.annotations.Nullable;

public class ResinBowlBlockEntity extends BlockEntity {
    private static final int MAX_PROGRESS = 20 * 60 * 5;
    private int progress = 0;

    public ResinBowlBlockEntity(BlockPos pos, BlockState state) {
        super(SnailBlockEntities.RESIN_BOWL, pos, state);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (hasTappedLog(world, state, pos)) {
            increaseProgress();
            if (hasProgressComplete()) {
                world.setBlockState(pos, state.with(ResinBowlBlock.HAS_RESIN, true));
                decayLog(world, pos, state);
                resetProgress();
            }
        } else {
            resetProgress();
        }
    }

    private static final Block[][] DECAYING_LOG = {
            {SnailBlocks.TAPPED_SPRUCE_LOG, SnailBlocks.DECAYED_SPRUCE_LOG},
            {SnailBlocks.TAPPED_BIRCH_LOG, SnailBlocks.DECAYED_BIRCH_LOG},
            {SnailBlocks.TAPPED_PALE_OAK_LOG, SnailBlocks.DECAYED_PALE_OAK_LOG}
    };

    private void decayLog(World world, BlockPos pos, BlockState state) {
        for (Block[] block : DECAYING_LOG) {
            System.out.println("Decaying Log");
            Direction facing = state.get(ResinBowlBlock.FACING);
            BlockPos oppositeOffset = pos.offset(facing.getOpposite());
            if (world.getBlockState(oppositeOffset).isOf(block[0])) {
                world.setBlockState(oppositeOffset, block[1].getStateWithProperties(state));
                System.out.println("Log Decayed");
            }
        }
    }

    private boolean hasProgressComplete() {
        return progress >= MAX_PROGRESS;
    }

    private void resetProgress() {
        this.progress = 0;
    }

    private void increaseProgress() {
        progress++;
    }

    private boolean hasTappedLog(World world, BlockState state, BlockPos pos) {
        Direction facing = state.get(ResinBowlBlock.FACING);
        BlockPos oppositeOffset = pos.offset(facing.getOpposite());
        return world.getBlockState(oppositeOffset).isIn(SnailBlockTagsProvider.TAPPED_LOGS);
    }
}
