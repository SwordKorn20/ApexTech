/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package ic2.core.recipe;

import ic2.api.recipe.ICraftingRecipeManager;
import ic2.core.recipe.AdvRecipe;
import ic2.core.recipe.AdvShapelessRecipe;
import net.minecraft.item.ItemStack;

public class AdvCraftingRecipeManager
implements ICraftingRecipeManager {
    @Override
    public /* varargs */ void addRecipe(ItemStack output, Object ... input) {
        AdvRecipe.addAndRegister(output, input);
    }

    @Override
    public /* varargs */ void addShapelessRecipe(ItemStack output, Object ... input) {
        AdvShapelessRecipe.addAndRegister(output, input);
    }
}

