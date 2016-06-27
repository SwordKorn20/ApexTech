/*
 * Decompiled with CFR 0_114.
 */
package ic2.core.block.machine.tileentity;

import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlotProcessable;
import ic2.core.block.invslot.InvSlotProcessableSmelting;
import ic2.core.block.machine.tileentity.TileEntityStandardMachine;
import ic2.core.upgrade.UpgradableProperty;
import java.util.EnumSet;
import java.util.Set;

public class TileEntityElectricFurnace
extends TileEntityStandardMachine {
    public TileEntityElectricFurnace() {
        super(3, 100, 1);
        this.inputSlot = new InvSlotProcessableSmelting(this, "input", 1);
    }

    @Override
    public String getStartSoundFile() {
        return "Machines/Electro Furnace/ElectroFurnaceLoop.ogg";
    }

    @Override
    public String getInterruptSoundFile() {
        return null;
    }

    @Override
    public Set<UpgradableProperty> getUpgradableProperties() {
        return EnumSet.of(UpgradableProperty.Processing, UpgradableProperty.Transformer, UpgradableProperty.EnergyStorage, UpgradableProperty.ItemConsuming, UpgradableProperty.ItemProducing);
    }
}

