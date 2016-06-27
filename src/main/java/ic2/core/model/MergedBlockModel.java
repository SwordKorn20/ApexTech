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
 *  net.minecraft.client.renderer.vertex.DefaultVertexFormats
 *  net.minecraft.client.renderer.vertex.VertexFormat
 *  net.minecraft.util.EnumFacing
 *  net.minecraftforge.client.model.IPerspectiveAwareModel
 *  org.apache.commons.lang3.tuple.Pair
 */
package ic2.core.model;

import java.nio.IntBuffer;
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
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import org.apache.commons.lang3.tuple.Pair;

public class MergedBlockModel
implements IPerspectiveAwareModel {
    private static final byte[][] uvMap = new byte[][]{{1, 0, 0, 0, 0, 1}, {1, 0, 0, 0, 1, 0}, {0, 0, 1, 0, 1, 0}};
    private static final int quadVertexCount = 4;
    private static final VertexFormat vertexFormat = DefaultVertexFormats.BLOCK;
    private static final int dataStride = vertexFormat.getNextOffset() / 4;
    private static final int xDataIndex = 0;
    private static final int yDataIndex = 1;
    private static final int zDataIndex = 2;
    private static final int colorDataIndex = 3;
    private static final int uDataIndex = 4;
    private static final int vDataIndex = 5;
    private final IPerspectiveAwareModel parent;
    private final List<BakedQuad>[] mergedFaceQuads;
    private final int[] retextureStart;
    private static final int textureStride = 1;
    private final float[][] currentUvs = new float[6][];
    private final int[][] currentColorMultipliers = new int[6][];

    public MergedBlockModel(IPerspectiveAwareModel parent, List<BakedQuad>[] mergedFaceQuads, int[] retextureStart) {
        this.parent = parent;
        this.mergedFaceQuads = mergedFaceQuads;
        this.retextureStart = retextureStart;
    }

    public MergedBlockModel copy() {
        List[] newMergedQuads = new List[this.mergedFaceQuads.length];
        for (int side = 0; side < this.mergedFaceQuads.length; ++side) {
            List<BakedQuad> mergedFaceQuads = this.mergedFaceQuads[side];
            ArrayList<BakedQuad> newMergedFaceQuads = new ArrayList<BakedQuad>(mergedFaceQuads);
            for (int i = this.retextureStart[side]; i < mergedFaceQuads.size(); ++i) {
                BakedQuad oldQuad = mergedFaceQuads.get(i);
                int[] vertexData = Arrays.copyOf(oldQuad.getVertexData(), oldQuad.getVertexData().length);
                BakedQuad newQuad = new BakedQuad(vertexData, oldQuad.getTintIndex(), oldQuad.getFace(), oldQuad.getSprite(), oldQuad.shouldApplyDiffuseLighting(), oldQuad.getFormat());
                newMergedFaceQuads.set(i, newQuad);
            }
        }
        return new MergedBlockModel(this.parent, newMergedQuads, this.retextureStart);
    }

    public void setSprite(float[][] uvs, int[][] colorMultipliers) {
        for (int i = 0; i < 6; ++i) {
            boolean matchingUvs = Arrays.equals(uvs[i], this.currentUvs[i]);
            boolean matchingColorMul = Arrays.equals(colorMultipliers[i], this.currentColorMultipliers[i]);
            if (matchingUvs && matchingColorMul) continue;
            if (!matchingUvs) {
                this.currentUvs[i] = uvs[i];
            }
            if (!matchingColorMul) {
                this.currentColorMultipliers[i] = colorMultipliers[i];
            }
            if (this.currentColorMultipliers[i] == null) continue;
            this.setSpriteUnchecked(this.mergedFaceQuads[i], this.retextureStart[i], this.currentUvs[i], uvMap[i / 2], this.currentColorMultipliers[i]);
        }
    }

    private void setSpriteUnchecked(List<BakedQuad> quads, int retextureStart, float[] uvs, byte[] uvMap, int[] colorMultipliers) {
        if (quads.size() - retextureStart > colorMultipliers.length) {
            throw new IllegalStateException(String.format("mismatched size/stride/multipliers: retex-quads=%d, stride=%d, muls=%d", quads.size() - retextureStart, 1, colorMultipliers.length));
        }
        if (uvs.length != colorMultipliers.length * 4) {
            throw new IllegalStateException(String.format("mismatched uvs/multipliers: uvs=%d, muls=%d", uvs.length, colorMultipliers.length));
        }
        for (int texture = 0; texture < colorMultipliers.length; ++texture) {
            float uS = uvs[texture * 4];
            float vS = uvs[texture * 4 + 1];
            float uE = uvs[texture * 4 + 2];
            float vE = uvs[texture * 4 + 3];
            float du = uE - uS;
            float dv = vE - vS;
            int colorMultiplier = MergedBlockModel.mapColor(colorMultipliers[texture]);
            for (int i = 0; i < 1; ++i) {
                int[] vertexData = quads.get(retextureStart + texture + i).getVertexData();
                for (int j = 0; j < 4; ++j) {
                    int offset = j * dataStride;
                    vertexData[offset + 3] = colorMultiplier;
                    float x = Float.intBitsToFloat(vertexData[offset + 0]);
                    float y = Float.intBitsToFloat(vertexData[offset + 1]);
                    float z = Float.intBitsToFloat(vertexData[offset + 2]);
                    vertexData[offset + 4] = Float.floatToRawIntBits(uS + du * (x * (float)uvMap[0] + y * (float)uvMap[1] + z * (float)uvMap[2]));
                    vertexData[offset + 5] = Float.floatToRawIntBits(vS + dv * (x * (float)uvMap[3] + y * (float)uvMap[4] + z * (float)uvMap[5]));
                }
            }
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
        if (side == null) {
            return this.parent.getQuads(state, side, rand);
        }
        return this.mergedFaceQuads[side.ordinal()];
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

    public static IntBuffer getQuadBuffer() {
        return IntBuffer.allocate(4 * dataStride);
    }

    public static void generateVertex(float x, float y, float z, int color, float u, float v, IntBuffer out) {
        out.put(Float.floatToRawIntBits(x));
        out.put(Float.floatToRawIntBits(y));
        out.put(Float.floatToRawIntBits(z));
        out.put(color);
        out.put(Float.floatToRawIntBits(u));
        out.put(Float.floatToRawIntBits(v));
        out.put(0);
    }
}

