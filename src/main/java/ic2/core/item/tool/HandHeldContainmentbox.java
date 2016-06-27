/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.item.tool;

import ic2.core.ContainerBase;
import ic2.core.item.reactor.ItemReactorMOX;
import ic2.core.item.reactor.ItemReactorUranium;
import ic2.core.item.tool.ContainerContainmentbox;
import ic2.core.item.tool.GuiContainmentbox;
import ic2.core.item.tool.HandHeldInventory;
import ic2.core.ref.IItemModelProvider;
import ic2.core.ref.ItemName;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class HandHeldContainmentbox
extends HandHeldInventory {
    public HandHeldContainmentbox(EntityPlayer player, ItemStack stack1, int inventorySize) {
        super(player, stack1, inventorySize);
    }

    public ContainerBase<HandHeldContainmentbox> getGuiContainer(EntityPlayer player) {
        return new ContainerContainmentbox(player, this);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return new GuiContainmentbox(new ContainerContainmentbox(player, this));
    }

    public String getName() {
        return "ic2.itemContainmentbox";
    }

    public boolean hasCustomName() {
        return false;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (stack == null) {
            return false;
        }
        return stack.getItem() == ItemName.nuclear.getInstance() || stack.getItem() instanceof ItemReactorMOX || stack.getItem() instanceof ItemReactorUranium;
    }
}

