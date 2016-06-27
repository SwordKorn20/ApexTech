/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Supplier
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.audio.ISound
 *  net.minecraft.client.audio.PositionedSoundRecord
 *  net.minecraft.client.audio.SoundHandler
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.SoundEvent
 */
package ic2.core.gui;

import com.google.common.base.Supplier;
import ic2.core.GuiIC2;
import ic2.core.gui.GuiElement;
import ic2.core.gui.IClickHandler;
import ic2.core.gui.MouseButton;
import ic2.core.init.Localization;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;

public abstract class Button<T extends Button<T>>
extends GuiElement<T> {
    private static final int iconSize = 16;
    private final IClickHandler handler;
    private Supplier<String> textProvider;
    private Supplier<ItemStack> iconProvider;

    protected Button(GuiIC2<?> gui, int x, int y, int width, int height, IClickHandler handler) {
        super(gui, x, y, width, height);
        this.handler = handler;
    }

    public T withText(final String text) {
        return this.withText(new Supplier<String>(){

            public String get() {
                return text;
            }
        });
    }

    public T withText(Supplier<String> textProvider) {
        this.textProvider = textProvider;
        return (T)this;
    }

    public T withIcon(Supplier<ItemStack> iconProvider) {
        this.iconProvider = iconProvider;
        return (T)this;
    }

    protected int getTextColor(int mouseX, int mouseY) {
        return 14540253;
    }

    @Override
    public void drawBackground(int mouseX, int mouseY) {
        ItemStack stack;
        if (this.textProvider != null) {
            String text = (String)this.textProvider.get();
            if (text != null && !text.isEmpty()) {
                text = Localization.translate(text);
                this.gui.drawXYCenteredString(this.x + this.width / 2, this.y + this.height / 2, text, this.getTextColor(mouseX, mouseY), true);
            }
        } else if (this.iconProvider != null && (stack = (ItemStack)this.iconProvider.get()) != null && stack.getItem() != null) {
            this.gui.drawItem(this.x + (this.width - 16) / 2, this.y + (this.height - 16) / 2, stack);
        }
    }

    @Override
    public void onMouseClick(int mouseX, int mouseY, MouseButton button) {
        this.gui.mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord((SoundEvent)SoundEvents.UI_BUTTON_CLICK, (float)1.0f));
        this.handler.onClick(button);
    }

}

