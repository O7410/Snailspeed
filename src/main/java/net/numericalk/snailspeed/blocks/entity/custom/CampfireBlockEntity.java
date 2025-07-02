package net.numericalk.snailspeed.blocks.entity.custom;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.numericalk.snailspeed.blocks.custom.CampfireBlock;
import net.numericalk.snailspeed.blocks.entity.ImplementedInventory;
import net.numericalk.snailspeed.blocks.entity.SnailBlockEntities;
import net.numericalk.snailspeed.items.SnailItems;
import org.jetbrains.annotations.Nullable;

public class CampfireBlockEntity extends BlockEntity implements ImplementedInventory {

    private final float fireDegradeTimeFinal = 1200f;
    private float fireDegradeTime = fireDegradeTimeFinal;
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);
    private final int[] progress = new int[3];
    private int maxProgress;

    public CampfireBlockEntity(BlockPos pos, BlockState state) {
        super(SnailBlockEntities.CAMPFIRE, pos, state);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, inventory, registryLookup);
        nbt.putFloat("FireDegradeTime", fireDegradeTime);
        nbt.putIntArray("CookTime", progress);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        Inventories.readNbt(nbt, inventory, registryLookup);
        fireDegradeTime = nbt.getFloat("FireDegradeTime");

        int[] savedProgress = nbt.getIntArray("CookTime");
        System.arraycopy(savedProgress, 0, progress, 0, Math.min(savedProgress.length, progress.length));
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
        for (int i = 0; i < 3; i++) {
            if (this.getStack(i).isOf(SnailItems.AIR)) {
                this.setStack(i, ItemStack.EMPTY);
            }
        }
        if (!canExtinguishFire(state) && fireDegradeTime > 0 && getLitBlockState(state) > CampfireBlock.LIT_UNLIT) {
            spawnSmokeParticle(world, pos, state);
            fireDegradeTime--;
        } else if (canExtinguishFire(state)) {
            extinguishFire(world, pos, state);
        }

        if (isWorldRaining(state, world) && isSkyVisible(world, pos)) {
            extinguishFireWithoutBeingBurnt(world, pos, state);
        }
        if (canUpgradeFire(state, CampfireBlock.LIT_SMALL, 200f)) {
            upgradeFire(world, pos, state, CampfireBlock.LIT_SMALL);
        }
        if (canDegradeFire(state, CampfireBlock.LIT_MEDIUM)) {
            degradeFire(world, pos, state, CampfireBlock.LIT_MEDIUM);
        }
        if (canUpgradeFire(state, CampfireBlock.LIT_MEDIUM, 400f)) {
            upgradeFire(world, pos, state, CampfireBlock.LIT_MEDIUM);
        }
        if (canDegradeFire(state, CampfireBlock.LIT_LARGE)) {
            degradeFire(world, pos, state, CampfireBlock.LIT_LARGE);
        }

        //RECIPE

        if (getLitBlockState(state) == 2) {
            maxProgress = 20 * 60 * 4;
            cookItem(world, pos, maxProgress);
        }
        if (getLitBlockState(state) == 3) {
            maxProgress = 20 * 60 * 2;
            cookItem(world, pos, maxProgress);
        }
        if (getLitBlockState(state) == 4) {
            maxProgress = 20 * 60;
            burnItem(world, pos, maxProgress);
        }
    }

    private void spawnSmokeParticle(World world, BlockPos pos, BlockState state) {
        if (!(world instanceof ServerWorld serverWorld)) return;
        serverWorld.spawnParticles(
                ParticleTypes.WHITE_SMOKE,
                pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                1,
                0.0, 0.5, 0.0,
                0.01
        );
    }

    private void extinguishFireWithoutBeingBurnt(World world, BlockPos pos, BlockState state) {
        world.setBlockState(pos, state.with(CampfireBlock.LIT, 1));
        setFireDegradeTime(fireDegradeTimeFinal);
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

    private boolean isWorldRaining(BlockState state, World world) {
        return (world.isRaining() || world.isThundering()) && state.get(CampfireBlock.LIT) >= CampfireBlock.LIT_SMALL;
    }

    private void burnItem(World world, BlockPos pos, int maxProgress) {
        for (int i = 0; i < 3; i++) {
            ItemStack stack = getStack(i);
            if (stack.isEmpty()) continue;

            Item cooked = getBurntItem(stack.getItem());
            if (cooked != null) {
                progress[i]++;
                if (progress[i] >= maxProgress) {
                    setStack(i, new ItemStack(cooked));
                    progress[i] = 0;

                    if (!world.isClient) {
                        markDirty();
                        world.updateListeners(pos, getCachedState(), getCachedState(), 3);
                    }
                }
            } else {
                progress[i] = 0;
            }
        }
    }

    private void cookItem(World world, BlockPos pos, int maxProgress) {
        for (int i = 0; i < 3; i++) {
            ItemStack stack = getStack(i);
            if (stack.isEmpty()) continue;
            System.out.println(progress[i]);

            Item cooked = getCookedItem(stack.getItem());
            if (cooked != null) {
                progress[i]++;
                if (progress[i] >= maxProgress) {
                    setStack(i, new ItemStack(cooked));
                    progress[i] = 0;

                    if (!world.isClient) {
                        markDirty();
                        world.updateListeners(pos, getCachedState(), getCachedState(), 3);
                    }
                }
            } else {
                progress[i] = 0;
            }
        }
    }

    private Item getCookedItem(Item raw) {
        for (Item[] entry : COOKING_RECIPES) {
            if (entry[0] == raw) return entry[1];
            if (entry[1] == raw) return entry[2];
        }
        return null;
    }
    private Item getBurntItem(Item raw) {
        for (Item[] entry : COOKING_RECIPES) {
            if (entry[0] == raw) return entry[2];
        }
        return null;
    }

    private static final Item[][] COOKING_RECIPES = {
            {Items.POTATO, Items.BAKED_POTATO, SnailItems.BURNT_POTATO},
            {Items.CHORUS_FRUIT, Items.POPPED_CHORUS_FRUIT, SnailItems.BURNT_POPPED_CHORUS_FRUIT},
            {Items.CHICKEN, Items.COOKED_CHICKEN, SnailItems.BURNT_CHICKEN},
            {Items.COD, Items.COOKED_COD, SnailItems.BURNT_COD},
            {Items.MUTTON, Items.COOKED_MUTTON, SnailItems.BURNT_MUTTON},
            {Items.PORKCHOP, Items.COOKED_PORKCHOP, SnailItems.BURNT_PORKCHOP},
            {Items.RABBIT, Items.COOKED_RABBIT, SnailItems.BURNT_RABBIT},
            {Items.SALMON, Items.COOKED_SALMON, SnailItems.BURNT_SALMON},
            {Items.KELP, Items.DRIED_KELP, SnailItems.BURNT_KELP},
            {Items.BEEF, Items.COOKED_BEEF, SnailItems.BURNT_BEEF},
    };



    private boolean canExtinguishFire(BlockState state) {
        return state.get(CampfireBlock.LIT) == 2 && fireDegradeTime <= 0;
    }

    private void extinguishFire(World world, BlockPos pos, BlockState state) {
        world.setBlockState(pos, state.with(CampfireBlock.LIT, 1).with(CampfireBlock.STAGES, 6));
        setFireDegradeTime(fireDegradeTimeFinal);
    }

    private boolean canDegradeFire(BlockState state, int currentLitState) {
        return getLitBlockState(state) == currentLitState && fireDegradeTime <= fireDegradeTimeFinal;
    }

    private void degradeFire(World world, BlockPos pos, BlockState state, int currentLitState) {
        world.setBlockState(pos, state.with(CampfireBlock.LIT, currentLitState - 1));
    }

    private boolean canUpgradeFire(BlockState state, int currentLitState, float timeNeededToUpgrade) {
        return getLitBlockState(state) == currentLitState && timeNeededToUpgradeFire(timeNeededToUpgrade);
    }
    private void upgradeFire(World world, BlockPos pos, BlockState state, int currentLitState) {
        world.setBlockState(pos, state.with(CampfireBlock.LIT, currentLitState + 1));
    }

    public int getLitBlockState(BlockState state) {
        return state.get(CampfireBlock.LIT);
    }

    public int getStagesBlockState(BlockState state) {
        return state.get(CampfireBlock.STAGES);
    }

    private boolean timeNeededToUpgradeFire(float upgradeFirePercentage) {
        return fireDegradeTime >= (upgradeFirePercentage / 100f) * fireDegradeTimeFinal;
    }

    public void calculateAddedFireTime() {
        fireDegradeTime += ((fireDegradeTimeFinal/100f) * 50f);
    }

    public float getFireDegradeTime() {
        return fireDegradeTime;
    }

    public void setFireDegradeTime(float fireDegradeTime) {
        this.fireDegradeTime = fireDegradeTime;
    }

    public float getFireDegradeTimeLimit() {
        return fireDegradeTimeFinal * 4f;
    }
}
