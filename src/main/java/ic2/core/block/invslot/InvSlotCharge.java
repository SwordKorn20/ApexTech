/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package ic2.core.block.invslot;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItemManager;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import net.minecraft.item.ItemStack;

public class InvSlotCharge
extends InvSlot {
    public int tier;

    public InvSlotCharge(TileEntityInventory base1, int tier) {
        super(base1, "charge", InvSlot.Access.IO, 1, InvSlot.InvSide.TOP);
        this.tier = tier;
    }

    @Override
    public boolean accepts(ItemStack stack) {
        return ElectricItem.manager.charge(stack, Double.POSITIVE_INFINITY, this.tier, true, true) > 0.0;
    }

    public double charge(double amount) {
        if (amount <= 0.0) {
            throw new IllegalArgumentException("Amount must be > 0.");
        }
        ItemStack stack = this.get(0);
        if (stack == null) {
            return 0.0;
        }
        return ElectricItem.manager.charge(stack, amount, this.tier, false, false);
    }

    public void setTier(int tier1) {
        this.tier = tier1;
    }
}

