/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockBush
 *  net.minecraft.block.BlockGrass
 *  net.minecraft.block.BlockMushroom
 *  net.minecraft.block.BlockMycelium
 *  net.minecraft.block.BlockTallGrass
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.init.Biomes
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.BlockPos$MutableBlockPos
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraft.world.biome.Biome
 */
package ic2.core.item.tfbp;

import ic2.core.block.machine.tileentity.TileEntityTerra;
import ic2.core.item.tfbp.TerraformerBase;
import ic2.core.util.BiomeUtil;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockMushroom;
import net.minecraft.block.BlockMycelium;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

class Mushroom
extends TerraformerBase {
    Mushroom() {
    }

    @Override
    boolean terraform(World world, BlockPos pos) {
        if ((pos = TileEntityTerra.getFirstSolidBlockFrom(world, pos, 20)) == null) {
            return false;
        }
        if (Mushroom.growBlockWithDependancy(world, pos, Blocks.BROWN_MUSHROOM_BLOCK, (Block)Blocks.BROWN_MUSHROOM)) {
            return true;
        }
        return false;
    }

    private static boolean growBlockWithDependancy(World world, BlockPos pos, Block target, Block dependancy) {
        BlockPos.MutableBlockPos cPos = new BlockPos.MutableBlockPos();
        for (int xm = pos.getX() - 1; dependancy != null && xm < pos.getX() + 1; ++xm) {
            block1 : for (int zm = pos.getZ() - 1; zm < pos.getZ() + 1; ++zm) {
                for (int ym = pos.getY() + 5; ym > pos.getY() - 2; --ym) {
                    cPos.setPos(xm, ym, zm);
                    IBlockState state = world.getBlockState((BlockPos)cPos);
                    Block block = state.getBlock();
                    if (dependancy == Blocks.MYCELIUM) {
                        if (block == dependancy || block == Blocks.BROWN_MUSHROOM_BLOCK || block == Blocks.RED_MUSHROOM_BLOCK) continue block1;
                        if (block.isAir(state, (IBlockAccess)world, (BlockPos)cPos) || block != Blocks.DIRT && block != Blocks.GRASS) continue;
                        BlockPos dstPos = new BlockPos((Vec3i)cPos);
                        world.setBlockState(dstPos, dependancy.getDefaultState());
                        BiomeUtil.setBiome(world, dstPos, Biomes.MUSHROOM_ISLAND);
                        return true;
                    }
                    if (dependancy != Blocks.BROWN_MUSHROOM) continue;
                    if (block == Blocks.BROWN_MUSHROOM || block == Blocks.RED_MUSHROOM) continue block1;
                    if (block.isAir(state, (IBlockAccess)world, (BlockPos)cPos) || !Mushroom.growBlockWithDependancy(world, (BlockPos)cPos, (Block)Blocks.BROWN_MUSHROOM, (Block)Blocks.MYCELIUM)) continue;
                    return true;
                }
            }
        }
        if (target == Blocks.BROWN_MUSHROOM) {
            IBlockState state;
            BlockPos above;
            Block block;
            Block base = world.getBlockState(pos).getBlock();
            if (base != Blocks.MYCELIUM) {
                if (base == Blocks.BROWN_MUSHROOM_BLOCK || base == Blocks.RED_MUSHROOM_BLOCK) {
                    world.setBlockState(pos, Blocks.MYCELIUM.getDefaultState());
                } else {
                    return false;
                }
            }
            if (!(block = (state = world.getBlockState(above = pos.up())).getBlock()).isAir(state, (IBlockAccess)world, above) && block != Blocks.TALLGRASS) {
                return false;
            }
            BlockBush shroom = world.rand.nextBoolean() ? Blocks.BROWN_MUSHROOM : Blocks.RED_MUSHROOM;
            world.setBlockState(above, shroom.getDefaultState());
            return true;
        }
        if (target == Blocks.BROWN_MUSHROOM_BLOCK) {
            BlockPos above = pos.up();
            IBlockState state = world.getBlockState(above);
            Block base = state.getBlock();
            if (base != Blocks.BROWN_MUSHROOM && base != Blocks.RED_MUSHROOM) {
                return false;
            }
            if (((BlockMushroom)base).generateBigMushroom(world, above, state, world.rand)) {
                for (int xm2 = pos.getX() - 1; xm2 < pos.getX() + 1; ++xm2) {
                    for (int zm = pos.getZ() - 1; zm < pos.getZ() + 1; ++zm) {
                        cPos.setPos(xm2, above.getY(), zm);
                        Block block = world.getBlockState((BlockPos)cPos).getBlock();
                        if (block != Blocks.BROWN_MUSHROOM && block != Blocks.RED_MUSHROOM) continue;
                        world.setBlockToAir(new BlockPos((Vec3i)cPos));
                    }
                }
                return true;
            }
        }
        return false;
    }
}

