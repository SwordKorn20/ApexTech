/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package ic2.api.item;

import net.minecraft.item.ItemStack;

public interface IElectricItem {
    public boolean canProvideEnergy(ItemStack var1);

    public double getMaxCharge(ItemStack var1);

    public int getTier(ItemStack var1);

    public double getTransferLimit(ItemStack var1);
}

