/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.BlockModelShapes
 *  net.minecraft.client.renderer.BlockRendererDispatcher
 *  net.minecraft.client.renderer.ItemModelMesher
 *  net.minecraft.client.renderer.RenderItem
 *  net.minecraft.client.renderer.block.model.IBakedModel
 *  net.minecraft.client.renderer.block.model.ModelManager
 *  net.minecraft.client.renderer.block.model.ModelResourceLocation
 *  net.minecraft.client.renderer.block.statemap.DefaultStateMapper
 *  net.minecraft.util.ResourceLocation
 */
package ic2.core.model;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.util.ResourceLocation;

public class ModelUtil {
    private static final DefaultStateMapper defaultStateMapper = new DefaultStateMapper();

    public static ModelResourceLocation getModelLocation(ResourceLocation loc, IBlockState state) {
        return new ModelResourceLocation(loc, ModelUtil.getVariant(state));
    }

    public static String getVariant(IBlockState state) {
        return defaultStateMapper.getPropertyString((Map)state.getProperties());
    }

    public static IBakedModel getMissingModel() {
        return ModelUtil.getModelManager().getMissingModel();
    }

    public static IBakedModel getModel(ModelResourceLocation loc) {
        return ModelUtil.getModelManager().getModel(loc);
    }

    public static IBakedModel getBlockModel(IBlockState state) {
        return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(state);
    }

    private static ModelManager getModelManager() {
        return Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getModelManager();
    }
}

