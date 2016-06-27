/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockLiquid
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyInteger
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.BlockPos$MutableBlockPos
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.world.World
 */
package ic2.core.util;

import java.util.HashSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public class PumpUtil {
    private static int moveUp(World world, BlockPos.MutableBlockPos pos) {
        pos.setPos(pos.getX(), pos.getY() + 1, pos.getZ());
        int newDecay = PumpUtil.getFlowDecay(world, (BlockPos)pos);
        if (newDecay >= 0) {
            return newDecay;
        }
        pos.setPos(pos.getX() + 1, pos.getY(), pos.getZ());
        newDecay = PumpUtil.getFlowDecay(world, (BlockPos)pos);
        if (newDecay >= 0) {
            return newDecay;
        }
        pos.setPos(pos.getX() - 2, pos.getY(), pos.getZ());
        newDecay = PumpUtil.getFlowDecay(world, (BlockPos)pos);
        if (newDecay >= 0) {
            return newDecay;
        }
        pos.setPos(pos.getX() + 1, pos.getY(), pos.getZ() + 1);
        newDecay = PumpUtil.getFlowDecay(world, (BlockPos)pos);
        if (newDecay >= 0) {
            return newDecay;
        }
        pos.setPos(pos.getX(), pos.getY(), pos.getZ() - 2);
        newDecay = PumpUtil.getFlowDecay(world, (BlockPos)pos);
        if (newDecay >= 0) {
            return newDecay;
        }
        pos.setPos(pos.getX(), pos.getY() - 1, pos.getZ() + 1);
        return -1;
    }

    private static int moveSideways(World world, BlockPos.MutableBlockPos pos, int decay) {
        pos.setPos(pos.getX() - 1, pos.getY(), pos.getZ());
        int newDecay = PumpUtil.getFlowDecay(world, (BlockPos)pos);
        if (newDecay >= 0 && newDecay < decay) {
            return newDecay;
        }
        pos.setPos(pos.getX() + 1, pos.getY(), pos.getZ() + 1);
        newDecay = PumpUtil.getFlowDecay(world, (BlockPos)pos);
        if (newDecay >= 0 && newDecay < decay) {
            return newDecay;
        }
        pos.setPos(pos.getX(), pos.getY(), pos.getZ() - 2);
        newDecay = PumpUtil.getFlowDecay(world, (BlockPos)pos);
        if (newDecay >= 0 && newDecay < decay) {
            return newDecay;
        }
        pos.setPos(pos.getX() + 1, pos.getY(), pos.getZ() + 1);
        newDecay = PumpUtil.getFlowDecay(world, (BlockPos)pos);
        if (newDecay >= 0 && newDecay < decay) {
            return newDecay;
        }
        pos.setPos(pos.getX() - 1, pos.getY(), pos.getZ());
        return -1;
    }

    public static BlockPos searchFluidSource(World world, BlockPos startPos) {
        int newDecay;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        pos.setPos(startPos.getX(), startPos.getY(), startPos.getZ());
        int decay = PumpUtil.getFlowDecay(world, (BlockPos)pos);
        for (int i = 0; i < 64 && ((newDecay = PumpUtil.moveUp(world, pos)) >= 0 || (newDecay = PumpUtil.moveSideways(world, pos, decay)) >= 0); ++i) {
            decay = newDecay;
        }
        HashSet<BlockPos> visited = new HashSet<BlockPos>(64);
        for (int i2 = 0; i2 < 64; ++i2) {
            int newDecay2;
            visited.add(new BlockPos((Vec3i)pos));
            pos.setPos(pos.getX() - 1, pos.getY(), pos.getZ());
            if (!visited.contains((Object)pos) && (newDecay2 = PumpUtil.getFlowDecay(world, (BlockPos)pos)) >= 0) {
                if (newDecay2 != 0) continue;
                return pos;
            }
            pos.setPos(pos.getX() + 1, pos.getY(), pos.getZ() + 1);
            if (!visited.contains((Object)pos) && (newDecay2 = PumpUtil.getFlowDecay(world, (BlockPos)pos)) >= 0) {
                if (newDecay2 != 0) continue;
                return pos;
            }
            pos.setPos(pos.getX(), pos.getY(), pos.getZ() - 2);
            if (!visited.contains((Object)pos) && (newDecay2 = PumpUtil.getFlowDecay(world, (BlockPos)pos)) >= 0) {
                if (newDecay2 != 0) continue;
                return pos;
            }
            pos.setPos(pos.getX() + 1, pos.getY(), pos.getZ() + 1);
            if (!visited.contains((Object)pos) && (newDecay2 = PumpUtil.getFlowDecay(world, (BlockPos)pos)) >= 0) {
                if (newDecay2 != 0) continue;
                return pos;
            }
            pos.setPos(pos.getX() - 1, pos.getY(), pos.getZ());
            break;
        }
        BlockPos.MutableBlockPos cPos = new BlockPos.MutableBlockPos();
        for (int ix = -2; ix <= 2; ++ix) {
            for (int iz = -2; iz <= 2; ++iz) {
                cPos.setPos(pos.getX() + ix, pos.getY(), pos.getZ() + iz);
                IBlockState state = world.getBlockState((BlockPos)cPos);
                decay = PumpUtil.getFlowDecay(state);
                if (decay == 0) {
                    return new BlockPos((Vec3i)cPos);
                }
                if (decay >= 1 && decay < 7) {
                    world.setBlockState(new BlockPos((Vec3i)cPos), state.withProperty((IProperty)BlockLiquid.LEVEL, (Comparable)Integer.valueOf(decay + 1)));
                    continue;
                }
                if (decay < 7) continue;
                world.setBlockToAir(new BlockPos((Vec3i)cPos));
            }
        }
        return null;
    }

    protected static int getFlowDecay(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        return PumpUtil.getFlowDecay(state);
    }

    protected static int getFlowDecay(IBlockState state) {
        if (!(state.getBlock() instanceof BlockLiquid)) {
            return -1;
        }
        return (Integer)state.getValue((IProperty)BlockLiquid.LEVEL);
    }

    protected static boolean isExistInArray(int x, int y, int z, int[][] xyz, int end_i) {
        for (int i = 0; i <= end_i; ++i) {
            if (xyz[i][0] != x || xyz[i][1] != y || xyz[i][2] != z) continue;
            return true;
        }
        return false;
    }
}

