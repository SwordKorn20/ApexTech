/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block.generator.gui;

import ic2.core.ContainerBase;
import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.block.generator.container.ContainerWaterGenerator;
import ic2.core.block.generator.tileentity.TileEntityWaterGenerator;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public class GuiWaterGenerator
extends GuiIC2<ContainerWaterGenerator> {
    private static final ResourceLocation background = new ResourceLocation(IC2.textureDomain, "textures/gui/GUIWaterGenerator.png");

    public GuiWaterGenerator(ContainerWaterGenerator container) {
        super(container);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        this.bindTexture();
        if (((TileEntityWaterGenerator)((ContainerWaterGenerator)this.container).base).fuel > 0) {
            int l = ((TileEntityWaterGenerator)((ContainerWaterGenerator)this.container).base).gaugeFuelScaled(14);
            this.drawTexturedModalRect(this.guiLeft + 80, this.guiTop + 36 + 14 - l, 176, 14 - l, 14, l);
        }
    }

    @Override
    protected ResourceLocation getTexture() {
        return background;
    }
}

