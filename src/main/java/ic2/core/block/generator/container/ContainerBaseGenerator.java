/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.Slot
 */
package ic2.core.block.generator.container;

import ic2.core.ContainerFullInv;
import ic2.core.block.generator.tileentity.TileEntityBaseGenerator;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotCharge;
import ic2.core.slot.SlotInvSlot;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

public abstract class ContainerBaseGenerator<T extends TileEntityBaseGenerator>
extends ContainerFullInv<T> {
    public short lastStorage = -1;
    public int lastFuel = -1;

    public ContainerBaseGenerator(EntityPlayer player, T tileEntity1, int height, int chargeX, int chargeY) {
        super(player, tileEntity1, height);
        this.addSlotToContainer((Slot)new SlotInvSlot(tileEntity1.chargeSlot, 0, chargeX, chargeY));
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("fuel");
        return ret;
    }
}

