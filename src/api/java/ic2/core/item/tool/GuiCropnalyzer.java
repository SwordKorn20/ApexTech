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
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 *  org.lwjgl.opengl.GL11
 */
package ic2.core.item.tool;

import ic2.core.IC2;
import ic2.core.item.tool.ContainerCropnalyzer;
import ic2.core.item.tool.HandHeldCropnalyzer;
import ic2.core.ref.ItemName;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(value=Side.CLIENT)
public class GuiCropnalyzer
extends GuiContainer {
    public ContainerCropnalyzer container;
    public String name;
    private static final ResourceLocation background = new ResourceLocation(IC2.textureDomain, "textures/gui/GUICropnalyzer.png");

    public GuiCropnalyzer(ContainerCropnalyzer container1) {
        super((Container)container1);
        this.container = container1;
        this.name = ItemName.cropnalyzer.getItemStack().getDisplayName();
        this.ySize = 223;
    }

    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        this.fontRendererObj.drawString(this.name, 74, 11, 0);
        int level = ((HandHeldCropnalyzer)this.container.base).getScannedLevel();
        if (level <= -1) {
            return;
        }
        if (level == 0) {
            this.fontRendererObj.drawString("UNKNOWN", 8, 37, 16777215);
            return;
        }
        this.fontRendererObj.drawString(((HandHeldCropnalyzer)this.container.base).getSeedName(), 8, 37, 16777215);
        if (level >= 2) {
            this.fontRendererObj.drawString("Tier: " + ((HandHeldCropnalyzer)this.container.base).getSeedTier(), 8, 50, 16777215);
            this.fontRendererObj.drawString("Discovered by:", 8, 73, 16777215);
            this.fontRendererObj.drawString(((HandHeldCropnalyzer)this.container.base).getSeedDiscovered(), 8, 86, 16777215);
        }
        if (level >= 3) {
            this.fontRendererObj.drawString(((HandHeldCropnalyzer)this.container.base).getSeedDesc(0), 8, 109, 16777215);
            this.fontRendererObj.drawString(((HandHeldCropnalyzer)this.container.base).getSeedDesc(1), 8, 122, 16777215);
        }
        if (level >= 4) {
            this.fontRendererObj.drawString("Growth:", 118, 37, 11403055);
            this.fontRendererObj.drawString("" + ((HandHeldCropnalyzer)this.container.base).getSeedGrowth(), 118, 50, 11403055);
            this.fontRendererObj.drawString("Gain:", 118, 73, 15649024);
            this.fontRendererObj.drawString("" + ((HandHeldCropnalyzer)this.container.base).getSeedGain(), 118, 86, 15649024);
            this.fontRendererObj.drawString("Resis.:", 118, 109, 52945);
            this.fontRendererObj.drawString("" + ((HandHeldCropnalyzer)this.container.base).getSeedResistence(), 118, 122, 52945);
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

