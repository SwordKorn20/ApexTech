/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.item.tool;

import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.item.tool.ContainerContainmentbox;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public class GuiContainmentbox
extends GuiIC2<ContainerContainmentbox> {
    public GuiContainmentbox(ContainerContainmentbox container) {
        super(container);
    }

    @Override
    public ResourceLocation getTexture() {
        return new ResourceLocation(IC2.textureDomain, "textures/gui/GUIContainmentbox.png");
    }
}

