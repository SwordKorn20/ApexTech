/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Supplier
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.fluids.FluidTank
 *  net.minecraftforge.fluids.IFluidTank
 */
package ic2.core.block.machine.gui;

import com.google.common.base.Supplier;
import ic2.core.ContainerBase;
import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.block.machine.container.ContainerSteamGenerator;
import ic2.core.block.machine.tileentity.TileEntitySteamGenerator;
import ic2.core.gui.Gauge;
import ic2.core.gui.GuiElement;
import ic2.core.gui.LinkedGauge;
import ic2.core.gui.TankGauge;
import ic2.core.gui.Text;
import ic2.core.gui.dynamic.IGuiValueProvider;
import ic2.core.gui.dynamic.TextProvider;
import ic2.core.init.Localization;
import ic2.core.network.NetworkManager;
import ic2.core.util.SideGateway;
import java.io.IOException;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;

public class GuiSteamGenerator
extends GuiIC2<ContainerSteamGenerator> {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(IC2.textureDomain, "textures/gui/GUISteamGenerator.png");

    public GuiSteamGenerator(ContainerSteamGenerator container) {
        super(container, 220);
        this.addElement(TankGauge.createPlain(this, 10, 155, 75, 47, (IFluidTank)((TileEntitySteamGenerator)container.base).waterTank));
        this.addElement(new LinkedGauge(this, 13, 70, (IGuiValueProvider)container.base, "heat", Gauge.GaugeStyle.HeatSteamGenerator).withTooltip(new Supplier<String>(){

            public String get() {
                return Localization.translate("ic2.SteamGenerator.gui.systemheat", Float.valueOf(((TileEntitySteamGenerator)((ContainerSteamGenerator)GuiSteamGenerator.access$000((GuiSteamGenerator)GuiSteamGenerator.this)).base).getSystemHeat()));
            }
        }));
        this.addElement(new LinkedGauge(this, 155, 61, (IGuiValueProvider)container.base, "calcification", Gauge.GaugeStyle.CalcificationSteamGenerator).withTooltip(new Supplier<String>(){

            public String get() {
                return Localization.translate("ic2.SteamGenerator.gui.calcification", Float.valueOf(((TileEntitySteamGenerator)((ContainerSteamGenerator)GuiSteamGenerator.access$100((GuiSteamGenerator)GuiSteamGenerator.this)).base).getCalcification())) + "%";
            }
        }));
        this.addElement(Text.create(this, 91, 172, 59, 13, TextProvider.of(new Supplier<String>(){

            public String get() {
                return "" + ((TileEntitySteamGenerator)((ContainerSteamGenerator)GuiSteamGenerator.access$200((GuiSteamGenerator)GuiSteamGenerator.this)).base).getInputMB() + Localization.translate("ic2.generic.text.mb") + Localization.translate("ic2.generic.text.tick");
            }
        }), 2157374, false, true, true).withTooltip(new Supplier<String>(){

            public String get() {
                return Localization.translate("ic2.SteamGenerator.gui.info.waterinput");
            }
        }));
        this.addElement(Text.create(this, 31, 133, 111, 13, TextProvider.of(new Supplier<String>(){

            public String get() {
                return Localization.translate("ic2.SteamGenerator.gui.heatInput", ((TileEntitySteamGenerator)((ContainerSteamGenerator)GuiSteamGenerator.access$300((GuiSteamGenerator)GuiSteamGenerator.this)).base).getHeatInput());
            }
        }), 2157374, false, 4, 0, false, true).withTooltip(new Supplier<String>(){

            public String get() {
                return Localization.translate("ic2.SteamGenerator.gui.info.heatinput");
            }
        }));
        this.addElement(Text.create(this, 22, 35, 42, 13, TextProvider.of(new Supplier<String>(){

            public String get() {
                return Localization.translate("ic2.SteamGenerator.gui.pressurevalve", ((TileEntitySteamGenerator)((ContainerSteamGenerator)GuiSteamGenerator.access$400((GuiSteamGenerator)GuiSteamGenerator.this)).base).getPressure());
            }
        }), 2157374, false, 4, 0, false, true).withTooltip(new Supplier<String>(){

            public String get() {
                return Localization.translate("ic2.SteamGenerator.gui.info.pressvalve");
            }
        }));
        this.addElement(Text.create(this, 66, 25, 81, 13, TextProvider.of(new Supplier<String>(){

            public String get() {
                return "" + ((TileEntitySteamGenerator)((ContainerSteamGenerator)GuiSteamGenerator.access$500((GuiSteamGenerator)GuiSteamGenerator.this)).base).getOutputMB() + Localization.translate("ic2.generic.text.mb") + Localization.translate("ic2.generic.text.tick");
            }
        }), 2157374, false, 4, 0, false, true).withTooltip(new Supplier<String>(){

            public String get() {
                return Localization.translate("ic2.SteamGenerator.gui.info.fluidoutput");
            }
        }));
        this.addElement(Text.create(this, 66, 45, 100, 13, TextProvider.of(new Supplier<String>(){

            public String get() {
                return ((TileEntitySteamGenerator)((ContainerSteamGenerator)GuiSteamGenerator.access$600((GuiSteamGenerator)GuiSteamGenerator.this)).base).getOutputFluidName();
            }
        }), 2157374, false, 4, 0, false, true));
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        super.mouseClicked(mouseX, mouseY, button);
        int x = mouseX - this.guiLeft;
        int y = mouseY - this.guiTop;
        TileEntitySteamGenerator te = (TileEntitySteamGenerator)((ContainerSteamGenerator)this.container).base;
        if (x >= 92 && y >= 186 && x <= 100 && y <= 194) {
            IC2.network.get(false).initiateClientTileEntityEvent(te, -1000);
        }
        if (x >= 102 && y >= 186 && x <= 110 && y <= 194) {
            IC2.network.get(false).initiateClientTileEntityEvent(te, -100);
        }
        if (x >= 112 && y >= 186 && x <= 120 && y <= 194) {
            IC2.network.get(false).initiateClientTileEntityEvent(te, -10);
        }
        if (x >= 122 && y >= 186 && x <= 130 && y <= 194) {
            IC2.network.get(false).initiateClientTileEntityEvent(te, -1);
        }
        if (x >= 122 && y >= 162 && x <= 130 && y <= 170) {
            IC2.network.get(false).initiateClientTileEntityEvent(te, 1);
        }
        if (x >= 112 && y >= 162 && x <= 120 && y <= 170) {
            IC2.network.get(false).initiateClientTileEntityEvent(te, 10);
        }
        if (x >= 102 && y >= 162 && x <= 110 && y <= 170) {
            IC2.network.get(false).initiateClientTileEntityEvent(te, 100);
        }
        if (x >= 92 && y >= 162 && x <= 100 && y <= 170) {
            IC2.network.get(false).initiateClientTileEntityEvent(te, 1000);
        }
        if (x >= 23 && y >= 49 && x <= 31 && y <= 57) {
            IC2.network.get(false).initiateClientTileEntityEvent(te, -2100);
        }
        if (x >= 33 && y >= 49 && x <= 41 && y <= 57) {
            IC2.network.get(false).initiateClientTileEntityEvent(te, -2010);
        }
        if (x >= 43 && y >= 49 && x <= 51 && y <= 57) {
            IC2.network.get(false).initiateClientTileEntityEvent(te, -2001);
        }
        if (x >= 43 && y >= 25 && x <= 51 && y <= 33) {
            IC2.network.get(false).initiateClientTileEntityEvent(te, 2001);
        }
        if (x >= 33 && y >= 25 && x <= 41 && y <= 33) {
            IC2.network.get(false).initiateClientTileEntityEvent(te, 2010);
        }
        if (x >= 23 && y >= 25 && x <= 31 && y <= 33) {
            IC2.network.get(false).initiateClientTileEntityEvent(te, 2100);
        }
    }

    @Override
    public ResourceLocation getTexture() {
        return BACKGROUND;
    }

    static /* synthetic */ ContainerBase access$000(GuiSteamGenerator x0) {
        return x0.container;
    }

    static /* synthetic */ ContainerBase access$100(GuiSteamGenerator x0) {
        return x0.container;
    }

    static /* synthetic */ ContainerBase access$200(GuiSteamGenerator x0) {
        return x0.container;
    }

    static /* synthetic */ ContainerBase access$300(GuiSteamGenerator x0) {
        return x0.container;
    }

    static /* synthetic */ ContainerBase access$400(GuiSteamGenerator x0) {
        return x0.container;
    }

    static /* synthetic */ ContainerBase access$500(GuiSteamGenerator x0) {
        return x0.container;
    }

    static /* synthetic */ ContainerBase access$600(GuiSteamGenerator x0) {
        return x0.container;
    }

}

