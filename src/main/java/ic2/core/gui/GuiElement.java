/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Supplier
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.texture.TextureManager
 *  net.minecraft.client.renderer.texture.TextureMap
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.util.ResourceLocation
 */
package ic2.core.gui;

import com.google.common.base.Supplier;
import ic2.core.ContainerBase;
import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.gui.IEnableHandler;
import ic2.core.gui.MouseButton;
import ic2.core.gui.dynamic.TextProvider;
import ic2.core.init.Localization;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public abstract class GuiElement<T extends GuiElement<T>> {
    protected static final int hoverColor = -2130706433;
    public static final ResourceLocation commonTexture = new ResourceLocation(IC2.textureDomain, "textures/gui/common.png");
    protected final GuiIC2<?> gui;
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    private IEnableHandler enableHandler;
    private Supplier<String> tooltipProvider;

    protected GuiElement(GuiIC2<?> gui, int x, int y, int width, int height) {
        if (width < 0) {
            throw new IllegalArgumentException("negative width");
        }
        if (height < 0) {
            throw new IllegalArgumentException("negative height");
        }
        this.gui = gui;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public final boolean isEnabled() {
        return this.enableHandler == null || this.enableHandler.isEnabled();
    }

    public boolean contains(int x, int y) {
        return x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + this.height;
    }

    public T withEnableHandler(IEnableHandler enableHandler) {
        this.enableHandler = enableHandler;
        return (T)this;
    }

    public T withTooltip(final String tooltip) {
        return this.withTooltip(new Supplier<String>(){

            public String get() {
                return tooltip;
            }
        });
    }

    public T withTooltip(Supplier<String> tooltipProvider) {
        this.tooltipProvider = tooltipProvider;
        return (T)this;
    }

    public void drawBackground(int mouseX, int mouseY) {
    }

    public void drawForeground(int mouseX, int mouseY) {
        if (this.contains(mouseX, mouseY) && !this.suppressTooltip(mouseX, mouseY)) {
            String tooltip;
            List<String> lines = this.getToolTip();
            if (this.tooltipProvider != null && (tooltip = (String)this.tooltipProvider.get()) != null && !tooltip.isEmpty()) {
                GuiElement.addLines(lines, this.processText(tooltip));
            }
            if (!lines.isEmpty()) {
                this.gui.drawTooltip(mouseX, mouseY, lines);
            }
        }
    }

    private static void addLines(List<String> list, String str) {
        int pos;
        int startPos = 0;
        while ((pos = str.indexOf(10, startPos)) != -1) {
            list.add(str.substring(startPos, pos));
            startPos = pos + 1;
        }
        if (startPos == 0) {
            list.add(str);
        } else {
            list.add(str.substring(startPos));
        }
    }

    public void onMouseClick(int mouseX, int mouseY, MouseButton button) {
    }

    protected boolean suppressTooltip(int mouseX, int mouseY) {
        return false;
    }

    protected List<String> getToolTip() {
        return new ArrayList<String>();
    }

    protected final String processText(String text) {
        return Localization.translate(text);
    }

    protected final IInventory getBase() {
        return this.gui.getContainer().base;
    }

    protected final Map<String, TextProvider.ITextProvider> getTokens() {
        HashMap<String, TextProvider.ITextProvider> ret = new HashMap<String, TextProvider.ITextProvider>();
        ret.put("name", TextProvider.ofTranslated(this.getBase().getName()));
        return ret;
    }

    protected static void bindTexture(ResourceLocation texture) {
        Minecraft.getMinecraft().renderEngine.bindTexture(texture);
    }

    public static void bindCommonTexture() {
        Minecraft.getMinecraft().renderEngine.bindTexture(commonTexture);
    }

    protected static void bindBlockTexture() {
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
    }

    protected static TextureMap getBlockTextureMap() {
        return Minecraft.getMinecraft().getTextureMapBlocks();
    }

}

