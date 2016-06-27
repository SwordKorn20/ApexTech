/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.renderer.block.model.ModelResourceLocation
 *  net.minecraft.client.renderer.block.statemap.IStateMapper
 *  net.minecraft.item.Item
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.client.model.ModelLoader
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core;

import com.google.common.collect.ImmutableList;
import ic2.core.ref.FluidName;
import ic2.core.ref.IFluidModelProvider;
import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Ic2Fluid
extends Fluid
implements IFluidModelProvider {
    private static final ResourceLocation fluidLocation = new ResourceLocation("ic2", "fluid");

    public Ic2Fluid(FluidName name) {
        super(name.getName(), name.getTextureLocation(false), name.getTextureLocation(true));
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public void registerModels(FluidName name) {
        if (!name.getInstance().canBePlacedInWorld()) {
            return;
        }
        final String variant = "type=" + name.name();
        ModelLoader.setCustomStateMapper((Block)this.getBlock(), (IStateMapper)new IStateMapper(){

            public Map<IBlockState, ModelResourceLocation> putStateModelLocations(Block blockIn) {
                IdentityHashMap<IBlockState, ModelResourceLocation> ret = new IdentityHashMap<IBlockState, ModelResourceLocation>();
                ModelResourceLocation loc = new ModelResourceLocation(fluidLocation, variant);
                for (IBlockState state : Ic2Fluid.this.getBlock().getBlockState().getValidStates()) {
                    ret.put(state, loc);
                }
                return ret;
            }
        });
        Item item = Item.getItemFromBlock((Block)this.getBlock());
        if (item != null) {
            ModelLoader.setCustomModelResourceLocation((Item)item, (int)0, (ModelResourceLocation)new ModelResourceLocation(fluidLocation, variant));
        }
    }

    public String getUnlocalizedName() {
        return "ic2." + super.getUnlocalizedName().substring(6);
    }

}

