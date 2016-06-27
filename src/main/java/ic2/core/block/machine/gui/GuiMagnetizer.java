/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.FontRenderer
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block.machine.gui;

import ic2.core.ContainerBase;
import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.block.BlockIC2Fence;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.machine.container.ContainerMagnetizer;
import ic2.core.gui.EnergyGauge;
import ic2.core.gui.GuiElement;
import ic2.core.init.Localization;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public class GuiMagnetizer
extends GuiIC2<ContainerMagnetizer> {
    public GuiMagnetizer(ContainerMagnetizer container) {
        super(container);
        this.addElement(EnergyGauge.asBolt(this, 11, 28, (TileEntityBlock)container.base));
    }

    @Override
    protected ResourceLocation getTexture() {
        return new ResourceLocation(IC2.textureDomain, "textures/gui/GUIMagnetizer.png");
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        if (BlockIC2Fence.hasMetalShoes(((ContainerMagnetizer)this.container).player)) {
            this.fontRendererObj.drawString(Localization.translate("ic2.Magnetizer.gui.hasMetalShoes"), 18, 66, 4259648);
        } else {
            this.fontRendererObj.drawString(Localization.translate("ic2.Magnetizer.gui.noMetalShoes"), 18, 66, 16728128);
        }
    }
}

