/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  mezz.jei.api.recipe.IRecipeHandler
 *  mezz.jei.api.recipe.IRecipeWrapper
 */
package ic2.jeiIntegration.recipe.machine;

import ic2.jeiIntegration.recipe.machine.IORecipeCategory;
import ic2.jeiIntegration.recipe.machine.IORecipeWrapper;
import javax.annotation.Nonnull;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class IORecipeHandler
implements IRecipeHandler<IORecipeWrapper> {
    public Class<IORecipeWrapper> getRecipeClass() {
        return IORecipeWrapper.class;
    }

    @Nonnull
    public String getRecipeCategoryUid() {
        return "debug";
    }

    @Nonnull
    public IRecipeWrapper getRecipeWrapper(@Nonnull IORecipeWrapper recipe) {
        return recipe;
    }

    public boolean isRecipeValid(@Nonnull IORecipeWrapper recipe) {
        return true;
    }

    public String getRecipeCategoryUid(IORecipeWrapper arg0) {
        return arg0.category.getUid();
    }
}

