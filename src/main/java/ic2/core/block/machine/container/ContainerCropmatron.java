/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.Slot
 */
package ic2.core.block.machine.container;

import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumable;
import ic2.core.block.invslot.InvSlotConsumableLiquidByTank;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.block.machine.container.ContainerElectricMachine;
import ic2.core.block.machine.tileentity.TileEntityCropmatron;
import ic2.core.slot.SlotInvSlot;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

public class ContainerCropmatron
extends ContainerElectricMachine<TileEntityCropmatron> {
    public ContainerCropmatron(EntityPlayer player, TileEntityCropmatron base) {
        super(player, base, 191, 134, 80);
        for (int i = 0; i < base.fertilizerSlot.size(); ++i) {
            this.addSlotToContainer((Slot)new SlotInvSlot(base.fertilizerSlot, i, 26 + i * 18, 80));
        }
        this.addSlotToContainer((Slot)new SlotInvSlot(base.wasserinputSlot, 0, 67, 27));
        this.addSlotToContainer((Slot)new SlotInvSlot(base.wasseroutputSlot, 0, 85, 27));
        this.addSlotToContainer((Slot)new SlotInvSlot(base.exInputSlot, 0, 75, 56));
        this.addSlotToContainer((Slot)new SlotInvSlot(base.exOutputSlot, 0, 93, 56));
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("waterTank");
        ret.add("exTank");
        return ret;
    }
}

