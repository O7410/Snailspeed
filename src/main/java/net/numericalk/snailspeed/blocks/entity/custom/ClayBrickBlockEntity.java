package net.numericalk.snailspeed.blocks.entity.custom;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.numericalk.snailspeed.blocks.SnailBlocks;
import net.numericalk.snailspeed.blocks.entity.SnailBlockEntities;
import org.jetbrains.annotations.Nullable;

public class ClayBrickBlockEntity extends BlockEntity {
    private static final int MAX_DRY_TIME = 20 * 60 * 2;
    private int dryTimeRemaining = 0;

    public ClayBrickBlockEntity(BlockPos pos, BlockState state) {
        super(SnailBlockEntities.CLAY_BRICK, pos, state);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putInt("DryTimeRemaining", dryTimeRemaining);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        dryTimeRemaining = nbt.getInt("DryTimeRemaining");
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
        if (isClayBrick(state)) {
            if (!isRaining(world) && hasDaylight(world, pos) && world.isDay()) {
                if (hasProgressComplete()) {
                    dryClay(world, pos, state);
                }
                increaseProgress();
                spawnSmokeParticle(world, pos);
            } else if (!hasDaylight(world, pos)) {
                pauseProgress();
            } else if (isRaining(world)) {
                resetProgress();
            }
        } else {
            resetProgress();
        }
    }
    private void spawnSmokeParticle(World world, BlockPos pos) {
        if (!(world instanceof ServerWorld serverWorld)) return;
        serverWorld.spawnParticles(
                ParticleTypes.WHITE_SMOKE,
                pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                1,
                0.2, 0, 0.2,
                0.001
        );
    }

    private void resetProgress() {
        dryTimeRemaining = 0;
    }

    private void pauseProgress() {
    }

    private void increaseProgress() {
        dryTimeRemaining++;
    }

    private void dryClay(World world, BlockPos pos, BlockState state) {
        if (world.isClient()) {
            return;
        }
        world.setBlockState(pos, SnailBlocks.DRIED_CLAY_BRICK.getStateWithProperties(state));
    }

    private boolean hasProgressComplete() {
        return dryTimeRemaining >= MAX_DRY_TIME;
    }

    private boolean hasDaylight(World world, BlockPos pos) {
        return world.getLightLevel(LightType.SKY, pos.up()) == 15;
    }

    private boolean isRaining(World world) {
        return world.isRaining() || world.isThundering();
    }

    private boolean isClayBrick(BlockState state) {
        return state.isOf(SnailBlocks.CLAY_BRICK);
    }
}
