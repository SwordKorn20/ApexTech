/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  mezz.jei.api.gui.IGuiItemStackGroup
 *  mezz.jei.api.gui.IRecipeLayout
 *  mezz.jei.api.recipe.IRecipeCategory
 *  mezz.jei.api.recipe.IRecipeWrapper
 *  net.minecraft.client.Minecraft
 *  net.minecraft.item.ItemStack
 */
package ic2.jeiIntegration.recipe.machine;

import ic2.core.block.state.IIdProvider;
import ic2.core.ref.BlockName;
import ic2.core.ref.TeBlock;
import ic2.jeiIntegration.SlotPosition;
import java.util.List;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

public abstract class IORecipeCategory<T>
implements IRecipeCategory {
    final TeBlock block;
    final T recipeManager;

    public IORecipeCategory(TeBlock block, T recipeManager) {
        this.block = block;
        this.recipeManager = recipeManager;
    }

    public String getUid() {
        return this.block.getName();
    }

    public String getTitle() {
        return BlockName.te.getItemStack(this.block).getDisplayName();
    }

    public void drawExtras(Minecraft minecraft) {
    }

    public void drawAnimations(Minecraft minecraft) {
    }

    protected abstract List<SlotPosition> getInputSlotPos();

    protected abstract List<SlotPosition> getOutputSlotPos();

    protected abstract List<List<ItemStack>> getInputStacks(IRecipeWrapper var1);

    protected abstract List<ItemStack> getOutputStacks(IRecipeWrapper var1);

    public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper) {
        int idx;
        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
        List<SlotPosition> inputSlots = this.getInputSlotPos();
        List<List<ItemStack>> inputStacks = this.getInputStacks(recipeWrapper);
        for (idx = 0; idx < inputSlots.size(); ++idx) {
            SlotPosition pos = inputSlots.get(idx);
            itemStacks.init(idx, true, pos.getX(), pos.getY());
            if (idx >= inputStacks.size()) continue;
            itemStacks.setFromRecipe(idx, inputStacks.get(idx));
        }
        List<SlotPosition> outputSlots = this.getOutputSlotPos();
        List<ItemStack> outputStacks = this.getOutputStacks(recipeWrapper);
        int i = 0;
        while (i < outputSlots.size()) {
            SlotPosition pos = outputSlots.get(i);
            itemStacks.init(idx, false, pos.getX(), pos.getY());
            if (i < outputStacks.size()) {
                itemStacks.setFromRecipe(idx, (Object)outputStacks.get(i));
            }
            ++i;
            ++idx;
        }
    }

    public TeBlock getBlock() {
        return this.block;
    }
}

