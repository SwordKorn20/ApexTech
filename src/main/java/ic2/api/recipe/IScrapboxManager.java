/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package ic2.api.recipe;

import java.util.Map;
import net.minecraft.item.ItemStack;

public interface IScrapboxManager {
    public void addDrop(ItemStack var1, float var2);

    public ItemStack getDrop(ItemStack var1, boolean var2);

    public Map<ItemStack, Float> getDrops();
}

