/*
 * Decompiled with CFR 0_114.
 */
package ic2.core.block.machine.tileentity;

import ic2.api.recipe.IMachineRecipeManager;
import ic2.api.recipe.Recipes;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlotProcessable;
import ic2.core.block.invslot.InvSlotProcessableGeneric;
import ic2.core.block.machine.tileentity.TileEntityStandardMachine;
import ic2.core.recipe.BasicMachineRecipeManager;
import ic2.core.upgrade.UpgradableProperty;
import java.util.EnumSet;
import java.util.Set;

public class TileEntityCompressor
extends TileEntityStandardMachine {
    public TileEntityCompressor() {
        super(2, 300, 1);
        this.inputSlot = new InvSlotProcessableGeneric(this, "input", 1, Recipes.compressor);
    }

    public static void init() {
        Recipes.compressor = new BasicMachineRecipeManager();
    }

    @Override
    public String getStartSoundFile() {
        return "Machines/CompressorOp.ogg";
    }

    @Override
    public String getInterruptSoundFile() {
        return "Machines/InterruptOne.ogg";
    }

    @Override
    public Set<UpgradableProperty> getUpgradableProperties() {
        return EnumSet.of(UpgradableProperty.Processing, UpgradableProperty.Transformer, UpgradableProperty.EnergyStorage, UpgradableProperty.ItemConsuming, UpgradableProperty.ItemProducing);
    }
}

