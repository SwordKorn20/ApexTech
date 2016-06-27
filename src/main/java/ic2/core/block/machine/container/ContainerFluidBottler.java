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
import ic2.core.block.machine.container.ContainerStandardMachine;
import ic2.core.block.machine.tileentity.TileEntityFluidBottler;
import ic2.core.slot.SlotInvSlot;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

public class ContainerFluidBottler
extends ContainerStandardMachine<TileEntityFluidBottler> {
    public ContainerFluidBottler(EntityPlayer player, TileEntityFluidBottler tileEntity1) {
        super(player, tileEntity1, 184, 8, 53, 0, 0, 117, 53, 152, 26);
        this.addSlotToContainer((Slot)new SlotInvSlot(tileEntity1.drainInputSlot, 0, 44, 35));
        this.addSlotToContainer((Slot)new SlotInvSlot(tileEntity1.fillInputSlot, 0, 44, 72));
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("fluidTank");
        return ret;
    }
}

