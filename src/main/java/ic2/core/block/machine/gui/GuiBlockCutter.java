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

import ic2.core.ContainerBase;
import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.machine.container.ContainerBlockCutter;
import ic2.core.block.machine.tileentity.TileEntityBlockCutter;
import ic2.core.gui.EnergyGauge;
import ic2.core.gui.GuiElement;
import ic2.core.gui.IEnableHandler;
import ic2.core.gui.Image;
import ic2.core.gui.SlotGrid;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public class GuiBlockCutter
extends GuiIC2<ContainerBlockCutter> {
    private static final ResourceLocation texture = new ResourceLocation(IC2.textureDomain, "textures/gui/GUIBlockCutter.png");

    public GuiBlockCutter(final ContainerBlockCutter container) {
        super(container);
        this.addElement(new SlotGrid(this, 70, 35, SlotGrid.SlotStyle.Plain).withTooltip("ic2.BlockCutter.gui.bladeslot"));
        this.addElement(EnergyGauge.asBolt(this, 29, 37, (TileEntityBlock)container.base));
        this.addElement(((Image)Image.create(this, 63, 54, 30, 26, texture, 176, 34).withEnableHandler(new IEnableHandler(){

            @Override
            public boolean isEnabled() {
                return ((TileEntityBlockCutter)container.base).isBladeTooWeak();
            }
        })).withTooltip("ic2.BlockCutter.gui.bladeTooWeak"));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        super.drawGuiContainerBackgroundLayer(f, x, y);
        this.bindTexture();
        int progress = (int)(46.0f * ((TileEntityBlockCutter)((ContainerBlockCutter)this.container).base).getProgress());
        if (progress > 0) {
            this.drawTexturedModalRect(this.guiLeft + 55, this.guiTop + 33, 176, 14, progress + 1, 19);
        }
    }

    @Override
    public ResourceLocation getTexture() {
        return texture;
    }

}

