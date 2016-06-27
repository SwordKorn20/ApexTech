/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.renderer.block.model.BakedQuad
 *  net.minecraft.client.renderer.block.model.ItemCameraTransforms
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.client.renderer.vertex.DefaultVertexFormats
 *  net.minecraft.client.renderer.vertex.VertexFormat
 *  net.minecraft.util.EnumFacing
 */
package ic2.core.model;

import ic2.core.model.AbstractBakedModel;
import java.util.Collections;
import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;

public class BasicBakedItemModel
extends AbstractBakedModel {
    private final List<BakedQuad> quads;
    private final TextureAtlasSprite particleTexture;

    public BasicBakedItemModel(List<BakedQuad> quads, TextureAtlasSprite particleTexture) {
        this.quads = quads;
        this.particleTexture = particleTexture;
    }

    public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
        if (side != null) {
            return Collections.emptyList();
        }
        return this.quads;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return this.particleTexture;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }

    public static BakedQuad createQuad(int[] vertexData, EnumFacing side) {
        return new BakedQuad(vertexData, -1, side, null, true, DefaultVertexFormats.ITEM);
    }
}

