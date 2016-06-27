/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Supplier
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.util.ResourceLocation
 */
package ic2.core.block.heatgenerator.gui;

import com.google.common.base.Supplier;
import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.block.heatgenerator.container.ContainerSolidHeatGenerator;
import ic2.core.block.heatgenerator.tileentity.TileEntitySolidHeatGenerator;
import ic2.core.gui.Gauge;
import ic2.core.gui.GuiElement;
import ic2.core.gui.LinkedGauge;
import ic2.core.gui.Text;
import ic2.core.gui.dynamic.IGuiValueProvider;
import ic2.core.gui.dynamic.TextProvider;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class GuiSolidHeatGenerator
extends GuiIC2<ContainerSolidHeatGenerator> {
    private static final ResourceLocation background = new ResourceLocation(IC2.textureDomain, "textures/gui/GUISolidHeatGenerator.png");

    public GuiSolidHeatGenerator(final ContainerSolidHeatGenerator container) {
        super(container);
        this.addElement(new LinkedGauge(this, 81, 29, (IGuiValueProvider)container.base, "fuel", Gauge.GaugeStyle.Fuel));
        this.addElement(Text.create(this, 48, 66, 79, 13, TextProvider.of(new Supplier<String>(){

            public String get() {
                return "" + ((TileEntitySolidHeatGenerator)container.base).gettransmitHeat() + " / " + ((TileEntitySolidHeatGenerator)container.base).getMaxHeatEmittedPerTick();
            }
        }), 5752026, false, 0, 0, true, true).withTooltip("ic2.SolidHeatGenerator.gui.tooltipheat"));
    }

    @Override
    protected ResourceLocation getTexture() {
        return background;
    }

}

