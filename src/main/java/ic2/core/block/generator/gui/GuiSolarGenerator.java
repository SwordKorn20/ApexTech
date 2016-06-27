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
import ic2.core.block.generator.container.ContainerSolarGenerator;
import ic2.core.block.generator.tileentity.TileEntitySolarGenerator;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public class GuiSolarGenerator
extends GuiIC2<ContainerSolarGenerator> {
    private static final ResourceLocation background = new ResourceLocation(IC2.textureDomain, "textures/gui/GUISolarGenerator.png");

    public GuiSolarGenerator(ContainerSolarGenerator container) {
        super(container);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        this.bindTexture();
        if (((TileEntitySolarGenerator)((ContainerSolarGenerator)this.container).base).skyLight > 0.0f) {
            this.drawTexturedRect(80.0, 45.0, 14.0, 14.0, 176.0, 0.0);
        }
    }

    @Override
    protected ResourceLocation getTexture() {
        return background;
    }
}

