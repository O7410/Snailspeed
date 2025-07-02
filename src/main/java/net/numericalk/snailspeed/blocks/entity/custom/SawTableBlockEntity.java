package net.numericalk.snailspeed.blocks.entity.custom;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.numericalk.snailspeed.blocks.entity.ImplementedInventory;
import net.numericalk.snailspeed.blocks.entity.SnailBlockEntities;
import net.numericalk.snailspeed.items.SnailItems;
import net.numericalk.snailspeed.screen.custom.SawTableScreenHandler;
import net.numericalk.snailspeed.utils.enums.SawCraftable;
import net.numericalk.snailspeed.utils.records.SawTableRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public class SawTableBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory<BlockPos>, ImplementedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);

    public static final int INPUT = 0;
    public static final int INPUT_ADDITIONAL = 1;
    public static final int OUTPUT = 2;

    public SawTableBlockEntity(BlockPos pos, BlockState state) {
        super(SnailBlockEntities.SAW_TABLE, pos, state);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, inventory, registryLookup);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        Inventories.readNbt(nbt, inventory, registryLookup);
        super.readNbt(nbt, registryLookup);
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayerEntity serverPlayerEntity) {
        return this.pos;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("block.snailspeed.saw_table");
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new SawTableScreenHandler(syncId, playerInventory, this);
    }

    private static final Map<Item, SawTableRecipe> SAW_RECIPES = Map.ofEntries(
            Map.entry(Items.OAK_LOG, new SawTableRecipe(Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.OAK_PLANKS, Items.AIR, Items.AIR, Items.AIR)),
            Map.entry(Items.STRIPPED_OAK_LOG, new SawTableRecipe(Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.OAK_PLANKS, Items.AIR, Items.AIR, Items.AIR)),
            Map.entry(Items.OAK_WOOD, new SawTableRecipe(Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.OAK_PLANKS, Items.AIR, Items.AIR, Items.AIR)),
            Map.entry(Items.STRIPPED_OAK_WOOD, new SawTableRecipe(Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.OAK_PLANKS, Items.AIR, Items.AIR, Items.AIR)),

            Map.entry(Items.SPRUCE_LOG, new SawTableRecipe(Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.SPRUCE_PLANKS, Items.AIR, Items.AIR, Items.AIR)),
            Map.entry(Items.STRIPPED_SPRUCE_LOG, new SawTableRecipe(Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.SPRUCE_PLANKS, Items.AIR, Items.AIR, Items.AIR)),
            Map.entry(Items.SPRUCE_WOOD, new SawTableRecipe(Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.SPRUCE_PLANKS, Items.AIR, Items.AIR, Items.AIR)),
            Map.entry(Items.STRIPPED_SPRUCE_WOOD, new SawTableRecipe(Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.SPRUCE_PLANKS, Items.AIR, Items.AIR, Items.AIR)),

            Map.entry(Items.BIRCH_LOG, new SawTableRecipe(Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.BIRCH_PLANKS, Items.AIR, Items.AIR, Items.AIR)),
            Map.entry(Items.STRIPPED_BIRCH_LOG, new SawTableRecipe(Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.BIRCH_PLANKS, Items.AIR, Items.AIR, Items.AIR)),
            Map.entry(Items.BIRCH_WOOD, new SawTableRecipe(Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.BIRCH_PLANKS, Items.AIR, Items.AIR, Items.AIR)),
            Map.entry(Items.STRIPPED_BIRCH_WOOD, new SawTableRecipe(Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.BIRCH_PLANKS, Items.AIR, Items.AIR, Items.AIR)),

            Map.entry(Items.JUNGLE_LOG, new SawTableRecipe(Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.JUNGLE_PLANKS, Items.AIR, Items.AIR, Items.AIR)),
            Map.entry(Items.STRIPPED_JUNGLE_LOG, new SawTableRecipe(Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.JUNGLE_PLANKS, Items.AIR, Items.AIR, Items.AIR)),
            Map.entry(Items.JUNGLE_WOOD, new SawTableRecipe(Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.JUNGLE_PLANKS, Items.AIR, Items.AIR, Items.AIR)),
            Map.entry(Items.STRIPPED_JUNGLE_WOOD, new SawTableRecipe(Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.JUNGLE_PLANKS, Items.AIR, Items.AIR, Items.AIR)),

            Map.entry(Items.ACACIA_LOG, new SawTableRecipe(Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.ACACIA_PLANKS, Items.AIR, Items.AIR, Items.AIR)),
            Map.entry(Items.STRIPPED_ACACIA_LOG, new SawTableRecipe(Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.ACACIA_PLANKS, Items.AIR, Items.AIR, Items.AIR)),
            Map.entry(Items.ACACIA_WOOD, new SawTableRecipe(Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.ACACIA_PLANKS, Items.AIR, Items.AIR, Items.AIR)),
            Map.entry(Items.STRIPPED_ACACIA_WOOD, new SawTableRecipe(Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.ACACIA_PLANKS, Items.AIR, Items.AIR, Items.AIR)),

            Map.entry(Items.DARK_OAK_LOG, new SawTableRecipe(Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.DARK_OAK_PLANKS, Items.AIR, Items.AIR, Items.AIR)),
            Map.entry(Items.STRIPPED_DARK_OAK_LOG, new SawTableRecipe(Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.DARK_OAK_PLANKS, Items.AIR, Items.AIR, Items.AIR)),
            Map.entry(Items.DARK_OAK_WOOD, new SawTableRecipe(Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.DARK_OAK_PLANKS, Items.AIR, Items.AIR, Items.AIR)),
            Map.entry(Items.STRIPPED_DARK_OAK_WOOD, new SawTableRecipe(Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.DARK_OAK_PLANKS, Items.AIR, Items.AIR, Items.AIR)),

            Map.entry(Items.MANGROVE_LOG, new SawTableRecipe(Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.MANGROVE_PLANKS, Items.AIR, Items.AIR, Items.AIR)),
            Map.entry(Items.STRIPPED_MANGROVE_LOG, new SawTableRecipe(Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.MANGROVE_PLANKS, Items.AIR, Items.AIR, Items.AIR)),
            Map.entry(Items.MANGROVE_WOOD, new SawTableRecipe(Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.MANGROVE_PLANKS, Items.AIR, Items.AIR, Items.AIR)),
            Map.entry(Items.STRIPPED_MANGROVE_WOOD, new SawTableRecipe(Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.MANGROVE_PLANKS, Items.AIR, Items.AIR, Items.AIR)),

            Map.entry(Items.CHERRY_LOG, new SawTableRecipe(Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.CHERRY_PLANKS, Items.AIR, Items.AIR, Items.AIR)),
            Map.entry(Items.STRIPPED_CHERRY_LOG, new SawTableRecipe(Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.CHERRY_PLANKS, Items.AIR, Items.AIR, Items.AIR)),
            Map.entry(Items.CHERRY_WOOD, new SawTableRecipe(Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.CHERRY_PLANKS, Items.AIR, Items.AIR, Items.AIR)),
            Map.entry(Items.STRIPPED_CHERRY_WOOD, new SawTableRecipe(Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.CHERRY_PLANKS, Items.AIR, Items.AIR, Items.AIR)),

            Map.entry(Items.BAMBOO_BLOCK, new SawTableRecipe(Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.BAMBOO_PLANKS, Items.AIR, Items.AIR, Items.AIR)),
            Map.entry(Items.STRIPPED_BAMBOO_BLOCK, new SawTableRecipe(Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.BAMBOO_PLANKS, Items.AIR, Items.AIR, Items.AIR)),

            Map.entry(Items.PALE_OAK_LOG, new SawTableRecipe(Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.PALE_OAK_PLANKS, Items.AIR, Items.AIR, Items.AIR)),
            Map.entry(Items.STRIPPED_PALE_OAK_LOG, new SawTableRecipe(Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.PALE_OAK_PLANKS, Items.AIR, Items.AIR, Items.AIR)),
            Map.entry(Items.PALE_OAK_WOOD, new SawTableRecipe(Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.PALE_OAK_PLANKS, Items.AIR, Items.AIR, Items.AIR)),
            Map.entry(Items.STRIPPED_PALE_OAK_WOOD, new SawTableRecipe(Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.AIR, Items.PALE_OAK_PLANKS, Items.AIR, Items.AIR, Items.AIR)),


            Map.entry(Items.OAK_PLANKS, new SawTableRecipe(Items.OAK_STAIRS, Items.OAK_SLAB, Items.OAK_DOOR, Items.OAK_FENCE, Items.OAK_FENCE_GATE, Items.CHEST, Items.BARREL, Items.WHITE_BED, Items.AIR, Items.OAK_HANGING_SIGN, Items.OAK_SIGN, Items.OAK_TRAPDOOR)),
            Map.entry(Items.SPRUCE_PLANKS, new SawTableRecipe(Items.SPRUCE_STAIRS, Items.SPRUCE_SLAB, Items.SPRUCE_DOOR, Items.SPRUCE_FENCE, Items.SPRUCE_FENCE_GATE, Items.CHEST, Items.BARREL, Items.WHITE_BED, Items.AIR, Items.SPRUCE_HANGING_SIGN, Items.SPRUCE_SIGN, Items.SPRUCE_TRAPDOOR)),
            Map.entry(Items.BIRCH_PLANKS, new SawTableRecipe(Items.BIRCH_STAIRS, Items.BIRCH_SLAB, Items.BIRCH_DOOR, Items.BIRCH_FENCE, Items.BIRCH_FENCE_GATE, Items.CHEST, Items.BARREL, Items.WHITE_BED, Items.AIR, Items.BIRCH_HANGING_SIGN, Items.BIRCH_SIGN, Items.BIRCH_TRAPDOOR)),
            Map.entry(Items.JUNGLE_PLANKS, new SawTableRecipe(Items.JUNGLE_STAIRS, Items.JUNGLE_SLAB, Items.JUNGLE_DOOR, Items.JUNGLE_FENCE, Items.JUNGLE_FENCE_GATE, Items.CHEST, Items.BARREL, Items.WHITE_BED, Items.AIR, Items.JUNGLE_HANGING_SIGN, Items.JUNGLE_SIGN, Items.JUNGLE_TRAPDOOR)),
            Map.entry(Items.ACACIA_PLANKS, new SawTableRecipe(Items.ACACIA_STAIRS, Items.ACACIA_SLAB, Items.ACACIA_DOOR, Items.ACACIA_FENCE, Items.ACACIA_FENCE_GATE, Items.CHEST, Items.BARREL, Items.WHITE_BED, Items.AIR, Items.ACACIA_HANGING_SIGN, Items.ACACIA_SIGN, Items.ACACIA_TRAPDOOR)),
            Map.entry(Items.DARK_OAK_PLANKS, new SawTableRecipe(Items.DARK_OAK_STAIRS, Items.DARK_OAK_SLAB, Items.DARK_OAK_DOOR, Items.DARK_OAK_FENCE, Items.DARK_OAK_FENCE_GATE, Items.CHEST, Items.BARREL, Items.WHITE_BED, Items.AIR, Items.DARK_OAK_HANGING_SIGN, Items.DARK_OAK_SIGN, Items.DARK_OAK_TRAPDOOR)),
            Map.entry(Items.MANGROVE_PLANKS, new SawTableRecipe(Items.MANGROVE_STAIRS, Items.MANGROVE_SLAB, Items.MANGROVE_DOOR, Items.MANGROVE_FENCE, Items.MANGROVE_FENCE_GATE, Items.CHEST, Items.BARREL, Items.WHITE_BED, Items.AIR, Items.MANGROVE_HANGING_SIGN, Items.MANGROVE_SIGN, Items.MANGROVE_TRAPDOOR)),
            Map.entry(Items.CHERRY_PLANKS, new SawTableRecipe(Items.CHERRY_STAIRS, Items.CHERRY_SLAB, Items.CHERRY_DOOR, Items.CHERRY_FENCE, Items.CHERRY_FENCE_GATE, Items.CHEST, Items.BARREL, Items.WHITE_BED, Items.AIR, Items.CHERRY_HANGING_SIGN, Items.CHERRY_SIGN, Items.CHERRY_TRAPDOOR)),
            Map.entry(Items.BAMBOO_PLANKS, new SawTableRecipe(Items.BAMBOO_STAIRS, Items.BAMBOO_SLAB, Items.BAMBOO_DOOR, Items.BAMBOO_FENCE, Items.BAMBOO_FENCE_GATE, Items.CHEST, Items.BARREL, Items.WHITE_BED, Items.AIR, Items.BAMBOO_HANGING_SIGN, Items.BAMBOO_SIGN, Items.BAMBOO_TRAPDOOR)),
            Map.entry(Items.PALE_OAK_PLANKS, new SawTableRecipe(Items.PALE_OAK_STAIRS, Items.PALE_OAK_SLAB, Items.PALE_OAK_DOOR, Items.PALE_OAK_FENCE, Items.PALE_OAK_FENCE_GATE, Items.CHEST, Items.BARREL, Items.WHITE_BED, Items.AIR, Items.PALE_OAK_HANGING_SIGN, Items.PALE_OAK_SIGN, Items.PALE_OAK_TRAPDOOR)),
            Map.entry(Items.CRIMSON_PLANKS, new SawTableRecipe(Items.CRIMSON_STAIRS, Items.CRIMSON_SLAB, Items.CRIMSON_DOOR, Items.CRIMSON_FENCE, Items.CRIMSON_FENCE_GATE, Items.CHEST, Items.BARREL, Items.WHITE_BED, Items.AIR, Items.CRIMSON_HANGING_SIGN, Items.CRIMSON_SIGN, Items.CRIMSON_TRAPDOOR)),
            Map.entry(Items.WARPED_PLANKS, new SawTableRecipe(Items.WARPED_STAIRS, Items.WARPED_SLAB, Items.WARPED_DOOR, Items.WARPED_FENCE, Items.WARPED_FENCE_GATE, Items.CHEST, Items.BARREL, Items.WHITE_BED, Items.AIR, Items.WARPED_HANGING_SIGN, Items.WARPED_SIGN, Items.WARPED_TRAPDOOR))

    );

    SawCraftable selected = SawCraftable.STAIRS;

    public void setSelectedPiece(SawCraftable selected) {
        this.selected = selected;
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (hasRecipe()) {
            tryToCraftItem();
        } else {
            removeStack(OUTPUT);
        }
    }



    public void tryToCraftItem() {
        Item inputs = this.getStack(INPUT).getItem();
        SawTableRecipe recipe = SAW_RECIPES.get(inputs);
        Set<Item> keys = SAW_RECIPES.keySet();
        for (Item input : keys) {
            Item stairs = recipe.stairs();
            Item slab = recipe.slab();
            Item door = recipe.door();
            Item planks = recipe.planks();
            Item trapdoor = recipe.trapdoor();
            Item fence = recipe.fence();
            Item fenceGate = recipe.fenceGate();
            Item chest = recipe.chest();
            Item barrel = recipe.barrel();
            Item bed = recipe.bed();
            Item hangingSign = recipe.hangingSign();
            Item sign = recipe.sign();
            if (this.getStack(INPUT).isOf(input)) {
                if (!hasAdditionalInput()) {
                    switch (selected) {
                        case STAIRS -> this.setStack(OUTPUT, stairs.getDefaultStack().copyWithCount(1));
                        case SLAB -> this.setStack(OUTPUT, slab.getDefaultStack().copyWithCount(2));
                        case DOOR -> this.setStack(OUTPUT, door.getDefaultStack().copyWithCount(1));
                        case PLANKS -> this.setStack(OUTPUT, planks.getDefaultStack().copyWithCount(4));
                        case TRAPDOOR -> this.setStack(OUTPUT, trapdoor.getDefaultStack().copyWithCount(1));
                        default -> this.setStack(OUTPUT, ItemStack.EMPTY);
                    }
                } else if (hasAdditionalInputOf(Items.COPPER_INGOT.getDefaultStack())) {
                    switch (selected) {
                        case BARREL -> this.setStack(OUTPUT, barrel.getDefaultStack().copyWithCount(1));
                        case CHEST -> this.setStack(OUTPUT, chest.getDefaultStack().copyWithCount(1));
                        default -> this.setStack(OUTPUT, ItemStack.EMPTY);
                    }
                } else if (hasAdditionalInputIn(ItemTags.WOOL)) {
                    if (selected.equals(SawCraftable.BED)) {
                        this.setStack(OUTPUT, bed.getDefaultStack().copyWithCount(1));
                    } else {
                        this.setStack(OUTPUT, ItemStack.EMPTY);
                    }
                } else if (hasAdditionalInputOf(SnailItems.LONG_STICK.getDefaultStack())) {
                    switch (selected) {
                        case FENCE -> this.setStack(OUTPUT, fence.getDefaultStack().copyWithCount(2));
                        case FENCE_GATE -> this.setStack(OUTPUT, fenceGate.getDefaultStack().copyWithCount(1));
                        case HANGING_SIGN -> this.setStack(OUTPUT, hangingSign.getDefaultStack().copyWithCount(1));
                        case SIGN -> this.setStack(OUTPUT, sign.getDefaultStack().copyWithCount(2));
                        default -> this.setStack(OUTPUT, ItemStack.EMPTY);
                    }
                }
            }
        }
    }

    private boolean hasRecipe() {
        Set<Item> keys = SAW_RECIPES.keySet();
        for (Item input : keys) {
            if (this.getStack(INPUT).isOf(input)) {
                if (!hasAdditionalInput()) {
                    switch (selected) {
                        case STAIRS, SLAB, DOOR, PLANKS, TRAPDOOR -> {
                            return true;
                        }
                    }
                } else if (hasAdditionalInputOf(Items.COPPER_INGOT.getDefaultStack())) {
                    return true;
                } else if (hasAdditionalInputIn(ItemTags.WOOL)) {
                    return true;
                } else if (hasAdditionalInputOf(SnailItems.LONG_STICK.getDefaultStack())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasAdditionalInputIn(TagKey<Item> itemTag) {
        return this.getStack(INPUT_ADDITIONAL).isIn(itemTag);
    }

    private boolean hasAdditionalInputOf(ItemStack stack) {
        return this.getStack(INPUT_ADDITIONAL).isOf(stack.getItem());
    }

    private boolean hasAdditionalInput() {
        return !this.getStack(INPUT_ADDITIONAL).isEmpty();
    }

    public void decrementInput() {
        this.getStack(INPUT).decrement(1);
        this.getStack(INPUT_ADDITIONAL).decrement(1);
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


}
