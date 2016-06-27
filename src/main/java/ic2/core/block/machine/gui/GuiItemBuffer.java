/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.ResourceLocation
 */
package ic2.core.block.machine.gui;

import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.block.machine.container.ContainerItemBuffer;
import net.minecraft.util.ResourceLocation;

public class GuiItemBuffer
extends GuiIC2<ContainerItemBuffer> {
    public GuiItemBuffer(ContainerItemBuffer container) {
        super(container);
        this.ySize = 232;
    }

    @Override
    public ResourceLocation getTexture() {
        return new ResourceLocation(IC2.textureDomain, "textures/gui/GUIItemBuffer.png");
    }
}

