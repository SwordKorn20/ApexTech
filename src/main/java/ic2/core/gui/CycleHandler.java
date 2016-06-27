/*
 * Decompiled with CFR 0_114.
 */
package ic2.core.gui;

import ic2.core.gui.CustomButton;
import ic2.core.gui.IClickHandler;
import ic2.core.gui.INumericValueHandler;
import ic2.core.gui.MouseButton;

public class CycleHandler
implements IClickHandler,
CustomButton.IOverlaySupplier {
    private final int overlayX;
    private final int overlayY;
    private final int overlayStep;
    private final boolean vertical;
    private final int options;
    private final INumericValueHandler handler;

    public CycleHandler(int overlayX, int overlayY, int overlayStep, boolean vertical, int options, INumericValueHandler handler) {
        this.overlayX = overlayX;
        this.overlayY = overlayY;
        this.overlayStep = overlayStep;
        this.vertical = vertical;
        this.options = options;
        this.handler = handler;
    }

    @Override
    public void onClick(MouseButton button) {
        int value = this.getValue();
        if (button == MouseButton.left) {
            value = (value + 1) % this.options;
        } else if (button == MouseButton.right) {
            value = (value + this.options - 1) % this.options;
        } else {
            return;
        }
        this.handler.onChange(value);
    }

    @Override
    public int getOverlayX() {
        if (this.vertical) {
            return this.overlayX;
        }
        return this.overlayX + this.overlayStep * this.getValue();
    }

    @Override
    public int getOverlayY() {
        if (!this.vertical) {
            return this.overlayY;
        }
        return this.overlayY + this.overlayStep * this.getValue();
    }

    private int getValue() {
        int ret = this.handler.getValue();
        if (ret < 0 || ret >= this.options) {
            throw new RuntimeException("invalid value: " + ret);
        }
        return ret;
    }
}

