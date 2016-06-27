/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.inventory.ClickType
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.inventory.Slot
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 */
package ic2.core.slot;

import ic2.core.util.StackUtil;
import java.io.PrintStream;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class SlotHologramSlot
extends Slot {
    protected final ItemStack[] stacks;
    protected final int index;
    protected final int stackSizeLimit;
    protected final ChangeCallback changeCallback;

    public SlotHologramSlot(ItemStack[] stacks, int index, int x, int y, int stackSizeLimit, ChangeCallback changeCallback) {
        super(null, 0, x, y);
        if (index >= stacks.length) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        this.stacks = stacks;
        this.index = index;
        this.stackSizeLimit = stackSizeLimit;
        this.changeCallback = changeCallback;
    }

    public boolean canTakeStack(EntityPlayer player) {
        return false;
    }

    public int getSlotStackLimit() {
        return this.stackSizeLimit;
    }

    public boolean isItemValid(ItemStack stack) {
        return false;
    }

    public ItemStack getStack() {
        return this.stacks[this.index];
    }

    public void putStack(ItemStack stack) {
        this.stacks[this.index] = stack;
    }

    public void onSlotChanged() {
        if (this.changeCallback != null) {
            this.changeCallback.onChanged(this.index);
        }
    }

    public ItemStack decrStackSize(int amount) {
        return null;
    }

    public boolean isHere(IInventory inventory, int index) {
        return false;
    }

    public ItemStack slotClick(int dragType, ClickType clickType, EntityPlayer player) {
        if (player.worldObj.isRemote) {
            System.out.printf("dragType=%d clickType=%s stack=%s%n", new Object[]{dragType, clickType, player.inventory.getItemStack()});
        }
        if (clickType == ClickType.PICKUP && (dragType == 0 || dragType == 1)) {
            ItemStack playerStack = player.inventory.getItemStack();
            ItemStack slotStack = this.stacks[this.index];
            if (playerStack != null) {
                if (slotStack == null) {
                    this.stacks[this.index] = dragType == 0 ? StackUtil.copyWithSize(playerStack, Math.min(playerStack.stackSize, this.stackSizeLimit)) : StackUtil.copyWithSize(playerStack, 1);
                } else if (StackUtil.checkItemEquality(playerStack, slotStack)) {
                    int increment = dragType == 0 ? playerStack.stackSize : 1;
                    System.out.println("add " + increment + " to " + (Object)slotStack + " -> " + Math.min(slotStack.stackSize + increment, Math.min(this.stackSizeLimit, slotStack.getMaxStackSize())));
                    slotStack.stackSize = Math.min(slotStack.stackSize + increment, Math.min(this.stackSizeLimit, slotStack.getMaxStackSize()));
                } else {
                    this.stacks[this.index] = StackUtil.copyWithSize(playerStack, Math.min(playerStack.stackSize, this.stackSizeLimit));
                }
            } else if (slotStack != null) {
                if (dragType == 0) {
                    this.stacks[this.index] = null;
                } else {
                    int newSize = slotStack.stackSize / 2;
                    if (newSize <= 0) {
                        this.stacks[this.index] = null;
                    } else {
                        slotStack.stackSize = newSize;
                    }
                }
            }
            this.onSlotChanged();
        }
        return null;
    }

    public static interface ChangeCallback {
        public void onChanged(int var1);
    }

}

