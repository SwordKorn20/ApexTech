/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block.machine.gui;

import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.machine.container.ContainerCropHavester;
import ic2.core.gui.EnergyGauge;
import ic2.core.gui.GuiElement;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public class GuiCropHavester
extends GuiIC2<ContainerCropHavester> {
    public GuiCropHavester(ContainerCropHavester container) {
        super(container, 191);
        this.addElement(EnergyGauge.asBolt(this, 156, 43, (TileEntityBlock)container.base));
    }

    @Override
    public ResourceLocation getTexture() {
        return new ResourceLocation(IC2.textureDomain, "textures/gui/GUICropHavester.png");
    }
}
