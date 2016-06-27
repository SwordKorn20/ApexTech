/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  mezz.jei.api.recipe.IRecipeHandler
 *  mezz.jei.api.recipe.IRecipeWrapper
 *  net.minecraft.item.ItemStack
 */
package ic2.jeiIntegration.recipe.crafting;

import ic2.api.recipe.IRecipeInput;
import ic2.core.recipe.AdvRecipe;
import ic2.jeiIntegration.recipe.crafting.AdvRecipeWrapper;
import java.util.List;
import javax.annotation.Nonnull;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;

public class AdvRecipeHandler
implements IRecipeHandler<AdvRecipe> {
    @Nonnull
    public Class<AdvRecipe> getRecipeClass() {
        return AdvRecipe.class;
    }

    @Nonnull
    public String getRecipeCategoryUid() {
        return "minecraft.crafting";
    }

    @Nonnull
    public IRecipeWrapper getRecipeWrapper(@Nonnull AdvRecipe recipe) {
        return new AdvRecipeWrapper(recipe);
    }

    public boolean isRecipeValid(@Nonnull AdvRecipe recipe) {
        if (recipe.hidden) {
            return false;
        }
        for (IRecipeInput input : recipe.input) {
            if (!input.getInputs().isEmpty()) continue;
            return false;
        }
        return true;
    }

    public String getRecipeCategoryUid(AdvRecipe arg0) {
        return this.getRecipeCategoryUid();
    }
}

