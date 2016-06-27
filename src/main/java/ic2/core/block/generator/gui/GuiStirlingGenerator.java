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
import ic2.core.block.TileEntityBlock;
import ic2.core.block.generator.container.ContainerStirlingGenerator;
import ic2.core.block.generator.tileentity.TileEntityStirlingGenerator;
import ic2.core.gui.EnergyGauge;
import ic2.core.gui.GuiElement;
import ic2.core.gui.Text;
import ic2.core.gui.dynamic.TextProvider;
import ic2.core.init.Localization;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class GuiStirlingGenerator
extends GuiIC2<ContainerStirlingGenerator> {
    private static final ResourceLocation background = new ResourceLocation(IC2.textureDomain, "textures/gui/GUIStirlingGenerator.png");

    public GuiStirlingGenerator(final ContainerStirlingGenerator container) {
        super(container);
        this.addElement(EnergyGauge.asBar(this, 59, 33, (TileEntityBlock)container.base));
        this.addElement(Text.create(this, 41, 49, 96, 13, TextProvider.of(new Supplier<String>(){

            public String get() {
                return Localization.translate("ic2.generic.text.hu") + ((TileEntityStirlingGenerator)container.base).receivedheat + " / " + ((TileEntityStirlingGenerator)container.base).production + Localization.translate("ic2.generic.text.EU");
            }
        }), 5752026, false, true, true).withTooltip(new Supplier<String>(){

            public String get() {
                return Localization.translate("ic2.StirlingGenerator.gui.productiontooltip");
            }
        }));
    }

    @Override
    protected ResourceLocation getTexture() {
        return background;
    }

}

