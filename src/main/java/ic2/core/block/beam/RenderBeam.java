/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.client.renderer.ActiveRenderInfo
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.VertexBuffer
 *  net.minecraft.client.renderer.entity.Render
 *  net.minecraft.client.renderer.entity.RenderManager
 *  net.minecraft.client.renderer.vertex.DefaultVertexFormats
 *  net.minecraft.client.renderer.vertex.VertexFormat
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.ResourceLocation
 */
package ic2.core.block.beam;

import ic2.core.IC2;
import ic2.core.block.beam.EntityParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class RenderBeam
extends Render<EntityParticle> {
    private final ResourceLocation texture = new ResourceLocation(IC2.textureDomain, "textures/models/beam.png");

    public RenderBeam(RenderManager manager) {
        super(manager);
    }

    public void doRender(EntityParticle entity, double x, double y, double z, float yaw, float partialTickTime) {
        EntityParticle particle = entity;
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        double playerX = player.prevPosX + (player.posX - player.prevPosX) * (double)partialTickTime;
        double playerY = player.prevPosY + (player.posY - player.prevPosY) * (double)partialTickTime;
        double playerZ = player.prevPosZ + (player.posZ - player.prevPosZ) * (double)partialTickTime;
        double particleX = particle.prevPosX + (particle.posX - particle.prevPosX) * (double)partialTickTime - playerX;
        double particleY = particle.prevPosY + (particle.posY - particle.prevPosY) * (double)partialTickTime - playerY;
        double particleZ = particle.prevPosZ + (particle.posZ - particle.prevPosZ) * (double)partialTickTime - playerZ;
        double u1 = 0.0;
        double u2 = 1.0;
        double v1 = 0.0;
        double v2 = 1.0;
        double scale = 0.1;
        this.bindTexture(this.getEntityTexture(entity));
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer worldrenderer = tessellator.getBuffer();
        GlStateManager.depthMask((boolean)false);
        GlStateManager.enableBlend();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(particleX - (double)(ActiveRenderInfo.getRotationX() + ActiveRenderInfo.getRotationYZ()) * scale, particleY - (double)ActiveRenderInfo.getRotationXZ() * scale, particleZ - (double)(ActiveRenderInfo.getRotationZ() + ActiveRenderInfo.getRotationXY()) * scale).tex(u2, v2).endVertex();
        worldrenderer.pos(particleX - (double)(ActiveRenderInfo.getRotationX() - ActiveRenderInfo.getRotationYZ()) * scale, particleY + (double)ActiveRenderInfo.getRotationXZ() * scale, particleZ - (double)(ActiveRenderInfo.getRotationZ() - ActiveRenderInfo.getRotationXY()) * scale).tex(u2, v1).endVertex();
        worldrenderer.pos(particleX + (double)(ActiveRenderInfo.getRotationX() + ActiveRenderInfo.getRotationYZ()) * scale, particleY + (double)ActiveRenderInfo.getRotationXZ() * scale, particleZ + (double)(ActiveRenderInfo.getRotationZ() + ActiveRenderInfo.getRotationXY()) * scale).tex(u1, v1).endVertex();
        worldrenderer.pos(particleX + (double)(ActiveRenderInfo.getRotationX() - ActiveRenderInfo.getRotationYZ()) * scale, particleY - (double)ActiveRenderInfo.getRotationXZ() * scale, particleZ + (double)(ActiveRenderInfo.getRotationZ() - ActiveRenderInfo.getRotationXY()) * scale).tex(u1, v2).endVertex();
        tessellator.draw();
        GlStateManager.disableBlend();
        GlStateManager.depthMask((boolean)true);
    }

    protected ResourceLocation getEntityTexture(EntityParticle entity) {
        return this.texture;
    }
}

