/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.Slot
 */
package ic2.core.block.machine.container;

import ic2.core.ContainerFullInv;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumableLiquid;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.block.invslot.InvSlotProcessableGeneric;
import ic2.core.block.invslot.InvSlotUpgrade;
import ic2.core.block.machine.tileentity.TileEntityMatter;
import ic2.core.slot.SlotInvSlot;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

public class ContainerMatter
extends ContainerFullInv<TileEntityMatter> {
    public ContainerMatter(EntityPlayer player, TileEntityMatter tileEntity1) {
        super(player, tileEntity1, 166);
        this.addSlotToContainer((Slot)new SlotInvSlot(tileEntity1.amplifierSlot, 0, 72, 40));
        this.addSlotToContainer((Slot)new SlotInvSlot(tileEntity1.outputSlot, 0, 125, 59));
        this.addSlotToContainer((Slot)new SlotInvSlot(tileEntity1.containerslot, 0, 125, 23));
        for (int i = 0; i < 4; ++i) {
            this.addSlotToContainer((Slot)new SlotInvSlot(tileEntity1.upgradeSlot, i, 152, 8 + i * 18));
        }
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("energy");
        ret.add("scrap");
        ret.add("fluidTank");
        return ret;
    }
}

