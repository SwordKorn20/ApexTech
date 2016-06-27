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
import ic2.core.block.invslot.InvSlotConsumableLiquidByTank;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.block.machine.tileentity.TileEntityFluidDistributor;
import ic2.core.slot.SlotInvSlot;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

public class ContainerFluidDistributor
extends ContainerFullInv<TileEntityFluidDistributor> {
    public ContainerFluidDistributor(EntityPlayer player, TileEntityFluidDistributor tileEntite) {
        super(player, tileEntite, 184);
        this.addSlotToContainer((Slot)new SlotInvSlot(tileEntite.inputSlot, 0, 9, 54));
        this.addSlotToContainer((Slot)new SlotInvSlot(tileEntite.OutputSlot, 0, 9, 72));
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("fluidTank");
        return ret;
    }
}

