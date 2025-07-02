package net.numericalk.snailspeed.screen.custom;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;
import net.numericalk.snailspeed.blocks.entity.custom.SawTableBlockEntity;
import net.numericalk.snailspeed.screen.SnailScreenHandlers;

import java.util.Objects;

public class SawTableScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    public final SawTableBlockEntity blockEntity;
    private final BlockPos pos;

    public SawTableScreenHandler(int syncId, PlayerInventory inventory, BlockPos pos) {
        this(syncId, inventory, Objects.requireNonNull(inventory.player.getWorld().getBlockEntity(pos)));
    }

    public SawTableScreenHandler(int syncId, PlayerInventory playerInventory,
                                 BlockEntity blockEntity) {
        super(SnailScreenHandlers.SAW_TABLE_SCREEN_HANDLER, syncId);
        this.inventory = ((Inventory) blockEntity);
        this.blockEntity = ((SawTableBlockEntity) blockEntity);
        this.pos = blockEntity.getPos();

        this.addSlot(new Slot(inventory, 0, 15, 15));
        this.addSlot(new Slot(inventory, 1, 15, 53));
        this.addSlot(new Slot(inventory, 2, 143, 33) {
            @Override
            public void onTakeItem(PlayerEntity player, ItemStack stack) {
                ((SawTableBlockEntity) blockEntity).decrementInput();
            }

            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }

            @Override
            public boolean canTakeItems(PlayerEntity playerEntity) {
                return true;
            }
        });

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }

    public BlockPos getBlockPos() {
        return pos;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();

            if (invSlot == SawTableBlockEntity.OUTPUT) {
                blockEntity.decrementInput();

                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickTransfer(originalStack, newStack);
            } else if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        return newStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    private void addPlayerInventory(PlayerInventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
}
