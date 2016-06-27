/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  mezz.jei.api.recipe.IRecipeWrapper
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.FontRenderer
 *  net.minecraft.item.ItemStack
 *  net.minecraftforge.fluids.FluidStack
 */
package ic2.jeiIntegration.recipe.misc;

import ic2.api.recipe.IScrapboxManager;
import ic2.api.recipe.Recipes;
import ic2.core.block.state.IIdProvider;
import ic2.core.item.type.CraftingItemType;
import ic2.core.ref.ItemName;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class ScrapboxRecipeWrapper
implements IRecipeWrapper {
    private final Map.Entry<ItemStack, Float> entry;

    public ScrapboxRecipeWrapper(Map.Entry<ItemStack, Float> entry) {
        this.entry = entry;
    }

    public List<ItemStack> getInputs() {
        return Collections.singletonList(ItemName.crafting.getItemStack(CraftingItemType.scrap_box));
    }

    public List<ItemStack> getOutputs() {
        return Collections.singletonList(this.entry.getKey());
    }

    public List<FluidStack> getFluidInputs() {
        return null;
    }

    public List<FluidStack> getFluidOutputs() {
        return null;
    }

    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        float value = this.entry.getValue().floatValue();
        String text = (double)value < 0.001 ? "< 0.01" : "  " + String.format("%.2f", Float.valueOf(value * 100.0f));
        minecraft.fontRendererObj.drawString(text + "%", 86, 9, 4210752);
    }

    public void drawAnimations(Minecraft minecraft, int recipeWidth, int recipeHeight) {
    }

    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        return null;
    }

    public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
        return false;
    }

    public static List<ScrapboxRecipeWrapper> createRecipes() {
        ArrayList<ScrapboxRecipeWrapper> recipes = new ArrayList<ScrapboxRecipeWrapper>();
        for (Map.Entry<ItemStack, Float> e : Recipes.scrapboxDrops.getDrops().entrySet()) {
            recipes.add(new ScrapboxRecipeWrapper(e));
        }
        return recipes;
    }
}

