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

public class DriedGrassSheafBlockEntity extends BlockEntity {
    private static final int MAX_BURN_TIME = 20 * 60 * 2;
    private int burnTimeRemaining = 0;

    public DriedGrassSheafBlockEntity(BlockPos pos, BlockState state) {
        super(SnailBlockEntities.DRIED_GRASS_SHEAF, pos, state);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putInt("BurnTimeRemaining", burnTimeRemaining);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        burnTimeRemaining = nbt.getInt("BurnTimeRemaining");
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
        if (isDriedGrassSheaf(state)) {
            if (!isRaining(world) && hasDaylight(world, pos) && world.isDay()) {
                if (hasProgressComplete()) {
                    dryGrassSheaf(world, pos, state);
                }
                spawnSmokeParticle(world, pos);
                increaseProgress();
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
                ParticleTypes.SMOKE,
                pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                1,
                0.4, 0, 0.4,
                0.001
        );
    }

    private void resetProgress() {
        burnTimeRemaining = 0;
    }

    private void pauseProgress() {
    }

    private void increaseProgress() {
        burnTimeRemaining++;
    }

    private void dryGrassSheaf(World world, BlockPos pos, BlockState state) {
        if (world.isClient()) {
            return;
        }
        world.setBlockState(pos, SnailBlocks.DRIED_GRASS_SHEAF.getStateWithProperties(state));
    }

    private boolean hasProgressComplete() {
        return burnTimeRemaining >= MAX_BURN_TIME;
    }

    private boolean hasDaylight(World world, BlockPos pos) {
        return world.getLightLevel(LightType.SKY, pos.up()) == 15;
    }

    private boolean isRaining(World world) {
        return world.isRaining() || world.isThundering();
    }

    private boolean isDriedGrassSheaf(BlockState state) {
        return state.isOf(SnailBlocks.DRIED_GRASS_SHEAF);
    }
}
