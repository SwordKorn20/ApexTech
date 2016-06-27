/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.inventory.Slot
 *  net.minecraft.item.Item
 */
package ic2.core.item.tool;

import ic2.core.item.ContainerHandHeldInventory;
import ic2.core.item.tool.HandHeldCropnalyzer;
import ic2.core.ref.IItemModelProvider;
import ic2.core.ref.ItemName;
import ic2.core.slot.SlotCustom;
import ic2.core.slot.SlotDischarge;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;

public class ContainerCropnalyzer
extends ContainerHandHeldInventory<HandHeldCropnalyzer> {
    public ContainerCropnalyzer(EntityPlayer player, HandHeldCropnalyzer cropnalyzer1) {
        super(cropnalyzer1);
        this.addSlotToContainer((Slot)new SlotCustom(cropnalyzer1, (Item)ItemName.crop_seed_bag.getInstance(), 0, 8, 7));
        this.addSlotToContainer((Slot)new SlotCustom(cropnalyzer1, null, 1, 41, 7));
        this.addSlotToContainer((Slot)new SlotDischarge(cropnalyzer1, 2, 152, 7));
        this.addPlayerInventorySlots(player, 223);
    }
}

