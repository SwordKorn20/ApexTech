/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.Slot
 */
package ic2.core.block.machine.container;

import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumableLiquid;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.block.invslot.InvSlotUpgrade;
import ic2.core.block.machine.container.ContainerElectricMachine;
import ic2.core.block.machine.tileentity.TileEntityReplicator;
import ic2.core.slot.SlotInvSlot;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

public class ContainerReplicator
extends ContainerElectricMachine<TileEntityReplicator> {
    public ContainerReplicator(EntityPlayer player, TileEntityReplicator tileEntity1) {
        super(player, tileEntity1, 184, 152, 83);
        this.addSlotToContainer((Slot)new SlotInvSlot(tileEntity1.outputSlot, 0, 90, 59));
        this.addSlotToContainer((Slot)new SlotInvSlot(tileEntity1.fluidSlot, 0, 8, 27));
        this.addSlotToContainer((Slot)new SlotInvSlot(tileEntity1.cellSlot, 0, 8, 72));
        for (int i = 0; i < 4; ++i) {
            this.addSlotToContainer((Slot)new SlotInvSlot(tileEntity1.upgradeSlot, i, 152, 8 + i * 18));
        }
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("fluidTank");
        ret.add("uuProcessed");
        ret.add("pattern");
        ret.add("mode");
        ret.add("index");
        ret.add("maxIndex");
        ret.add("patternUu");
        ret.add("patternEu");
        return ret;
    }
}

