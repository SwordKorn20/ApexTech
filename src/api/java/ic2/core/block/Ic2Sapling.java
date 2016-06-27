/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockBush
 *  net.minecraft.block.IGrowable
 *  net.minecraft.block.SoundType
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.common.EnumPlantType
 *  net.minecraftforge.fml.common.registry.GameRegistry
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block;

import ic2.core.CreativeTabIC2;
import ic2.core.IC2;
import ic2.core.block.BlockBase;
import ic2.core.block.WorldGenRubTree;
import ic2.core.item.block.ItemBlockIC2;
import ic2.core.ref.BlockName;
import ic2.core.ref.IBlockModelProvider;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Ic2Sapling
extends BlockBush
implements IBlockModelProvider,
IGrowable {
    public Ic2Sapling() {
        this.setHardness(0.0f);
        this.setSoundType(SoundType.PLANT);
        this.setUnlocalizedName(BlockName.sapling.name());
        this.setCreativeTab((CreativeTabs)IC2.tabIC2);
        GameRegistry.registerBlock((Block)this, (Class)ItemBlockIC2.class, (String)BlockName.sapling.name());
        BlockName.sapling.setInstance(this);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public void registerModels(BlockName name) {
        BlockBase.registerDefaultItemModel((Block)this);
    }

    public String getUnlocalizedName() {
        return "ic2." + super.getUnlocalizedName().substring(5) + ".rubber";
    }

    public boolean canBeReplacedByLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
        return true;
    }

    public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {
        if (world.isRemote) {
            return;
        }
        if (!this.canBlockStay(world, pos, state)) {
            this.dropBlockAsItem(world, pos, state, 0);
            world.setBlockToAir(pos);
            return;
        }
        if (world.getLightFromNeighbors(pos.up()) >= 9 && random.nextInt(30) == 0) {
            this.grow(world, random, pos, state);
        }
    }

    public void grow(World world, Random rand, BlockPos pos, IBlockState state) {
        new WorldGenRubTree(true).grow(world, pos, rand);
    }

    public int damageDropped(IBlockState state) {
        return 0;
    }

    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        list.add(new ItemStack(item));
    }

    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
        return true;
    }

    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        return true;
    }

    public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
        return EnumPlantType.Plains;
    }
}

