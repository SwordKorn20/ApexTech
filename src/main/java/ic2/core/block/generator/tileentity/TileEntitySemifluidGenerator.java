/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumFacing
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidStack
 *  net.minecraftforge.fluids.FluidTank
 *  net.minecraftforge.fluids.IFluidTank
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package ic2.core.block.generator.tileentity;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItemManager;
import ic2.api.recipe.ILiquidAcceptManager;
import ic2.api.recipe.ISemiFluidFuelManager;
import ic2.api.recipe.Recipes;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.Platform;
import ic2.core.SemiFluidFuelManager;
import ic2.core.audio.AudioManager;
import ic2.core.audio.AudioSource;
import ic2.core.audio.PositionSpec;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.TileEntityLiquidTankInventory;
import ic2.core.block.comp.Energy;
import ic2.core.block.comp.TileEntityComponent;
import ic2.core.block.invslot.InvSlotCharge;
import ic2.core.block.invslot.InvSlotConsumableLiquid;
import ic2.core.block.invslot.InvSlotConsumableLiquidByManager;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.gui.dynamic.DynamicContainer;
import ic2.core.gui.dynamic.DynamicGui;
import ic2.core.gui.dynamic.GuiParser;
import ic2.core.gui.dynamic.IFluidTankProvider;
import ic2.core.init.MainConfig;
import ic2.core.ref.TeBlock;
import ic2.core.util.ConfigUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.mutable.MutableObject;

