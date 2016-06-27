/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidStack
 *  net.minecraftforge.fluids.FluidTank
 *  net.minecraftforge.fluids.IFluidTank
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package ic2.core.block.machine.tileentity;

import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.api.recipe.RecipeOutput;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.TileEntityLiquidTankElectricMachine;
import ic2.core.block.comp.Energy;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumableLiquid;
import ic2.core.block.invslot.InvSlotConsumableLiquidByTank;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.block.machine.container.ContainerFluidRegulator;
import ic2.core.block.machine.gui.GuiFluidRegulator;
import ic2.core.init.Localization;
import ic2.core.util.LiquidUtil;
import java.util.List;
import java.util.Random;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.mutable.MutableObject;

public class TileEntityFluidRegulator
extends TileEntityLiquidTankElectricMachine
implements IHasGui,
INetworkClientTileEntityEventListener {
    private int mode;
    private int updateTicker;
    private int outputmb;
    private boolean newActive;
    public final InvSlotOutput wasseroutputSlot;
    public final InvSlotConsumableLiquidByTank wasserinputSlot;

    public TileEntityFluidRegulator() {
        super(10000, 4, 10);
        this.wasserinputSlot = new InvSlotConsumableLiquidByTank(this, "wasserinputSlot", InvSlot.Access.I, 1, InvSlot.InvSide.TOP, InvSlotConsumableLiquid.OpType.Drain, (IFluidTank)this.fluidTank);
        this.wasseroutputSlot = new InvSlotOutput(this, "wasseroutputSlot", 1);
        this.newActive = false;
        this.outputmb = 0;
        this.mode = 0;
        this.updateTicker = IC2.random.nextInt(this.getTickRate());
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.outputmb = nbt.getInteger("outputmb");
        this.mode = nbt.getInteger("mode");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("outputmb", this.outputmb);
        nbt.setInteger("mode", this.mode);
        return nbt;
    }

    private RecipeOutput processInputSlot(boolean simulate) {
        MutableObject output;
        if (!this.wasserinputSlot.isEmpty() && this.wasserinputSlot.transferToTank((IFluidTank)this.fluidTank, output = new MutableObject(), simulate) && (output.getValue() == null || this.wasseroutputSlot.canAdd((ItemStack)output.getValue()))) {
            if (output.getValue() == null) {
                return new RecipeOutput(null, new ItemStack[0]);
            }
            return new RecipeOutput(null, (ItemStack)output.getValue());
        }
        return null;
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        RecipeOutput outputinputSlot = this.processInputSlot(true);
        if (outputinputSlot != null) {
            this.processInputSlot(false);
            List<ItemStack> processResult = outputinputSlot.items;
            this.wasseroutputSlot.add(processResult);
        }
        if (this.updateTicker++ % this.getTickRate() != 0 && this.mode == 0) {
            return;
        }
        this.newActive = this.work();
        if (this.getActive() != this.newActive) {
            this.setActive(this.newActive);
        }
    }

    private boolean work() {
        int amount;
        EnumFacing side;
        if (this.outputmb == 0) {
            return false;
        }
        if (this.energy.getEnergy() < 10.0) {
            return false;
        }
        if (this.fluidTank.getFluidAmount() <= 0) {
            return false;
        }
        EnumFacing dir = this.getFacing();
        TileEntity te = this.worldObj.getTileEntity(this.pos.offset(dir));
        if (LiquidUtil.isFluidTile(te, side = dir.getOpposite()) && (amount = LiquidUtil.fillTile(te, side, this.getFluidTank().drain(this.outputmb, false), false)) > 0) {
            this.getFluidTank().drain(this.outputmb, true);
            this.energy.useEnergy(10.0);
            return true;
        }
        return false;
    }

    @Override
    public void onNetworkEvent(EntityPlayer player, int event) {
        if (event == 1001 || event == 1002) {
            if (event == 1001 && this.mode == 0) {
                this.mode = 1;
            }
            if (event == 1002 && this.mode == 1) {
                this.mode = 0;
            }
            return;
        }
        this.outputmb += event;
        if (this.outputmb > 1000) {
            this.outputmb = 1000;
        }
        if (this.outputmb < 0) {
            this.outputmb = 0;
        }
    }

    public int getTickRate() {
        return 20;
    }

    public ContainerBase<TileEntityFluidRegulator> getGuiContainer(EntityPlayer player) {
        return new ContainerFluidRegulator(player, this);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return new GuiFluidRegulator(new ContainerFluidRegulator(player, this));
    }

    @Override
    public void onGuiClosed(EntityPlayer player) {
    }

    public int gaugeLiquidScaled(int i, int tank) {
        switch (tank) {
            case 0: {
                if (this.fluidTank.getFluidAmount() <= 0) {
                    return 0;
                }
                return this.fluidTank.getFluidAmount() * i / this.fluidTank.getCapacity();
            }
        }
        return 0;
    }

    @Override
    public boolean canFill(EnumFacing from, Fluid fluid) {
        if (from == this.getFacing()) {
            return false;
        }
        return true;
    }

    @Override
    public boolean canDrain(EnumFacing from, Fluid fluid) {
        return false;
    }

    public int getoutputmb() {
        return this.outputmb;
    }

    public String getmodegui() {
        switch (this.mode) {
            case 0: {
                return Localization.translate("ic2.generic.text.sec");
            }
            case 1: {
                return Localization.translate("ic2.generic.text.tick");
            }
        }
        return "";
    }
}

