/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.inventory.Slot
 */
package ic2.core.item.tool;

import ic2.core.item.ContainerHandHeldInventory;
import ic2.core.item.tool.HandHeldToolbox;
import ic2.core.slot.SlotBoxable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class ContainerToolbox
extends ContainerHandHeldInventory<HandHeldToolbox> {
    protected static final int height = 166;
    protected static final int windowBorder = 8;
    protected static final int slotSize = 16;
    protected static final int slotDistance = 2;
    protected static final int slotSeparator = 4;
    protected static final int hotbarYOffset = -24;
    protected static final int inventoryYOffset = -82;

    public ContainerToolbox(EntityPlayer player, HandHeldToolbox Toolbox1) {
        int col;
        super(Toolbox1);
        for (col = 0; col < 9; ++col) {
            this.addSlotToContainer((Slot)new SlotBoxable(Toolbox1, col, 8 + col * 18, 41));
        }
        for (int row = 0; row < 3; ++row) {
            for (int col2 = 0; col2 < 9; ++col2) {
                this.addSlotToContainer(new Slot((IInventory)player.inventory, col2 + row * 9 + 9, 8 + col2 * 18, 84 + row * 18));
            }
        }
        for (col = 0; col < 9; ++col) {
            this.addSlotToContainer(new Slot((IInventory)player.inventory, col, 8 + col * 18, 142));
        }
    }
}

