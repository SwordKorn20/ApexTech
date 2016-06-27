/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.renderer.block.model.BakedQuad
 *  net.minecraft.client.renderer.block.model.IBakedModel
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.client.renderer.vertex.VertexFormat
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.MathHelper
 *  net.minecraftforge.common.model.IModelState
 *  net.minecraftforge.common.property.IUnlistedProperty
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.crop;

import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import ic2.api.crops.CropCard;
import ic2.api.crops.Crops;
import ic2.core.block.state.Ic2BlockState;
import ic2.core.crop.cropcard.TeCrop;
import ic2.core.model.AbstractModel;
import ic2.core.model.BasicBakedBlockModel;
import ic2.core.model.ModelUtil;
import java.nio.Buffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public class CropModel
extends AbstractModel {
    private static final int[] faceShades = CropModel.getFaceShades();
    private final Map<ResourceLocation, TextureAtlasSprite> textures = CropModel.generateTextureLocations();
    private final LoadingCache<TeCrop.CropRenderState, IBakedModel> modelCache;

    public CropModel() {
        this.modelCache = CacheBuilder.newBuilder().maximumSize(256).expireAfterAccess(5, TimeUnit.MINUTES).build((CacheLoader)new CacheLoader<TeCrop.CropRenderState, IBakedModel>(){

            public IBakedModel load(TeCrop.CropRenderState key) throws Exception {
                return CropModel.this.generateModel(key);
            }
        });
    }

    @Override
    public Collection<ResourceLocation> getTextures() {
        return this.textures.keySet();
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        for (Map.Entry<ResourceLocation, TextureAtlasSprite> entry : this.textures.entrySet()) {
            entry.setValue((TextureAtlasSprite)bakedTextureGetter.apply((Object)entry.getKey()));
        }
        return this;
    }

    private static Map<ResourceLocation, TextureAtlasSprite> generateTextureLocations() {
        HashMap<ResourceLocation, TextureAtlasSprite> ret = new HashMap<ResourceLocation, TextureAtlasSprite>();
        StringBuilder name = new StringBuilder();
        name.append("blocks/crop/");
        int reset0 = name.length();
        for (CropCard crop : Crops.instance.getCrops()) {
            name.append(crop.getName());
            int reset1 = name.length();
            for (int size = 1; size <= crop.getMaxSize(); ++size) {
                name.append('_');
                name.append(size);
                ret.put(new ResourceLocation("ic2", name.toString()), null);
                name.setLength(reset1);
            }
            name.setLength(reset0);
        }
        return ret;
    }

    private static ResourceLocation getTextureLocation(CropCard crop, int size) {
        return new ResourceLocation("ic2", "blocks/crop/" + crop.getName() + "_" + size);
    }

    @Override
    public List<BakedQuad> getQuads(IBlockState rawState, EnumFacing side, long rand) {
        if (!(rawState instanceof Ic2BlockState.Ic2BlockStateInstance)) {
            return ModelUtil.getMissingModel().getQuads(rawState, side, rand);
        }
        Ic2BlockState.Ic2BlockStateInstance state = (Ic2BlockState.Ic2BlockStateInstance)rawState;
        if (!state.hasValue(TeCrop.renderStateProperty)) {
            return ModelUtil.getMissingModel().getQuads((IBlockState)state, side, rand);
        }
        TeCrop.CropRenderState prop = state.getValue(TeCrop.renderStateProperty);
        try {
            return ((IBakedModel)this.modelCache.get((Object)prop)).getQuads((IBlockState)state, side, rand);
        }
        catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private IBakedModel generateModel(TeCrop.CropRenderState prop) {
        List[] faceQuads = new List[EnumFacing.VALUES.length];
        for (int i = 0; i < faceQuads.length; ++i) {
            faceQuads[i] = new ArrayList();
        }
        ArrayList<BakedQuad> generalQuads = new ArrayList();
        TextureAtlasSprite sprite = this.textures.get((Object)CropModel.getTextureLocation(prop.crop, prop.size));
        TextureAtlasSprite sprite2 = this.textures.get((Object)new ResourceLocation("ic2", "blocks/crop/stick2"));
        TextureAtlasSprite sprite3 = this.textures.get((Object)new ResourceLocation("ic2", "blocks/crop/reed_3"));
        TextureAtlasSprite top = this.textures.get((Object)new ResourceLocation("ic2", "blocks/machine/fluid/tank_top"));
        TextureAtlasSprite sides = this.textures.get((Object)new ResourceLocation("ic2", "blocks/machine/fluid/tank_side"));
        TextureAtlasSprite bottom = this.textures.get((Object)new ResourceLocation("ic2", "blocks/machine/fluid/tank_bottom"));
        block9 : for (EnumFacing facing : EnumFacing.VALUES) {
            TextureAtlasSprite usedSprite = null;
            switch (facing) {
                case DOWN: {
                    usedSprite = bottom;
                    CropModel.addCuboid(0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, EnumSet.of(facing), usedSprite, faceQuads, generalQuads);
                    continue block9;
                }
                case UP: {
                    usedSprite = top;
                    CropModel.addCuboid(0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, EnumSet.of(facing), usedSprite, faceQuads, generalQuads);
                    continue block9;
                }
                case NORTH: {
                    usedSprite = sides;
                    CropModel.addCuboid(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, EnumSet.of(facing), usedSprite, faceQuads, generalQuads);
                    continue block9;
                }
                case SOUTH: {
                    usedSprite = sides;
                    CropModel.addCuboid(0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, EnumSet.of(facing), usedSprite, faceQuads, generalQuads);
                    continue block9;
                }
                case WEST: {
                    usedSprite = sides;
                    CropModel.addCuboid(0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, EnumSet.of(facing), usedSprite, faceQuads, generalQuads);
                    continue block9;
                }
                case EAST: {
                    usedSprite = sides;
                    CropModel.addCuboid(1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, EnumSet.of(facing), usedSprite, faceQuads, generalQuads);
                    continue block9;
                }
                default: {
                    throw new RuntimeException();
                }
            }
        }
        int used = 0;
        for (int i2 = 0; i2 < faceQuads.length; ++i2) {
            if (faceQuads[i2].isEmpty()) {
                faceQuads[i2] = Collections.emptyList();
                continue;
            }
            ++used;
        }
        if (used == 0) {
            faceQuads = null;
        }
        if (generalQuads.isEmpty()) {
            generalQuads = Collections.emptyList();
        }
        return new BasicBakedBlockModel(faceQuads, generalQuads, sprite);
    }

    private static void addCuboid(float xS, float yS, float zS, float xE, float yE, float zE, Set<EnumFacing> faces, TextureAtlasSprite sprite, List<BakedQuad>[] faceQuads, List<BakedQuad> generalQuads) {
        float spriteU = sprite.getMinU();
        float spriteV = sprite.getMinV();
        float spriteWidth = sprite.getMaxU() - spriteU;
        float spriteHeight = sprite.getMaxV() - spriteV;
        IntBuffer quadBuffer = IntBuffer.allocate(28);
        block8 : for (EnumFacing facing : faces) {
            boolean isFace;
            switch (facing) {
                case DOWN: {
                    if (xS == xE || zS == zE) continue block8;
                    CropModel.addVertex(xS, yS, zS, spriteU + spriteWidth * xS, spriteV + spriteHeight * zS, facing, quadBuffer);
                    CropModel.addVertex(xE, yS, zS, spriteU + spriteWidth * xE, spriteV + spriteHeight * zS, facing, quadBuffer);
                    CropModel.addVertex(xE, yS, zE, spriteU + spriteWidth * xE, spriteV + spriteHeight * zE, facing, quadBuffer);
                    CropModel.addVertex(xS, yS, zE, spriteU + spriteWidth * xS, spriteV + spriteHeight * zE, facing, quadBuffer);
                    isFace = yS == 0.0f;
                    break;
                }
                case UP: {
                    if (xS == xE || zS == zE) continue block8;
                    CropModel.addVertex(xS, yE, zS, spriteU + spriteWidth * xS, spriteV + spriteHeight * zS, facing, quadBuffer);
                    CropModel.addVertex(xS, yE, zE, spriteU + spriteWidth * xS, spriteV + spriteHeight * zE, facing, quadBuffer);
                    CropModel.addVertex(xE, yE, zE, spriteU + spriteWidth * xE, spriteV + spriteHeight * zE, facing, quadBuffer);
                    CropModel.addVertex(xE, yE, zS, spriteU + spriteWidth * xE, spriteV + spriteHeight * zS, facing, quadBuffer);
                    isFace = yE == 1.0f;
                    break;
                }
                case NORTH: {
                    if (xS == xE || yS == yE) continue block8;
                    CropModel.addVertex(xS, yS, zS, spriteU + spriteWidth * xS, spriteV + spriteHeight * yS, facing, quadBuffer);
                    CropModel.addVertex(xS, yE, zS, spriteU + spriteWidth * xS, spriteV + spriteHeight * yE, facing, quadBuffer);
                    CropModel.addVertex(xE, yE, zS, spriteU + spriteWidth * xE, spriteV + spriteHeight * yE, facing, quadBuffer);
                    CropModel.addVertex(xE, yS, zS, spriteU + spriteWidth * xE, spriteV + spriteHeight * yS, facing, quadBuffer);
                    isFace = zS == 0.0f;
                    break;
                }
                case SOUTH: {
                    if (xS == xE || yS == yE) continue block8;
                    CropModel.addVertex(xS, yS, zE, spriteU + spriteWidth * xS, spriteV + spriteHeight * yS, facing, quadBuffer);
                    CropModel.addVertex(xE, yS, zE, spriteU + spriteWidth * xE, spriteV + spriteHeight * yS, facing, quadBuffer);
                    CropModel.addVertex(xE, yE, zE, spriteU + spriteWidth * xE, spriteV + spriteHeight * yE, facing, quadBuffer);
                    CropModel.addVertex(xS, yE, zE, spriteU + spriteWidth * xS, spriteV + spriteHeight * yE, facing, quadBuffer);
                    isFace = zE == 1.0f;
                    break;
                }
                case WEST: {
                    if (yS == yE || zS == zE) continue block8;
                    CropModel.addVertex(xS, yS, zS, spriteU + spriteWidth * zS, spriteV + spriteHeight * yS, facing, quadBuffer);
                    CropModel.addVertex(xS, yS, zE, spriteU + spriteWidth * zE, spriteV + spriteHeight * yS, facing, quadBuffer);
                    CropModel.addVertex(xS, yE, zE, spriteU + spriteWidth * zE, spriteV + spriteHeight * yE, facing, quadBuffer);
                    CropModel.addVertex(xS, yE, zS, spriteU + spriteWidth * zS, spriteV + spriteHeight * yE, facing, quadBuffer);
                    isFace = xS == 0.0f;
                    break;
                }
                case EAST: {
                    if (yS == yE || zS == zE) continue block8;
                    CropModel.addVertex(xE, yS, zS, spriteU + spriteWidth * zS, spriteV + spriteHeight * yS, facing, quadBuffer);
                    CropModel.addVertex(xE, yE, zS, spriteU + spriteWidth * zS, spriteV + spriteHeight * yE, facing, quadBuffer);
                    CropModel.addVertex(xE, yE, zE, spriteU + spriteWidth * zE, spriteV + spriteHeight * yE, facing, quadBuffer);
                    CropModel.addVertex(xE, yS, zE, spriteU + spriteWidth * zE, spriteV + spriteHeight * yS, facing, quadBuffer);
                    isFace = xE == 1.0f;
                    break;
                }
                default: {
                    throw new IllegalArgumentException();
                }
            }
            if (quadBuffer.position() <= 0) continue;
            BakedQuad quad = BasicBakedBlockModel.createQuad(Arrays.copyOf(quadBuffer.array(), quadBuffer.position()), facing);
            if (isFace) {
                faceQuads[facing.ordinal()].add(quad);
            } else {
                generalQuads.add(quad);
            }
            quadBuffer.rewind();
        }
    }

    public static void addVertex(float x, float y, float z, float u, float v, EnumFacing face, IntBuffer output) {
        output.put(Float.floatToRawIntBits(x));
        output.put(Float.floatToRawIntBits(y));
        output.put(Float.floatToRawIntBits(z));
        output.put(faceShades[face.ordinal()]);
        output.put(Float.floatToRawIntBits(u));
        output.put(Float.floatToRawIntBits(v));
        output.put(0);
    }

    private static int[] getFaceShades() {
        int[] ret = new int[EnumFacing.VALUES.length];
        double[] faceBrightness = new double[]{0.5, 1.0, 0.8, 0.8, 0.6, 0.6};
        for (EnumFacing facing : EnumFacing.VALUES) {
            int brightness = MathHelper.clamp_int((int)((int)(faceBrightness[facing.ordinal()] * 255.0)), (int)0, (int)255);
            ret[facing.ordinal()] = -16777216 | brightness << 16 | brightness << 8 | brightness;
        }
        return ret;
    }

}

