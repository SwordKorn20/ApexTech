/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.FontRenderer
 *  net.minecraft.client.gui.inventory.GuiContainer
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.RenderItem
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.VertexBuffer
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.client.renderer.texture.TextureManager
 *  net.minecraft.client.renderer.texture.TextureMap
 *  net.minecraft.client.renderer.vertex.DefaultVertexFormats
 *  net.minecraft.client.renderer.vertex.VertexFormat
 *  net.minecraft.inventory.Container
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.ResourceLocation
 */
package ic2.core;

import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.gui.GuiElement;
import ic2.core.gui.IClickHandler;
import ic2.core.gui.MouseButton;
import ic2.core.init.Localization;
import ic2.core.network.NetworkManager;
import ic2.core.upgrade.IUpgradableBlock;
import ic2.core.upgrade.IUpgradeItem;
import ic2.core.upgrade.UpgradableProperty;
import ic2.core.upgrade.UpgradeRegistry;
import ic2.core.util.SideGateway;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public abstract class GuiIC2<T extends ContainerBase<? extends IInventory>>
extends GuiContainer {
    public static final int textHeight = 8;
    protected final T container;
    protected final List<GuiElement<?>> elements = new ArrayList();

    public GuiIC2(T container) {
        this(container, 176, 166);
    }

    public GuiIC2(T container, int ySize) {
        this(container, 176, ySize);
    }

    public GuiIC2(T container, int xSize, int ySize) {
        super(container);
        this.container = container;
        this.ySize = ySize;
        this.xSize = xSize;
    }

    public T getContainer() {
        return this.container;
    }

    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mouseX -= this.guiLeft;
        mouseY -= this.guiTop;
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        this.bindTexture();
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        String name = Localization.translate(this.container.base.getName());
        this.drawXCenteredString(this.xSize / 2, 6, name, 4210752, false);
        if (this.container.base instanceof IUpgradableBlock) {
            this.mc.getTextureManager().bindTexture(new ResourceLocation(IC2.textureDomain, "textures/gui/infobutton.png"));
            this.drawTexturedRect(3.0, 3.0, 10.0, 10.0, 0.0, 0.0);
        }
        for (GuiElement element : this.elements) {
            if (!element.isEnabled()) continue;
            element.drawBackground(mouseX, mouseY);
        }
    }

    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        mouseX -= this.guiLeft;
        mouseY -= this.guiTop;
        if (this.container.base instanceof IUpgradableBlock) {
            this.handleUpgradeTooltip(mouseX, mouseY);
        }
        for (GuiElement element : this.elements) {
            if (!element.isEnabled()) continue;
            element.drawForeground(mouseX, mouseY);
        }
    }

    private void handleUpgradeTooltip(int mouseX, int mouseY) {
        int areaSize = 12;
        if (mouseX < 0 || mouseX > 12 || mouseY < 0 || mouseY > 12) {
            return;
        }
        ArrayList<String> text = new ArrayList<String>();
        text.add(Localization.translate("ic2.generic.text.upgrade"));
        for (ItemStack stack : GuiIC2.getCompatibleUpgrades((IUpgradableBlock)this.container.base)) {
            text.add(stack.getDisplayName());
        }
        this.drawTooltip(mouseX, mouseY, text);
    }

    private static List<ItemStack> getCompatibleUpgrades(IUpgradableBlock block) {
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        Set<UpgradableProperty> properties = block.getUpgradableProperties();
        for (ItemStack stack : UpgradeRegistry.getUpgrades()) {
            IUpgradeItem item = (IUpgradeItem)stack.getItem();
            if (!item.isSuitableFor(stack, properties)) continue;
            ret.add(stack);
        }
        return ret;
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        MouseButton button = MouseButton.get(mouseButton);
        if (button == null) {
            return;
        }
        mouseX -= this.guiLeft;
        mouseY -= this.guiTop;
        for (GuiElement element : this.elements) {
            if (!element.isEnabled() || !element.contains(mouseX, mouseY)) continue;
            element.onMouseClick(mouseX, mouseY, button);
        }
    }

    public void drawTexturedRect(double x, double y, double width, double height, double texX, double texY) {
        this.drawTexturedRect(x, y, width, height, texX, texY, false);
    }

    public void drawTexturedRect(double x, double y, double width, double height, double texX, double texY, boolean mirrorX) {
        this.drawTexturedRect(x, y, width, height, texX, texY, 0.00390625, 0.00390625, mirrorX);
    }

    public void drawTexturedRect(double x, double y, double width, double height, double texX, double texY, double uScale, double vScale, boolean mirrorX) {
        double xE = (x += (double)this.guiLeft) + width;
        double yE = (y += (double)this.guiTop) + height;
        double uS = texX * uScale;
        double vS = texY * vScale;
        double uE = (texX + width) * uScale;
        double vE = (texY + height) * vScale;
        if (mirrorX) {
            double tmp = uS;
            uS = uE;
            uE = tmp;
        }
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer worldrenderer = tessellator.getBuffer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(x, y, (double)this.zLevel).tex(uS, vS).endVertex();
        worldrenderer.pos(x, yE, (double)this.zLevel).tex(uS, vE).endVertex();
        worldrenderer.pos(xE, yE, (double)this.zLevel).tex(uE, vE).endVertex();
        worldrenderer.pos(xE, y, (double)this.zLevel).tex(uE, vS).endVertex();
        tessellator.draw();
    }

    public void drawSprite(double x, double y, double width, double height, TextureAtlasSprite sprite, int color, double scale) {
        if (sprite == null) {
            sprite = this.mc.getTextureMapBlocks().getMissingSprite();
        }
        y += (double)this.guiTop;
        double uS = sprite.getMinU();
        double vS = sprite.getMinV();
        double spriteWidth = (double)sprite.getMaxU() - uS;
        double spriteHeight = (double)sprite.getMaxV() - vS;
        int a = color >>> 24 & 255;
        int r = color >>> 16 & 255;
        int g = color >>> 8 & 255;
        int b = color & 255;
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        for (double xS = x += (double)this.guiLeft; xS <= x + width; xS += (scale *= 16.0)) {
            double xE = Math.min(xS + scale, x + width);
            double uE = uS + (xE - xS) / scale * spriteWidth;
            for (double yS = y; yS <= y + height; yS += scale) {
                double yE = Math.min(yS + scale, y + height);
                double vE = vS + (yE - yS) / scale * spriteHeight;
                buffer.pos(xS, yS, (double)this.zLevel).tex(uS, vS).color(r, g, b, a).endVertex();
                buffer.pos(xS, yE, (double)this.zLevel).tex(uS, vE).color(r, g, b, a).endVertex();
                buffer.pos(xE, yE, (double)this.zLevel).tex(uE, vE).color(r, g, b, a).endVertex();
                buffer.pos(xE, yS, (double)this.zLevel).tex(uE, vS).color(r, g, b, a).endVertex();
            }
        }
        tessellator.draw();
    }

    public void drawItem(int x, int y, ItemStack stack) {
        this.mc.getRenderItem().renderItemIntoGUI(stack, this.guiLeft + x, this.guiTop + y);
    }

    public void drawColoredRect(int x, int y, int width, int height, int color) {
        GuiIC2.drawRect((int)x, (int)y, (int)((x += this.guiLeft) + width), (int)((y += this.guiTop) + height), (int)color);
    }

    public void drawString(int x, int y, String text, int color, boolean shadow) {
        this.fontRendererObj.drawString(text, (float)(this.guiLeft + x), (float)(this.guiTop + y), color, shadow);
    }

    public void drawXCenteredString(int x, int y, String text, int color, boolean shadow) {
        this.drawCenteredString(x, y, text, color, shadow, true, false);
    }

    public void drawXYCenteredString(int x, int y, String text, int color, boolean shadow) {
        this.drawCenteredString(x, y, text, color, shadow, true, true);
    }

    public void drawCenteredString(int x, int y, String text, int color, boolean shadow, boolean centerX, boolean centerY) {
        if (centerX) {
            x -= this.getStringWidth(text) / 2;
        }
        if (centerY) {
            y -= 4;
        }
        this.fontRendererObj.drawString(text, this.guiLeft + x, this.guiTop + y, color);
    }

    public int getStringWidth(String text) {
        return this.fontRendererObj.getStringWidth(text);
    }

    public void drawTooltip(int x, int y, List<String> text) {
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        this.drawHoveringText(text, x, y);
        GlStateManager.disableLighting();
    }

    protected void addElement(GuiElement<?> element) {
        this.elements.add(element);
    }

    protected final void bindTexture() {
        this.mc.getTextureManager().bindTexture(this.getTexture());
    }

    protected IClickHandler createEventSender(final int event) {
        if (this.container.base instanceof TileEntity) {
            return new IClickHandler(){

                @Override
                public void onClick(MouseButton button) {
                    IC2.network.get(false).initiateClientTileEntityEvent((TileEntity)GuiIC2.this.container.base, event);
                }
            };
        }
        throw new IllegalArgumentException("not applicable for " + this.container.base);
    }

    protected abstract ResourceLocation getTexture();

}

