/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.inventory.Slot
 *  net.minecraft.item.ItemStack
 */
package ic2.core.gui;

import ic2.core.GuiIC2;
import ic2.core.gui.GuiElement;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotGrid
extends GuiElement<SlotGrid> {
    private final SlotStyle style;
    private final int border;
    private final int spacing;

    public SlotGrid(GuiIC2<?> gui, int x, int y, SlotStyle style) {
        this(gui, x, y, 1, 1, style);
    }

    public SlotGrid(GuiIC2<?> gui, int x, int y, int xCount, int yCount, SlotStyle style) {
        this(gui, x, y, xCount, yCount, style, 0, 0);
    }

    public SlotGrid(GuiIC2<?> gui, int x, int y, SlotStyle style, int border) {
        this(gui, x, y, 1, 1, style, border, 0);
    }

    public SlotGrid(GuiIC2<?> gui, int x, int y, int xCount, int yCount, SlotStyle style, int border, int spacing) {
        super(gui, x - border, y - border, xCount * style.width + 2 * border + (xCount - 1) * spacing, yCount * style.height + 2 * border + (yCount - 1) * spacing);
        this.style = style;
        this.border = border;
        this.spacing = spacing;
    }

    @Override
    public void drawBackground(int mouseX, int mouseY) {
        super.drawBackground(mouseX, mouseY);
        if (this.style.hasBackground) {
            SlotGrid.bindCommonTexture();
            int startX = this.x + this.border;
            int startY = this.y + this.border;
            int maxX = this.x + this.width - this.border;
            int maxY = this.y + this.height - this.border;
            int xStep = this.style.width + this.spacing;
            int yStep = this.style.height + this.spacing;
            for (int cy = startY; cy < maxY; cy += yStep) {
                for (int cx = startX; cx < maxX; cx += xStep) {
                    this.gui.drawTexturedRect(cx, cy, this.style.width, this.style.height, this.style.u, this.style.v);
                }
            }
        }
    }

    @Override
    protected boolean suppressTooltip(int mouseX, int mouseY) {
        if (this.gui.mc.thePlayer.inventory.getItemStack() != null) {
            return false;
        }
        Slot slot = this.gui.getSlotUnderMouse();
        return slot != null && slot.getHasStack();
    }

    public static enum SlotStyle {
        Normal(103, 7, 18, 18),
        Large(99, 35, 26, 26),
        Plain(16, 16);
        
        private static final Map<String, SlotStyle> map;
        public static final int refSize = 16;
        public final String name;
        public final int u;
        public final int v;
        public final int width;
        public final int height;
        public final boolean hasBackground;

        private SlotStyle(int u, int v, int width, int height) {
            this(u, v, width, height, true);
        }

        private SlotStyle(int width, int height) {
            this(0, 0, width, height, false);
        }

        private SlotStyle(int u, int v, int width, int height, boolean hasBackground) {
            this.name = this.name().toLowerCase(Locale.ENGLISH);
            this.u = u;
            this.v = v;
            this.width = width;
            this.height = height;
            this.hasBackground = hasBackground;
        }

        public static SlotStyle get(String name) {
            return map.get(name);
        }

        private static Map<String, SlotStyle> getMap() {
            SlotStyle[] values = SlotStyle.values();
            HashMap<String, SlotStyle> ret = new HashMap<String, SlotStyle>(values.length);
            for (SlotStyle style : values) {
                ret.put(style.name, style);
            }
            return ret;
        }

        static {
            map = SlotStyle.getMap();
        }
    }

}

