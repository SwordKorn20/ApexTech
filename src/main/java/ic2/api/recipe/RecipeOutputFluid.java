/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraftforge.fluids.FluidStack
 */
package ic2.api.recipe;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

public class RecipeOutputFluid {
    public final List<FluidStack> outputs;
    public final NBTTagCompound metadata;

    public RecipeOutputFluid(NBTTagCompound metadata, List<FluidStack> outputs) {
        assert (!outputs.contains(null));
        this.metadata = metadata;
        this.outputs = outputs;
    }

    public /* varargs */ RecipeOutputFluid(NBTTagCompound metadata, FluidStack ... outputs) {
        this(metadata, Arrays.asList(outputs));
    }

    public boolean equals(Object obj) {
        if (obj instanceof RecipeOutputFluid) {
            RecipeOutputFluid ro = (RecipeOutputFluid)obj;
            if (this.outputs.size() == ro.outputs.size() && (this.metadata == null && ro.metadata == null || this.metadata != null && ro.metadata != null && this.metadata.equals((Object)ro.metadata))) {
                Iterator<FluidStack> itA = this.outputs.iterator();
                Iterator<FluidStack> itB = ro.outputs.iterator();
                while (itA.hasNext() && itB.hasNext()) {
                    FluidStack stackB;
                    FluidStack stackA = itA.next();
                    if (!stackA.isFluidStackIdentical(stackB = itB.next())) continue;
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    public String toString() {
        return "ROutput<" + this.outputs + "," + (Object)this.metadata + ">";
    }
}

