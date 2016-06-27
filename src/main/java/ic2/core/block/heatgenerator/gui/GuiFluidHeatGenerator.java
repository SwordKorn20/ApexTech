/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.FontRenderer
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.fluids.FluidTank
 *  net.minecraftforge.fluids.IFluidTank
 */
package ic2.core.block.heatgenerator.gui;

import ic2.core.ContainerBase;
import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.block.heatgenerator.container.ContainerFluidHeatGenerator;
import ic2.core.block.heatgenerator.tileentity.TileEntityFluidHeatGenerator;
import ic2.core.gui.GuiElement;
import ic2.core.gui.TankGauge;
import ic2.core.init.Localization;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;

public class GuiFluidHeatGenerator
extends GuiIC2<ContainerFluidHeatGenerator> {
    private static final ResourceLocation background = new ResourceLocation(IC2.textureDomain, "textures/gui/GUIFluidHeatGenerator.png");

    public GuiFluidHeatGenerator(ContainerFluidHeatGenerator container) {
        super(container);
        this.addElement(TankGauge.createNormal(this, 70, 20, (IFluidTank)((TileEntityFluidHeatGenerator)container.base).getFluidTank()));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        this.fontRendererObj.drawString(Localization.translate("ic2.FluidHeatGenerator.gui.info.Emit") + ((TileEntityFluidHeatGenerator)((ContainerFluidHeatGenerator)this.container).base).gettransmitHeat(), 96, 33, 5752026);
        this.fontRendererObj.drawString(Localization.translate("ic2.FluidHeatGenerator.gui.info.MaxEmit") + ((TileEntityFluidHeatGenerator)((ContainerFluidHeatGenerator)this.container).base).getMaxHeatEmittedPerTick(), 96, 52, 5752026);
    }

    @Override
    protected ResourceLocation getTexture() {
        return background;
    }
}

