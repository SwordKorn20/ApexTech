/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package ic2.api.info;

import net.minecraft.item.ItemStack;

public interface IInfoProvider {
    public double getEnergyValue(ItemStack var1);

    public int getFuelValue(ItemStack var1, boolean var2);
}

