/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.block.model.IBakedModel
 *  net.minecraft.client.renderer.block.model.ItemOverrideList
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.client.renderer.texture.TextureMap
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.world.World
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidStack
 */
package ic2.core.item;

import ic2.core.item.ItemFluidCell;
import ic2.core.model.MaskOverlayModel;
import ic2.core.model.ModelUtil;
import ic2.core.ref.IItemModelProvider;
import ic2.core.ref.ItemName;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class FluidCellModel
extends MaskOverlayModel {
    private static final ResourceLocation baseModelLoc = new ResourceLocation("ic2", "item/cell/fluid_cell_case");
    private static final ResourceLocation maskTextureLoc = new ResourceLocation("ic2", "textures/items/cell/fluid_cell_window.png");
    private final ItemOverrideList overrideHandler;

    public FluidCellModel() {
        super(baseModelLoc, maskTextureLoc, false, -0.1f);
        this.overrideHandler = new ItemOverrideList(Collections.emptyList()){

            public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
                ResourceLocation spriteLoc;
                if (stack == null) {
                    return ModelUtil.getMissingModel();
                }
                FluidStack fs = ((ItemFluidCell)ItemName.fluid_cell.getInstance()).getFluid(stack);
                if (fs == null || (spriteLoc = fs.getFluid().getStill(fs)) == null) {
                    return FluidCellModel.this.get();
                }
                return FluidCellModel.this.get(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(spriteLoc.toString()), fs.getFluid().getColor(fs));
            }
        };
    }

    @Override
    public ItemOverrideList getOverrides() {
        return this.overrideHandler;
    }

}

