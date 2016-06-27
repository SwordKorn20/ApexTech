/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NBTTagCompound
 */
package ic2.core.block.machine.tileentity;

import ic2.api.recipe.IMachineRecipeManager;
import ic2.api.recipe.RecipeOutput;
import ic2.api.recipe.Recipes;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.comp.Energy;
import ic2.core.block.comp.Redstone;
import ic2.core.block.comp.TileEntityComponent;
import ic2.core.block.invslot.InvSlotProcessable;
import ic2.core.block.invslot.InvSlotProcessableGeneric;
import ic2.core.block.machine.tileentity.TileEntityStandardMachine;
import ic2.core.network.GuiSynced;
import ic2.core.recipe.BasicMachineRecipeManager;
import ic2.core.upgrade.UpgradableProperty;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityCentrifuge
extends TileEntityStandardMachine {
    protected final Redstone redstone;
    public short maxHeat = 5000;
    @GuiSynced
    public short heat = 0;
    @GuiSynced
    public short workheat = this.maxHeat;

    public TileEntityCentrifuge() {
        super(48, 500, 3, 2);
        this.inputSlot = new InvSlotProcessableGeneric(this, "input", 1, Recipes.centrifuge);
        this.redstone = this.addComponent(new Redstone(this));
    }

    public static void init() {
        Recipes.centrifuge = new BasicMachineRecipeManager();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.heat = nbt.getShort("heat");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setShort("heat", this.heat);
        return nbt;
    }

    public double getHeatRatio() {
        return (double)this.heat / (double)this.workheat;
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        boolean energyPerHeat = true;
        boolean coolingPerTick = true;
        boolean heating = false;
        if (this.energy.getEnergy() >= 1.0) {
            int heatRequested = Integer.MIN_VALUE;
            RecipeOutput output = super.getOutput();
            if (output != null) {
                heatRequested = Math.min(this.maxHeat, output.metadata.getInteger("minHeat"));
                this.workheat = (short)heatRequested;
                if (this.heat > heatRequested) {
                    this.heat = (short)heatRequested;
                }
            } else if (this.heat < this.maxHeat && this.redstone.hasRedstoneInput()) {
                heatRequested = this.maxHeat;
                this.workheat = (short)heatRequested;
            }
            if (this.heat - 1 < heatRequested) {
                this.energy.useEnergy(1.0);
                heating = true;
            }
        }
        this.heat = heating ? (short)(this.heat + 1) : (short)(this.heat - Math.min(this.heat, 1));
    }

    @Override
    public RecipeOutput getOutput() {
        RecipeOutput ret = super.getOutput();
        if (ret != null) {
            if (ret.metadata == null) {
                return null;
            }
            if (ret.metadata.getInteger("minHeat") > this.heat) {
                return null;
            }
        }
        return ret;
    }

    @Override
    public Set<UpgradableProperty> getUpgradableProperties() {
        return EnumSet.of(UpgradableProperty.Processing, new UpgradableProperty[]{UpgradableProperty.RedstoneSensitive, UpgradableProperty.Transformer, UpgradableProperty.EnergyStorage, UpgradableProperty.ItemConsuming, UpgradableProperty.ItemProducing});
    }

    @Override
    public double getGuiValue(String name) {
        if ("heat".equals(name)) {
            return (double)this.heat / (double)this.workheat;
        }
        return super.getGuiValue(name);
    }
}

