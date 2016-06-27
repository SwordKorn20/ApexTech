/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package ic2.core.block.invslot;

import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import ic2.core.util.StackUtil;
import java.util.List;
import net.minecraft.item.ItemStack;

public class InvSlotOutput
extends InvSlot {
    public InvSlotOutput(TileEntityInventory base1, String name1, int count) {
        super(base1, name1, InvSlot.Access.O, count, InvSlot.InvSide.BOTTOM);
    }

    @Override
    public boolean accepts(ItemStack stack) {
        return false;
    }

    public int add(List<ItemStack> stacks) {
        return this.add(stacks.toArray((T[])new ItemStack[0]), false);
    }

    public int add(ItemStack stack) {
        if (stack == null) {
            throw new NullPointerException("null ItemStack");
        }
        return this.add(new ItemStack[]{stack}, false);
    }

    public boolean canAdd(List<ItemStack> stacks) {
        return this.add(stacks.toArray((T[])new ItemStack[0]), true) == 0;
    }

    public boolean canAdd(ItemStack stack) {
        if (stack == null) {
            throw new NullPointerException("null ItemStack");
        }
        return this.add(new ItemStack[]{stack}, true) == 0;
    }

    private int add(ItemStack[] stacks, boolean simulate) {
        if (stacks == null || stacks.length == 0) {
            return 0;
        }
        ItemStack[] backup = simulate ? this.backup() : null;
        int totalAmount = 0;
        for (ItemStack stack : stacks) {
            int amount = stack.stackSize;
            if (amount <= 0) continue;
            block1 : for (int pass = 0; pass < 2; ++pass) {
                for (int i = 0; i < this.size(); ++i) {
                    ItemStack existingStack = this.get(i);
                    int space = this.getStackSizeLimit();
                    if (existingStack != null) {
                        space = Math.min(space, existingStack.getMaxStackSize()) - existingStack.stackSize;
                    }
                    if (space <= 0) continue;
                    if (pass == 0 && existingStack != null && StackUtil.checkItemEqualityStrict(stack, existingStack)) {
                        if (space >= amount) {
                            existingStack.stackSize += amount;
                            amount = 0;
                            break block1;
                        }
                        existingStack.stackSize += space;
                        amount -= space;
                        continue;
                    }
                    if (pass != 1 || existingStack != null) continue;
                    if (space >= amount) {
                        this.put(i, StackUtil.copyWithSize(stack, amount));
                        amount = 0;
                        break block1;
                    }
                    this.put(i, StackUtil.copyWithSize(stack, space));
                    amount -= space;
                }
            }
            totalAmount += amount;
        }
        if (simulate) {
            this.restore(backup);
        }
        return totalAmount;
    }
}

