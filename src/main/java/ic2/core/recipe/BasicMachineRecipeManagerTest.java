/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 */
package ic2.core.recipe;

import ic2.api.recipe.IMachineRecipeManager;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeOutput;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class BasicMachineRecipeManagerTest
implements IMachineRecipeManager {
    private List<IMachineRecipeManager.RecipeIoContainer> recipes = new LinkedList<IMachineRecipeManager.RecipeIoContainer>();

    @Override
    public /* varargs */ boolean addRecipe(IRecipeInput input, NBTTagCompound metadata, boolean replace, ItemStack ... outputs) {
        if (replace) {
            this.recipes.add(0, new IMachineRecipeManager.RecipeIoContainer(input, new RecipeOutput(metadata, outputs)));
        } else if (this.getCollidingRecipe(input) == null) {
            this.recipes.add(new IMachineRecipeManager.RecipeIoContainer(input, new RecipeOutput(metadata, outputs)));
        } else {
            return false;
        }
        return true;
    }

    @Override
    public RecipeOutput getOutputFor(ItemStack input, boolean adjustInput) {
        IMachineRecipeManager.RecipeIoContainer recipe = this.getRecipe(input, true);
        if (recipe == null) {
            return null;
        }
        if (adjustInput) {
            if (input.getItem().hasContainerItem(input)) {
                ItemStack container = input.getItem().getContainerItem(input);
                input.setItem(container.getItem());
                input.setItemDamage(container.getItemDamage());
                input.stackSize = container.stackSize;
                input.setTagCompound(container.getTagCompound());
            } else {
                input.stackSize -= recipe.input.getAmount();
            }
        }
        return recipe.output;
    }

    private IMachineRecipeManager.RecipeIoContainer getCollidingRecipe(IRecipeInput input) {
        for (ItemStack itemStackIn : input.getInputs()) {
            IMachineRecipeManager.RecipeIoContainer recipe = this.getRecipe(itemStackIn, false);
            if (recipe == null) continue;
            return recipe;
        }
        return null;
    }

    private IMachineRecipeManager.RecipeIoContainer getRecipe(ItemStack stack, boolean checkAmount) {
        for (IMachineRecipeManager.RecipeIoContainer container : this.recipes) {
            if (!container.input.matches(stack)) continue;
            if (!checkAmount) {
                return container;
            }
            if (stack.stackSize < container.input.getAmount() || stack.getItem().hasContainerItem(stack) && stack.stackSize != container.input.getAmount()) continue;
            return container;
        }
        return null;
    }

    @Override
    public Iterable<IMachineRecipeManager.RecipeIoContainer> getRecipes() {
        return this.recipes;
    }

    @Override
    public boolean isIterable() {
        return true;
    }
}

