/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Supplier
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.fluids.FluidTank
 *  net.minecraftforge.fluids.IFluidTank
 */
package ic2.core.block.kineticgenerator.gui;

import com.google.common.base.Supplier;
import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.block.kineticgenerator.container.ContainerSteamKineticGenerator;
import ic2.core.block.kineticgenerator.tileentity.TileEntitySteamKineticGenerator;
import ic2.core.gui.GuiElement;
import ic2.core.gui.IEnableHandler;
import ic2.core.gui.Image;
import ic2.core.gui.SlotGrid;
import ic2.core.gui.TankGauge;
import ic2.core.gui.Text;
import ic2.core.gui.dynamic.TextProvider;
import ic2.core.init.Localization;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;

public class GuiSteamKineticGenerator
extends GuiIC2<ContainerSteamKineticGenerator> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(IC2.textureDomain, "textures/gui/GUISteamKineticGenerator.png");

    public GuiSteamKineticGenerator(final ContainerSteamKineticGenerator container) {
        super(container);
        this.addElement(TankGauge.createPlain(this, 75, 21, 26, 26, (IFluidTank)((TileEntitySteamKineticGenerator)container.base).getDistilledWaterTank()));
        this.addElement(new SlotGrid(this, 80, 26, SlotGrid.SlotStyle.Plain).withTooltip(new Supplier<String>(){

            public String get() {
                if (!((TileEntitySteamKineticGenerator)container.base).hasTurbine()) {
                    return "ic2.SteamKineticGenerator.gui.turbineslot";
                }
                return null;
            }
        }));
        this.addElement(((Image)Image.create(this, 110, 20, 30, 26, TEXTURE, 176, 0).withEnableHandler(new IEnableHandler(){

            @Override
            public boolean isEnabled() {
                return !((TileEntitySteamKineticGenerator)container.base).isHotSteam() && ((TileEntitySteamKineticGenerator)container.base).hasTurbine();
            }
        })).withTooltip("ic2.SteamKineticGenerator.gui.condensationwarrning"));
        this.addElement(Text.create(this, 8, 51, 160, 13, TextProvider.of(new Supplier<String>(){

            public String get() {
                if (!((TileEntitySteamKineticGenerator)container.base).hasTurbine()) {
                    return "ic2.SteamKineticGenerator.gui.error.noturbine";
                }
                if (((TileEntitySteamKineticGenerator)container.base).isTurbineBlockedByWater()) {
                    return "ic2.SteamKineticGenerator.gui.error.filledupwithwater";
                }
                if (((TileEntitySteamKineticGenerator)container.base).getActive()) {
                    return "ic2.SteamKineticGenerator.gui.aktive";
                }
                return "ic2.SteamKineticGenerator.gui.waiting";
            }
        }), new Supplier<Integer>(){

            public Integer get() {
                if (!((TileEntitySteamKineticGenerator)container.base).hasTurbine() || ((TileEntitySteamKineticGenerator)container.base).isTurbineBlockedByWater()) {
                    return 14946604;
                }
                return 2157374;
            }
        }, false, 4, 0, false, true));
        this.addElement(Text.create(this, 8, 68, 160, 13, TextProvider.of(new Supplier<String>(){

            public String get() {
                return Localization.translate("ic2.SteamKineticGenerator.gui.turbine.ouput", ((TileEntitySteamKineticGenerator)container.base).getKUoutput());
            }
        }), 2157374, false, 4, 0, false, true).withEnableHandler(new IEnableHandler(){

            @Override
            public boolean isEnabled() {
                return ((TileEntitySteamKineticGenerator)container.base).hasTurbine() && !((TileEntitySteamKineticGenerator)container.base).isTurbineBlockedByWater();
            }
        }));
    }

    @Override
    protected ResourceLocation getTexture() {
        return TEXTURE;
    }

}

