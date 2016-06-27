/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.fluids.FluidTank
 *  net.minecraftforge.fluids.IFluidTank
 */
package ic2.core.block.kineticgenerator.gui;

import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.block.kineticgenerator.container.ContainerStirlingKineticGenerator;
import ic2.core.block.kineticgenerator.tileentity.TileEntityStirlingKineticGenerator;
import ic2.core.gui.GuiElement;
import ic2.core.gui.TankGauge;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;

public class GuiStirlingKineticGenerator
extends GuiIC2<ContainerStirlingKineticGenerator> {
    public GuiStirlingKineticGenerator(ContainerStirlingKineticGenerator container) {
        super(container, 204);
        this.addElement(TankGauge.createPlain(this, 19, 47, 12, 44, (IFluidTank)((TileEntityStirlingKineticGenerator)container.base).getInputTank()));
        this.addElement(TankGauge.createPlain(this, 145, 47, 12, 44, (IFluidTank)((TileEntityStirlingKineticGenerator)container.base).getOutputTank()));
    }

    @Override
    protected ResourceLocation getTexture() {
        return new ResourceLocation(IC2.textureDomain, "textures/gui/GUIStirlingKineticGenerator.png");
    }
}

