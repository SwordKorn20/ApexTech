/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.entity.Render
 *  net.minecraft.client.renderer.entity.RenderManager
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.ResourceLocation
 */
package ic2.core.item.tool;

import ic2.core.IC2;
import ic2.core.item.tool.EntityParticle;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderBillboardEntity
extends Render<EntityParticle> {
    private final ResourceLocation texture = new ResourceLocation(IC2.textureDomain, "textures/models/beam.png");

    public RenderBillboardEntity(RenderManager manager) {
        super(manager);
    }

    public void doRender(EntityParticle entity, double x, double y, double z, float yaw, float partialTickTime) {
    }

    protected ResourceLocation getEntityTexture(EntityParticle entity) {
        return this.texture;
    }
}

