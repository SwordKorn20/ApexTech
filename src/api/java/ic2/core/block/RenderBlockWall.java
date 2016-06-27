/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.renderer.block.model.BakedQuad
 *  net.minecraft.client.renderer.block.model.IBakedModel
 *  net.minecraft.util.EnumFacing
 *  net.minecraftforge.client.model.IPerspectiveAwareModel
 *  net.minecraftforge.common.property.IUnlistedProperty
 */
package ic2.core.block;

import ic2.core.block.TileEntityWall;
import ic2.core.block.comp.Obscuration;
import ic2.core.block.state.IIdProvider;
import ic2.core.block.state.Ic2BlockState;
import ic2.core.item.tool.ItemObscurator;
import ic2.core.model.AbstractModel;
import ic2.core.model.BasicBakedBlockModel;
import ic2.core.model.MergedBlockModel;
import ic2.core.model.ModelUtil;
import ic2.core.ref.BlockName;
import ic2.core.util.Ic2Color;
import java.nio.Buffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.common.property.IUnlistedProperty;

public class RenderBlockWall
extends AbstractModel {
    @Override
    public List<BakedQuad> getQuads(IBlockState rawState, EnumFacing side, long rand) {
        if (!(rawState instanceof Ic2BlockState.Ic2BlockStateInstance)) {
            return ModelUtil.getMissingModel().getQuads(rawState, side, rand);
        }
        Ic2BlockState.Ic2BlockStateInstance state = (Ic2BlockState.Ic2BlockStateInstance)rawState;
        if (!state.hasValue(TileEntityWall.renderStateProperty)) {
            return ModelUtil.getMissingModel().getQuads((IBlockState)state, side, rand);
        }
        TileEntityWall.WallRenderState prop = state.getValue(TileEntityWall.renderStateProperty);
        float[][] uvs = new float[6][];
        int[][] colorMultipliers = new int[6][];
        int total = 0;
        for (int i = 0; i < 6; ++i) {
            ItemObscurator.ObscuredRenderInfo renderInfo;
            Obscuration.ObscurationData data = prop.obscurations[i];
            if (data == null || (renderInfo = ItemObscurator.getRenderInfo(data.state, data.side)) == null) continue;
            uvs[i] = renderInfo.uvs;
            colorMultipliers[i] = data.colorMultipliers;
            total += data.colorMultipliers.length;
        }
        IBakedModel baseModel = ModelUtil.getBlockModel(BlockName.wall.getBlockState(prop.color));
        if (total == 0) {
            return baseModel.getQuads((IBlockState)state, side, rand);
        }
        MergedBlockModel mergedModel = RenderBlockWall.generateModel(baseModel, (IBlockState)state, colorMultipliers);
        mergedModel.setSprite(uvs, colorMultipliers);
        return mergedModel.getQuads((IBlockState)state, side, rand);
    }

    private static MergedBlockModel generateModel(IBakedModel baseModel, IBlockState state, int[][] colorMultipliers) {
        float offset = 0.001f;
        List[] mergedQuads = new List[6];
        int[] retextureStart = new int[6];
        IntBuffer buffer = MergedBlockModel.getQuadBuffer();
        for (EnumFacing side : EnumFacing.VALUES) {
            int[] sideColorMultipliers = colorMultipliers[side.ordinal()];
            List baseFaceQuads = baseModel.getQuads(state, side, 0);
            if (sideColorMultipliers == null) {
                mergedQuads[side.ordinal()] = baseFaceQuads;
            } else {
                ArrayList<BakedQuad> mergedFaceQuads = new ArrayList<BakedQuad>(baseFaceQuads.size() + sideColorMultipliers.length);
                mergedFaceQuads.addAll(baseFaceQuads);
                for (int i = 0; i < sideColorMultipliers.length; ++i) {
                    RenderBlockWall.generateQuad(side, 0.001f, buffer);
                    mergedFaceQuads.add(BasicBakedBlockModel.createQuad(Arrays.copyOf(buffer.array(), buffer.position()), side));
                    buffer.rewind();
                }
                mergedQuads[side.ordinal()] = mergedFaceQuads;
            }
            retextureStart[side.ordinal()] = baseFaceQuads.size();
        }
        return new MergedBlockModel((IPerspectiveAwareModel)baseModel, mergedQuads, retextureStart);
    }

    private static void generateQuad(EnumFacing side, float offset, IntBuffer out) {
        int color = -1;
        float neg = - offset;
        float pos = 1.0f + offset;
        switch (side) {
            case DOWN: {
                MergedBlockModel.generateVertex(neg, neg, neg, -1, 0.0f, 0.0f, out);
                MergedBlockModel.generateVertex(pos, neg, neg, -1, 1.0f, 0.0f, out);
                MergedBlockModel.generateVertex(pos, neg, pos, -1, 1.0f, 1.0f, out);
                MergedBlockModel.generateVertex(neg, neg, pos, -1, 0.0f, 1.0f, out);
                break;
            }
            case UP: {
                MergedBlockModel.generateVertex(neg, pos, neg, -1, 0.0f, 0.0f, out);
                MergedBlockModel.generateVertex(neg, pos, pos, -1, 0.0f, 1.0f, out);
                MergedBlockModel.generateVertex(pos, pos, pos, -1, 1.0f, 1.0f, out);
                MergedBlockModel.generateVertex(pos, pos, neg, -1, 1.0f, 0.0f, out);
                break;
            }
            case NORTH: {
                MergedBlockModel.generateVertex(neg, neg, neg, -1, 0.0f, 0.0f, out);
                MergedBlockModel.generateVertex(neg, pos, neg, -1, 0.0f, 1.0f, out);
                MergedBlockModel.generateVertex(pos, pos, neg, -1, 1.0f, 1.0f, out);
                MergedBlockModel.generateVertex(pos, neg, neg, -1, 1.0f, 0.0f, out);
                break;
            }
            case SOUTH: {
                MergedBlockModel.generateVertex(neg, neg, pos, -1, 0.0f, 0.0f, out);
                MergedBlockModel.generateVertex(pos, neg, pos, -1, 1.0f, 0.0f, out);
                MergedBlockModel.generateVertex(pos, pos, pos, -1, 1.0f, 1.0f, out);
                MergedBlockModel.generateVertex(neg, pos, pos, -1, 0.0f, 1.0f, out);
                break;
            }
            case WEST: {
                MergedBlockModel.generateVertex(neg, neg, neg, -1, 0.0f, 0.0f, out);
                MergedBlockModel.generateVertex(neg, neg, pos, -1, 1.0f, 0.0f, out);
                MergedBlockModel.generateVertex(neg, pos, pos, -1, 1.0f, 1.0f, out);
                MergedBlockModel.generateVertex(neg, pos, neg, -1, 0.0f, 1.0f, out);
                break;
            }
            case EAST: {
                MergedBlockModel.generateVertex(pos, neg, neg, -1, 0.0f, 0.0f, out);
                MergedBlockModel.generateVertex(pos, pos, neg, -1, 0.0f, 1.0f, out);
                MergedBlockModel.generateVertex(pos, pos, pos, -1, 1.0f, 1.0f, out);
                MergedBlockModel.generateVertex(pos, neg, pos, -1, 1.0f, 0.0f, out);
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }

}

