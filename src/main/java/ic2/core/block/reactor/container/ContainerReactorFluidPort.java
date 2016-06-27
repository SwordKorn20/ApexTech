/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.Slot
 */
package ic2.core.block.reactor.container;

import ic2.core.ContainerFullInv;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotUpgrade;
import ic2.core.block.reactor.tileentity.TileEntityReactorFluidPort;
import ic2.core.slot.SlotInvSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

public class ContainerReactorFluidPort
extends ContainerFullInv<TileEntityReactorFluidPort> {
    public ContainerReactorFluidPort(EntityPlayer player, TileEntityReactorFluidPort te) {
        super(player, te, 166);
        this.addSlotToContainer((Slot)new SlotInvSlot(te.upgradeSlot, 0, 80, 43));
    }
}

