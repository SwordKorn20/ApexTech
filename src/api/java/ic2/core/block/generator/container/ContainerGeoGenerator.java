/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.Slot
 */
package ic2.core.block.generator.container;

import ic2.core.ContainerFullInv;
import ic2.core.block.generator.tileentity.TileEntityGeoGenerator;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotCharge;
import ic2.core.block.invslot.InvSlotConsumableLiquid;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.slot.SlotInvSlot;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

public class ContainerGeoGenerator
extends ContainerFullInv<TileEntityGeoGenerator> {
    public ContainerGeoGenerator(EntityPlayer player, TileEntityGeoGenerator tileEntity1) {
        super(player, tileEntity1, 166);
        this.addSlotToContainer((Slot)new SlotInvSlot(tileEntity1.chargeSlot, 0, 117, 49));
        this.addSlotToContainer((Slot)new SlotInvSlot(tileEntity1.fluidSlot, 0, 27, 21));
        this.addSlotToContainer((Slot)new SlotInvSlot(tileEntity1.outputSlot, 0, 27, 54));
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("fluidTank");
        return ret;
    }
}

