/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.Slot
 */
package ic2.core.block.kineticgenerator.container;

import ic2.core.ContainerFullInv;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumableClass;
import ic2.core.block.kineticgenerator.tileentity.TileEntityWindKineticGenerator;
import ic2.core.slot.SlotInvSlot;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

public class ContainerWindKineticGenerator
extends ContainerFullInv<TileEntityWindKineticGenerator> {
    public ContainerWindKineticGenerator(EntityPlayer player, TileEntityWindKineticGenerator tileEntity1) {
        super(player, tileEntity1, 166);
        this.addSlotToContainer((Slot)new SlotInvSlot(tileEntity1.rotorSlot, 0, 80, 26));
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("windStrength");
        return ret;
    }
}

