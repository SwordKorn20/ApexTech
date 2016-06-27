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
import ic2.core.block.machine.container.ContainerSolarDestiller;
import ic2.core.block.machine.tileentity.TileEntitySolarDestiller;
import ic2.core.gui.GuiElement;
import ic2.core.gui.TankGauge;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public class GuiSolarDestiller
extends GuiIC2<ContainerSolarDestiller> {
    public GuiSolarDestiller(ContainerSolarDestiller container) {
        super(container, 184);
        this.addElement(TankGauge.createPlain(this, 37, 43, 53, 18, (IFluidTank)((TileEntitySolarDestiller)container.base).inputTank));
        this.addElement(TankGauge.createPlain(this, 115, 55, 17, 43, (IFluidTank)((TileEntitySolarDestiller)container.base).outputTank));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        super.drawGuiContainerBackgroundLayer(f, x, y);
        this.bindTexture();
        if (((TileEntitySolarDestiller)((ContainerSolarDestiller)this.container).base).canWork()) {
            this.drawTexturedModalRect(this.guiLeft + 36, this.guiTop + 26, 0, 184, 97, 29);
        }
    }

    @Override
    protected ResourceLocation getTexture() {
        return new ResourceLocation(IC2.textureDomain, "textures/gui/GUISolarDestiller.png");
    }
}

