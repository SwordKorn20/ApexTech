/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidStack
 *  net.minecraftforge.fluids.IFluidTank
 */
package ic2.core.gui;

import ic2.core.GuiIC2;
import ic2.core.gui.GuiElement;
import ic2.core.init.Localization;
import java.util.List;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

public class FluidSlot
extends GuiElement<FluidSlot> {
    private static final int posU = 8;
    private static final int posV = 160;
    private static final int normalWidth = 18;
    private static final int normalHeight = 18;
    private static final int fluidOffsetX = 1;
    private static final int fluidOffsetY = 1;
    private static final int fluidNetWidth = 16;
    private static final int fluidNetHeight = 16;
    private final IFluidTank tank;

    public static FluidSlot createFluidSlot(GuiIC2<?> gui, int x, int y, IFluidTank tank) {
        return new FluidSlot(gui, x, y, 18, 18, tank);
    }

    private FluidSlot(GuiIC2<?> gui, int x, int y, int width, int height, IFluidTank tank) {
        super(gui, x, y, width, height);
        if (tank == null) {
            throw new NullPointerException("Null FluidTank instance.");
        }
        this.tank = tank;
    }

    @Override
    public void drawBackground(int mouseX, int mouseY) {
        FluidSlot.bindCommonTexture();
        FluidStack fs = this.tank.getFluid();
        this.gui.drawTexturedRect(this.x, this.y, this.width, this.height, 8.0, 160.0);
        if (fs != null && fs.amount > 0) {
            int fluidX = this.x + 1;
            int fluidY = this.y + 1;
            int fluidWidth = 16;
            int fluidHeight = 16;
            Fluid fluid = fs.getFluid();
            TextureAtlasSprite sprite = fluid != null ? FluidSlot.getBlockTextureMap().getAtlasSprite(fluid.getStill(fs).toString()) : null;
            int color = fluid != null ? fluid.getColor(fs) : -1;
            FluidSlot.bindBlockTexture();
            this.gui.drawSprite(fluidX, fluidY, fluidWidth, fluidHeight, sprite, color, 1.0);
        }
    }

    @Override
    protected List<String> getToolTip() {
        List<String> ret = super.getToolTip();
        FluidStack fs = this.tank.getFluid();
        if (fs == null || fs.amount <= 0) {
            ret.add("No Fluid");
            ret.add("Amount: 0 " + Localization.translate("ic2.generic.text.mb"));
            ret.add("Type: Not Available");
        } else {
            Fluid fluid = fs.getFluid();
            if (fluid != null) {
                ret.add(fluid.getLocalizedName(fs));
                ret.add("Amount: " + fs.amount + " " + Localization.translate("ic2.generic.text.mb"));
                String state = fs.getFluid().isGaseous() ? "Gas" : "Liquid";
                ret.add("Type: " + state);
            } else {
                ret.add("Invalid FluidStack instance.");
            }
        }
        return ret;
    }
}

