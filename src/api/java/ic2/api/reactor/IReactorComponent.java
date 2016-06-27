/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package ic2.api.reactor;

import ic2.api.reactor.IReactor;
import net.minecraft.item.ItemStack;

public interface IReactorComponent {
    public void processChamber(ItemStack var1, IReactor var2, int var3, int var4, boolean var5);

    public boolean acceptUraniumPulse(ItemStack var1, IReactor var2, ItemStack var3, int var4, int var5, int var6, int var7, boolean var8);

    public boolean canStoreHeat(ItemStack var1, IReactor var2, int var3, int var4);

    public int getMaxHeat(ItemStack var1, IReactor var2, int var3, int var4);

    public int getCurrentHeat(ItemStack var1, IReactor var2, int var3, int var4);

    public int alterHeat(ItemStack var1, IReactor var2, int var3, int var4, int var5);

    public float influenceExplosion(ItemStack var1, IReactor var2);
}

