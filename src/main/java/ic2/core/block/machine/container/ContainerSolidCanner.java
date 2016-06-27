/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.Slot
 */
package ic2.core.block.machine.container;

import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumableSolidCanner;
import ic2.core.block.machine.container.ContainerStandardMachine;
import ic2.core.block.machine.tileentity.TileEntitySolidCanner;
import ic2.core.slot.SlotInvSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

public class ContainerSolidCanner
extends ContainerStandardMachine<TileEntitySolidCanner> {
    public ContainerSolidCanner(EntityPlayer player, TileEntitySolidCanner tileEntity1) {
        super(player, tileEntity1, 166, 8, 62, 37, 36, 116, 36, 152, 8);
        this.addSlotToContainer((Slot)new SlotInvSlot(tileEntity1.canInputSlot, 0, 67, 36));
    }
}

