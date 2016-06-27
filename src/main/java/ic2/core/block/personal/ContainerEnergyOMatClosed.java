/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.Container
 *  net.minecraft.inventory.IContainerListener
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.inventory.Slot
 */
package ic2.core.block.personal;

import ic2.core.ContainerFullInv;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotCharge;
import ic2.core.block.invslot.InvSlotConsumableLinked;
import ic2.core.block.personal.TileEntityEnergyOMat;
import ic2.core.slot.SlotInvSlot;
import ic2.core.slot.SlotInvSlotReadOnly;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class ContainerEnergyOMatClosed
extends ContainerFullInv<TileEntityEnergyOMat> {
    private int lastTier = -1;

    public ContainerEnergyOMatClosed(EntityPlayer player, TileEntityEnergyOMat tileEntity1) {
        super(player, tileEntity1, 166);
        this.addSlotToContainer((Slot)new SlotInvSlotReadOnly(tileEntity1.demandSlot, 0, 50, 17));
        this.addSlotToContainer((Slot)new SlotInvSlot(tileEntity1.inputSlot, 0, 143, 17));
        this.addSlotToContainer((Slot)new SlotInvSlot(tileEntity1.chargeSlot, 0, 143, 53));
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("paidFor");
        ret.add("euOffer");
        return ret;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (int i = 0; i < this.listeners.size(); ++i) {
            IContainerListener listener = (IContainerListener)this.listeners.get(i);
            if (((TileEntityEnergyOMat)this.base).chargeSlot.tier == this.lastTier) continue;
            listener.sendProgressBarUpdate((Container)this, 0, ((TileEntityEnergyOMat)this.base).chargeSlot.tier);
        }
        this.lastTier = ((TileEntityEnergyOMat)this.base).chargeSlot.tier;
    }

    public void updateProgressBar(int index, int value) {
        super.updateProgressBar(index, value);
        switch (index) {
            case 0: {
                ((TileEntityEnergyOMat)this.base).chargeSlot.tier = value;
            }
        }
    }
}

