/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockBush
 *  net.minecraft.block.BlockCrops
 *  net.minecraft.block.BlockDirectional
 *  net.minecraft.block.BlockDoublePlant
 *  net.minecraft.block.BlockDoublePlant$EnumBlockHalf
 *  net.minecraft.block.BlockDoublePlant$EnumPlantType
 *  net.minecraft.block.BlockFlower
 *  net.minecraft.block.BlockGrass
 *  net.minecraft.block.BlockPlanks
 *  net.minecraft.block.BlockPlanks$EnumType
 *  net.minecraft.block.BlockSand
 *  net.minecraft.block.BlockSapling
 *  net.minecraft.block.BlockTallGrass
 *  net.minecraft.block.BlockTallGrass$EnumType
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyDirection
 *  net.minecraft.block.properties.PropertyEnum
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 */
package ic2.core.item.tfbp;

import com.google.common.collect.ImmutableMap;
import ic2.core.block.machine.tileentity.TileEntityTerra;
import ic2.core.item.tfbp.TerraformerBase;
import ic2.core.ref.BlockName;
import ic2.core.ref.IBlockModelProvider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

class Cultivation
extends TerraformerBase {
    static ArrayList<IBlockState> plants = new ArrayList();

    Cultivation() {
    }

    @Override
    void init() {
        plants.add(Blocks.TALLGRASS.getDefaultState().withProperty((IProperty)BlockTallGrass.TYPE, (Comparable)BlockTallGrass.EnumType.GRASS));
        plants.add(Blocks.TALLGRASS.getDefaultState().withProperty((IProperty)BlockTallGrass.TYPE, (Comparable)BlockTallGrass.EnumType.GRASS));
        plants.add(Blocks.TALLGRASS.getDefaultState().withProperty((IProperty)BlockTallGrass.TYPE, (Comparable)BlockTallGrass.EnumType.FERN));
        plants.add(Blocks.RED_FLOWER.getDefaultState());
        plants.add(Blocks.YELLOW_FLOWER.getDefaultState());
        plants.add(Blocks.DOUBLE_PLANT.getDefaultState().withProperty((IProperty)BlockDoublePlant.VARIANT, (Comparable)BlockDoublePlant.EnumPlantType.GRASS));
        plants.add(Blocks.DOUBLE_PLANT.getDefaultState().withProperty((IProperty)BlockDoublePlant.VARIANT, (Comparable)BlockDoublePlant.EnumPlantType.ROSE));
        plants.add(Blocks.DOUBLE_PLANT.getDefaultState().withProperty((IProperty)BlockDoublePlant.VARIANT, (Comparable)BlockDoublePlant.EnumPlantType.SUNFLOWER));
        for (BlockPlanks.EnumType type : BlockSapling.TYPE.getAllowedValues()) {
            plants.add(Blocks.SAPLING.getDefaultState().withProperty((IProperty)BlockSapling.TYPE, (Comparable)type));
        }
        plants.add(Blocks.WHEAT.getDefaultState());
        plants.add(Blocks.RED_MUSHROOM.getDefaultState());
        plants.add(Blocks.BROWN_MUSHROOM.getDefaultState());
        plants.add(Blocks.PUMPKIN.getDefaultState());
        plants.add(Blocks.MELON_BLOCK.getDefaultState());
        plants.add(BlockName.sapling.getInstance().getDefaultState());
    }

    @Override
    boolean terraform(World world, BlockPos pos) {
        Block block;
        if ((pos = TileEntityTerra.getFirstSolidBlockFrom(world, pos, 10)) == null) {
            return false;
        }
        if (TileEntityTerra.switchGround(world, pos, (Block)Blocks.SAND, Blocks.DIRT.getDefaultState(), true)) {
            return true;
        }
        if (TileEntityTerra.switchGround(world, pos, Blocks.END_STONE, Blocks.DIRT.getDefaultState(), true)) {
            int i = 4;
            while (--i > 0 && TileEntityTerra.switchGround(world, pos, Blocks.END_STONE, Blocks.DIRT.getDefaultState(), true)) {
            }
        }
        if ((block = world.getBlockState(pos).getBlock()) == Blocks.DIRT) {
            world.setBlockState(pos, Blocks.GRASS.getDefaultState());
            return true;
        }
        if (block == Blocks.GRASS) {
            return Cultivation.growPlantsOn(world, pos);
        }
        return false;
    }

    private static boolean growPlantsOn(World world, BlockPos pos) {
        BlockPos above = pos.up();
        IBlockState state = world.getBlockState(above);
        Block block = state.getBlock();
        if (block.isAir(state, (IBlockAccess)world, above) || block == Blocks.TALLGRASS && world.rand.nextInt(4) == 0) {
            IBlockState plant = Cultivation.pickRandomPlant(world.rand);
            if (plant.getProperties().containsKey((Object)BlockDirectional.FACING)) {
                plant = plant.withProperty((IProperty)BlockDirectional.FACING, (Comparable)EnumFacing.HORIZONTALS[world.rand.nextInt(EnumFacing.HORIZONTALS.length)]);
            }
            if (plant.getBlock() instanceof BlockCrops) {
                world.setBlockState(pos, Blocks.FARMLAND.getDefaultState());
            } else if (plant.getBlock() == Blocks.DOUBLE_PLANT) {
                plant = plant.withProperty((IProperty)BlockDoublePlant.HALF, (Comparable)BlockDoublePlant.EnumBlockHalf.LOWER);
                world.setBlockState(above, plant.withProperty((IProperty)BlockDoublePlant.HALF, (Comparable)BlockDoublePlant.EnumBlockHalf.LOWER));
                world.setBlockState(above.up(), plant.withProperty((IProperty)BlockDoublePlant.HALF, (Comparable)BlockDoublePlant.EnumBlockHalf.UPPER));
                return true;
            }
            world.setBlockState(above, plant);
            return true;
        }
        return false;
    }

    private static IBlockState pickRandomPlant(Random random) {
        return plants.get(random.nextInt(plants.size()));
    }
}

