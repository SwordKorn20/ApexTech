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

import ic2.core.ContainerBase;
import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.machine.container.ContainerFluidBottler;
import ic2.core.block.machine.tileentity.TileEntityFluidBottler;
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
public class GuiFluidBottler
extends GuiIC2<ContainerFluidBottler> {
    public GuiFluidBottler(ContainerFluidBottler container) {
        super(container, 184);
        this.addElement(EnergyGauge.asBolt(this, 12, 35, (TileEntityBlock)container.base));
        this.addElement(TankGauge.createNormal(this, 78, 34, (IFluidTank)((TileEntityFluidBottler)container.base).getFluidTank()));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        super.drawGuiContainerBackgroundLayer(f, x, y);
        this.bindTexture();
        int progressSize = Math.round(((TileEntityFluidBottler)((ContainerFluidBottler)this.container).base).getProgress() * 16.0f);
        if (progressSize > 0) {
            this.drawTexturedModalRect(this.guiLeft + 61, this.guiTop + 36, 198, 0, progressSize, 13);
            this.drawTexturedModalRect(this.guiLeft + 61, this.guiTop + 73, 198, 0, progressSize, 13);
            this.drawTexturedModalRect(this.guiLeft + 99, this.guiTop + 55, 198, 0, progressSize, 13);
        }
    }

    @Override
    public ResourceLocation getTexture() {
        return new ResourceLocation(IC2.textureDomain, "textures/gui/GUIBottler.png");
    }
}

