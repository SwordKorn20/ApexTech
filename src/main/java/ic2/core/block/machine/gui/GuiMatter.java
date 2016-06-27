/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.FontRenderer
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
import ic2.core.block.machine.container.ContainerMatter;
import ic2.core.block.machine.tileentity.TileEntityMatter;
import ic2.core.gui.GuiElement;
import ic2.core.gui.TankGauge;
import ic2.core.init.Localization;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public class GuiMatter
extends GuiIC2<ContainerMatter> {
    public String progressLabel;
    public String amplifierLabel;

    public GuiMatter(ContainerMatter container) {
        super(container);
        this.addElement(TankGauge.createNormal(this, 96, 22, (IFluidTank)((TileEntityMatter)container.base).getFluidTank()));
        this.progressLabel = Localization.translate("ic2.Matter.gui.info.progress");
        this.amplifierLabel = Localization.translate("ic2.Matter.gui.info.amplifier");
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        this.fontRendererObj.drawString(this.progressLabel, 8, 22, 4210752);
        this.fontRendererObj.drawString(((TileEntityMatter)((ContainerMatter)this.container).base).getProgressAsString(), 18, 31, 4210752);
        if (((TileEntityMatter)((ContainerMatter)this.container).base).scrap > 0) {
            this.fontRendererObj.drawString(this.amplifierLabel, 8, 46, 4210752);
            this.fontRendererObj.drawString("" + ((TileEntityMatter)((ContainerMatter)this.container).base).scrap, 8, 58, 4210752);
        }
    }

    @Override
    public ResourceLocation getTexture() {
        return new ResourceLocation(IC2.textureDomain, "textures/gui/GUIMatter.png");
    }
}

