/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.renderer.block.model.BakedQuad
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
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;

public class BasicBakedBlockModel
extends AbstractBakedModel {
    private final List<BakedQuad>[] faceQuads;
    private final List<BakedQuad> generalQuads;
    private final TextureAtlasSprite particleTexture;

    public BasicBakedBlockModel(List<BakedQuad>[] faceQuads, List<BakedQuad> generalQuads, TextureAtlasSprite particleTexture) {
        this.faceQuads = faceQuads;
        this.generalQuads = generalQuads;
        this.particleTexture = particleTexture;
    }

    public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
        if (side == null) {
            return this.generalQuads;
        }
        if (this.faceQuads == null) {
            return Collections.emptyList();
        }
        return this.faceQuads[side.ordinal()];
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return this.particleTexture;
    }

    public static BakedQuad createQuad(int[] vertexData, EnumFacing side) {
        return new BakedQuad(vertexData, -1, side, null, true, DefaultVertexFormats.BLOCK);
    }
}

