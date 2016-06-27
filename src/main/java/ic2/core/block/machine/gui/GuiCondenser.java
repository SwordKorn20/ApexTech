/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Supplier
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.fluids.FluidTank
 *  net.minecraftforge.fluids.IFluidTank
 */
package ic2.core.block.machine.gui;

import com.google.common.base.Supplier;
import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.machine.container.ContainerCondenser;
import ic2.core.block.machine.tileentity.TileEntityCondenser;
import ic2.core.gui.EnergyGauge;
import ic2.core.gui.Gauge;
import ic2.core.gui.GuiElement;
import ic2.core.gui.LinkedGauge;
import ic2.core.gui.SlotGrid;
import ic2.core.gui.TankGauge;
import ic2.core.gui.dynamic.IGuiValueProvider;
import ic2.core.init.Localization;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;

public class GuiCondenser
extends GuiIC2<ContainerCondenser> {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(IC2.textureDomain, "textures/gui/GUICondenser.png");

    public GuiCondenser(final ContainerCondenser container) {
        super(container, 184);
        Supplier<String> ventTooltipSupplier = new Supplier<String>(){

            public String get() {
                Object[] arrobject = new Object[1];
                ((TileEntityCondenser)container.base).getClass();
                arrobject[0] = 2;
                return Localization.translate("ic2.Condenser.gui.tooltipvent", arrobject);
            }
        };
        this.addElement(new SlotGrid(this, 25, 25, 1, 2, SlotGrid.SlotStyle.Normal).withTooltip(ventTooltipSupplier));
        this.addElement(new SlotGrid(this, 133, 25, 1, 2, SlotGrid.SlotStyle.Normal).withTooltip(ventTooltipSupplier));
        this.addElement(EnergyGauge.asBolt(this, 12, 26, (TileEntityBlock)container.base));
        this.addElement(TankGauge.createPlain(this, 46, 27, 84, 33, (IFluidTank)((TileEntityCondenser)container.base).getInputTank()));
        this.addElement(TankGauge.createPlain(this, 46, 74, 84, 15, (IFluidTank)((TileEntityCondenser)container.base).getOutputTank()));
        this.addElement(new LinkedGauge(this, 47, 63, (IGuiValueProvider)container.base, "progress", Gauge.GaugeStyle.ProgressCondenser));
    }

    @Override
    protected ResourceLocation getTexture() {
        return BACKGROUND;
    }

}

