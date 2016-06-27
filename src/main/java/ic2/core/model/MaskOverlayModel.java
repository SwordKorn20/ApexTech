/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.block.model.BakedQuad
 *  net.minecraft.client.renderer.block.model.IBakedModel
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.client.renderer.texture.TextureUtil
 *  net.minecraft.client.renderer.vertex.VertexFormat
 *  net.minecraft.client.resources.IResource
 *  net.minecraft.client.resources.IResourceManager
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.client.model.IModel
 *  net.minecraftforge.client.model.IPerspectiveAwareModel
 *  net.minecraftforge.client.model.ModelLoaderRegistry
 *  net.minecraftforge.common.model.IModelState
 */
package ic2.core.model;

import com.google.common.base.Function;
import ic2.core.model.AbstractModel;
import ic2.core.model.BasicBakedItemModel;
import ic2.core.model.ItemGeo;
import ic2.core.model.MergedItemModel;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelState;

public abstract class MaskOverlayModel
extends AbstractModel {
    private final ResourceLocation baseModelLocation;
    private final ResourceLocation maskTextureLocation;
    private final boolean scaleOverlay;
    private final float offset;
    private IBakedModel bakedModel;
    private MergedItemModel mergedModel;
    private float uS;
    private float vS;
    private float uE;
    private float vE;
    private final ThreadLocal<MergedItemModel> currentMergedModel;

    protected MaskOverlayModel(ResourceLocation baseModelLocation, ResourceLocation maskTextureLocation, boolean scaleOverlay, float offset) {
        this.currentMergedModel = new ThreadLocal<MergedItemModel>(){

            @Override
            protected MergedItemModel initialValue() {
                return MaskOverlayModel.this.mergedModel.copy();
            }
        };
        this.baseModelLocation = baseModelLocation;
        this.maskTextureLocation = maskTextureLocation;
        this.scaleOverlay = scaleOverlay;
        this.offset = offset;
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return Arrays.asList(new ResourceLocation[]{this.baseModelLocation});
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        IModel baseModel;
        BufferedImage img;
        try {
            baseModel = ModelLoaderRegistry.getModel((ResourceLocation)this.baseModelLocation);
            IResource resource = Minecraft.getMinecraft().getResourceManager().getResource(this.maskTextureLocation);
            img = TextureUtil.readBufferedImage((InputStream)resource.getInputStream());
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        int width = img.getWidth();
        int height = img.getHeight();
        List<Area> areas = MaskOverlayModel.searchAreas(MaskOverlayModel.readMask(img), width);
        this.bakedModel = baseModel.bake(baseModel.getDefaultState(), format, bakedTextureGetter);
        List origQuads = this.bakedModel.getQuads(null, null, 0);
        int retextureStart = origQuads.size();
        ArrayList<BakedQuad> mergedQuads = new ArrayList<BakedQuad>(retextureStart + areas.size() * 2);
        mergedQuads.addAll(origQuads);
        MaskOverlayModel.generateQuads(areas, width, height, this.offset, -1, mergedQuads);
        this.calculateUV(areas, width, height);
        this.mergedModel = new MergedItemModel((IPerspectiveAwareModel)this.bakedModel, mergedQuads, retextureStart, areas.size() * 2);
        return this;
    }

    protected IBakedModel get() {
        return this.bakedModel;
    }

    protected IBakedModel get(TextureAtlasSprite overlay, int colorMultiplier) {
        if (overlay == null) {
            throw new NullPointerException();
        }
        MergedItemModel ret = this.currentMergedModel.get();
        if (this.scaleOverlay) {
            ret.setSprite(overlay, colorMultiplier, this.uS, this.vS, this.uE, this.vE);
        } else {
            ret.setSprite(overlay, colorMultiplier, 0.0f, 0.0f, 1.0f, 1.0f);
        }
        return ret;
    }

    protected IBakedModel get(float[] uvs, int[] colorMultipliers) {
        if (uvs == null) {
            throw new NullPointerException();
        }
        if (uvs.length == 0) {
            return this.get();
        }
        if (uvs.length % 4 != 0) {
            throw new IllegalArgumentException("invalid uv array");
        }
        MergedItemModel ret = this.currentMergedModel.get();
        if (this.scaleOverlay) {
            ret.setSprite(uvs, colorMultipliers, this.uS, this.vS, this.uE, this.vE);
        } else {
            ret.setSprite(uvs, colorMultipliers, 0.0f, 0.0f, 1.0f, 1.0f);
        }
        return ret;
    }

    private static BitSet readMask(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        BitSet ret = new BitSet(width * height);
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                int alpha = img.getRGB(x, y) >>> 24;
                if (alpha <= 128) continue;
                ret.set(y * width + x);
            }
        }
        return ret;
    }

    private static List<Area> searchAreas(BitSet pixels, int width) {
        ArrayList<Area> ret = new ArrayList<Area>();
        int idx = 0;
        while ((idx = pixels.nextSetBit(idx)) != -1) {
            int y = idx / width;
            int x = idx - y * width;
            int areaWidth = Math.min(width - x, pixels.nextClearBit(idx + 1) - idx);
            int areaHeight = 1;
            int nextLineIdx = idx + width;
            while (pixels.get(nextLineIdx) && pixels.nextClearBit(nextLineIdx + 1) >= nextLineIdx + areaWidth) {
                pixels.clear(nextLineIdx, nextLineIdx + areaWidth);
                ++areaHeight;
                nextLineIdx += width;
            }
            ret.add(new Area(x, y, areaWidth, areaHeight));
            idx += areaWidth;
        }
        return ret;
    }

    private static void generateQuads(List<Area> areas, int width, int height, float offset, int tint, List<BakedQuad> out) {
        assert (tint == -1);
        float zF = (7.5f - offset) / 16.0f;
        float zB = (8.5f + offset) / 16.0f;
        int color = -1;
        IntBuffer buffer = ItemGeo.getQuadBuffer();
        for (Area area : areas) {
            float xS = (float)area.x / (float)width;
            float yS = 1.0f - (float)area.y / (float)height;
            float xE = (float)(area.x + area.width) / (float)width;
            float yE = 1.0f - (float)(area.y + area.height) / (float)height;
            ItemGeo.generateVertex(xS, yS, zF, -1, 0.0f, 0.0f, EnumFacing.SOUTH, buffer);
            ItemGeo.generateVertex(xE, yS, zF, -1, 1.0f, 0.0f, EnumFacing.SOUTH, buffer);
            ItemGeo.generateVertex(xE, yE, zF, -1, 1.0f, 1.0f, EnumFacing.SOUTH, buffer);
            ItemGeo.generateVertex(xS, yE, zF, -1, 0.0f, 1.0f, EnumFacing.SOUTH, buffer);
            out.add(BasicBakedItemModel.createQuad(Arrays.copyOf(buffer.array(), buffer.position()), EnumFacing.SOUTH));
            buffer.rewind();
            ItemGeo.generateVertex(xS, yS, zB, -1, 0.0f, 0.0f, EnumFacing.NORTH, buffer);
            ItemGeo.generateVertex(xS, yE, zB, -1, 0.0f, 1.0f, EnumFacing.NORTH, buffer);
            ItemGeo.generateVertex(xE, yE, zB, -1, 1.0f, 1.0f, EnumFacing.NORTH, buffer);
            ItemGeo.generateVertex(xE, yS, zB, -1, 1.0f, 0.0f, EnumFacing.NORTH, buffer);
            out.add(BasicBakedItemModel.createQuad(Arrays.copyOf(buffer.array(), buffer.position()), EnumFacing.NORTH));
            buffer.rewind();
        }
    }

    private void calculateUV(List<Area> areas, int width, int height) {
        if (!this.scaleOverlay) {
            return;
        }
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (Area area : areas) {
            if (area.x < minX) {
                minX = area.x;
            }
            if (area.y < minY) {
                minY = area.y;
            }
            if (area.x + area.width > maxX) {
                maxX = area.x + area.width;
            }
            if (area.y + area.height <= maxY) continue;
            maxY = area.y + area.height;
        }
        this.uS = (float)minX / (float)width;
        this.vS = (float)minY / (float)height;
        this.uE = (float)maxX / (float)width;
        this.vE = (float)maxY / (float)height;
    }

    private static class Area {
        final int x;
        final int y;
        final int width;
        final int height;

        public Area(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public String toString() {
            return String.format("%d/%d %dx%d", this.x, this.y, this.width, this.height);
        }
    }

}

