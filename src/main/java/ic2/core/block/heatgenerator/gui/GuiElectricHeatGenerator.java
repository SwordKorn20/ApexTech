/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Supplier
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block.heatgenerator.gui;

import com.google.common.base.Supplier;
import ic2.core.ContainerBase;
import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.heatgenerator.container.ContainerElectricHeatGenerator;
import ic2.core.block.heatgenerator.tileentity.TileEntityElectricHeatGenerator;
import ic2.core.gui.EnergyGauge;
import ic2.core.gui.GuiElement;
import ic2.core.gui.SlotGrid;
import ic2.core.gui.Text;
import ic2.core.gui.dynamic.TextProvider;
import ic2.core.init.Localization;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public class GuiElectricHeatGenerator
extends GuiIC2<ContainerElectricHeatGenerator> {
    private static final ResourceLocation background = new ResourceLocation(IC2.textureDomain, "textures/gui/GUIElectricHeatGenerator.png");

    public GuiElectricHeatGenerator(ContainerElectricHeatGenerator container) {
        super(container);
        this.addElement(new SlotGrid(this, 43, 26, 5, 2, SlotGrid.SlotStyle.Normal).withTooltip("ic2.ElectricHeatGenerator.gui.coils"));
        this.addElement(EnergyGauge.asBolt(this, 12, 44, (TileEntityBlock)container.base));
        this.addElement(Text.create(this, 34, 66, 109, 13, TextProvider.of(new Supplier<String>(){

            public String get() {
                return Localization.translate("ic2.ElectricHeatGenerator.gui.hUmax", ((TileEntityElectricHeatGenerator)((ContainerElectricHeatGenerator)GuiElectricHeatGenerator.access$000((GuiElectricHeatGenerator)GuiElectricHeatGenerator.this)).base).gettransmitHeat(), ((TileEntityElectricHeatGenerator)((ContainerElectricHeatGenerator)GuiElectricHeatGenerator.access$100((GuiElectricHeatGenerator)GuiElectricHeatGenerator.this)).base).getMaxHeatEmittedPerTick());
            }
        }), 5752026, false, true, true).withTooltip(new Supplier<String>(){

            public String get() {
                return Localization.translate("ic2.ElectricHeatGenerator.gui.tooltipheat");
            }
        }));
    }

    @Override
    protected ResourceLocation getTexture() {
        return background;
    }

    static /* synthetic */ ContainerBase access$000(GuiElectricHeatGenerator x0) {
        return x0.container;
    }

    static /* synthetic */ ContainerBase access$100(GuiElectricHeatGenerator x0) {
        return x0.container;
    }

}

