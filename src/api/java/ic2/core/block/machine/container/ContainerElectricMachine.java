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
import ic2.core.block.invslot.InvSlotDischarge;
import ic2.core.block.machine.tileentity.TileEntityElectricMachine;
import ic2.core.slot.SlotInvSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

public abstract class ContainerElectricMachine<T extends TileEntityElectricMachine>
extends ContainerFullInv<T> {
    public ContainerElectricMachine(EntityPlayer player, T base1, int height, int dischargeX, int dischargeY) {
        super(player, base1, height);
        this.addSlotToContainer((Slot)new SlotInvSlot(base1.dischargeSlot, 0, dischargeX, dischargeY));
    }
}

