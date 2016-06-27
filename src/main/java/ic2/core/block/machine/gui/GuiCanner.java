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
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block.machine.gui;

import com.google.common.base.Supplier;
import ic2.core.ContainerBase;
import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.machine.container.ContainerCanner;
import ic2.core.block.machine.tileentity.TileEntityCanner;
import ic2.core.gui.CustomButton;
import ic2.core.gui.CycleHandler;
import ic2.core.gui.EnergyGauge;
import ic2.core.gui.GuiElement;
import ic2.core.gui.IClickHandler;
import ic2.core.gui.INumericValueHandler;
import ic2.core.gui.TankGauge;
import ic2.core.network.NetworkManager;
import ic2.core.util.SideGateway;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public class GuiCanner
extends GuiIC2<ContainerCanner> {
    private static final ResourceLocation texture = new ResourceLocation(IC2.textureDomain, "textures/gui/GUICanner.png");

    public GuiCanner(ContainerCanner container) {
        super(container, 184);
        this.addElement(EnergyGauge.asBolt(this, 12, 62, (TileEntityBlock)container.base));
        CycleHandler cycleHandler = new CycleHandler(176, 18, 14, true, 4, new INumericValueHandler(){

            @Override
            public int getValue() {
                return ((TileEntityCanner)((ContainerCanner)GuiCanner.access$000((GuiCanner)GuiCanner.this)).base).getMode().ordinal();
            }

            @Override
            public void onChange(int value) {
                IC2.network.get(false).initiateClientTileEntityEvent((TileEntity)((ContainerCanner)GuiCanner.access$100((GuiCanner)GuiCanner.this)).base, 0 + value);
            }
        });
        this.addElement(new CustomButton(this, 63, 81, 50, 14, cycleHandler, texture, cycleHandler).withTooltip(new Supplier<String>(){

            public String get() {
                switch (((TileEntityCanner)((ContainerCanner)GuiCanner.access$200((GuiCanner)GuiCanner.this)).base).getMode()) {
                    case BottleSolid: {
                        return "ic2.Canner.gui.switch.BottleSolid";
                    }
                    case EmptyLiquid: {
                        return "ic2.Canner.gui.switch.EmptyLiquid";
                    }
                    case BottleLiquid: {
                        return "ic2.Canner.gui.switch.BottleLiquid";
                    }
                    case EnrichLiquid: {
                        return "ic2.Canner.gui.switch.EnrichLiquid";
                    }
                }
                return null;
            }
        }));
        this.addElement(new CustomButton(this, 77, 64, 22, 13, this.createEventSender(TileEntityCanner.eventSwapTanks)).withTooltip("ic2.Canner.gui.switchTanks"));
        this.addElement(TankGauge.createNormal(this, 39, 42, (IFluidTank)((TileEntityCanner)container.base).getInputTank()));
        this.addElement(TankGauge.createNormal(this, 117, 42, (IFluidTank)((TileEntityCanner)container.base).getOutputTank()));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        super.drawGuiContainerBackgroundLayer(f, x, y);
        this.bindTexture();
        switch (((TileEntityCanner)((ContainerCanner)this.container).base).getMode()) {
            case BottleSolid: {
                this.drawTexturedRect(59.0, 53.0, 9.0, 18.0, 3.0, 4.0);
                this.drawTexturedRect(99.0, 53.0, 18.0, 23.0, 3.0, 4.0);
                break;
            }
            case EmptyLiquid: {
                this.drawTexturedRect(71.0, 43.0, 26.0, 18.0, 196.0, 0.0);
                this.drawTexturedRect(59.0, 53.0, 9.0, 18.0, 3.0, 4.0);
                break;
            }
            case BottleLiquid: {
                this.drawTexturedRect(99.0, 53.0, 18.0, 23.0, 3.0, 4.0);
                this.drawTexturedRect(71.0, 43.0, 26.0, 18.0, 196.0, 0.0);
                break;
            }
        }
        int progressSize = Math.round(((TileEntityCanner)((ContainerCanner)this.container).base).getProgress() * 23.0f);
        if (progressSize > 0) {
            this.drawTexturedRect(74.0, 22.0, progressSize, 14.0, 233.0, 0.0);
        }
    }

    @Override
    protected ResourceLocation getTexture() {
        return texture;
    }

    static /* synthetic */ ContainerBase access$000(GuiCanner x0) {
        return x0.container;
    }

    static /* synthetic */ ContainerBase access$100(GuiCanner x0) {
        return x0.container;
    }

    static /* synthetic */ ContainerBase access$200(GuiCanner x0) {
        return x0.container;
    }

}

