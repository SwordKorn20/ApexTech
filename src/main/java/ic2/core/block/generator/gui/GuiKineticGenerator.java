/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Supplier
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.util.ResourceLocation
 */
package ic2.core.block.generator.gui;

import com.google.common.base.Supplier;
import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.block.generator.container.ContainerKineticGenerator;
import ic2.core.block.generator.tileentity.TileEntityKineticGenerator;
import ic2.core.gui.GuiElement;
import ic2.core.gui.Text;
import ic2.core.gui.dynamic.TextProvider;
import ic2.core.init.Localization;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class GuiKineticGenerator
extends GuiIC2<ContainerKineticGenerator> {
    private static final ResourceLocation background = new ResourceLocation(IC2.textureDomain, "textures/gui/GUIKineticGenerator.png");

    public GuiKineticGenerator(final ContainerKineticGenerator container) {
        super(container);
        this.addElement(Text.create(this, 41, 49, 96, 13, TextProvider.of(new Supplier<String>(){

            public String get() {
                return Localization.translate("ic2.KineticGenerator.gui.Output", Float.valueOf((float)Math.round(((TileEntityKineticGenerator)container.base).getproduction() * 10.0) / 10.0f));
            }
        }), 2157374, false, 4, 0, false, true));
    }

    @Override
    protected ResourceLocation getTexture() {
        return background;
    }

}

