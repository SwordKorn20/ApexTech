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
import ic2.core.block.invslot.InvSlotConsumableItemStack;
import ic2.core.block.invslot.InvSlotDischarge;
import ic2.core.block.kineticgenerator.tileentity.TileEntityElectricKineticGenerator;
import ic2.core.slot.SlotInvSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

public class ContainerElectricKineticGenerator
extends ContainerFullInv<TileEntityElectricKineticGenerator> {
    public ContainerElectricKineticGenerator(EntityPlayer player, TileEntityElectricKineticGenerator tileEntity1) {
        int i;
        super(player, tileEntity1, 166);
        for (i = 0; i < 5; ++i) {
            this.addSlotToContainer((Slot)new SlotInvSlot(tileEntity1.slotMotor, i, 44 + i * 18, 27));
        }
        for (i = 5; i < 10; ++i) {
            this.addSlotToContainer((Slot)new SlotInvSlot(tileEntity1.slotMotor, i, 44 + (i - 5) * 18, 45));
        }
        this.addSlotToContainer((Slot)new SlotInvSlot(tileEntity1.dischargeSlot, 0, 8, 62));
    }
}

