/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Supplier
 *  com.google.common.base.Suppliers
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.FontRenderer
 *  net.minecraft.inventory.IInventory
 */
package ic2.core.gui;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import ic2.core.ContainerBase;
import ic2.core.GuiIC2;
import ic2.core.gui.GuiElement;
import ic2.core.gui.dynamic.TextProvider;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.inventory.IInventory;

public class Text
extends GuiElement<Text> {
    private final TextProvider.ITextProvider textProvider;
    private final Supplier<Integer> color;
    private final boolean shadow;
    private final boolean fixedHoverWidth;
    private final boolean fixedHoverHeight;
    private final int baseX;
    private final int baseY;
    private final boolean centerX;
    private final boolean centerY;

    public static Text create(GuiIC2<?> gui, int x, int y, String text, int color, boolean shadow) {
        return Text.create(gui, x, y, TextProvider.of(text), color, shadow);
    }

    public static Text create(GuiIC2<?> gui, int x, int y, TextProvider.ITextProvider textProvider, int color, boolean shadow) {
        return Text.create(gui, x, y, textProvider, color, shadow, false, false);
    }

    public static Text create(GuiIC2<?> gui, int x, int y, String text, int color, boolean shadow, boolean centerX, boolean centerY) {
        return Text.create(gui, x, y, TextProvider.of(text), color, shadow, centerX, centerY);
    }

    public static Text create(GuiIC2<?> gui, int x, int y, TextProvider.ITextProvider textProvider, int color, boolean shadow, boolean centerX, boolean centerY) {
        return Text.create(gui, x, y, -1, -1, textProvider, color, shadow, centerX, centerY);
    }

    public static Text create(GuiIC2<?> gui, int x, int y, int width, int height, TextProvider.ITextProvider textProvider, int color, boolean shadow, boolean centerX, boolean centerY) {
        return Text.create(gui, x, y, width, height, textProvider, color, shadow, 0, 0, centerX, centerY);
    }

    public static Text create(GuiIC2<?> gui, int x, int y, int width, int height, TextProvider.ITextProvider textProvider, int color, boolean shadow, int xOffset, int yOffset, boolean centerX, boolean centerY) {
        return Text.create(gui, x, y, width, height, textProvider, Suppliers.ofInstance((Object)color), shadow, xOffset, yOffset, centerX, centerY);
    }

    public static Text create(GuiIC2<?> gui, int x, int y, int width, int height, TextProvider.ITextProvider textProvider, Supplier<Integer> color, boolean shadow, int xOffset, int yOffset, boolean centerX, boolean centerY) {
        boolean fixedHoverHeight;
        boolean fixedHoverWidth;
        if (width < 0) {
            fixedHoverWidth = false;
            width = Text.getWidth(gui, textProvider);
        } else {
            fixedHoverWidth = true;
        }
        if (height < 0) {
            fixedHoverHeight = false;
            height = 8;
        } else {
            fixedHoverHeight = true;
        }
        int baseX = x + xOffset;
        int baseY = y + yOffset;
        if (centerX) {
            if (fixedHoverWidth) {
                baseX += width / 2;
            } else {
                x -= width / 2;
            }
        }
        if (centerY) {
            if (fixedHoverHeight) {
                baseY += (height + 1) / 2;
            } else {
                y -= height / 2;
            }
        }
        return new Text(gui, x, y, width, height, textProvider, color, shadow, fixedHoverWidth, fixedHoverHeight, baseX, baseY, centerX, centerY);
    }

    private Text(GuiIC2<?> gui, int x, int y, int width, int height, TextProvider.ITextProvider textProvider, Supplier<Integer> color, boolean shadow, boolean fixedHoverWidth, boolean fixedHoverHeight, int baseX, int baseY, boolean centerX, boolean centerY) {
        super(gui, x, y, width, height);
        this.textProvider = textProvider;
        this.color = color;
        this.shadow = shadow;
        this.fixedHoverWidth = fixedHoverWidth;
        this.fixedHoverHeight = fixedHoverHeight;
        this.baseX = baseX;
        this.baseY = baseY;
        this.centerX = centerX;
        this.centerY = centerY;
    }

    private static int getWidth(GuiIC2<?> gui, TextProvider.ITextProvider textProvider) {
        String text = textProvider.get(gui.getContainer().base, TextProvider.emptyTokens());
        if (text.isEmpty()) {
            return 0;
        }
        return Minecraft.getMinecraft().fontRendererObj.getStringWidth(text);
    }

    @Override
    public void drawBackground(int mouseX, int mouseY) {
        int textHeight;
        int textWidth;
        String text = this.textProvider.get((Object)this.getBase(), this.getTokens());
        if (text.isEmpty()) {
            textHeight = 0;
            textWidth = 0;
        } else {
            textWidth = this.gui.getStringWidth(text);
            textHeight = 8;
        }
        int textX = this.baseX;
        if (this.centerX) {
            textX -= textWidth / 2;
        }
        int textY = this.baseY;
        if (this.centerY) {
            textY -= textHeight / 2;
        }
        if (!this.fixedHoverWidth) {
            this.x = textX;
            this.width = textWidth;
        }
        if (!this.fixedHoverHeight) {
            this.y = textY;
            this.height = textHeight;
        }
        super.drawBackground(mouseX, mouseY);
        if (!text.isEmpty()) {
            this.gui.drawString(textX, textY, text, (Integer)this.color.get(), this.shadow);
        }
    }

    public static enum TextAlignment {
        Start,
        Center,
        End;
        
        private static final Map<String, TextAlignment> map;
        public final String name;

        private TextAlignment() {
            this.name = this.name().toLowerCase(Locale.ENGLISH);
        }

        public static TextAlignment get(String name) {
            return map.get(name);
        }

        private static Map<String, TextAlignment> getMap() {
            TextAlignment[] values = TextAlignment.values();
            HashMap<String, TextAlignment> ret = new HashMap<String, TextAlignment>(values.length);
            for (TextAlignment style : values) {
                ret.put(style.name, style);
            }
            return ret;
        }

        static {
            map = TextAlignment.getMap();
        }
    }

}

