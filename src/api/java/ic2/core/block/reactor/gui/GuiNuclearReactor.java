/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Supplier
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.fluids.FluidTank
 *  net.minecraftforge.fluids.IFluidTank
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block.reactor.gui;

import com.google.common.base.Supplier;
import ic2.core.ContainerBase;
import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.block.reactor.container.ContainerNuclearReactor;
import ic2.core.block.reactor.tileentity.TileEntityNuclearReactorElectric;
import ic2.core.gui.Area;
import ic2.core.gui.Gauge;
import ic2.core.gui.GuiElement;
import ic2.core.gui.IEnableHandler;
import ic2.core.gui.LinkedGauge;
import ic2.core.gui.TankGauge;
import ic2.core.gui.Text;
import ic2.core.gui.dynamic.IGuiValueProvider;
import ic2.core.gui.dynamic.TextProvider;
import ic2.core.init.Localization;
import ic2.core.init.MainConfig;
import ic2.core.util.ConfigUtil;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public class GuiNuclearReactor
extends GuiIC2<ContainerNuclearReactor> {
    private final ResourceLocation background = new ResourceLocation(IC2.textureDomain, "textures/gui/GUINuclearReactor.png");
    private final ResourceLocation backgroundFluid = new ResourceLocation(IC2.textureDomain, "textures/gui/GUINuclearReactorFluid.png");

    public GuiNuclearReactor(ContainerNuclearReactor container) {
        super(container, 212, 243);
        IEnableHandler enableHandler = new IEnableHandler(){

            @Override
            public boolean isEnabled() {
                return ((TileEntityNuclearReactorElectric)((ContainerNuclearReactor)GuiNuclearReactor.access$000((GuiNuclearReactor)GuiNuclearReactor.this)).base).isFluidCooled();
            }
        };
        this.addElement(TankGauge.createBorderless(this, 10, 54, (IFluidTank)((TileEntityNuclearReactorElectric)container.base).getinputtank(), false).withEnableHandler(enableHandler));
        this.addElement(TankGauge.createBorderless(this, 190, 54, (IFluidTank)((TileEntityNuclearReactorElectric)container.base).getoutputtank(), true).withEnableHandler(enableHandler));
        this.addElement(new LinkedGauge(this, 7, 136, (IGuiValueProvider)container.base, "heat", Gauge.GaugeStyle.HeatNuclearReactor).withTooltip(new Supplier<String>(){

            public String get() {
                return Localization.translate("ic2.NuclearReactor.gui.info.temp", ((TileEntityNuclearReactorElectric)((ContainerNuclearReactor)GuiNuclearReactor.access$100((GuiNuclearReactor)GuiNuclearReactor.this)).base).getGuiValue("heat") * 100.0, 0);
            }
        }));
        this.addElement(Text.create(this, 107, 136, 200, 13, TextProvider.of(new Supplier<String>(){

            public String get() {
                if (((TileEntityNuclearReactorElectric)((ContainerNuclearReactor)GuiNuclearReactor.access$200((GuiNuclearReactor)GuiNuclearReactor.this)).base).isFluidCooled()) {
                    return Localization.translate("ic2.NuclearReactor.gui.info.HUoutput", ((TileEntityNuclearReactorElectric)((ContainerNuclearReactor)GuiNuclearReactor.access$300((GuiNuclearReactor)GuiNuclearReactor.this)).base).EmitHeat);
                }
                return Localization.translate("ic2.NuclearReactor.gui.info.EUoutput", Math.round(((TileEntityNuclearReactorElectric)((ContainerNuclearReactor)GuiNuclearReactor.access$400((GuiNuclearReactor)GuiNuclearReactor.this)).base).output * 5.0f * ConfigUtil.getFloat(MainConfig.get(), "balance/energy/generator/nuclear")));
            }
        }), 5752026, false, 4, 0, false, true));
        this.addElement(new Area(this, 5, 160, 18, 18).withTooltip(new Supplier<String>(){

            public String get() {
                if (((TileEntityNuclearReactorElectric)((ContainerNuclearReactor)GuiNuclearReactor.access$500((GuiNuclearReactor)GuiNuclearReactor.this)).base).isFluidCooled()) {
                    return "ic2.NuclearReactor.gui.mode.fluid";
                }
                return "ic2.NuclearReactor.gui.mode.electric";
            }
        }));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        int size = ((TileEntityNuclearReactorElectric)((ContainerNuclearReactor)this.container).base).getReactorSize();
        int startX = 26;
        int startY = 25;
        this.bindTexture();
        for (int y = 0; y < 6; ++y) {
            for (int x = size; x < 9; ++x) {
                this.drawTexturedRect(26 + x * 18, 25 + y * 18, 16.0, 16.0, 213.0, 1.0);
            }
        }
        if (((TileEntityNuclearReactorElectric)((ContainerNuclearReactor)this.container).base).isFluidCooled()) {
            int i2 = ((TileEntityNuclearReactorElectric)((ContainerNuclearReactor)this.container).base).gaugeHeatScaled(160);
            this.drawTexturedRect(186 - i2, 23.0, 0.0, 243.0, i2, 2.0);
            this.drawTexturedRect(186 - i2, 41.0, 0.0, 243.0, i2, 2.0);
            this.drawTexturedRect(186 - i2, 59.0, 0.0, 243.0, i2, 2.0);
            this.drawTexturedRect(186 - i2, 77.0, 0.0, 243.0, i2, 2.0);
            this.drawTexturedRect(186 - i2, 95.0, 0.0, 243.0, i2, 2.0);
            this.drawTexturedRect(186 - i2, 113.0, 0.0, 243.0, i2, 2.0);
            this.drawTexturedRect(186 - i2, 131.0, 0.0, 243.0, i2, 2.0);
        }
    }

    @Override
    protected ResourceLocation getTexture() {
        if (((TileEntityNuclearReactorElectric)((ContainerNuclearReactor)this.container).base).isFluidCooled()) {
            return this.backgroundFluid;
        }
        return this.background;
    }

    static /* synthetic */ ContainerBase access$000(GuiNuclearReactor x0) {
        return x0.container;
    }

    static /* synthetic */ ContainerBase access$100(GuiNuclearReactor x0) {
        return x0.container;
    }

    static /* synthetic */ ContainerBase access$200(GuiNuclearReactor x0) {
        return x0.container;
    }

    static /* synthetic */ ContainerBase access$300(GuiNuclearReactor x0) {
        return x0.container;
    }

    static /* synthetic */ ContainerBase access$400(GuiNuclearReactor x0) {
        return x0.container;
    }

    static /* synthetic */ ContainerBase access$500(GuiNuclearReactor x0) {
        return x0.container;
    }

}

