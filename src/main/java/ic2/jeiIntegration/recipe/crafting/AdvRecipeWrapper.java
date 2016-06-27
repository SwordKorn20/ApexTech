/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  mezz.jei.api.recipe.BlankRecipeWrapper
 *  mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 */
package ic2.jeiIntegration.recipe.crafting;

import ic2.api.item.IElectricItem;
import ic2.api.recipe.IRecipeInput;
import ic2.core.recipe.AdvRecipe;
import ic2.core.util.StackUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class AdvRecipeWrapper
extends BlankRecipeWrapper
implements IShapedCraftingRecipeWrapper {
    private final AdvRecipe recipe;

    public AdvRecipeWrapper(AdvRecipe recipe) {
        this.recipe = recipe;
    }

    public List<List<ItemStack>> getInputs() {
        int mask = this.recipe.masks[0];
        int itemIndex = 0;
        ArrayList<IRecipeInput> ret = new ArrayList<IRecipeInput>();
        for (int i = 0; i < 9; ++i) {
            if (i % 3 >= this.recipe.inputWidth || i / 3 >= this.recipe.inputHeight) continue;
            if ((mask >>> 8 - i & 1) != 0) {
                ret.add(this.recipe.input[itemIndex++]);
                continue;
            }
            ret.add(null);
        }
        return AdvRecipeWrapper.replaceRecipeInputs(ret);
    }

    public List<ItemStack> getOutputs() {
        return Collections.singletonList(this.recipe.getRecipeOutput());
    }

    public int getWidth() {
        return this.recipe.inputWidth;
    }

    public int getHeight() {
        return this.recipe.inputHeight;
    }

    public static List<List<ItemStack>> replaceRecipeInputs(List<IRecipeInput> list) {
        ArrayList<List<ItemStack>> out = new ArrayList<List<ItemStack>>(list.size());
        for (IRecipeInput recipe : list) {
            if (recipe == null) {
                out.add(null);
                continue;
            }
            List<ItemStack> replace = recipe.getInputs();
            for (int i = 0; i < replace.size(); ++i) {
                ItemStack stack = replace.get(i);
                if (stack == null || !(stack.getItem() instanceof IElectricItem)) continue;
                replace.set(i, StackUtil.copyWithWildCard(stack));
            }
            out.add(replace);
        }
        return out;
    }
}

