/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.BlockRendererDispatcher
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.entity.Render
 *  net.minecraft.client.renderer.entity.RenderManager
 *  net.minecraft.client.renderer.texture.TextureMap
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block;

import ic2.core.block.EntityIC2Explosive;
import ic2.core.util.Util;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public class RenderExplosiveBlock
extends Render<EntityIC2Explosive> {
    public RenderExplosiveBlock(RenderManager manager) {
        super(manager);
        this.shadowSize = 0.5f;
    }

    public void doRender(EntityIC2Explosive entity, double x, double y, double z, float entityYaw, float partialTicks) {
        BlockRendererDispatcher blockRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)((float)x), (float)((float)y + 0.5f), (float)((float)z));
        if ((float)entity.fuse - partialTicks + 1.0f < 10.0f) {
            float scale = 1.0f - ((float)entity.fuse - partialTicks + 1.0f) / 10.0f;
            scale = Util.limit(scale, 0.0f, 1.0f);
            scale = Util.square(Util.square(scale));
            scale = 1.0f + scale * 0.3f;
            GlStateManager.scale((float)scale, (float)scale, (float)scale);
        }
        float alpha = (1.0f - ((float)entity.fuse - partialTicks + 1.0f) / 100.0f) * 0.8f;
        this.bindEntityTexture((Entity)entity);
        GlStateManager.rotate((float)-90.0f, (float)0.0f, (float)1.0f, (float)0.0f);
        GlStateManager.translate((float)-0.5f, (float)-0.5f, (float)0.5f);
        blockRenderer.renderBlockBrightness(entity.renderBlockState, entity.getBrightness(partialTicks));
        GlStateManager.translate((float)0.0f, (float)0.0f, (float)1.0f);
        if (entity.fuse / 5 % 2 == 0) {
            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc((int)770, (int)772);
            GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)alpha);
            GlStateManager.doPolygonOffset((float)-3.0f, (float)-3.0f);
            GlStateManager.enablePolygonOffset();
            blockRenderer.renderBlockBrightness(entity.renderBlockState, 1.0f);
            GlStateManager.doPolygonOffset((float)0.0f, (float)0.0f);
            GlStateManager.disablePolygonOffset();
            GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
            GlStateManager.enableTexture2D();
        }
        GlStateManager.popMatrix();
        super.doRender((Entity)entity, x, y, z, entityYaw, partialTicks);
    }

    protected ResourceLocation getEntityTexture(EntityIC2Explosive entity) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }
}

