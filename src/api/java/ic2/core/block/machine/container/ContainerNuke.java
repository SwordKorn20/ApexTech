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
import ic2.core.block.invslot.InvSlotConsumable;
import ic2.core.block.machine.tileentity.TileEntityNuke;
import ic2.core.slot.SlotInvSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

public class ContainerNuke
extends ContainerFullInv<TileEntityNuke> {
    public ContainerNuke(EntityPlayer player, TileEntityNuke base1) {
        super(player, base1, 219);
        this.addSlotToContainer((Slot)new SlotInvSlot(base1.insideSlot, 0, 79, 62));
        this.addSlotToContainer((Slot)new SlotInvSlot(base1.outsideSlot, 0, 52, 8));
        this.addSlotToContainer((Slot)new SlotInvSlot(base1.outsideSlot, 0, 106, 8));
        this.addSlotToContainer((Slot)new SlotInvSlot(base1.outsideSlot, 0, 26, 35));
        this.addSlotToContainer((Slot)new SlotInvSlot(base1.outsideSlot, 0, 133, 35));
        this.addSlotToContainer((Slot)new SlotInvSlot(base1.outsideSlot, 0, 26, 89));
        this.addSlotToContainer((Slot)new SlotInvSlot(base1.outsideSlot, 0, 133, 89));
        this.addSlotToContainer((Slot)new SlotInvSlot(base1.outsideSlot, 0, 52, 116));
        this.addSlotToContainer((Slot)new SlotInvSlot(base1.outsideSlot, 0, 106, 116));
    }
}

