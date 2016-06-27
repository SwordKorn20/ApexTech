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
import ic2.core.ContainerBase;
import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.block.machine.container.ContainerLiquidHeatExchanger;
import ic2.core.block.machine.tileentity.TileEntityLiquidHeatExchanger;
import ic2.core.gui.GuiElement;
import ic2.core.gui.SlotGrid;
import ic2.core.gui.TankGauge;
import ic2.core.gui.Text;
import ic2.core.gui.dynamic.TextProvider;
import ic2.core.init.Localization;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;

public class GuiLiquidHeatExchanger
extends GuiIC2<ContainerLiquidHeatExchanger> {
    public GuiLiquidHeatExchanger(ContainerLiquidHeatExchanger container) {
        super(container, 204);
        this.addElement(new SlotGrid(this, 46, 50, 5, 1, SlotGrid.SlotStyle.Plain, 1, 1).withTooltip("ic2.LiquidHeatExchanger.gui.tooltipvent"));
        this.addElement(new SlotGrid(this, 46, 72, 5, 1, SlotGrid.SlotStyle.Plain, 1, 1).withTooltip("ic2.LiquidHeatExchanger.gui.tooltipvent"));
        this.addElement(TankGauge.createPlain(this, 19, 47, 12, 44, (IFluidTank)((TileEntityLiquidHeatExchanger)container.base).getInputTank()));
        this.addElement(TankGauge.createPlain(this, 145, 47, 12, 44, (IFluidTank)((TileEntityLiquidHeatExchanger)container.base).getOutputTank()));
        this.addElement(Text.create(this, 50, 28, 78, 13, TextProvider.of(new Supplier<String>(){

            public String get() {
                return "" + ((TileEntityLiquidHeatExchanger)((ContainerLiquidHeatExchanger)GuiLiquidHeatExchanger.access$000((GuiLiquidHeatExchanger)GuiLiquidHeatExchanger.this)).base).gettransmitHeat() + " / " + ((TileEntityLiquidHeatExchanger)((ContainerLiquidHeatExchanger)GuiLiquidHeatExchanger.access$100((GuiLiquidHeatExchanger)GuiLiquidHeatExchanger.this)).base).getMaxHeatEmittedPerTick();
            }
        }), 5752026, false, true, true).withTooltip(new Supplier<String>(){

            public String get() {
                return Localization.translate("ic2.LiquidHeatExchanger.gui.tooltipheat");
            }
        }));
    }

    @Override
    protected ResourceLocation getTexture() {
        return new ResourceLocation(IC2.textureDomain, "textures/gui/GUIHeatSourceFluid.png");
    }

    static /* synthetic */ ContainerBase access$000(GuiLiquidHeatExchanger x0) {
        return x0.container;
    }

    static /* synthetic */ ContainerBase access$100(GuiLiquidHeatExchanger x0) {
        return x0.container;
    }

}

