/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.entity.RenderBoat
 *  net.minecraft.client.renderer.entity.RenderManager
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityBoat
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.item;

import ic2.core.IC2;
import ic2.core.item.EntityIC2Boat;
import net.minecraft.client.renderer.entity.RenderBoat;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public class RenderIC2Boat
extends RenderBoat {
    public RenderIC2Boat(RenderManager manager) {
        super(manager);
    }

    protected ResourceLocation getEntityTexture(EntityBoat entity) {
        return new ResourceLocation(IC2.textureDomain, ((EntityIC2Boat)entity).getTexture());
    }
}

