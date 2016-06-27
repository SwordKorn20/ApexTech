/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidEvent
 *  net.minecraftforge.fluids.FluidEvent$FluidSpilledEvent
 *  net.minecraftforge.fluids.FluidRegistry
 *  net.minecraftforge.fluids.FluidStack
 *  net.minecraftforge.fluids.FluidTank
 *  net.minecraftforge.fluids.IFluidTank
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package ic2.core.block.generator.tileentity;

import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.Platform;
import ic2.core.audio.AudioManager;
import ic2.core.audio.AudioSource;
import ic2.core.audio.PositionSpec;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.TileEntityLiquidTankInventory;
import ic2.core.block.comp.Energy;
import ic2.core.block.comp.TileEntityComponent;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotCharge;
import ic2.core.block.invslot.InvSlotConsumableLiquid;
import ic2.core.block.invslot.InvSlotConsumableLiquidByList;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.gui.dynamic.DynamicContainer;
import ic2.core.gui.dynamic.DynamicGui;
import ic2.core.gui.dynamic.GuiParser;
import ic2.core.init.MainConfig;
import ic2.core.ref.TeBlock;
import ic2.core.util.ConfigUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidEvent;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.mutable.MutableObject;

public class TileEntityGeoGenerator
extends TileEntityLiquidTankInventory
implements IHasGui {
    private static final int fluidPerTick = 2;
    public final InvSlotCharge chargeSlot;
    public final InvSlotConsumableLiquid fluidSlot;
    public final InvSlotOutput outputSlot;
    protected final Energy energy;
    public final int production = Math.round(20.0f * ConfigUtil.getFloat(MainConfig.get(), "balance/energy/generator/geothermal"));
    public AudioSource audioSource;

    public TileEntityGeoGenerator() {
        super(8);
        this.chargeSlot = new InvSlotCharge(this, 1);
        this.fluidSlot = new InvSlotConsumableLiquidByList((TileEntityInventory)this, "fluidSlot", 1, FluidRegistry.LAVA);
        this.outputSlot = new InvSlotOutput(this, "output", 1);
        this.energy = this.addComponent(Energy.asBasicSource(this, 24000.0).addManagedSlot(this.chargeSlot));
    }

    @Override
    protected void updateEntityServer() {
        MutableObject output;
        super.updateEntityServer();
        boolean needsInvUpdate = false;
        if (this.needsFluid() && this.fluidSlot.transferToTank((IFluidTank)this.fluidTank, output = new MutableObject(), true) && (output.getValue() == null || this.outputSlot.canAdd((ItemStack)output.getValue()))) {
            needsInvUpdate = this.fluidSlot.transferToTank((IFluidTank)this.fluidTank, output, false);
            if (output.getValue() != null) {
                this.outputSlot.add((ItemStack)output.getValue());
            }
        }
        boolean newActive = this.gainEnergy();
        if (needsInvUpdate) {
            this.markDirty();
        }
        if (this.getActive() != newActive) {
            this.setActive(newActive);
        }
    }

    @Override
    protected void onUnloaded() {
        if (IC2.platform.isRendering() && this.audioSource != null) {
            IC2.audioManager.removeSources(this);
            this.audioSource = null;
        }
        super.onUnloaded();
    }

    @Override
    public void onGuiClosed(EntityPlayer player) {
    }

    @Override
    public void onNetworkUpdate(String field) {
        if (field.equals("active")) {
            if (this.audioSource == null && this.getOperationSoundFile() != null) {
                this.audioSource = IC2.audioManager.createSource(this, PositionSpec.Center, this.getOperationSoundFile(), true, false, IC2.audioManager.getDefaultVolume());
            }
            if (this.getActive()) {
                if (this.audioSource != null) {
                    this.audioSource.play();
                }
            } else if (this.audioSource != null) {
                this.audioSource.stop();
            }
        }
        super.onNetworkUpdate(field);
    }

    public boolean gainEnergy() {
        if (this.isConverting()) {
            this.energy.addEnergy(this.production);
            this.getFluidTank().drain(2, true);
            return true;
        }
        return false;
    }

    @Override
    public boolean canFill(EnumFacing from, Fluid fluid) {
        return fluid == FluidRegistry.LAVA;
    }

    @Override
    public boolean canDrain(EnumFacing from, Fluid fluid) {
        return false;
    }

    public boolean isConverting() {
        return this.getTankAmount() >= 2 && this.energy.getEnergy() + (double)this.production <= this.energy.getCapacity();
    }

    public String getOperationSoundFile() {
        return "Generators/GeothermalLoop.ogg";
    }

    public ContainerBase<TileEntityGeoGenerator> getGuiContainer(EntityPlayer player) {
        return DynamicContainer.create(this, player, GuiParser.parse(this.teBlock));
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return DynamicGui.create(this, player, GuiParser.parse(this.teBlock));
    }

    @Override
    protected void onBlockBreak() {
        super.onBlockBreak();
        FluidEvent.fireEvent((FluidEvent)new FluidEvent.FluidSpilledEvent(new FluidStack(FluidRegistry.LAVA, 1000), this.worldObj, this.pos));
    }
}

