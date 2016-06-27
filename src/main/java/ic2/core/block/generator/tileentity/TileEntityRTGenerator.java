/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package ic2.core.block.generator.tileentity;

import ic2.core.block.TileEntityInventory;
import ic2.core.block.comp.Energy;
import ic2.core.block.generator.tileentity.TileEntityBaseGenerator;
import ic2.core.block.invslot.InvSlotConsumable;
import ic2.core.block.invslot.InvSlotConsumableItemStack;
import ic2.core.block.state.IIdProvider;
import ic2.core.init.MainConfig;
import ic2.core.item.type.NuclearResourceType;
import ic2.core.ref.ItemName;
import ic2.core.util.ConfigUtil;
import net.minecraft.item.ItemStack;

public class TileEntityRTGenerator
extends TileEntityBaseGenerator {
    public final InvSlotConsumable fuelSlot;
    private static final float efficiency = ConfigUtil.getFloat(MainConfig.get(), "balance/energy/generator/radioisotope");

    public TileEntityRTGenerator() {
        super(Math.round(16.0f * efficiency), 1, 20000);
        this.fuelSlot = new InvSlotConsumableItemStack((TileEntityInventory)this, "fuel", 6, ItemName.nuclear.getItemStack(NuclearResourceType.rtg_pellet));
    }

    @Override
    public boolean gainEnergy() {
        int counter = 0;
        for (int i = 0; i < this.fuelSlot.size(); ++i) {
            if (this.fuelSlot.get(i) == null) continue;
            ++counter;
        }
        if (counter == 0) {
            return false;
        }
        this.energy.addEnergy(Math.pow(2.0, counter - 1) * (double)efficiency);
        return true;
    }

    @Override
    public boolean gainFuel() {
        return false;
    }

    @Override
    public boolean needsFuel() {
        return false;
    }

    @Override
    protected boolean delayActiveUpdate() {
        return true;
    }
}

