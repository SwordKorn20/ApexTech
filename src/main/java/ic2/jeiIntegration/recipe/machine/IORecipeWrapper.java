/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  mezz.jei.api.recipe.BlankRecipeWrapper
 *  net.minecraft.client.Minecraft
 *  net.minecraft.item.ItemStack
 */
package ic2.jeiIntegration.recipe.machine;

import ic2.api.recipe.IMachineRecipeManager;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeOutput;
import ic2.jeiIntegration.recipe.machine.IORecipeCategory;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

public class IORecipeWrapper
extends BlankRecipeWrapper {
    private final IMachineRecipeManager.RecipeIoContainer container;
    final IORecipeCategory category;

    IORecipeWrapper(IMachineRecipeManager.RecipeIoContainer container, IORecipeCategory category) {
        if (container == null) {
            throw new NullPointerException();
        }
        this.container = container;
        this.category = category;
    }

    @Nonnull
    public List<List<ItemStack>> getInputs() {
        return Collections.singletonList(this.container.input.getInputs());
    }

    @Nonnull
    public List<ItemStack> getOutputs() {
        return this.container.output.items;
    }

    public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
    }

    public boolean equals(Object obj) {
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        return this.container.equals(((IORecipeWrapper)obj).container);
    }
}

