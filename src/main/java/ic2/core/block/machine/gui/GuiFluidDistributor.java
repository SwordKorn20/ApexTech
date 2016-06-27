/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.FontRenderer
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.fluids.FluidTank
 *  net.minecraftforge.fluids.IFluidTank
 */
package ic2.core.block.machine.gui;

import ic2.core.ContainerBase;
import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.block.machine.container.ContainerFluidDistributor;
import ic2.core.block.machine.tileentity.TileEntityFluidDistributor;
import ic2.core.gui.GuiElement;
import ic2.core.gui.TankGauge;
import ic2.core.init.Localization;
import ic2.core.network.NetworkManager;
import ic2.core.util.SideGateway;
import java.io.IOException;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;

public class GuiFluidDistributor
extends GuiIC2<ContainerFluidDistributor> {
    public GuiFluidDistributor(ContainerFluidDistributor container) {
        super(container, 184);
        this.addElement(TankGauge.createPlain(this, 29, 38, 55, 47, (IFluidTank)((TileEntityFluidDistributor)container.base).getFluidTank()));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        super.drawGuiContainerForegroundLayer(par1, par2);
        this.fontRendererObj.drawString(Localization.translate("ic2.FluidDistributor.gui.mode.info"), 112, 47, 5752026);
        if (((TileEntityFluidDistributor)((ContainerFluidDistributor)this.container).base).getActive()) {
            this.fontRendererObj.drawString(Localization.translate("ic2.FluidDistributor.gui.mode.concentrate"), 95, 71, 5752026);
        } else {
            this.fontRendererObj.drawString(Localization.translate("ic2.FluidDistributor.gui.mode.distribute"), 95, 71, 5752026);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if ((mouseX -= this.guiLeft) >= 117 && mouseY >= 58 && mouseX <= 135 && (mouseY -= this.guiTop) <= 66) {
            IC2.network.get(false).initiateClientTileEntityEvent((TileEntity)((ContainerFluidDistributor)this.container).base, 1);
        }
    }

    @Override
    protected ResourceLocation getTexture() {
        return new ResourceLocation(IC2.textureDomain, "textures/gui/GUIFluidDistributor.png");
    }
}

