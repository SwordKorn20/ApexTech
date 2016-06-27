/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  mezz.jei.api.recipe.BlankRecipeWrapper
 *  mezz.jei.api.recipe.wrapper.ICraftingRecipeWrapper
 *  net.minecraft.item.ItemStack
 */
package ic2.jeiIntegration.recipe.crafting;

import ic2.api.recipe.IRecipeInput;
import ic2.core.recipe.AdvShapelessRecipe;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import mezz.jei.api.recipe.wrapper.ICraftingRecipeWrapper;
import net.minecraft.item.ItemStack;

public class AdvShapelessRecipeWrapper
extends BlankRecipeWrapper
implements ICraftingRecipeWrapper {
    private final AdvShapelessRecipe recipe;

    public AdvShapelessRecipeWrapper(AdvShapelessRecipe recipe) {
        this.recipe = recipe;
    }

    public List<List<ItemStack>> getInputs() {
        ArrayList<List<ItemStack>> ret = new ArrayList<List<ItemStack>>(this.recipe.input.length);
        for (IRecipeInput input : this.recipe.input) {
            ret.add(input.getInputs());
        }
        return ret;
    }

    public List<ItemStack> getOutputs() {
        return Collections.singletonList(this.recipe.getRecipeOutput());
    }
}

