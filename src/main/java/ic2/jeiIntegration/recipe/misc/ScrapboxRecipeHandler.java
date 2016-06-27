/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  mezz.jei.api.recipe.IRecipeHandler
 *  mezz.jei.api.recipe.IRecipeWrapper
 */
package ic2.jeiIntegration.recipe.misc;

import ic2.jeiIntegration.recipe.misc.ScrapboxRecipeWrapper;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class ScrapboxRecipeHandler
implements IRecipeHandler<ScrapboxRecipeWrapper> {
    public Class<ScrapboxRecipeWrapper> getRecipeClass() {
        return ScrapboxRecipeWrapper.class;
    }

    public String getRecipeCategoryUid() {
        return "ic2.scrapbox";
    }

    public IRecipeWrapper getRecipeWrapper(ScrapboxRecipeWrapper recipe) {
        return recipe;
    }

    public boolean isRecipeValid(ScrapboxRecipeWrapper recipe) {
        return true;
    }

    public String getRecipeCategoryUid(ScrapboxRecipeWrapper arg0) {
        return this.getRecipeCategoryUid();
    }
}

