/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Supplier
 *  net.minecraft.item.ItemStack
 */
package ic2.core.gui;

import com.google.common.base.Supplier;
import ic2.core.GuiIC2;
import ic2.core.gui.GuiElement;
import net.minecraft.item.ItemStack;

public class ItemImage
extends GuiElement<ItemImage> {
    private final Supplier<ItemStack> itemSupplier;

    public ItemImage(GuiIC2<?> gui, int x, int y, Supplier<ItemStack> itemSupplier) {
        super(gui, x, y, 16, 16);
        this.itemSupplier = itemSupplier;
    }

    @Override
    public void drawBackground(int mouseX, int mouseY) {
        super.drawBackground(mouseX, mouseY);
        ItemStack stack = (ItemStack)this.itemSupplier.get();
        if (stack != null) {
            this.gui.drawItem(this.x, this.y, stack);
        }
    }
}

