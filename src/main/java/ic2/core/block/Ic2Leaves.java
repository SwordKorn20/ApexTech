/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockLeaves
 *  net.minecraft.block.BlockPlanks
 *  net.minecraft.block.BlockPlanks$EnumType
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyBool
 *  net.minecraft.block.properties.PropertyEnum
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.renderer.block.statemap.IStateMapper
 *  net.minecraft.client.renderer.block.statemap.StateMap
 *  net.minecraft.client.renderer.block.statemap.StateMap$Builder
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.BlockRenderLayer
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.IStringSerializable
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
import ic2.core.block.BlockBase;
import ic2.core.item.block.ItemIc2Leaves;
import ic2.core.ref.BlockName;
import ic2.core.ref.IBlockModelProvider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Ic2Leaves
extends BlockLeaves
implements IBlockModelProvider {
    public static final PropertyEnum<LeavesType> typeProperty = PropertyEnum.create((String)"type", (Class)LeavesType.class);
    private static final int checkDecayFlag = 8;
    private static final int decayableFlag = 4;

    public Ic2Leaves() {
        this.setUnlocalizedName(BlockName.leaves.name());
        this.setCreativeTab((CreativeTabs)IC2.tabIC2);
        GameRegistry.registerBlock((Block)this, (Class)ItemIc2Leaves.class, (String)BlockName.leaves.name());
        BlockName.leaves.setInstance(this);
        this.setDefaultState(this.blockState.getBaseState().withProperty((IProperty)CHECK_DECAY, (Comparable)Boolean.valueOf(true)).withProperty((IProperty)DECAYABLE, (Comparable)Boolean.valueOf(true)).withProperty(typeProperty, (Comparable)((Object)LeavesType.rubber)));
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public void registerModels(BlockName name) {
        StateMap mapper = new StateMap.Builder().ignore(new IProperty[]{CHECK_DECAY, DECAYABLE}).build();
        ModelLoader.setCustomStateMapper((Block)this, (IStateMapper)mapper);
        ArrayList<IBlockState> states = new ArrayList<IBlockState>(typeProperty.getAllowedValues().size());
        for (LeavesType type : values) {
            states.add(Ic2Leaves.getDropState(this.getDefaultState().withProperty(typeProperty, (Comparable)((Object)type))));
        }
        BlockBase.registerItemModels((Block)this, states, (IStateMapper)mapper);
    }

    private static IBlockState getDropState(IBlockState state) {
        return state.withProperty((IProperty)CHECK_DECAY, (Comparable)Boolean.valueOf(false)).withProperty((IProperty)DECAYABLE, (Comparable)Boolean.valueOf(false));
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer((Block)this, new IProperty[]{CHECK_DECAY, DECAYABLE, typeProperty});
    }

    public IBlockState getStateFromMeta(int meta) {
        boolean checkDecay = (meta & 8) != 0;
        boolean decayable = (meta & 4) != 0;
        IBlockState ret = this.getDefaultState().withProperty((IProperty)CHECK_DECAY, (Comparable)Boolean.valueOf(checkDecay)).withProperty((IProperty)DECAYABLE, (Comparable)Boolean.valueOf(decayable));
        if ((meta &= 3) < values.length) {
            ret = ret.withProperty(typeProperty, (Comparable)((Object)values[meta]));
        }
        return ret;
    }

    public int getMetaFromState(IBlockState state) {
        int ret = 0;
        if (((Boolean)state.getValue((IProperty)CHECK_DECAY)).booleanValue()) {
            ret |= 8;
        }
        if (((Boolean)state.getValue((IProperty)DECAYABLE)).booleanValue()) {
            ret |= 4;
        }
        return ret |= ((LeavesType)((Object)state.getValue(typeProperty))).ordinal();
    }

    public boolean isOpaqueCube(IBlockState state) {
        return Blocks.LEAVES.isOpaqueCube(state);
    }

    @SideOnly(value=Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return Blocks.LEAVES.getBlockLayer();
    }

    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        BlockPos nPos = pos.offset(side);
        return (!this.isOpaqueCube(state) || world.getBlockState(nPos) != state) && !world.getBlockState(nPos).doesSideBlockRendering(world, nPos, side.getOpposite());
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return ((LeavesType)((Object)state.getValue(typeProperty))).getSapling().getItem();
    }

    public int damageDropped(IBlockState state) {
        return ((LeavesType)((Object)state.getValue(typeProperty))).getSapling().getMetadata();
    }

    protected int getSaplingDropChance(IBlockState state) {
        return ((LeavesType)state.getValue(Ic2Leaves.typeProperty)).saplingDropChance;
    }

    public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
        IBlockState state = Ic2Leaves.getDropState(world.getBlockState(pos));
        return Arrays.asList(new ItemStack[]{new ItemStack((Block)this, 1, this.getMetaFromState(state))});
    }

    @SideOnly(value=Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        IBlockState state = Ic2Leaves.getDropState(this.getDefaultState());
        for (LeavesType type : values) {
            list.add(new ItemStack(item, 1, this.getMetaFromState(state.withProperty(typeProperty, (Comparable)((Object)type)))));
        }
    }

    public BlockPlanks.EnumType getWoodType(int meta) {
        return null;
    }

    public boolean canBeReplacedByLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
        return true;
    }

    public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos) {
        return true;
    }

    public boolean isLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
        return true;
    }

    public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return 30;
    }

    public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return 20;
    }

    public static enum LeavesType implements IStringSerializable
    {
        rubber(35);
        
        public final int saplingDropChance;
        private static final LeavesType[] values;

        private LeavesType(int saplingDropChance) {
            this.saplingDropChance = saplingDropChance;
        }

        public String getName() {
            return this.name();
        }

        public ItemStack getSapling() {
            return new ItemStack(BlockName.sapling.getInstance());
        }

        static {
            values = LeavesType.values();
        }
    }

}

