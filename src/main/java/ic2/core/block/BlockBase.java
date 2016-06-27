/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.renderer.block.model.ModelResourceLocation
 *  net.minecraft.client.renderer.block.statemap.IStateMapper
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.item.EnumRarity
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraftforge.client.model.ModelLoader
 *  net.minecraftforge.fml.common.registry.GameRegistry
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block;

import ic2.core.CreativeTabIC2;
import ic2.core.IC2;
import ic2.core.init.Localization;
import ic2.core.item.block.ItemBlockIC2;
import ic2.core.model.ModelUtil;
import ic2.core.ref.BlockName;
import ic2.core.ref.IBlockModelProvider;
import ic2.core.util.Util;
import java.util.Arrays;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BlockBase
extends Block
implements IBlockModelProvider {
    protected BlockBase(BlockName name, Material material) {
        this(name, material, ItemBlockIC2.class);
    }

    protected BlockBase(BlockName name, Material material, Class<? extends ItemBlock> itemClass) {
        super(material);
        this.setCreativeTab((CreativeTabs)IC2.tabIC2);
        if (name != null) {
            this.setUnlocalizedName(name.name());
            GameRegistry.registerBlock((Block)this, itemClass, (String)name.name());
            name.setInstance(this);
        }
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public void registerModels(BlockName name) {
        BlockBase.registerDefaultItemModel(this);
    }

    @SideOnly(value=Side.CLIENT)
    public static void registerDefaultItemModel(Block block) {
        BlockBase.registerItemModels(block, Arrays.asList(new IBlockState[]{block.getDefaultState()}));
    }

    @SideOnly(value=Side.CLIENT)
    public static void registerItemModels(Block block, Iterable<IBlockState> states) {
        BlockBase.registerItemModels(block, states, null);
    }

    @SideOnly(value=Side.CLIENT)
    public static void registerItemModels(Block block, Iterable<IBlockState> states, IStateMapper mapper) {
        Item item = Item.getItemFromBlock((Block)block);
        if (item == null) {
            return;
        }
        ResourceLocation loc = Util.getName(item);
        if (loc == null) {
            return;
        }
        Map locations = mapper != null ? mapper.putStateModelLocations(block) : null;
        for (IBlockState state : states) {
            ModelResourceLocation location;
            int meta = block.getMetaFromState(state);
            ModelResourceLocation modelResourceLocation = location = locations != null ? (ModelResourceLocation)locations.get((Object)state) : ModelUtil.getModelLocation(loc, state);
            if (location == null) {
                throw new RuntimeException("can't map state " + (Object)state);
            }
            ModelLoader.setCustomModelResourceLocation((Item)item, (int)meta, (ModelResourceLocation)location);
        }
    }

    @SideOnly(value=Side.CLIENT)
    public static void registerDefaultVanillaItemModel(Block block, String path) {
        Item item = Item.getItemFromBlock((Block)block);
        if (item == null) {
            return;
        }
        ResourceLocation loc = Util.getName(item);
        if (loc == null) {
            return;
        }
        path = path == null || path.isEmpty() ? loc.toString() : path + "/" + loc.toString();
        ModelLoader.setCustomModelResourceLocation((Item)item, (int)0, (ModelResourceLocation)new ModelResourceLocation(path, null));
    }

    public String getUnlocalizedName() {
        return "ic2." + super.getUnlocalizedName().substring(5);
    }

    public String getLocalizedName() {
        return Localization.translate(this.getUnlocalizedName());
    }

    public boolean canBeReplacedByLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }

    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.COMMON;
    }
}

