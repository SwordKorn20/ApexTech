/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.FontRenderer
 *  net.minecraft.client.gui.inventory.GuiContainer
 *  net.minecraft.client.renderer.texture.TextureManager
 *  net.minecraft.inventory.Container
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 *  org.lwjgl.opengl.GL11
 */
package ic2.core.block.kineticgenerator.gui;

import ic2.core.IC2;
import ic2.core.block.invslot.InvSlotConsumableClass;
import ic2.core.block.kineticgenerator.container.ContainerWaterKineticGenerator;
import ic2.core.block.kineticgenerator.tileentity.TileEntityWaterKineticGenerator;
import ic2.core.init.Localization;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(value=Side.CLIENT)
public class GuiWaterKineticGenerator
extends GuiContainer {
    public ContainerWaterKineticGenerator container;
    public String name;
    private static final ResourceLocation background = new ResourceLocation(IC2.textureDomain, "textures/gui/GUIWaterKineticGenerator.png");

    public GuiWaterKineticGenerator(ContainerWaterKineticGenerator container1) {
        super((Container)container1);
        this.container = container1;
        this.name = Localization.translate("ic2.WaterKineticGenerator.gui.name");
    }

    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        this.fontRendererObj.drawString(this.name, (this.xSize - this.fontRendererObj.getStringWidth(this.name)) / 2, 6, 4210752);
        if (((TileEntityWaterKineticGenerator)this.container.base).type == -1) {
            this.fontRendererObj.drawString(Localization.translate("ic2.WaterKineticGenerator.gui.wrongbiome1"), 38, 52, 2157374);
            this.fontRendererObj.drawString(Localization.translate("ic2.WaterKineticGenerator.gui.wrongbiome2"), 45, 69, 2157374);
        } else if (!((TileEntityWaterKineticGenerator)this.container.base).rotorSlot.isEmpty()) {
            if (((TileEntityWaterKineticGenerator)this.container.base).checkSpace(((TileEntityWaterKineticGenerator)this.container.base).getRotorDiameter(), true) != 0) {
                this.fontRendererObj.drawString(Localization.translate("ic2.WaterKineticGenerator.gui.rotorspace"), 20, 52, 2157374);
            } else {
                this.fontRendererObj.drawString(Localization.translate("ic2.WaterKineticGenerator.gui.output", ((TileEntityWaterKineticGenerator)this.container.base).getKuOutput()), 55, 52, 2157374);
                this.fontRendererObj.drawString(((TileEntityWaterKineticGenerator)this.container.base).getRotorHealth() + " %", 46, 70, 2157374);
            }
        } else {
            this.fontRendererObj.drawString(Localization.translate("ic2.WaterKineticGenerator.gui.rotormiss"), 27, 52, 2157374);
        }
    }

    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        this.mc.getTextureManager().bindTexture(background);
        int j = (this.width - this.xSize) / 2;
        int k = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(j, k, 0, 0, this.xSize, this.ySize);
    }
}

