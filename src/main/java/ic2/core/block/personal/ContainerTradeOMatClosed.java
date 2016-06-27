/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.Slot
 */
package ic2.core.block.personal;

import ic2.core.ContainerFullInv;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumableLinked;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.block.personal.TileEntityTradeOMat;
import ic2.core.slot.SlotInvSlot;
import ic2.core.slot.SlotInvSlotReadOnly;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

public class ContainerTradeOMatClosed
extends ContainerFullInv<TileEntityTradeOMat> {
    public ContainerTradeOMatClosed(EntityPlayer player, TileEntityTradeOMat tileEntity1) {
        super(player, tileEntity1, 166);
        this.addSlotToContainer((Slot)new SlotInvSlotReadOnly(tileEntity1.demandSlot, 0, 50, 19));
        this.addSlotToContainer((Slot)new SlotInvSlotReadOnly(tileEntity1.offerSlot, 0, 50, 38));
        this.addSlotToContainer((Slot)new SlotInvSlot(tileEntity1.inputSlot, 0, 143, 19));
        this.addSlotToContainer((Slot)new SlotInvSlot(tileEntity1.outputSlot, 0, 143, 53));
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("stock");
        return ret;
    }
}

