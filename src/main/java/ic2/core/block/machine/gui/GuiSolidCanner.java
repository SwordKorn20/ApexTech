/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block.machine.gui;

import ic2.core.ContainerBase;
import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.machine.container.ContainerSolidCanner;
import ic2.core.block.machine.tileentity.TileEntitySolidCanner;
import ic2.core.gui.EnergyGauge;
import ic2.core.gui.GuiElement;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public class GuiSolidCanner
extends GuiIC2<ContainerSolidCanner> {
    public GuiSolidCanner(ContainerSolidCanner container) {
        super(container);
        this.addElement(EnergyGauge.asBolt(this, 11, 46, (TileEntityBlock)container.base));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        super.drawGuiContainerBackgroundLayer(f, x, y);
        this.bindTexture();
        int progress = (int)(24.0f * ((TileEntitySolidCanner)((ContainerSolidCanner)this.container).base).getProgress());
        if (progress > 0) {
            this.drawTexturedModalRect(this.guiLeft + 88, this.guiTop + 35, 176, 14, progress + 1, 16);
        }
    }

    @Override
    protected ResourceLocation getTexture() {
        return new ResourceLocation(IC2.textureDomain, "textures/gui/GUISolidCanner.png");
    }
}

