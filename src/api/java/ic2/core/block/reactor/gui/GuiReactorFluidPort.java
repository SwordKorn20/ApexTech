/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.ResourceLocation
 */
package ic2.core.block.reactor.gui;

import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.block.reactor.container.ContainerReactorFluidPort;
import ic2.core.gui.GuiElement;
import ic2.core.gui.SlotGrid;
import net.minecraft.util.ResourceLocation;

public class GuiReactorFluidPort
extends GuiIC2<ContainerReactorFluidPort> {
    private static final ResourceLocation background = new ResourceLocation(IC2.textureDomain, "textures/gui/GUIReactorFluidPort.png");

    public GuiReactorFluidPort(ContainerReactorFluidPort container) {
        super(container);
        this.addElement(new SlotGrid(this, 79, 42, SlotGrid.SlotStyle.Normal).withTooltip("ic2.ReactorFluidPort.gui.info"));
    }

    @Override
    protected ResourceLocation getTexture() {
        return background;
    }
}

