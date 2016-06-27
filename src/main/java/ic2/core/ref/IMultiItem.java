/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package ic2.core.ref;

import ic2.core.block.state.IIdProvider;
import net.minecraft.item.ItemStack;

public interface IMultiItem<T extends Enum<T>> {
    public ItemStack getItemStack(T var1);

    public ItemStack getItemStack(String var1);

    public String getVariant(ItemStack var1);
}

