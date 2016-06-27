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
import ic2.core.block.machine.container.ContainerFermenter;
import ic2.core.block.machine.tileentity.TileEntityFermenter;
import ic2.core.gui.Gauge;
import ic2.core.gui.GuiElement;
import ic2.core.gui.LinkedGauge;
import ic2.core.gui.TankGauge;
import ic2.core.gui.dynamic.IGuiValueProvider;
import ic2.core.init.Localization;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;

public class GuiFermenter
extends GuiIC2<ContainerFermenter> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(IC2.textureDomain, "textures/gui/GUIFermenter.png");

    public GuiFermenter(final ContainerFermenter container) {
        super(container, 184);
        this.addElement(TankGauge.createPlain(this, 38, 49, 48, 30, (IFluidTank)((TileEntityFermenter)container.base).getInputTank()));
        this.addElement(TankGauge.createNormal(this, 125, 22, (IFluidTank)((TileEntityFermenter)container.base).getOutputTank()));
        this.addElement(new LinkedGauge(this, 42, 41, (IGuiValueProvider)container.base, "heat", Gauge.GaugeStyle.HeatFermenter).withTooltip(new Supplier<String>(){

            public String get() {
                return Localization.translate("ic2.Fermenter.gui.info.conversion") + " " + (int)(((TileEntityFermenter)container.base).getGuiValue("heat") * 100.0) + "%";
            }
        }));
        this.addElement(new LinkedGauge(this, 38, 88, (IGuiValueProvider)container.base, "progress", Gauge.GaugeStyle.ProgressFermenter).withTooltip("ic2.Fermenter.gui.info.waste"));
    }

    @Override
    protected ResourceLocation getTexture() {
        return TEXTURE;
    }

}

