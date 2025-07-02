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
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.numericalk.snailspeed.blocks.custom.BrickOvenBlock;
import net.numericalk.snailspeed.blocks.entity.ImplementedInventory;
import net.numericalk.snailspeed.blocks.entity.SnailBlockEntities;
import net.numericalk.snailspeed.datagen.SnailItemTagsProvider;
import net.numericalk.snailspeed.items.SnailItems;
import org.jetbrains.annotations.Nullable;

public class BrickOvenBlockEntity extends BlockEntity implements ImplementedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(6, ItemStack.EMPTY);

    public BrickOvenBlockEntity(BlockPos pos, BlockState state) {
        super(SnailBlockEntities.BRICK_OVEN, pos, state);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, inventory, registryLookup);
        nbt.putFloat("FireTime", fireTime);
        nbt.putIntArray("CookTime", progress);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        Inventories.readNbt(nbt, inventory, registryLookup);
        fireTime = nbt.getFloat("FireTime");

        int[] savedProgress = nbt.getIntArray("CookTime");
        System.arraycopy(savedProgress, 0, progress, 0, Math.min(savedProgress.length, progress.length));
    }

    public float fireTime;
    public float maxFireTime = 20 * 60 * 10;

    private final int[] progress = new int[5];
    private int maxProgress;

    public void tick(World world, BlockPos pos, BlockState state) {
        for (int i = 0; i < 5; i++) {
            if (this.getStack(i).isOf(SnailItems.AIR)) {
                this.setStack(i, ItemStack.EMPTY);
            }
        }
        if (hasFuel() && isLit(state)) {
            decreaseFireTime();
            if (fireTime <= 0) {
                fireTime = 0;
                setLitState(0, world, pos, state);
                this.getStack(5).decrement(1);
            } else if (fireTime <= 20 * 60 * 3 && state.get(BrickOvenBlock.LIT) == 3) {
                setLitState(2, world, pos, state);
            }
        } else if (hasFuel()) {
            displayHasFuel(world, pos, state);
            resetFireTime();
        }
        else {
            resetFireTime();
        }

        if (state.get(BrickOvenBlock.LIT) == 2) {
            maxProgress = 20 * 60 * 2;
            cookItem(state, world, pos, maxProgress);
        } else if (state.get(BrickOvenBlock.LIT) == 3) {
            maxProgress = 20 * 60;
            smeltItem(state, world, pos, maxProgress);
        }
    }

    private void spawnSmokeParticle(World world, BlockPos pos, BlockState state) {
        if (!(world instanceof ServerWorld serverWorld)) return;
        serverWorld.spawnParticles(
                ParticleTypes.SMOKE,
                pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                1,
                0, 0.2, 0,
                0.01
        );
    }

    private Item getCookedItem(Item raw) {
        for (Item[] entry : COOKING_RECIPES) {
            if (entry[0] == raw) return entry[1];
        }
        return null;
    }

    private Item getSmeltedItem(Item raw) {
        for (Item[] entry : SMELTING_RECIPES) {
            if (entry[0] == raw) return entry[1];
        }
        return null;
    }

    private void smeltItem(BlockState state, World world, BlockPos pos, int maxProgress) {
        for (int i = 0; i < 5; i++) {
            ItemStack stack = getStack(i);
            if (stack.isEmpty()) continue;

            Item smelted = getSmeltedItem(stack.getItem());
            if (smelted != null) {
                progress[i]++;
                spawnSmokeParticle(world, pos, state);
                if (progress[i] >= maxProgress) {
                    setStack(i, new ItemStack(smelted));
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

    private void cookItem(BlockState state, World world, BlockPos pos, int maxProgress) {
        for (int i = 0; i < 5; i++) {
            ItemStack stack = getStack(i);
            if (stack.isEmpty()) continue;

            Item cooked = getCookedItem(stack.getItem());
            if (cooked != null) {
                progress[i]++;
                spawnSmokeParticle(world, pos, state);
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

    private void setLitState(int lit, World world, BlockPos pos, BlockState state) {
        world.setBlockState(pos, state.with(BrickOvenBlock.LIT, lit));
    }

    private void displayHasFuel(World world, BlockPos pos, BlockState state) {
        world.setBlockState(pos, state.with(BrickOvenBlock.LIT, 1));
    }

    private void decreaseFireTime() {
        fireTime = Math.max(0, fireTime - 1);
    }

    public void calculatedAddedFireTime(TagKey<Item> fuelType) {
        if (fuelType == SnailItemTagsProvider.CAMPFIRE_FUEL) {
            fireTime += ((1200f/100f) * 50f);
        } else if (fuelType == SnailItemTagsProvider.OVEN_FUEL) {
            fireTime += ((2400f/100f) * 50f);
        }
    }

    private void resetFireTime() {
        if (this.getStack(5).isIn(SnailItemTagsProvider.CAMPFIRE_FUEL)) {
            fireTime = 20 * 30;
        } else if (this.getStack(5).isIn(SnailItemTagsProvider.OVEN_FUEL)) {
            fireTime = 20 * 60;
        }
    }

    public float getMaxFireTime() {
        return maxFireTime;
    }

    public float getFireTime() {
        return fireTime;
    }

    private boolean isLit(BlockState state) {
        return state.get(BrickOvenBlock.LIT) >= 2;
    }

    private boolean hasFuel() {
        return !this.getStack(5).isEmpty();
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

    private static final Item[][] COOKING_RECIPES = {
            {Items.POTATO, Items.BAKED_POTATO},
            {Items.CHORUS_FRUIT, Items.POPPED_CHORUS_FRUIT},
            {Items.CHICKEN, Items.COOKED_CHICKEN},
            {Items.COD, Items.COOKED_COD},
            {Items.MUTTON, Items.COOKED_MUTTON},
            {Items.PORKCHOP, Items.COOKED_PORKCHOP},
            {Items.RABBIT, Items.COOKED_RABBIT},
            {Items.SALMON, Items.COOKED_SALMON},
            {Items.KELP, Items.DRIED_KELP},
            {Items.BEEF, Items.COOKED_BEEF},
    };
    private static final Item[][] SMELTING_RECIPES = {
            {Items.POTATO, SnailItems.BURNT_POTATO},
            {Items.CHORUS_FRUIT, SnailItems.BURNT_POPPED_CHORUS_FRUIT},
            {Items.CHICKEN, SnailItems.BURNT_CHICKEN},
            {Items.COD, SnailItems.BURNT_COD},
            {Items.MUTTON, SnailItems.BURNT_MUTTON},
            {Items.PORKCHOP, SnailItems.BURNT_PORKCHOP},
            {Items.RABBIT, SnailItems.BURNT_RABBIT},
            {Items.SALMON, SnailItems.BURNT_SALMON},
            {Items.KELP, SnailItems.BURNT_KELP},
            {Items.BEEF, SnailItems.BURNT_BEEF},
            {SnailItems.COPPER_DUST, SnailItems.COPPER_NUGGET},
            {SnailItems.IRON_DUST, Items.IRON_NUGGET},
            {SnailItems.GOLD_DUST, Items.GOLD_NUGGET},
            {SnailItems.TIN_DUST, SnailItems.TIN_NUGGET}
    };
}
