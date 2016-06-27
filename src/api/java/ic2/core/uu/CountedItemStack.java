/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package ic2.core.uu;

import net.minecraft.item.ItemStack;

public class CountedItemStack {
    public final ItemStack stack;
    public final double amount;

    public CountedItemStack(ItemStack stack, double amount) {
        this.stack = stack;
        this.amount = amount;
    }

    public String toString() {
        return "" + this.amount + "* " + (Object)this.stack;
    }
}

