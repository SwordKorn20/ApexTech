/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.item.tool;

import ic2.api.item.ItemWrapper;
import ic2.core.ContainerBase;
import ic2.core.item.tool.ContainerToolbox;
import ic2.core.item.tool.Guitoolbox;
import ic2.core.item.tool.HandHeldInventory;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class HandHeldToolbox
extends HandHeldInventory {
    public HandHeldToolbox(EntityPlayer player, ItemStack stack, int inventorySize) {
        super(player, stack, inventorySize);
    }

    public ContainerBase<HandHeldToolbox> getGuiContainer(EntityPlayer player) {
        return new ContainerToolbox(player, this);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return new Guitoolbox(new ContainerToolbox(player, this));
    }

    public String getName() {
        return "toolbox";
    }

    public boolean hasCustomName() {
        return false;
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        if (itemstack == null) {
            return false;
        }
        return ItemWrapper.canBeStoredInToolbox(itemstack);
    }
}

