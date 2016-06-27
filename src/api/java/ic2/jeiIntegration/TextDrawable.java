/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  mezz.jei.api.gui.IDrawable
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.FontRenderer
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.util.text.translation.I18n
 */
package ic2.jeiIntegration;

import ic2.core.gui.Text;
import mezz.jei.api.gui.IDrawable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.translation.I18n;

public class TextDrawable
implements IDrawable {
    private String text;
    private Text.TextAlignment alignment;
    private int color;

    public TextDrawable(String text, Text.TextAlignment alignment, int color) {
        this.text = text;
        this.alignment = alignment;
        this.color = color;
    }

    public void draw(Minecraft arg0) {
        int x;
        switch (this.alignment) {
            case Start: {
                x = 0;
                break;
            }
            case Center: {
                x = arg0.currentScreen.width / 2;
                break;
            }
            case End: {
                x = arg0.currentScreen.width - this.getWidth();
                break;
            }
            default: {
                throw new IllegalArgumentException("invalid alignment: " + (Object)((Object)this.alignment));
            }
        }
        arg0.fontRendererObj.drawString(I18n.translateToLocal((String)this.text), x, 0, this.color);
    }

    public void draw(Minecraft arg0, int arg1, int arg2) {
    }

    public int getHeight() {
        return 12;
    }

    public int getWidth() {
        return Minecraft.getMinecraft().fontRendererObj.getStringWidth(I18n.translateToLocal((String)this.text));
    }

}

