/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.FontRenderer
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public class GuiIC2ErrorScreen
extends GuiScreen {
    private final String title;
    private final String error;

    public GuiIC2ErrorScreen(String title1, String error1) {
        this.title = title1;
        this.error = error1;
    }

    public void drawScreen(int par1, int par2, float par3) {
        String[] split;
        this.drawBackground(0);
        this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, this.height / 4 - 60 + 20, 16777215);
        int add = 0;
        for (String s : split = this.error.split("\n")) {
            this.drawString(this.fontRendererObj, s, this.width / 2 - 180, this.height / 4 - 60 + 60 - 10 + add, 10526880);
            add += 10;
        }
    }
}

