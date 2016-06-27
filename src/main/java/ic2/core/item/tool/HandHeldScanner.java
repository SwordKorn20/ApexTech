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

import ic2.core.ContainerBase;
import ic2.core.item.tool.ContainerToolScanner;
import ic2.core.item.tool.GuiToolScanner;
import ic2.core.item.tool.HandHeldInventory;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class HandHeldScanner
extends HandHeldInventory {
    ItemStack itemScanner;
    EntityPlayer player;

    public HandHeldScanner(EntityPlayer player, ItemStack itemScanner) {
        super(player, itemScanner, 0);
        this.itemScanner = itemScanner;
        this.player = player;
    }

    public ContainerBase<HandHeldScanner> getGuiContainer(EntityPlayer player) {
        return new ContainerToolScanner(player, this);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return new GuiToolScanner(new ContainerToolScanner(player, this));
    }

    public String getName() {
        return "toolscanner";
    }

    public boolean hasCustomName() {
        return false;
    }
}

