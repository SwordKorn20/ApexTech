/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  mezz.jei.api.IGuiHelper
 *  mezz.jei.api.gui.IDrawable
 *  mezz.jei.api.gui.IDrawableStatic
 *  mezz.jei.api.gui.IGuiItemStackGroup
 *  mezz.jei.api.gui.IRecipeLayout
 *  mezz.jei.api.recipe.IRecipeCategory
 *  mezz.jei.api.recipe.IRecipeWrapper
 *  net.minecraft.client.Minecraft
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.text.translation.I18n
 */
package ic2.jeiIntegration.recipe.misc;

import java.util.List;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;

public class ScrapboxRecipeCategory
implements IRecipeCategory {
    private final IDrawable background;

    public ScrapboxRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(new ResourceLocation("ic2:textures/gui/ScrapboxRecipes.png"), 55, 30, 82, 26);
    }

    public String getUid() {
        return "ic2.scrapbox";
    }

    public String getTitle() {
        return I18n.translateToLocal((String)"ic2.crafting.scrap_box");
    }

    public IDrawable getBackground() {
        return this.background;
    }

    public void drawExtras(Minecraft minecraft) {
    }

    public void drawAnimations(Minecraft minecraft) {
    }

    public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper) {
        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
        itemStacks.init(0, true, 0, 4);
        itemStacks.init(1, true, 60, 4);
        itemStacks.setFromRecipe(0, recipeWrapper.getInputs().get(0));
        itemStacks.setFromRecipe(1, recipeWrapper.getOutputs().get(0));
    }
}

