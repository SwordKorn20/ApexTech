/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Supplier
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.ResourceLocation
 */
package ic2.core.block.machine.gui;

import com.google.common.base.Supplier;
import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.machine.container.ContainerSortingMachine;
import ic2.core.block.machine.tileentity.TileEntitySortingMachine;
import ic2.core.gui.CustomButton;
import ic2.core.gui.EnergyGauge;
import ic2.core.gui.GuiElement;
import ic2.core.gui.IClickHandler;
import ic2.core.gui.Image;
import ic2.core.util.StackUtil;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class GuiSortingMachine
extends GuiIC2<ContainerSortingMachine> {
    private static final ResourceLocation texture = new ResourceLocation(IC2.textureDomain, "textures/gui/GUISortingMachine.png");

    public GuiSortingMachine(final ContainerSortingMachine container) {
        super(container, 212, 243);
        this.addElement(EnergyGauge.asBolt(this, 174, 220, (TileEntityBlock)container.base));
        EnumFacing[] arrenumFacing = EnumFacing.VALUES;
        int n = arrenumFacing.length;
        for (int i = 0; i < n; ++i) {
            EnumFacing dir;
            final EnumFacing cDir = dir = arrenumFacing[i];
            this.addElement(Image.create(this, 60, 18 + dir.ordinal() * 20, 18, 18, texture, new CustomButton.IOverlaySupplier(){

                @Override
                public int getOverlayX() {
                    return 212;
                }

                @Override
                public int getOverlayY() {
                    if (StackUtil.getAdjacentInventory((TileEntity)container.base, cDir) != null) {
                        return 15;
                    }
                    return 33;
                }
            }));
            this.addElement(new CustomButton(this, 42, 18 + dir.ordinal() * 20, 18, 18, new CustomButton.IOverlaySupplier(){

                @Override
                public int getOverlayX() {
                    return 230;
                }

                @Override
                public int getOverlayY() {
                    if (((TileEntitySortingMachine)container.base).defaultRoute != cDir) {
                        return 15;
                    }
                    return 33;
                }
            }, texture, this.createEventSender(dir.ordinal())).withTooltip(new Supplier<String>(){

                public String get() {
                    if (((TileEntitySortingMachine)container.base).defaultRoute != cDir) {
                        return "ic2.SortingMachine.whitelist";
                    }
                    return "ic2.SortingMachine.default";
                }
            }));
        }
    }

    @Override
    protected ResourceLocation getTexture() {
        return texture;
    }

}

