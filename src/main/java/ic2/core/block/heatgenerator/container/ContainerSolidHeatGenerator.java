/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.Slot
 */
package ic2.core.block.heatgenerator.container;

import ic2.core.ContainerFullInv;
import ic2.core.block.heatgenerator.tileentity.TileEntitySolidHeatGenerator;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumableFuel;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.slot.SlotInvSlot;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

public class ContainerSolidHeatGenerator
extends ContainerFullInv<TileEntitySolidHeatGenerator> {
    public ContainerSolidHeatGenerator(EntityPlayer player, TileEntitySolidHeatGenerator tileEntity1) {
        super(player, tileEntity1, 166);
        this.addSlotToContainer((Slot)new SlotInvSlot(tileEntity1.fuelSlot, 0, 80, 45));
        this.addSlotToContainer((Slot)new SlotInvSlot(tileEntity1.outputslot, 0, 113, 45));
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("transmitHeat");
        ret.add("maxHeatEmitpeerTick");
        ret.add("fuel");
        return ret;
    }
}

