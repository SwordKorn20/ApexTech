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
import ic2.core.block.invslot.InvSlotUpgrade;
import ic2.core.block.machine.container.ContainerElectricMachine;
import ic2.core.block.machine.tileentity.TileEntityCropHarvester;
import ic2.core.slot.SlotInvSlot;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

public class ContainerCropHavester
extends ContainerElectricMachine<TileEntityCropHarvester> {
    public ContainerCropHavester(EntityPlayer player, TileEntityCropHarvester base) {
        super(player, base, 191, 152, 58);
        for (int y = 0; y < base.contentSlot.size() / 5; ++y) {
            for (int x = 0; x < 5; ++x) {
                this.addSlotToContainer((Slot)new SlotInvSlot(base.contentSlot, x + y * 5, 44 + x * 18, 22 + y * 18));
            }
        }
        this.addSlotToContainer((Slot)new SlotInvSlot(base.upgradeSlot, 0, 80, 80));
        this.addSlotToContainer((Slot)new SlotInvSlot(base.cropnalyzerSlot, 0, 15, 40));
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("energy");
        return ret;
    }
}

