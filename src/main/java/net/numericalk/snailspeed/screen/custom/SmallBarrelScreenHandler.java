package net.numericalk.snailspeed.screen.custom;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;
import net.numericalk.snailspeed.blocks.entity.custom.SmallBarrelBlockEntity;
import net.numericalk.snailspeed.screen.SnailScreenHandlers;

public class SmallBarrelScreenHandler extends ScreenHandler {
    private final Inventory inv;
    private final SmallBarrelBlockEntity blockEntity;


    public SmallBarrelScreenHandler(int syncId, PlayerInventory inventory, BlockPos pos) {
        this(syncId, inventory, inventory.player.getWorld().getBlockEntity(pos));
    }

    public SmallBarrelScreenHandler(int syncId, PlayerInventory inventory, BlockEntity blockEntity) {
        super(SnailScreenHandlers.SMALL_BARREL_SCREEN_HANDLER, syncId);
        this.inv = ((Inventory) blockEntity);
        this.blockEntity = ((SmallBarrelBlockEntity) blockEntity);

        this.addSlot(new Slot(this.inv, 0, 44, 20));
        this.addSlot(new Slot(this.inv, 1, 62, 20));
        this.addSlot(new Slot(this.inv, 2, 80, 20));
        this.addSlot(new Slot(this.inv, 3, 98, 20));
        this.addSlot(new Slot(this.inv, 4,  116, 20));

        addPlayerInventory(inventory);
        addPlayerHotbar(inventory);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inv.size()) {
                if (!this.insertItem(originalStack, this.inv.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inv.size(), false)) {
                return ItemStack.EMPTY;
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
        return this.inv.canPlayerUse(player);
    }

    private void addPlayerInventory(PlayerInventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 51 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 109));
        }
    }
}
