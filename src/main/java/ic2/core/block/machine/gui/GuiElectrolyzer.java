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
import ic2.core.block.machine.container.ContainerElectrolyzer;
import ic2.core.block.machine.tileentity.TileEntityElectrolyzer;
import ic2.core.gui.EnergyGauge;
import ic2.core.gui.FluidSlot;
import ic2.core.gui.GuiElement;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public class GuiElectrolyzer
extends GuiIC2<ContainerElectrolyzer> {
    private static final ResourceLocation background = new ResourceLocation(IC2.textureDomain, "textures/gui/GUIElectrolyzer.png");

    public GuiElectrolyzer(ContainerElectrolyzer container) {
        super(container);
        this.addElement(EnergyGauge.asBolt(this, 12, 44, (TileEntityBlock)container.base));
        this.addElement(FluidSlot.createFluidSlot(this, 78, 16, (IFluidTank)((TileEntityElectrolyzer)container.base).getInput()));
    }

    @Override
    protected ResourceLocation getTexture() {
        return background;
    }
}

