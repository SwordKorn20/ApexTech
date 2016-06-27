/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.BlockModelRenderer
 *  net.minecraft.client.renderer.BlockRendererDispatcher
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.RenderHelper
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.VertexBuffer
 *  net.minecraft.client.renderer.block.model.IBakedModel
 *  net.minecraft.client.renderer.texture.TextureMap
 *  net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
 *  net.minecraft.client.renderer.vertex.DefaultVertexFormats
 *  net.minecraft.client.renderer.vertex.VertexFormat
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  org.lwjgl.opengl.GL11
 */
package ic2.core.block;

import ic2.core.block.BlockTileEntity;
import ic2.core.block.TileEntityBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class OverlayTesr
extends TileEntitySpecialRenderer<TileEntityBlock> {
    public void renderTileEntityAt(TileEntityBlock te, double x, double y, double z, float partialTicks, int destroyStage) {
        IBlockState state = te.getBlockType().getDefaultState();
        GL11.glPushAttrib((int)64);
        GL11.glPushMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.blendFunc((int)770, (int)771);
        GlStateManager.enableBlend();
        GlStateManager.disableCull();
        if (Minecraft.isAmbientOcclusionEnabled()) {
            GlStateManager.shadeModel((int)7425);
        } else {
            GlStateManager.shadeModel((int)7424);
        }
        this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        float zScale = 1.001f;
        GlStateManager.translate((float)((float)(x + 0.5)), (float)((float)(y + 0.5)), (float)((float)(z + 0.5)));
        GlStateManager.scale((float)zScale, (float)zScale, (float)zScale);
        GlStateManager.translate((float)((float)(- x + 0.5)), (float)((float)(- y + 0.5)), (float)((float)(- z + 0.5)));
        BlockRendererDispatcher renderer = Minecraft.getMinecraft().getBlockRendererDispatcher();
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer wr = tessellator.getBuffer();
        wr.begin(7, DefaultVertexFormats.BLOCK);
        wr.setTranslation(x - (double)te.getPos().getX(), y - (double)te.getPos().getY(), z - (double)te.getPos().getZ());
        renderer.getBlockModelRenderer().renderModel((IBlockAccess)te.getWorld(), renderer.getModelForState(state), state, te.getPos(), wr, true);
        wr.setTranslation(0.0, 0.0, 0.0);
        tessellator.draw();
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }
}

