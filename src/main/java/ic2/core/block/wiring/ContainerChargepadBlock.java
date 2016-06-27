/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.Slot
 */
package ic2.core.block.wiring;

import ic2.core.ContainerFullInv;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotCharge;
import ic2.core.block.invslot.InvSlotDischarge;
import ic2.core.block.wiring.TileEntityChargepadBlock;
import ic2.core.slot.SlotInvSlot;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

public class ContainerChargepadBlock
extends ContainerFullInv<TileEntityChargepadBlock> {
    public ContainerChargepadBlock(EntityPlayer player, TileEntityChargepadBlock tileEntity1) {
        super(player, tileEntity1, 162);
        this.addSlotToContainer((Slot)new SlotInvSlot(tileEntity1.chargeSlot, 0, 56, 17));
        this.addSlotToContainer((Slot)new SlotInvSlot(tileEntity1.dischargeSlot, 0, 56, 53));
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("redstoneMode");
        return ret;
    }
}

