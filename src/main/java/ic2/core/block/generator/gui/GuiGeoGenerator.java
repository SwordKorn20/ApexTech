/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.fluids.FluidTank
 *  net.minecraftforge.fluids.IFluidTank
 */
package ic2.core.block.generator.gui;

import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.generator.container.ContainerGeoGenerator;
import ic2.core.block.generator.tileentity.TileEntityGeoGenerator;
import ic2.core.gui.EnergyGauge;
import ic2.core.gui.GuiElement;
import ic2.core.gui.TankGauge;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;

public class GuiGeoGenerator
extends GuiIC2<ContainerGeoGenerator> {
    private static final ResourceLocation background = new ResourceLocation(IC2.textureDomain, "textures/gui/GUIFluidGenerator.png");

    public GuiGeoGenerator(ContainerGeoGenerator container) {
        super(container);
        this.addElement(EnergyGauge.asBar(this, 112, 29, (TileEntityBlock)container.base));
        this.addElement(TankGauge.createNormal(this, 70, 20, (IFluidTank)((TileEntityGeoGenerator)container.base).getFluidTank()));
    }

    @Override
    protected ResourceLocation getTexture() {
        return background;
    }
}

