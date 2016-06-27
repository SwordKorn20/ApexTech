/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockDynamicLiquid
 *  net.minecraft.block.BlockSnow
 *  net.minecraft.block.BlockStaticLiquid
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyInteger
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package ic2.core.item.tfbp;

import ic2.core.block.machine.tileentity.TileEntityTerra;
import ic2.core.item.tfbp.TerraformerBase;
import java.util.Collection;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

class Chilling
extends TerraformerBase {
    Chilling() {
    }

    @Override
    boolean terraform(World world, BlockPos pos) {
        if ((pos = TileEntityTerra.getFirstBlockFrom(world, pos, 10)) == null) {
            return false;
        }
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if (block == Blocks.WATER || block == Blocks.FLOWING_WATER) {
            world.setBlockState(pos, Blocks.ICE.getDefaultState());
            return true;
        }
        if (block == Blocks.ICE) {
            BlockPos below = pos.down();
            Block blockBelow = world.getBlockState(below).getBlock();
            if (blockBelow == Blocks.WATER || blockBelow == Blocks.FLOWING_WATER) {
                world.setBlockState(below, Blocks.ICE.getDefaultState());
                return true;
            }
        } else if (block == Blocks.SNOW_LAYER) {
            if (Chilling.isSurroundedBySnow(world, pos)) {
                world.setBlockState(pos, Blocks.SNOW.getDefaultState());
                return true;
            }
            int size = (Integer)state.getValue((IProperty)BlockSnow.LAYERS);
            if (BlockSnow.LAYERS.getAllowedValues().contains(size + 1)) {
                world.setBlockState(pos, state.withProperty((IProperty)BlockSnow.LAYERS, (Comparable)Integer.valueOf(size + 1)));
                return true;
            }
        }
        if (Blocks.SNOW_LAYER.canPlaceBlockAt(world, pos = pos.up()) || block == Blocks.ICE) {
            world.setBlockState(pos, Blocks.SNOW_LAYER.getDefaultState());
            return true;
        }
        return false;
    }

    private static boolean isSurroundedBySnow(World world, BlockPos pos) {
        for (EnumFacing dir : EnumFacing.HORIZONTALS) {
            if (Chilling.isSnowHere(world, pos.offset(dir))) continue;
            return false;
        }
        return true;
    }

    private static boolean isSnowHere(World world, BlockPos pos) {
        int prevY = pos.getY();
        if ((pos = TileEntityTerra.getFirstBlockFrom(world, pos, 16)) == null || prevY > pos.getY()) {
            return false;
        }
        Block block = world.getBlockState(pos).getBlock();
        if (block == Blocks.SNOW || block == Blocks.SNOW_LAYER) {
            return true;
        }
        if (Blocks.SNOW_LAYER.canPlaceBlockAt(world, pos = pos.up()) || block == Blocks.ICE) {
            world.setBlockState(pos, Blocks.SNOW_LAYER.getDefaultState());
        }
        return false;
    }
}

