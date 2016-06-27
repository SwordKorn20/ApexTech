/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.fluids.FluidTank
 *  net.minecraftforge.fluids.IFluidTank
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block.machine.gui;

import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.machine.container.ContainerCropmatron;
import ic2.core.block.machine.tileentity.TileEntityCropmatron;
import ic2.core.gui.EnergyGauge;
import ic2.core.gui.GuiElement;
import ic2.core.gui.TankGauge;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public class GuiCropmatron
extends GuiIC2<ContainerCropmatron> {
    public GuiCropmatron(ContainerCropmatron container) {
        super(container, 191);
        this.addElement(EnergyGauge.asBolt(this, 156, 82, (TileEntityBlock)container.base));
        this.addElement(TankGauge.createPlain(this, 29, 26, 24, 47, (IFluidTank)((TileEntityCropmatron)container.base).getWaterTank()));
        this.addElement(TankGauge.createPlain(this, 123, 26, 24, 47, (IFluidTank)((TileEntityCropmatron)container.base).getExTank()));
    }

    @Override
    public ResourceLocation getTexture() {
        return new ResourceLocation(IC2.textureDomain, "textures/gui/GUICropmatron.png");
    }
}

