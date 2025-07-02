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
import net.numericalk.snailspeed.blocks.custom.FilteringTrayBlock;
import net.numericalk.snailspeed.blocks.entity.ImplementedInventory;
import net.numericalk.snailspeed.blocks.entity.SnailBlockEntities;
import net.numericalk.snailspeed.datagen.SnailItemTagsProvider;
import net.numericalk.snailspeed.items.SnailItems;
import org.jetbrains.annotations.Nullable;

public class FilteringTrayBlockEntity extends BlockEntity implements ImplementedInventory {

    private static final int INPUT_1 = 0;
    private static final int INPUT_2 = 1;
    private static final int INPUT_3 = 2;
    private static final int INPUT_4 = 3;
    private static final int MAX_PROGRESS = 20 * 60 * 3;

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(5, ItemStack.EMPTY);
    private int progress = 0;

    public FilteringTrayBlockEntity(BlockPos pos, BlockState state) {
        super(SnailBlockEntities.FILTERING_TRAY, pos, state);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, inventory, registryLookup);
        nbt.putInt("Progress", progress);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        Inventories.readNbt(nbt, inventory, registryLookup);
        progress = nbt.getInt("Progress");
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
        for (int i = 0; i < 4; i++) {
            if (this.getStack(i).isOf(SnailItems.AIR)) {
                this.setStack(i, ItemStack.EMPTY);
            }
        }
        filterItem(world, pos, state);

        if (this.getStack(4).isIn(SnailItemTagsProvider.FILTERS)) {
            world.setBlockState(pos, state.with(FilteringTrayBlock.HAS_FILTER, true));
        } else {
            world.setBlockState(pos, state.with(FilteringTrayBlock.HAS_FILTER, false));
        }
    }

    private static final Item[][] FILTERING_RECIPES = {
            {SnailItems.GROUND_GRAPHITE, Items.CLAY_BALL, Items.AIR, Items.AIR, SnailItems.REFINED_GRAPHITE}
    };

    private void filterItem(World world, BlockPos pos, BlockState state) {
        boolean matchedRecipe = false;

        recipeLoop:
        for (Item[] items : FILTERING_RECIPES) {
            Item output = items[4];

            for (int i = 0; i < 4; i++) {
                if (!getStack(i).isOf(items[i])) {
                    continue recipeLoop;
                }
            }
            if (!state.get(FilteringTrayBlock.HAS_FILTER)) continue;

            matchedRecipe = true;
            progress++;
            spawnWaterParticle(world, pos, state);

            if (progress >= MAX_PROGRESS) {
                world.updateListeners(pos, getCachedState(), getCachedState(), FilteringTrayBlock.NOTIFY_ALL);
                setStack(INPUT_1, new ItemStack(output));

                setStack(INPUT_2, ItemStack.EMPTY);
                setStack(INPUT_3, ItemStack.EMPTY);
                setStack(INPUT_4, ItemStack.EMPTY);

                world.updateListeners(pos, getCachedState(), getCachedState(), FilteringTrayBlock.NOTIFY_ALL);

                progress = 0;
            }
            break;
        }

        if (!matchedRecipe) {
            progress = 0;
        }
    }

    private void spawnWaterParticle(World world, BlockPos pos, BlockState state) {
        if (!(this.world instanceof ServerWorld serverWorld)) return;
        serverWorld.spawnParticles(
                ParticleTypes.DRIPPING_WATER,
                pos.getX() + 0.5, pos.getY() + 0.7, pos.getZ() + 0.5,
                1,
                0.2, 0, 0.2,
                0.01
        );
    }


}
