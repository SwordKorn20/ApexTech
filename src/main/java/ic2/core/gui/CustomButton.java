/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.ResourceLocation
 */
package ic2.core.gui;

import ic2.core.GuiIC2;
import ic2.core.gui.Button;
import ic2.core.gui.IClickHandler;
import net.minecraft.util.ResourceLocation;

public class CustomButton
extends Button<CustomButton> {
    private int overlayX;
    private int overlayY;
    private final ResourceLocation texture;
    private final IOverlaySupplier overlaySupplier;

    public CustomButton(GuiIC2<?> gui, int x, int y, int width, int height, IClickHandler handler) {
        this(gui, x, y, width, height, 0, 0, null, handler);
    }

    public CustomButton(GuiIC2<?> gui, int x, int y, int width, int height, int overlayX, int overlayY, ResourceLocation texture, IClickHandler handler) {
        super(gui, x, y, width, height, handler);
        this.texture = texture;
        this.overlaySupplier = null;
    }

    public CustomButton(GuiIC2<?> gui, int x, int y, int width, int height, IOverlaySupplier overlaySupplier, ResourceLocation texture, IClickHandler handler) {
        super(gui, x, y, width, height, handler);
        this.overlayX = overlaySupplier.getOverlayX();
        this.overlayY = overlaySupplier.getOverlayY();
        this.texture = texture;
        this.overlaySupplier = overlaySupplier;
    }

    public void setOverlay(int x, int y) {
        this.overlayX = x;
        this.overlayY = y;
    }

    @Override
    public void drawBackground(int mouseX, int mouseY) {
        if (this.texture != null) {
            CustomButton.bindTexture(this.texture);
            if (this.overlaySupplier == null) {
                this.gui.drawTexturedRect(this.x, this.y, this.width, this.height, this.overlayX, this.overlayY);
            } else {
                this.gui.drawTexturedRect(this.x, this.y, this.width, this.height, this.overlaySupplier.getOverlayX(), this.overlaySupplier.getOverlayY());
            }
        }
        if (this.contains(mouseX, mouseY)) {
            this.gui.drawColoredRect(this.x, this.y, this.width, this.height, -2130706433);
        }
        super.drawBackground(mouseX, mouseY);
    }

    public static interface IOverlaySupplier {
        public int getOverlayX();

        public int getOverlayY();
    }

}

