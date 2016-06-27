/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.renderer.block.model.BakedQuad
 *  net.minecraft.client.renderer.block.model.IBakedModel
 *  net.minecraft.client.renderer.block.model.ModelResourceLocation
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.common.property.IUnlistedProperty
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block;

import ic2.api.crops.CropCard;
import ic2.api.crops.Crops;
import ic2.core.block.state.Ic2BlockState;
import ic2.core.crop.TileEntityCrop;
import ic2.core.model.AbstractModel;
import ic2.core.model.ModelUtil;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public class CropModel
extends AbstractModel {
    private final Map<ResourceLocation, IBakedModel> models = CropModel.generateModelLocations();

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return this.models.keySet();
    }

    private static Map<ResourceLocation, IBakedModel> generateModelLocations() {
        HashMap<ResourceLocation, IBakedModel> ret = new HashMap<ResourceLocation, IBakedModel>();
        StringBuilder name = new StringBuilder();
        name.append("blocks/crop/");
        int reset0 = name.length();
        for (CropCard cropCard : Crops.instance.getCrops()) {
            name.append(cropCard.getName());
            name.append("_");
            int reset1 = name.length();
            for (int size = 1; size <= cropCard.getMaxSize(); ++size) {
                name.append(size);
                ret.put(new ResourceLocation(cropCard.getOwner().toLowerCase(Locale.ENGLISH), name.toString()), null);
                name.setLength(reset1);
            }
            name.setLength(reset0);
        }
        return ret;
    }

    private static ResourceLocation getModelLocation(String resourceDomain, String cropName, int cropSize) {
        return new ResourceLocation(resourceDomain, "blocks/crop/" + cropName + "_" + cropSize);
    }

    @Override
    public List<BakedQuad> getQuads(IBlockState rawState, EnumFacing side, long rand) {
        if (!(rawState instanceof Ic2BlockState.Ic2BlockStateInstance)) {
            return ModelUtil.getMissingModel().getQuads(rawState, side, rand);
        }
        Ic2BlockState.Ic2BlockStateInstance state = (Ic2BlockState.Ic2BlockStateInstance)rawState;
        if (!state.hasValue(TileEntityCrop.modelProperty)) {
            return ModelUtil.getMissingModel().getQuads((IBlockState)state, side, rand);
        }
        return ModelUtil.getModel(state.getValue(TileEntityCrop.modelProperty)).getQuads((IBlockState)state, side, rand);
    }
}

