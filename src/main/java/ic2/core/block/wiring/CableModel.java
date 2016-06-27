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
package ic2.core.block.wiring;

import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import ic2.core.block.BlockFoam;
import ic2.core.block.TileEntityWall;
import ic2.core.block.state.Ic2BlockState;
import ic2.core.model.AbstractModel;
import ic2.core.model.BasicBakedBlockModel;
import ic2.core.model.ModelUtil;
import ic2.core.ref.BlockName;
import ic2.core.ref.TeBlock;
import ic2.core.util.Ic2Color;

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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public class CableModel
extends AbstractModel {
    private static final int[] faceShades = CableModel.getFaceShades();
    private final Map<ResourceLocation, TextureAtlasSprite> textures = CableModel.generateTextureLocations();
    private final LoadingCache<TileEntityCable.CableRenderState, IBakedModel> modelCache;

    public CableModel() {
        this.modelCache = CacheBuilder.newBuilder().maximumSize(256).expireAfterAccess(5, TimeUnit.MINUTES).build((CacheLoader)new CacheLoader<TileEntityCable.CableRenderState, IBakedModel>(){

            public IBakedModel load(TileEntityCable.CableRenderState key) throws Exception {
                return CableModel.this.generateModel(key);
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
        name.append("blocks/wiring/cable/");
        int reset0 = name.length();
        for (CableType type : CableType.values) {
            name.append(type.name());
            name.append("_cable");
            int reset1 = name.length();
            for (int insulation = 0; insulation <= type.maxInsulation; ++insulation) {
                if (type.maxInsulation != 0) {
                    name.append('_');
                    name.append(insulation);
                }
                if (insulation >= type.minColoredInsulation) {
                    name.append('_');
                    int reset2 = name.length();
                    for (Ic2Color color : Ic2Color.values) {
                        name.append(color.name());
                        ret.put(new ResourceLocation("ic2", name.toString()), null);
                        name.setLength(reset2);
                    }
                } else {
                    ret.put(new ResourceLocation("ic2", name.toString()), null);
                }
                name.setLength(reset1);
            }
            name.setLength(reset0);
        }
        return ret;
    }

    private static ResourceLocation getTextureLocation(CableType type, int insulation, Ic2Color color) {
        return new ResourceLocation("ic2", "blocks/wiring/cable/" + type.getName(insulation, color));
    }

    @Override
    public List<BakedQuad> getQuads(IBlockState rawState, EnumFacing side, long rand) {
        if (!(rawState instanceof Ic2BlockState.Ic2BlockStateInstance)) {
            return ModelUtil.getMissingModel().getQuads(rawState, side, rand);
        }
        Ic2BlockState.Ic2BlockStateInstance state = (Ic2BlockState.Ic2BlockStateInstance)rawState;
        if (!state.hasValue(TileEntityCable.renderStateProperty)) {
            return ModelUtil.getMissingModel().getQuads((IBlockState)state, side, rand);
        }
        TileEntityCable.CableRenderState prop = state.getValue(TileEntityCable.renderStateProperty);
        if (prop.foam == CableFoam.Soft) {
            return ModelUtil.getBlockModel(BlockName.foam.getBlockState(BlockFoam.FoamType.normal)).getQuads((IBlockState)state, side, rand);
        }
        if (prop.foam == CableFoam.Hardened) {
            TileEntityWall.WallRenderState wallProp = state.getValue(TileEntityWall.renderStateProperty);
            if (wallProp == null) {
                return ModelUtil.getMissingModel().getQuads((IBlockState)state, side, rand);
            }
            if (wallProp.obscurations == null) {
                return ModelUtil.getBlockModel(BlockName.wall.getBlockState(wallProp.color)).getQuads((IBlockState)state, side, rand);
            }
            IBakedModel model = ModelUtil.getBlockModel(BlockName.te.getBlockState(TeBlock.wall));
            return model.getQuads((IBlockState)state, side, rand);
        }
        try {
            return ((IBakedModel)this.modelCache.get((Object)prop)).getQuads((IBlockState)state, side, rand);
        }
        catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private IBakedModel generateModel(TileEntityCable.CableRenderState prop) {
        float th = prop.type.thickness + (float)(prop.insulation * 2) * 0.0625f;
        float sp = (1.0f - th) / 2.0f;
        List[] faceQuads = new List[EnumFacing.VALUES.length];
        for (int i = 0; i < faceQuads.length; ++i) {
            faceQuads[i] = new ArrayList();
        }
        ArrayList<BakedQuad> generalQuads = new ArrayList();
        TextureAtlasSprite sprite = this.textures.get((Object)CableModel.getTextureLocation(prop.type, prop.insulation, prop.color));
        for (EnumFacing facing : EnumFacing.VALUES) {
            float zS;
            float zE;
            boolean hasConnection = (prop.connectivity & 1 << facing.ordinal()) != 0;
            float yS = zS = sp;
            float xS = zS;
            float yE = zE = sp + th;
            float xE = zE;
            if (hasConnection) {
                switch (facing) {
                    case DOWN: {
                        yS = 0.0f;
                        yE = sp;
                        break;
                    }
                    case UP: {
                        yS = sp + th;
                        yE = 1.0f;
                        break;
                    }
                    case NORTH: {
                        zS = 0.0f;
                        zE = sp;
                        break;
                    }
                    case SOUTH: {
                        zS = sp + th;
                        zE = 1.0f;
                        break;
                    }
                    case WEST: {
                        xS = 0.0f;
                        xE = sp;
                        break;
                    }
                    case EAST: {
                        xS = sp + th;
                        xE = 1.0f;
                        break;
                    }
                    default: {
                        throw new RuntimeException();
                    }
                }
                CableModel.addCuboid(xS, yS, zS, xE, yE, zE, EnumSet.complementOf(EnumSet.of(facing.getOpposite())), sprite, faceQuads, generalQuads);
                continue;
            }
            CableModel.addCuboid(xS, yS, zS, xE, yE, zE, EnumSet.of(facing), sprite, faceQuads, generalQuads);
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
                    CableModel.addVertex(xS, yS, zS, spriteU + spriteWidth * xS, spriteV + spriteHeight * zS, facing, quadBuffer);
                    CableModel.addVertex(xE, yS, zS, spriteU + spriteWidth * xE, spriteV + spriteHeight * zS, facing, quadBuffer);
                    CableModel.addVertex(xE, yS, zE, spriteU + spriteWidth * xE, spriteV + spriteHeight * zE, facing, quadBuffer);
                    CableModel.addVertex(xS, yS, zE, spriteU + spriteWidth * xS, spriteV + spriteHeight * zE, facing, quadBuffer);
                    isFace = yS == 0.0f;
                    break;
                }
                case UP: {
                    if (xS == xE || zS == zE) continue block8;
                    CableModel.addVertex(xS, yE, zS, spriteU + spriteWidth * xS, spriteV + spriteHeight * zS, facing, quadBuffer);
                    CableModel.addVertex(xS, yE, zE, spriteU + spriteWidth * xS, spriteV + spriteHeight * zE, facing, quadBuffer);
                    CableModel.addVertex(xE, yE, zE, spriteU + spriteWidth * xE, spriteV + spriteHeight * zE, facing, quadBuffer);
                    CableModel.addVertex(xE, yE, zS, spriteU + spriteWidth * xE, spriteV + spriteHeight * zS, facing, quadBuffer);
                    isFace = yE == 1.0f;
                    break;
                }
                case NORTH: {
                    if (xS == xE || yS == yE) continue block8;
                    CableModel.addVertex(xS, yS, zS, spriteU + spriteWidth * xS, spriteV + spriteHeight * yS, facing, quadBuffer);
                    CableModel.addVertex(xS, yE, zS, spriteU + spriteWidth * xS, spriteV + spriteHeight * yE, facing, quadBuffer);
                    CableModel.addVertex(xE, yE, zS, spriteU + spriteWidth * xE, spriteV + spriteHeight * yE, facing, quadBuffer);
                    CableModel.addVertex(xE, yS, zS, spriteU + spriteWidth * xE, spriteV + spriteHeight * yS, facing, quadBuffer);
                    isFace = zS == 0.0f;
                    break;
                }
                case SOUTH: {
                    if (xS == xE || yS == yE) continue block8;
                    CableModel.addVertex(xS, yS, zE, spriteU + spriteWidth * xS, spriteV + spriteHeight * yS, facing, quadBuffer);
                    CableModel.addVertex(xE, yS, zE, spriteU + spriteWidth * xE, spriteV + spriteHeight * yS, facing, quadBuffer);
                    CableModel.addVertex(xE, yE, zE, spriteU + spriteWidth * xE, spriteV + spriteHeight * yE, facing, quadBuffer);
                    CableModel.addVertex(xS, yE, zE, spriteU + spriteWidth * xS, spriteV + spriteHeight * yE, facing, quadBuffer);
                    isFace = zE == 1.0f;
                    break;
                }
                case WEST: {
                    if (yS == yE || zS == zE) continue block8;
                    CableModel.addVertex(xS, yS, zS, spriteU + spriteWidth * zS, spriteV + spriteHeight * yS, facing, quadBuffer);
                    CableModel.addVertex(xS, yS, zE, spriteU + spriteWidth * zE, spriteV + spriteHeight * yS, facing, quadBuffer);
                    CableModel.addVertex(xS, yE, zE, spriteU + spriteWidth * zE, spriteV + spriteHeight * yE, facing, quadBuffer);
                    CableModel.addVertex(xS, yE, zS, spriteU + spriteWidth * zS, spriteV + spriteHeight * yE, facing, quadBuffer);
                    isFace = xS == 0.0f;
                    break;
                }
                case EAST: {
                    if (yS == yE || zS == zE) continue block8;
                    CableModel.addVertex(xE, yS, zS, spriteU + spriteWidth * zS, spriteV + spriteHeight * yS, facing, quadBuffer);
                    CableModel.addVertex(xE, yE, zS, spriteU + spriteWidth * zS, spriteV + spriteHeight * yE, facing, quadBuffer);
                    CableModel.addVertex(xE, yE, zE, spriteU + spriteWidth * zE, spriteV + spriteHeight * yE, facing, quadBuffer);
                    CableModel.addVertex(xE, yS, zE, spriteU + spriteWidth * zE, spriteV + spriteHeight * yS, facing, quadBuffer);
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

