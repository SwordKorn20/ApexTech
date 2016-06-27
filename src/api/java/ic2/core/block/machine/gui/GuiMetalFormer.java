/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Supplier
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block.machine.gui;

import com.google.common.base.Supplier;
import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.machine.container.ContainerMetalFormer;
import ic2.core.block.machine.tileentity.TileEntityMetalFormer;
import ic2.core.block.state.IIdProvider;
import ic2.core.block.wiring.CableType;
import ic2.core.gui.CustomGauge;
import ic2.core.gui.EnergyGauge;
import ic2.core.gui.Gauge;
import ic2.core.gui.GuiElement;
import ic2.core.gui.IClickHandler;
import ic2.core.gui.VanillaButton;
import ic2.core.init.Localization;
import ic2.core.ref.ItemName;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public class GuiMetalFormer
extends GuiIC2<ContainerMetalFormer> {
    public GuiMetalFormer(final ContainerMetalFormer container) {
        super(container);
        this.addElement(EnergyGauge.asBolt(this, 20, 37, (TileEntityBlock)container.base));
        this.addElement(CustomGauge.create(this, 52, 39, new CustomGauge.IGaugeRatioProvider(){

            @Override
            public double getRatio() {
                return ((TileEntityMetalFormer)container.base).getProgress();
            }
        }, Gauge.GaugeStyle.ProgressMetalFormer));
        this.addElement(((VanillaButton)new VanillaButton(this, 65, 53, 20, 20, this.createEventSender(0)).withIcon(new Supplier<ItemStack>(){

            public ItemStack get() {
                switch (((TileEntityMetalFormer)container.base).getMode()) {
                    case 0: {
                        return ItemName.cable.getItemStack(CableType.copper);
                    }
                    case 1: {
                        return ItemName.forge_hammer.getItemStack();
                    }
                    case 2: {
                        return ItemName.cutter.getItemStack();
                    }
                }
                return null;
            }
        })).withTooltip(new Supplier<String>(){

            public String get() {
                switch (((TileEntityMetalFormer)container.base).getMode()) {
                    case 0: {
                        return Localization.translate("ic2.MetalFormer.gui.switch.Extruding");
                    }
                    case 1: {
                        return Localization.translate("ic2.MetalFormer.gui.switch.Rolling");
                    }
                    case 2: {
                        return Localization.translate("ic2.MetalFormer.gui.switch.Cutting");
                    }
                }
                return null;
            }
        }));
    }

    @Override
    protected ResourceLocation getTexture() {
        return new ResourceLocation(IC2.textureDomain, "textures/gui/GUIMetalFormer.png");
    }

}

