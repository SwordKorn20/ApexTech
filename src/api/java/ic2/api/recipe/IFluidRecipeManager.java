/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraftforge.fluids.FluidStack
 */
package ic2.api.recipe;

import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeOutputFluid;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

public interface IFluidRecipeManager {
    public /* varargs */ boolean addRecipe(IRecipeInput var1, NBTTagCompound var2, boolean var3, FluidStack ... var4);

    public RecipeOutputFluid getOutputFor(ItemStack var1, boolean var2);

    public Iterable<RecipeIoContainerFluid> getRecipes();

    public boolean isIterable();

    public static class RecipeIoContainerFluid {
        public final IRecipeInput input;
        public final RecipeOutputFluid output;

        public RecipeIoContainerFluid(IRecipeInput input, RecipeOutputFluid output) {
            this.input = input;
            this.output = output;
        }
    }

}

