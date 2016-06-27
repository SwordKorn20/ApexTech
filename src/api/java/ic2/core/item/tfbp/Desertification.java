/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockDynamicLiquid
 *  net.minecraft.block.BlockFire
 *  net.minecraft.block.BlockGrass
 *  net.minecraft.block.BlockLeaves
 *  net.minecraft.block.BlockSand
 *  net.minecraft.block.BlockStaticLiquid
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package ic2.core.item.tfbp;

import ic2.core.block.machine.tileentity.TileEntityTerra;
import ic2.core.item.tfbp.Cultivation;
import ic2.core.item.tfbp.TerraformerBase;
import ic2.core.ref.BlockName;
import ic2.core.ref.IBlockModelProvider;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

class Desertification
extends TerraformerBase {
    Desertification() {
    }

    @Override
    boolean terraform(World world, BlockPos pos) {
        if ((pos = TileEntityTerra.getFirstBlockFrom(world, pos, 10)) == null) {
            return false;
        }
        IBlockState sand = Blocks.SAND.getDefaultState();
        if (TileEntityTerra.switchGround(world, pos, Blocks.DIRT, sand, false) || TileEntityTerra.switchGround(world, pos, (Block)Blocks.GRASS, sand, false) || TileEntityTerra.switchGround(world, pos, Blocks.FARMLAND, sand, false)) {
            TileEntityTerra.switchGround(world, pos, Blocks.DIRT, sand, false);
            return true;
        }
        Block block = world.getBlockState(pos).getBlock();
        if (block == Blocks.WATER || block == Blocks.FLOWING_WATER || block == Blocks.SNOW_LAYER || block == Blocks.LEAVES || block == Blocks.LEAVES2 || block == BlockName.leaves.getInstance() || Desertification.isPlant(block)) {
            world.setBlockToAir(pos);
            if (Desertification.isPlant(world.getBlockState(pos.up()).getBlock())) {
                world.setBlockToAir(pos.up());
            }
            return true;
        }
        if (block == Blocks.ICE || block == Blocks.SNOW) {
            world.setBlockState(pos, Blocks.FLOWING_WATER.getDefaultState());
            return true;
        }
        if ((block == Blocks.PLANKS || block == Blocks.LOG || block == BlockName.rubber_wood.getInstance()) && world.rand.nextInt(15) == 0) {
            world.setBlockState(pos, Blocks.FIRE.getDefaultState());
            return true;
        }
        return false;
    }

    private static boolean isPlant(Block block) {
        for (IBlockState state : Cultivation.plants) {
            if (state.getBlock() != block) continue;
            return true;
        }
        return false;
    }
}

