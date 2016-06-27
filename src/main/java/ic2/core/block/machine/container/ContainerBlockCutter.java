/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.Slot
 */
package ic2.core.block.machine.container;

import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumableClass;
import ic2.core.block.machine.container.ContainerStandardMachine;
import ic2.core.block.machine.tileentity.TileEntityBlockCutter;
import ic2.core.slot.SlotInvSlot;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

public class ContainerBlockCutter
extends ContainerStandardMachine<TileEntityBlockCutter> {
    public ContainerBlockCutter(EntityPlayer player, TileEntityBlockCutter tileEntity1) {
        super(player, tileEntity1, 166, 26, 53, 26, 17, 116, 34, 152, 8);
        this.addSlotToContainer((Slot)new SlotInvSlot(tileEntity1.cutterSlot, 0, 70, 35));
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("bladeTooWeak");
        return ret;
    }
}

