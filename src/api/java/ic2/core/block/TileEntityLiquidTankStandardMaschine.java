/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.EnumFacing
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidStack
 *  net.minecraftforge.fluids.FluidTank
 *  net.minecraftforge.fluids.FluidTankInfo
 *  net.minecraftforge.fluids.IFluidHandler
 *  net.minecraftforge.fluids.IFluidTank
 */
package ic2.core.block;

import ic2.core.block.machine.tileentity.TileEntityStandardMachine;
import ic2.core.gui.dynamic.IFluidTankProvider;
import ic2.core.network.GuiSynced;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;

public abstract class TileEntityLiquidTankStandardMaschine
extends TileEntityStandardMachine
implements IFluidHandler,
IFluidTankProvider {
    @GuiSynced
    protected final FluidTank fluidTank;

    public TileEntityLiquidTankStandardMaschine(int energyPerTick, int length, int outputSlots, int tanksize) {
        super(energyPerTick, length, outputSlots);
        this.fluidTank = new FluidTank(1000 * tanksize);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.fluidTank.readFromNBT(nbt.getCompoundTag("fluidTank"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        NBTTagCompound fluidTankTag = new NBTTagCompound();
        this.fluidTank.writeToNBT(fluidTankTag);
        nbt.setTag("fluidTank", (NBTBase)fluidTankTag);
        return nbt;
    }

    public FluidTank getFluidTank() {
        return this.fluidTank;
    }

    @Override
    public IFluidTank getFluidTank(String name) {
        if ("fluid".equals(name)) {
            return this.getFluidTank();
        }
        throw new IllegalArgumentException("Invalid fluid tank name: " + name);
    }

    public int gaugeLiquidScaled(int i) {
        if (this.getFluidTank().getFluidAmount() <= 0) {
            return 0;
        }
        return this.getFluidTank().getFluidAmount() * i / this.getFluidTank().getCapacity();
    }

    public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
        if (this.canFill(from, resource.getFluid())) {
            return this.getFluidTank().fill(resource, doFill);
        }
        return 0;
    }

    public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
        if (resource == null || !resource.isFluidEqual(this.getFluidTank().getFluid())) {
            return null;
        }
        if (!this.canDrain(from, resource.getFluid())) {
            return null;
        }
        return this.getFluidTank().drain(resource.amount, doDrain);
    }

    public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
        if (!this.canDrain(from, null)) {
            return null;
        }
        return this.getFluidTank().drain(maxDrain, doDrain);
    }

    public FluidTankInfo[] getTankInfo(EnumFacing from) {
        return new FluidTankInfo[]{this.getFluidTank().getInfo()};
    }

    public abstract boolean canFill(EnumFacing var1, Fluid var2);

    public abstract boolean canDrain(EnumFacing var1, Fluid var2);
}
