/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 */
package ic2.api.recipe;

import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeOutput;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public interface IMachineRecipeManager {
    public /* varargs */ boolean addRecipe(IRecipeInput var1, NBTTagCompound var2, boolean var3, ItemStack ... var4);

    public RecipeOutput getOutputFor(ItemStack var1, boolean var2);

    public Iterable<RecipeIoContainer> getRecipes();

    public boolean isIterable();

    public static class RecipeIoContainer {
        public final IRecipeInput input;
        public final RecipeOutput output;

        public RecipeIoContainer(IRecipeInput input, RecipeOutput output) {
            this.input = input;
            this.output = output;
        }
    }

}

