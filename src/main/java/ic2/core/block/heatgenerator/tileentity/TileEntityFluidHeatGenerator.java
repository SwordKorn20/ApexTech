/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.EnumFacing
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidStack
 *  net.minecraftforge.fluids.FluidTank
 *  net.minecraftforge.fluids.FluidTankInfo
 *  net.minecraftforge.fluids.IFluidHandler
 *  net.minecraftforge.fluids.IFluidTank
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package ic2.core.block.heatgenerator.tileentity;

import ic2.api.recipe.IFluidHeatManager;
import ic2.api.recipe.ILiquidAcceptManager;
import ic2.api.recipe.ISemiFluidFuelManager;
import ic2.api.recipe.Recipes;
import ic2.core.ContainerBase;
import ic2.core.FluidHeatManager;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityHeatSourceInventory;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.heatgenerator.container.ContainerFluidHeatGenerator;
import ic2.core.block.heatgenerator.gui.GuiFluidHeatGenerator;
import ic2.core.block.invslot.InvSlotConsumableLiquid;
import ic2.core.block.invslot.InvSlotConsumableLiquidByManager;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.init.MainConfig;
import ic2.core.util.ConfigUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.mutable.MutableObject;

public class TileEntityFluidHeatGenerator
extends TileEntityHeatSourceInventory
implements IHasGui,
IFluidHandler {
    public final InvSlotConsumableLiquid fluidSlot;
    public final InvSlotOutput outputSlot;
    protected final FluidTank fluidTank;
    private short ticker = 0;
    protected int burnAmount = 0;
    protected int production = 0;
    boolean newActive = false;

    public TileEntityFluidHeatGenerator() {
        this.fluidSlot = new InvSlotConsumableLiquidByManager(this, "fluidSlot", 1, Recipes.fluidHeatGenerator);
        this.outputSlot = new InvSlotOutput(this, "output", 1);
        this.fluidTank = new FluidTank(10000);
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        boolean needsInvUpdate = false;
        if (this.needsFluid()) {
            needsInvUpdate = this.gainFuel();
        }
        if (needsInvUpdate) {
            this.markDirty();
        }
        if (this.getActive() != this.newActive) {
            this.setActive(this.newActive);
        }
    }

    public boolean isConverting() {
        return this.getTankAmount() > 0 && this.HeatBuffer < this.getMaxHeatEmittedPerTick();
    }

    public static void init() {
        Recipes.fluidHeatGenerator = new FluidHeatManager();
        if (ConfigUtil.getFloat(MainConfig.get(), "balance/energy/generator/semiFluidOil") > 0.0f) {
            TileEntityFluidHeatGenerator.addFuel("oil", 10, Math.round(16.0f * ConfigUtil.getFloat(MainConfig.get(), "balance/energy/heatgenerator/semiFluidOil")));
        }
        if (ConfigUtil.getFloat(MainConfig.get(), "balance/energy/generator/semiFluidFuel") > 0.0f) {
            TileEntityFluidHeatGenerator.addFuel("fuel", 5, Math.round(64.0f * ConfigUtil.getFloat(MainConfig.get(), "balance/energy/heatgenerator/semiFluidFuel")));
        }
        if (ConfigUtil.getFloat(MainConfig.get(), "balance/energy/generator/semiFluidBiomass") > 0.0f) {
            TileEntityFluidHeatGenerator.addFuel("biomass", 20, Math.round(16.0f * ConfigUtil.getFloat(MainConfig.get(), "balance/energy/heatgenerator/semiFluidBiomass")));
        }
        if (ConfigUtil.getFloat(MainConfig.get(), "balance/energy/generator/semiFluidBioethanol") > 0.0f) {
            TileEntityFluidHeatGenerator.addFuel("bioethanol", 10, Math.round(32.0f * ConfigUtil.getFloat(MainConfig.get(), "balance/energy/heatgenerator/semiFluidBioethanol")));
        }
        if (ConfigUtil.getFloat(MainConfig.get(), "balance/energy/generator/semiFluidBiogas") > 0.0f) {
            TileEntityFluidHeatGenerator.addFuel("ic2biogas", 10, Math.round(32.0f * ConfigUtil.getFloat(MainConfig.get(), "balance/energy/heatgenerator/semiFluidBiogas")));
        }
    }

    public static void addFuel(String fluidName, int amount, int heat) {
        Recipes.fluidHeatGenerator.addFluid(fluidName, amount, heat);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        this.fluidTank.readFromNBT(nbttagcompound.getCompoundTag("fluidTank"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        NBTTagCompound fluidTankTag = new NBTTagCompound();
        this.fluidTank.writeToNBT(fluidTankTag);
        nbt.setTag("fluidTank", (NBTBase)fluidTankTag);
        return nbt;
    }

    @Override
    protected int fillHeatBuffer(int maxAmount) {
        if (this.isConverting()) {
            if (this.ticker >= 19) {
                this.getFluidTank().drain(this.burnAmount, true);
                this.ticker = 0;
            } else {
                this.ticker = (short)(this.ticker + 1);
            }
            this.newActive = true;
            return this.production;
        }
        this.newActive = false;
        return 0;
    }

    @Override
    public int getMaxHeatEmittedPerTick() {
        return this.calcHeatProduction();
    }

    public ContainerBase<TileEntityFluidHeatGenerator> getGuiContainer(EntityPlayer player) {
        return new ContainerFluidHeatGenerator(player, this);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return new GuiFluidHeatGenerator(new ContainerFluidHeatGenerator(player, this));
    }

    @Override
    public void onGuiClosed(EntityPlayer player) {
    }

    protected int calcHeatProduction() {
        IFluidHeatManager.BurnProperty property;
        if (this.fluidTank.getFluid() != null && this.getFluidfromTank() != null && (property = Recipes.fluidHeatGenerator.getBurnProperty(this.getFluidfromTank())) != null) {
            this.production = property.heat;
            return this.production;
        }
        this.production = 0;
        return 0;
    }

    protected void calcBurnAmount() {
        IFluidHeatManager.BurnProperty property;
        if (this.getFluidfromTank() != null && (property = Recipes.fluidHeatGenerator.getBurnProperty(this.getFluidfromTank())) != null) {
            this.burnAmount = property.amount;
            return;
        }
        this.burnAmount = 0;
    }

    public FluidTank getFluidTank() {
        return this.fluidTank;
    }

    public FluidStack getFluidStackfromTank() {
        return this.getFluidTank().getFluid();
    }

    public Fluid getFluidfromTank() {
        return this.getFluidStackfromTank().getFluid();
    }

    public int getTankAmount() {
        return this.getFluidTank().getFluidAmount();
    }

    public int gaugeLiquidScaled(int i) {
        if (this.getFluidTank().getFluidAmount() <= 0) {
            return 0;
        }
        return this.getFluidTank().getFluidAmount() * i / this.getFluidTank().getCapacity();
    }

    public boolean needsFluid() {
        return this.getFluidTank().getFluidAmount() <= this.getFluidTank().getCapacity();
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
        return this.getFluidTank().drain(maxDrain, doDrain);
    }

    public FluidTankInfo[] getTankInfo(EnumFacing from) {
        return new FluidTankInfo[]{this.getFluidTank().getInfo()};
    }

    public boolean canFill(EnumFacing from, Fluid fluid) {
        if (!Recipes.semiFluidGenerator.acceptsFluid(fluid)) {
            return false;
        }
        if (this.getFluidTank().getFluid() == null) {
            return true;
        }
        return this.getFluidTank().getFluid().getFluid() == fluid && this.getTankAmount() < this.getFluidTank().getCapacity();
    }

    public boolean canDrain(EnumFacing from, Fluid fluid) {
        return false;
    }

    protected boolean gainFuel() {
        if (this.fluidTank.getFluid() != null) {
            this.calcHeatProduction();
            this.calcBurnAmount();
        }
        boolean ret = false;
        MutableObject output = new MutableObject();
        if (this.fluidSlot.transferToTank((IFluidTank)this.fluidTank, output, true) && (output.getValue() == null || this.outputSlot.canAdd((ItemStack)output.getValue()))) {
            ret = this.fluidSlot.transferToTank((IFluidTank)this.fluidTank, output, false);
            if (output.getValue() != null) {
                this.outputSlot.add((ItemStack)output.getValue());
            }
        }
        return ret;
    }
}

