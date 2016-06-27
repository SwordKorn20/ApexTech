/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.EnumFacing
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidTank
 *  net.minecraftforge.fluids.IFluidTank
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package ic2.core.block.machine.tileentity;

import ic2.api.recipe.RecipeOutput;
import ic2.core.ContainerBase;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.TileEntityLiquidTankStandardMaschine;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumableLiquid;
import ic2.core.block.invslot.InvSlotConsumableLiquidByTank;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.block.machine.container.ContainerFluidBottler;
import ic2.core.block.machine.gui.GuiFluidBottler;
import ic2.core.upgrade.UpgradableProperty;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.mutable.MutableObject;

public class TileEntityFluidBottler
extends TileEntityLiquidTankStandardMaschine {
    public final InvSlotConsumableLiquid drainInputSlot;
    public final InvSlotConsumableLiquid fillInputSlot;

    public TileEntityFluidBottler() {
        super(2, 100, 1, 8);
        this.drainInputSlot = new InvSlotConsumableLiquidByTank(this, "drainInput", InvSlot.Access.I, 1, InvSlot.InvSide.TOP, InvSlotConsumableLiquid.OpType.Drain, (IFluidTank)this.fluidTank);
        this.fillInputSlot = new InvSlotConsumableLiquidByTank(this, "fillInput", InvSlot.Access.I, 1, InvSlot.InvSide.BOTTOM, InvSlotConsumableLiquid.OpType.Fill, (IFluidTank)this.fluidTank);
    }

    @Override
    public RecipeOutput getOutput() {
        return this.processInput(true);
    }

    @Override
    public void operateOnce(RecipeOutput output, List<ItemStack> processResult) {
        this.processInput(false);
        this.outputSlot.add(processResult);
    }

    public ContainerBase<TileEntityFluidBottler> getGuiContainer(EntityPlayer player) {
        return new ContainerFluidBottler(player, this);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return new GuiFluidBottler(new ContainerFluidBottler(player, this));
    }

    @Override
    public boolean canFill(EnumFacing from, Fluid fluid) {
        return true;
    }

    @Override
    public boolean canDrain(EnumFacing from, Fluid fluid) {
        return true;
    }

    @Override
    public void onGuiClosed(EntityPlayer player) {
    }

    private RecipeOutput processInput(boolean simulate) {
        MutableObject output;
        if (!this.drainInputSlot.isEmpty() && this.drainInputSlot.transferToTank((IFluidTank)this.fluidTank, output = new MutableObject(), simulate) && (output.getValue() == null || this.outputSlot.canAdd((ItemStack)output.getValue()))) {
            if (output.getValue() == null) {
                return new RecipeOutput(null, new ItemStack[0]);
            }
            return new RecipeOutput(null, (ItemStack)output.getValue());
        }
        if (!this.fillInputSlot.isEmpty() && this.fillInputSlot.transferFromTank((IFluidTank)this.fluidTank, output = new MutableObject(), simulate) && (output.getValue() == null || this.outputSlot.canAdd((ItemStack)output.getValue()))) {
            if (output.getValue() == null) {
                return new RecipeOutput(null, new ItemStack[0]);
            }
            return new RecipeOutput(null, (ItemStack)output.getValue());
        }
        return null;
    }

    @Override
    public Set<UpgradableProperty> getUpgradableProperties() {
        return EnumSet.of(UpgradableProperty.Processing, new UpgradableProperty[]{UpgradableProperty.Transformer, UpgradableProperty.EnergyStorage, UpgradableProperty.ItemConsuming, UpgradableProperty.ItemProducing, UpgradableProperty.FluidConsuming, UpgradableProperty.FluidProducing});
    }
}

