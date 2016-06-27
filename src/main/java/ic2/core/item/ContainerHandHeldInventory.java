/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.inventory.ClickType
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.item.ItemStack
 */
package ic2.core.item;

import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.Platform;
import ic2.core.item.tool.HandHeldInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class ContainerHandHeldInventory<T extends HandHeldInventory>
extends ContainerBase<T> {
    public ContainerHandHeldInventory(T inventory) {
        super(inventory);
    }

    @Override
    public ItemStack slotClick(int slot, int button, ClickType type, EntityPlayer player) {
        ItemStack stack;
        if (player instanceof EntityPlayerMP && IC2.platform.isSimulating() && slot == -999 && (button == 0 || button == 1) && ((HandHeldInventory)this.base).isThisContainer(stack = player.inventory.getItemStack())) {
            ((EntityPlayerMP)player).closeScreen();
        }
        return super.slotClick(slot, button, type, player);
    }

    public void onContainerClosed(EntityPlayer player) {
        ((HandHeldInventory)this.base).onGuiClosed(player);
        super.onContainerClosed(player);
    }
}

