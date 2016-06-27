/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.Slot
 */
package ic2.core.block.generator.container;

import ic2.core.block.generator.container.ContainerBaseGenerator;
import ic2.core.block.generator.tileentity.TileEntityWaterGenerator;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumableLiquid;
import ic2.core.slot.SlotInvSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

public class ContainerWaterGenerator
extends ContainerBaseGenerator<TileEntityWaterGenerator> {
    public ContainerWaterGenerator(EntityPlayer player, TileEntityWaterGenerator tileEntity1) {
        super(player, tileEntity1, 166, 80, 17);
        this.addSlotToContainer((Slot)new SlotInvSlot(tileEntity1.fuelSlot, 0, 80, 53));
    }
}

