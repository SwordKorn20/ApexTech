/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.Slot
 */
package ic2.core.block.machine.container;

import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumableId;
import ic2.core.block.invslot.InvSlotConsumableLiquidByTank;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.block.invslot.InvSlotUpgrade;
import ic2.core.block.machine.container.ContainerElectricMachine;
import ic2.core.block.machine.tileentity.TileEntityCondenser;
import ic2.core.slot.SlotInvSlot;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

public class ContainerCondenser
extends ContainerElectricMachine<TileEntityCondenser> {
    public ContainerCondenser(EntityPlayer player, TileEntityCondenser te) {
        super(player, te, 184, 8, 44);
        this.addSlotToContainer((Slot)new SlotInvSlot(te.waterInputSlot, 0, 26, 73));
        this.addSlotToContainer((Slot)new SlotInvSlot(te.waterOutputSlot, 0, 134, 73));
        this.addSlotToContainer((Slot)new SlotInvSlot(te.upgradeSlot, 0, 152, 73));
        for (int i = 0; i < 2; ++i) {
            this.addSlotToContainer((Slot)new SlotInvSlot(te.ventSlots, i, 26 + i * 108, 26));
            this.addSlotToContainer((Slot)new SlotInvSlot(te.ventSlots, i + 2, 26 + i * 108, 44));
        }
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("inputTank");
        ret.add("outputTank");
        ret.add("progress");
        return ret;
    }
}

