/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidContainerRegistry
 *  net.minecraftforge.fluids.FluidContainerRegistry$FluidContainerData
 *  net.minecraftforge.fluids.FluidStack
 *  net.minecraftforge.fluids.IFluidContainerItem
 */
package ic2.api.recipe;

import ic2.api.item.IC2Items;
import ic2.api.recipe.IRecipeInput;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

public class RecipeInputFluidContainer
implements IRecipeInput {
    public final Fluid fluid;
    public final int amount;

    public RecipeInputFluidContainer(Fluid fluid) {
        this(fluid, 1000);
    }

    public RecipeInputFluidContainer(Fluid fluid, int amount) {
        this.fluid = fluid;
        this.amount = amount;
    }

    @Override
    public boolean matches(ItemStack subject) {
        FluidStack fs = FluidContainerRegistry.getFluidForFilledItem((ItemStack)subject);
        if (fs == null && subject.getItem() instanceof IFluidContainerItem) {
            IFluidContainerItem item = (IFluidContainerItem)subject.getItem();
            fs = item.getFluid(subject);
        }
        return fs == null && this.fluid == null || fs != null && fs.getFluid() == this.fluid && fs.amount >= this.amount;
    }

    @Override
    public int getAmount() {
        return this.amount;
    }

    @Override
    public List<ItemStack> getInputs() {
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        for (FluidContainerRegistry.FluidContainerData data : FluidContainerRegistry.getRegisteredFluidContainerData()) {
            if (data.fluid.getFluid() != this.fluid) continue;
            ret.add(data.filledContainer);
        }
        ret.add(IC2Items.getItem("fluid_cell", this.fluid.getName()));
        return ret;
    }

    public String toString() {
        return "RInputFluidContainer<" + this.amount + "x" + this.fluid.getName() + ">";
    }
}

