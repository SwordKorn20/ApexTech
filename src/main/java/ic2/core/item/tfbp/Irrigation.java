/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockFire
 *  net.minecraft.block.BlockGrass
 *  net.minecraft.block.BlockSand
 *  net.minecraft.block.BlockTallGrass
 *  net.minecraft.block.BlockTallGrass$EnumType
 *  net.minecraft.block.IGrowable
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyEnum
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraft.world.storage.WorldInfo
 */
package ic2.core.item.tfbp;

import ic2.core.block.machine.tileentity.TileEntityTerra;
import ic2.core.item.tfbp.TerraformerBase;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.IGrowable;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;

class Irrigation
extends TerraformerBase {
    Irrigation() {
    }

    @Override
    boolean terraform(World world, BlockPos pos) {
        if (world.rand.nextInt(48000) == 0) {
            world.getWorldInfo().setRaining(true);
            return true;
        }
        if ((pos = TileEntityTerra.getFirstBlockFrom(world, pos, 10)) == null) {
            return false;
        }
        if (TileEntityTerra.switchGround(world, pos, (Block)Blocks.SAND, Blocks.DIRT.getDefaultState(), true)) {
            TileEntityTerra.switchGround(world, pos, (Block)Blocks.SAND, Blocks.DIRT.getDefaultState(), true);
            return true;
        }
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if (block instanceof IGrowable && ((IGrowable)block).canGrow(world, pos, state, false)) {
            ((IGrowable)block).grow(world, world.rand, pos, state);
            return true;
        }
        if (block == Blocks.TALLGRASS) {
            return Irrigation.spreadGrass(world, pos.north()) || Irrigation.spreadGrass(world, pos.east()) || Irrigation.spreadGrass(world, pos.south()) || Irrigation.spreadGrass(world, pos.west());
        }
        if (block == Blocks.LOG || block == Blocks.LOG2) {
            BlockPos above = pos.up();
            world.setBlockState(above, state);
            IBlockState leaves = Irrigation.getLeaves(world, pos);
            if (leaves != null) {
                Irrigation.createLeaves(world, above, leaves);
            }
            return true;
        }
        if (block == Blocks.FIRE) {
            world.setBlockToAir(pos);
            return true;
        }
        return false;
    }

    private static IBlockState getLeaves(World world, BlockPos pos) {
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            BlockPos cPos = pos.offset(facing);
            IBlockState state = world.getBlockState(cPos);
            if (!state.getBlock().isLeaves(state, (IBlockAccess)world, cPos)) continue;
            return state;
        }
        return null;
    }

    private static void createLeaves(World world, BlockPos pos, IBlockState state) {
        BlockPos above = pos.up();
        if (world.isAirBlock(above)) {
            world.setBlockState(above, state);
        }
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            BlockPos cPos = pos.offset(facing);
            if (!world.isAirBlock(cPos)) continue;
            world.setBlockState(cPos, state);
        }
    }

    private static boolean spreadGrass(World world, BlockPos pos) {
        if (world.rand.nextBoolean()) {
            return false;
        }
        if ((pos = TileEntityTerra.getFirstBlockFrom(world, pos, 0)) == null) {
            return false;
        }
        Block block = world.getBlockState(pos).getBlock();
        if (block == Blocks.DIRT) {
            world.setBlockState(pos, Blocks.GRASS.getDefaultState());
            return true;
        }
        if (block == Blocks.GRASS) {
            world.setBlockState(pos.up(), Blocks.TALLGRASS.getDefaultState().withProperty((IProperty)BlockTallGrass.TYPE, (Comparable)BlockTallGrass.EnumType.GRASS));
            return true;
        }
        return false;
    }
}

