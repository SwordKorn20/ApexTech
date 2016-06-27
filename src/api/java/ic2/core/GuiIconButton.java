/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.FontRenderer
 *  net.minecraft.client.gui.GuiButton
 *  net.minecraft.client.renderer.RenderItem
 *  net.minecraft.client.renderer.texture.TextureManager
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 *  org.lwjgl.opengl.GL11
 */
package ic2.core;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(value=Side.CLIENT)
public class GuiIconButton
extends GuiButton {
    private ResourceLocation texture;
    private int textureX;
    private int textureY;
    private ItemStack stack = null;
    private boolean drawQuantity;

    public GuiIconButton(int id1, int x, int y, int w, int h, ResourceLocation texture1, int textureX1, int textureY1) {
        super(id1, x, y, w, h, "");
        this.texture = texture1;
        this.textureX = textureX1;
        this.textureY = textureY1;
    }

    public GuiIconButton(int id1, int x, int y, int w, int h, ItemStack icon, boolean drawQuantity1) {
        super(id1, x, y, w, h, "");
        this.stack = icon;
        this.drawQuantity = drawQuantity1;
    }

    public void drawButton(Minecraft minecraft, int i, int j) {
        super.drawButton(minecraft, i, j);
        if (this.stack == null) {
            GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            minecraft.getTextureManager().bindTexture(this.texture);
            this.drawTexturedModalRect(this.xPosition + 2, this.yPosition + 1, this.textureX, this.textureY, this.width - 4, this.height - 4);
        } else {
            RenderItem renderItem = minecraft.getRenderItem();
            renderItem.renderItemIntoGUI(this.stack, this.xPosition + 2, this.yPosition + 1);
            if (this.drawQuantity) {
                renderItem.renderItemOverlays(minecraft.fontRendererObj, this.stack, this.xPosition + 2, this.xPosition + 1);
            }
        }
    }
}

