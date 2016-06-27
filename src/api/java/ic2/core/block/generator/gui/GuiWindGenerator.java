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
import ic2.core.block.generator.container.ContainerWindGenerator;
import ic2.core.block.generator.tileentity.TileEntityWindGenerator;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public class GuiWindGenerator
extends GuiIC2<ContainerWindGenerator> {
    private static final ResourceLocation background = new ResourceLocation(IC2.textureDomain, "textures/gui/GUIWindGenerator.png");

    public GuiWindGenerator(ContainerWindGenerator container) {
        super(container);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        this.bindTexture();
        int o = ((TileEntityWindGenerator)((ContainerWindGenerator)this.container).base).getOverheatScaled(14);
        if (o > 0) {
            this.drawTexturedModalRect(this.guiLeft + 80, this.guiTop + 45 + 14 - o, 176, 28 - o, 14, o);
        }
    }

    @Override
    protected ResourceLocation getTexture() {
        return background;
    }
}

