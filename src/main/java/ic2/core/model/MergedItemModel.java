/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  javax.vecmath.Matrix4f
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.renderer.block.model.BakedQuad
 *  net.minecraft.client.renderer.block.model.IBakedModel
 *  net.minecraft.client.renderer.block.model.ItemCameraTransforms
 *  net.minecraft.client.renderer.block.model.ItemCameraTransforms$TransformType
 *  net.minecraft.client.renderer.block.model.ItemOverrideList
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.client.renderer.vertex.VertexFormat
 *  net.minecraft.util.EnumFacing
 *  net.minecraftforge.client.model.IPerspectiveAwareModel
 *  org.apache.commons.lang3.tuple.Pair
 */
package ic2.core.model;

import ic2.core.model.ItemGeo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.vecmath.Matrix4f;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import org.apache.commons.lang3.tuple.Pair;

public class MergedItemModel
implements IPerspectiveAwareModel {
    private static final int xDataIndex = 0;
    private static final int yDataIndex = 1;
    private static final int colorDataIndex = 3;
    private static final int uDataIndex = 4;
    private static final int vDataIndex = 5;
    private final IPerspectiveAwareModel parent;
    private final List<BakedQuad> mergedQuads;
    private final int retextureStart;
    private final int textureStride;
    private float[] currentUvs;
    private int[] currentColorMultipliers;

    public MergedItemModel(IPerspectiveAwareModel parent, List<BakedQuad> mergedQuads, int retextureStart, int textureStride) {
        this.parent = parent;
        this.mergedQuads = mergedQuads;
        this.retextureStart = retextureStart;
        this.textureStride = textureStride;
    }

    public MergedItemModel copy() {
        ArrayList<BakedQuad> newMergedQuads = new ArrayList<BakedQuad>(this.mergedQuads);
        for (int i = this.retextureStart; i < this.mergedQuads.size(); ++i) {
            BakedQuad oldQuad = this.mergedQuads.get(i);
            int[] vertexData = Arrays.copyOf(oldQuad.getVertexData(), oldQuad.getVertexData().length);
            BakedQuad newQuad = new BakedQuad(vertexData, oldQuad.getTintIndex(), oldQuad.getFace(), oldQuad.getSprite(), oldQuad.shouldApplyDiffuseLighting(), oldQuad.getFormat());
            newMergedQuads.set(i, newQuad);
        }
        return new MergedItemModel(this.parent, newMergedQuads, this.retextureStart, this.textureStride);
    }

    public void setSprite(TextureAtlasSprite sprite, int colorMultiplier, float uSShift, float vSShift, float uEShift, float vEShift) {
        boolean matchingColorMul;
        boolean matchingUvs = this.currentUvs != null && this.currentUvs.length == 4 && this.currentUvs[0] == sprite.getMinU() && this.currentUvs[1] == sprite.getMinV() && this.currentUvs[2] == sprite.getMaxU() && this.currentUvs[3] == sprite.getMaxV();
        boolean bl = matchingColorMul = this.currentColorMultipliers != null && this.currentColorMultipliers[0] == colorMultiplier;
        if (!matchingUvs || !matchingColorMul) {
            if (!matchingUvs) {
                this.currentUvs = new float[]{sprite.getMinU(), sprite.getMinV(), sprite.getMaxU(), sprite.getMaxV()};
            }
            if (!matchingColorMul) {
                this.currentColorMultipliers = new int[]{colorMultiplier};
            }
            this.setSpriteUnchecked(uSShift, vSShift, uEShift, vEShift);
        }
    }

    public void setSprite(float[] uvs, int[] colorMultipliers, float uSShift, float vSShift, float uEShift, float vEShift) {
        boolean matchingUvs = Arrays.equals(uvs, this.currentUvs);
        boolean matchingColorMul = Arrays.equals(colorMultipliers, this.currentColorMultipliers);
        if (!matchingUvs || !matchingColorMul) {
            if (!matchingUvs) {
                this.currentUvs = uvs;
            }
            if (!matchingColorMul) {
                this.currentColorMultipliers = colorMultipliers;
            }
            this.setSpriteUnchecked(uSShift, vSShift, uEShift, vEShift);
        }
    }

    private void setSpriteUnchecked(float uSShift, float vSShift, float uEShift, float vEShift) {
        if (this.mergedQuads.size() - this.retextureStart > this.textureStride * this.currentColorMultipliers.length) {
            throw new IllegalStateException(String.format("mismatched size/stride/multipliers: retex-quads=%d, stride=%d, muls=%d", this.mergedQuads.size() - this.retextureStart, this.textureStride, this.currentColorMultipliers.length));
        }
        if (this.currentUvs.length != this.currentColorMultipliers.length * 4) {
            throw new IllegalStateException(String.format("mismatched uvs/multipliers: uvs=%d, muls=%d", this.currentUvs.length, this.currentColorMultipliers.length));
        }
        int texture = 0;
        for (int baseIdx = this.retextureStart; baseIdx < this.mergedQuads.size(); baseIdx += this.textureStride) {
            float uS = this.currentUvs[texture * 4];
            float vS = this.currentUvs[texture * 4 + 1];
            float uE = this.currentUvs[texture * 4 + 2];
            float vE = this.currentUvs[texture * 4 + 3];
            float du = uE - uS;
            float dv = vE - vS;
            uS -= (du /= uEShift - uSShift) * uSShift;
            vS -= (dv /= vEShift - vSShift) * (1.0f - vEShift);
            int colorMultiplier = MergedItemModel.mapColor(this.currentColorMultipliers[texture]);
            for (int i = 0; i < this.textureStride; ++i) {
                int[] vertexData = this.mergedQuads.get(baseIdx + i).getVertexData();
                for (int j = 0; j < 4; ++j) {
                    int offset = j * ItemGeo.dataStride;
                    vertexData[offset + 3] = colorMultiplier;
                    float x = Float.intBitsToFloat(vertexData[offset + 0]);
                    float y = Float.intBitsToFloat(vertexData[offset + 1]);
                    vertexData[offset + 4] = Float.floatToRawIntBits(uS + x * du);
                    vertexData[offset + 5] = Float.floatToRawIntBits(vS + y * dv);
                }
            }
            ++texture;
        }
    }

    private static int mapColor(int color) {
        int a = color >>> 24;
        if (a > 0) {
            return color & -16711936 | (color & 255) << 16 | (color & 16711680) >> 16;
        }
        return -16777216 | color & 65280 | (color & 255) << 16 | (color & 16711680) >> 16;
    }

    public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
        if (side != null) {
            return this.parent.getQuads(state, side, rand);
        }
        return this.mergedQuads;
    }

    public boolean isAmbientOcclusion() {
        return this.parent.isAmbientOcclusion();
    }

    public boolean isGui3d() {
        return this.parent.isGui3d();
    }

    public boolean isBuiltInRenderer() {
        return this.parent.isBuiltInRenderer();
    }

    public TextureAtlasSprite getParticleTexture() {
        return this.parent.getParticleTexture();
    }

    @Deprecated
    public ItemCameraTransforms getItemCameraTransforms() {
        return this.parent.getItemCameraTransforms();
    }

    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
        return Pair.of((Object)this, (Object)this.parent.handlePerspective(cameraTransformType).getRight());
    }

    public ItemOverrideList getOverrides() {
        return this.parent.getOverrides();
    }
}

