/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyEnum
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.BlockPos$MutableBlockPos
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraft.world.gen.feature.WorldGenerator
 *  net.minecraftforge.common.IPlantable
 */
package ic2.core.block;

import ic2.core.IC2;
import ic2.core.block.BlockRubWood;
import ic2.core.block.Ic2Leaves;
import ic2.core.ref.BlockName;
import ic2.core.ref.IBlockModelProvider;
import ic2.core.util.Log;
import ic2.core.util.LogCategory;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.IPlantable;

public class WorldGenRubTree
extends WorldGenerator {
    public static final int maxHeight = 8;

    public WorldGenRubTree(boolean notify) {
        super(notify);
    }

    public boolean generate(World world, Random random, BlockPos pos) {
        BlockPos.MutableBlockPos cPos = new BlockPos.MutableBlockPos();
        cPos.setPos(pos.getX(), IC2.getWorldHeight(world) - 1, pos.getZ());
        while (world.isAirBlock((BlockPos)cPos) && cPos.getY() > 0) {
            cPos.setPos(cPos.getX(), cPos.getY() - 1, cPos.getZ());
        }
        cPos.setPos(cPos.getX(), cPos.getY() + 1, cPos.getZ());
        return this.grow(world, (BlockPos)cPos, random);
    }

    public boolean grow(World world, BlockPos pos, Random random) {
        if (world == null) {
            IC2.log.warn(LogCategory.General, "RubberTree did not spawn! w=%s.", new Object[]{world});
            return false;
        }
        Object woodBlock = BlockName.rubber_wood.getInstance();
        IBlockState leaves = BlockName.leaves.getInstance().getDefaultState().withProperty(Ic2Leaves.typeProperty, (Comparable)((Object)Ic2Leaves.LeavesType.rubber));
        int treeholechance = 25;
        int height = this.getGrowHeight(world, pos);
        if (height < 2) {
            return false;
        }
        height -= random.nextInt(height / 2 + 1);
        BlockPos.MutableBlockPos tmpPos = new BlockPos.MutableBlockPos();
        for (int cHeight = 0; cHeight < height; ++cHeight) {
            BlockPos cPos = pos.up(cHeight);
            if (random.nextInt(100) <= treeholechance) {
                treeholechance -= 10;
                this.setBlockAndNotifyAdequately(world, cPos, woodBlock.getDefaultState().withProperty(BlockRubWood.stateProperty, (Comparable)((Object)BlockRubWood.RubberWoodState.getWet(EnumFacing.HORIZONTALS[random.nextInt(4)]))));
            } else {
                this.setBlockAndNotifyAdequately(world, cPos, woodBlock.getDefaultState().withProperty(BlockRubWood.stateProperty, (Comparable)((Object)BlockRubWood.RubberWoodState.plain_y)));
            }
            if (height >= 4 && (height >= 7 || cHeight <= 1) && cHeight <= 2) continue;
            for (int cx = pos.getX() - 2; cx <= pos.getX() + 2; ++cx) {
                for (int cz = pos.getZ() - 2; cz <= pos.getZ() + 2; ++cz) {
                    int chance = Math.max(1, cHeight + 4 - height);
                    int dx = Math.abs(cx - pos.getX());
                    int dz = Math.abs(cz - pos.getZ());
                    if (!(dx <= 1 && dz <= 1 || dx <= 1 && random.nextInt(chance) == 0) && (dz > 1 || random.nextInt(chance) != 0)) continue;
                    tmpPos.setPos(cx, pos.getY() + cHeight, cz);
                    if (!world.isAirBlock((BlockPos)tmpPos)) continue;
                    this.setBlockAndNotifyAdequately(world, new BlockPos((Vec3i)tmpPos), leaves);
                }
            }
        }
        for (int i = 0; i <= height / 4 + random.nextInt(2); ++i) {
            tmpPos.setPos(pos.getX(), pos.getY() + height + i, pos.getZ());
            if (!world.isAirBlock((BlockPos)tmpPos)) continue;
            this.setBlockAndNotifyAdequately(world, new BlockPos((Vec3i)tmpPos), leaves);
        }
        return true;
    }

    public int getGrowHeight(World world, BlockPos pos) {
        int height;
        BlockPos below = pos.down();
        IBlockState baseState = world.getBlockState(below);
        Block baseBlock = baseState.getBlock();
        if (baseBlock.isAir(baseState, (IBlockAccess)world, below) || !baseBlock.canSustainPlant(baseState, (IBlockAccess)world, below, EnumFacing.UP, (IPlantable)BlockName.sapling.getInstance()) || !world.isAirBlock(pos.up()) && world.getBlockState(pos.up()).getBlock() != BlockName.sapling.getInstance()) {
            return 0;
        }
        pos = pos.up();
        for (height = 1; world.isAirBlock(pos) && height < 8; ++height) {
            pos = pos.up();
        }
        return height;
    }
}