public class TileEntitySemifluidGenerator
extends TileEntityLiquidTankInventory
implements IHasGui,
IFluidTankProvider {
    public final InvSlotCharge chargeSlot;
    public final InvSlotConsumableLiquid fluidSlot;
    public final InvSlotOutput outputSlot;
    private short ticker = 0;
    protected int burnAmount = 0;
    protected double production = 0.0;
    protected AudioSource audioSource;
    private final Energy energy;

    public TileEntitySemifluidGenerator() {
        super(10);
        this.chargeSlot = new InvSlotCharge(this, 1);
        this.fluidSlot = new InvSlotConsumableLiquidByManager(this, "fluidSlot", 1, Recipes.semiFluidGenerator);
        this.outputSlot = new InvSlotOutput(this, "output", 1);
        this.energy = this.addComponent(Energy.asBasicSource(this, 32000.0, 1));
    }

    public static void init() {
        Recipes.semiFluidGenerator = new SemiFluidFuelManager();
        if (ConfigUtil.getFloat(MainConfig.get(), "balance/energy/generator/semiFluidOil") > 0.0f) {
            TileEntitySemifluidGenerator.addFuel("oil", 10, Math.round(8.0f * ConfigUtil.getFloat(MainConfig.get(), "balance/energy/generator/semiFluidOil")));
        }
        if (ConfigUtil.getFloat(MainConfig.get(), "balance/energy/generator/semiFluidFuel") > 0.0f) {
            TileEntitySemifluidGenerator.addFuel("fuel", 5, Math.round(32.0f * ConfigUtil.getFloat(MainConfig.get(), "balance/energy/generator/semiFluidFuel")));
        }
        if (ConfigUtil.getFloat(MainConfig.get(), "balance/energy/generator/semiFluidBiomass") > 0.0f) {
            TileEntitySemifluidGenerator.addFuel("biomass", 20, Math.round(8.0f * ConfigUtil.getFloat(MainConfig.get(), "balance/energy/generator/semiFluidBiomass")));
        }
        if (ConfigUtil.getFloat(MainConfig.get(), "balance/energy/generator/semiFluidBioethanol") > 0.0f) {
            TileEntitySemifluidGenerator.addFuel("bioethanol", 10, Math.round(16.0f * ConfigUtil.getFloat(MainConfig.get(), "balance/energy/generator/semiFluidBioethanol")));
        }
        if (ConfigUtil.getFloat(MainConfig.get(), "balance/energy/generator/semiFluidBiogas") > 0.0f) {
            TileEntitySemifluidGenerator.addFuel("ic2biogas", 10, Math.round(16.0f * ConfigUtil.getFloat(MainConfig.get(), "balance/energy/generator/semiFluidBiogas")));
        }
    }

    public static void addFuel(String fluidName, int amount, int eu) {
        Recipes.semiFluidGenerator.addFluid(fluidName, amount, eu);
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        boolean needsInvUpdate = false;
        if (this.needsFluid()) {
            needsInvUpdate = this.gainFuel();
        }
        boolean newActive = this.gainEnergy();
        if (this.energy.getEnergy() > this.energy.getCapacity()) {
            this.energy.forceAddEnergy(this.energy.getCapacity() - this.energy.getEnergy());
        }
        if (this.energy.getEnergy() >= 1.0 && this.chargeSlot.get() != null) {
            double used = ElectricItem.manager.charge(this.chargeSlot.get(), this.energy.getEnergy(), 1, false, false);
            this.energy.useEnergy(used);
            if (used > 0.0) {
                needsInvUpdate = true;
            }
        }
        if (needsInvUpdate) {
            this.markDirty();
        }
        if (this.getActive() != newActive) {
            this.setActive(newActive);
        }
    }

    protected boolean gainEnergy() {
        if (this.isConverting()) {
            this.energy.addEnergy(this.production);
            if (this.ticker >= 19) {
                this.getFluidTank().drain(this.burnAmount, true);
                this.ticker = 0;
            } else {
                this.ticker = (short)(this.ticker + 1);
            }
            return true;
        }
        return false;
    }

    public boolean isConverting() {
        return this.getTankAmount() > 0 && this.energy.getEnergy() + this.production <= this.energy.getCapacity();
    }

    protected void calcProduction() {
        ISemiFluidFuelManager.BurnProperty property;
        if (this.getFluidfromTank() != null && (property = Recipes.semiFluidGenerator.getBurnProperty(this.getFluidfromTank())) != null) {
            this.production = property.power;
            return;
        }
        this.production = 0.0;
    }

    protected void calcBurnAmount() {
        ISemiFluidFuelManager.BurnProperty property;
        if (this.getFluidfromTank() != null && (property = Recipes.semiFluidGenerator.getBurnProperty(this.getFluidfromTank())) != null) {
            this.burnAmount = property.amount;
            return;
        }
        this.burnAmount = 0;
    }

    @Override
    public boolean canFill(EnumFacing from, Fluid fluid) {
        if (!Recipes.semiFluidGenerator.acceptsFluid(fluid)) {
            return false;
        }
        if (this.getFluidTank().getFluid() == null) {
            return true;
        }
        return this.getFluidTank().getFluid().getFluid() == fluid && this.getTankAmount() < this.getFluidTank().getCapacity();
    }

    @Override
    public boolean canDrain(EnumFacing from, Fluid fluid) {
        return false;
    }

    protected boolean gainFuel() {
        if (this.fluidTank.getFluid() != null) {
            this.calcProduction();
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

    @Override
    protected void onUnloaded() {
        if (IC2.platform.isRendering() && this.audioSource != null) {
            IC2.audioManager.removeSources(this);
            this.audioSource = null;
        }
        super.onUnloaded();
    }

    public String getOperationSoundFile() {
        return "Generators/GeothermalLoop.ogg";
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

    public int gaugeStorageScaled(int i) {
        return (int)(this.energy.getEnergy() * (double)i / this.energy.getCapacity());
    }

    @Override
    public void onGuiClosed(EntityPlayer player) {
    }

    public ContainerBase<TileEntitySemifluidGenerator> getGuiContainer(EntityPlayer player) {
        return DynamicContainer.create(this, player, GuiParser.parse(this.teBlock));
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return DynamicGui.create(this, player, GuiParser.parse(this.teBlock));
    }

    @Override
    public IFluidTank getFluidTank(String name) {
        if ("fluid".equals(name)) {
            return this.fluidTank;
        }
        throw new IllegalArgumentException();
    }
}

