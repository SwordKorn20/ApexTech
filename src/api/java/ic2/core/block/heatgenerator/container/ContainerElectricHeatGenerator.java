/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.Slot
 */
package ic2.core.block.heatgenerator.container;

import ic2.core.ContainerFullInv;
import ic2.core.block.heatgenerator.tileentity.TileEntityElectricHeatGenerator;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumable;
import ic2.core.block.invslot.InvSlotDischarge;
import ic2.core.slot.SlotInvSlot;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

public class ContainerElectricHeatGenerator
extends ContainerFullInv<TileEntityElectricHeatGenerator> {
    public ContainerElectricHeatGenerator(EntityPlayer player, TileEntityElectricHeatGenerator tileEntity1) {
        int i;
        super(player, tileEntity1, 166);
        for (i = 0; i < 5; ++i) {
            this.addSlotToContainer((Slot)new SlotInvSlot(tileEntity1.CoilSlot, i, 44 + i * 18, 27));
        }
        for (i = 5; i < 10; ++i) {
            this.addSlotToContainer((Slot)new SlotInvSlot(tileEntity1.CoilSlot, i, 44 + (i - 5) * 18, 45));
        }
        this.addSlotToContainer((Slot)new SlotInvSlot(tileEntity1.dischargeSlot, 0, 8, 62));
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("transmitHeat");
        ret.add("maxHeatEmitpeerTick");
        return ret;
    }
}

