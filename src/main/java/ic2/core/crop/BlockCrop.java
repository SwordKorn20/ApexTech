/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.SoundType
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.Explosion
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.registry.GameRegistry
 */
package ic2.core.crop;

import ic2.core.block.BlockBase;
import ic2.core.crop.TileEntityCrop;
import ic2.core.item.block.ItemBlockIC2;
import ic2.core.ref.BlockName;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockCrop
extends BlockBase {
    public static TileEntityCrop tempStore;

    public BlockCrop(BlockName blockName) {
        super(blockName, Material.PLANTS, ItemBlockIC2.class);
        this.setHardness(0.8f);
        this.setResistance(0.2f);
        this.setSoundType(SoundType.PLANT);
        GameRegistry.registerTileEntity((Class)TileEntityCrop.class, (String)"TECrop");
    }

    public boolean canPlaceBlockAt(World world, BlockPos blockPos) {
        return world.getBlockState(blockPos.down()).getBlock() == Blocks.FARMLAND && super.canPlaceBlockAt(world, blockPos);
    }

    public void neighborChanged(IBlockState state, World world, BlockPos blockPos, Block neighborBlock) {
        super.neighborChanged(state, world, blockPos, neighborBlock);
        if (world.getBlockState(blockPos.down()).getBlock() != Blocks.FARMLAND) {
            world.setBlockToAir(blockPos);
            this.dropBlockAsItem(world, blockPos, state, 0);
        } else {
            TileEntityCrop tileEntityCrop = (TileEntityCrop)world.getTileEntity(blockPos);
            if (tileEntityCrop == null) {
                return;
            }
            tileEntityCrop.onNeighbourChange();
        }
    }

    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, World world, BlockPos blockPos) {
        double d = 0.2;
        return new AxisAlignedBB(d, 0.0, d, 1.0 - d, 0.7, 1.0 - d);
    }

    public void onEntityWalk(World world, BlockPos blockPos, Entity entity) {
        TileEntityCrop tileEntityCrop = (TileEntityCrop)world.getTileEntity(blockPos);
        if (tileEntityCrop == null) {
            return;
        }
        tileEntityCrop.onEntityCollision(entity);
    }

    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    public void breakBlock(World world, BlockPos blockPos, IBlockState blockState) {
        tempStore = (TileEntityCrop)world.getTileEntity(blockPos);
        super.breakBlock(world, blockPos, blockState);
    }

    public void onBlockDestroyedByExplosion(World world, BlockPos blockPos, Explosion explosion) {
        if (tempStore != null) {
            tempStore.onBlockDestroyed();
        }
    }

    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos blockPos) {
        TileEntityCrop tileEntityCrop = (TileEntityCrop)world.getTileEntity(blockPos);
        if (tileEntityCrop == null) {
            return 0;
        }
        return tileEntityCrop.getEmittedLight();
    }

    public void onBlockClicked(World world, BlockPos blockPos, EntityPlayer player) {
        if (world.isRemote) {
            return;
        }
        TileEntityCrop tileEntityCrop = (TileEntityCrop)world.getTileEntity(blockPos);
        if (tileEntityCrop == null) {
            return;
        }
        tileEntityCrop.leftClick(player);
    }

    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }
        TileEntityCrop tileEntityCrop = (TileEntityCrop)world.getTileEntity(pos);
        if (tileEntityCrop == null) {
            return false;
        }
        return tileEntityCrop.rightClick(player, heldItem);
    }
}

