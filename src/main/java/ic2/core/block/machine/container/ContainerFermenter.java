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
import ic2.core.block.invslot.InvSlotConsumableLiquidByList;
import ic2.core.block.invslot.InvSlotConsumableLiquidByTank;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.block.invslot.InvSlotUpgrade;
import ic2.core.block.machine.tileentity.TileEntityFermenter;
import ic2.core.slot.SlotInvSlot;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

public class ContainerFermenter
extends ContainerFullInv<TileEntityFermenter> {
    public ContainerFermenter(EntityPlayer player, TileEntityFermenter te) {
        super(player, te, 184);
        this.addSlotToContainer((Slot)new SlotInvSlot(te.fluidInputCellInSlot, 0, 14, 46));
        this.addSlotToContainer((Slot)new SlotInvSlot(te.fluidInputCellOutSlot, 0, 14, 64));
        this.addSlotToContainer((Slot)new SlotInvSlot(te.fluidOutputCellInSlot, 0, 148, 43));
        this.addSlotToContainer((Slot)new SlotInvSlot(te.fluidOutputCellOutSlot, 0, 148, 61));
        this.addSlotToContainer((Slot)new SlotInvSlot(te.fertiliserSlot, 0, 86, 83));
        for (int i = 0; i < 2; ++i) {
            this.addSlotToContainer((Slot)new SlotInvSlot(te.upgradeSlot, i, 125 + i * 18, 83));
        }
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("inputTank");
        ret.add("outputTank");
        ret.add("progress");
        ret.add("heatBuffer");
        return ret;
    }
}

