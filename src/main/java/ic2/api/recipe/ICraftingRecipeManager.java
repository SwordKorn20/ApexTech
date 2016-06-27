/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package ic2.api.recipe;

import net.minecraft.item.ItemStack;

public interface ICraftingRecipeManager {
    public /* varargs */ void addRecipe(ItemStack var1, Object ... var2);

    public /* varargs */ void addShapelessRecipe(ItemStack var1, Object ... var2);
}

