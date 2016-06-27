/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.inventory.Slot
 *  net.minecraft.item.ItemStack
 */
package ic2.core.slot;

import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotInvSlot
extends Slot {
    public final InvSlot invSlot;
    public final int index;

    public SlotInvSlot(InvSlot invSlot, int index, int x, int y) {
        super((IInventory)invSlot.base, invSlot.base.getBaseIndex(invSlot) + index, x, y);
        this.invSlot = invSlot;
        this.index = index;
    }

    public boolean isItemValid(ItemStack stack) {
        return this.invSlot.accepts(stack);
    }

    public ItemStack getStack() {
        return this.invSlot.get(this.index);
    }

    public void putStack(ItemStack stack) {
        this.invSlot.put(this.index, stack);
        this.onSlotChanged();
    }

    public ItemStack decrStackSize(int amount) {
        ItemStack stack = this.invSlot.get(this.index);
        if (stack == null) {
            return null;
        }
        if (stack.stackSize <= amount) {
            this.invSlot.put(this.index, null);
            this.onSlotChanged();
            return stack;
        }
        ItemStack ret = stack.copy();
        ret.stackSize = amount;
        stack.stackSize -= amount;
        this.onSlotChanged();
        return ret;
    }

    public boolean isHere(IInventory inventory, int index) {
        if (inventory != this.invSlot.base) {
            return false;
        }
        int baseIndex = this.invSlot.base.getBaseIndex(this.invSlot);
        if (baseIndex == -1) {
            return false;
        }
        return baseIndex + this.index == index;
    }

    public int getSlotStackLimit() {
        return this.invSlot.getStackSizeLimit();
    }

    public void onPickupFromSlot(EntityPlayer player, ItemStack stack) {
        super.onPickupFromSlot(player, stack);
        this.invSlot.onPickupFromSlot(player, stack);
    }
}

