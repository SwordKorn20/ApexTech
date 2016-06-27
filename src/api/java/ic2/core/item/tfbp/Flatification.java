/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockBush
 *  net.minecraft.block.BlockFlower
 *  net.minecraft.block.BlockGrass
 *  net.minecraft.block.BlockLeaves
 *  net.minecraft.block.BlockSand
 *  net.minecraft.block.BlockTallGrass
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package ic2.core.item.tfbp;

import ic2.core.block.machine.tileentity.TileEntityTerra;
import ic2.core.item.tfbp.TerraformerBase;
import ic2.core.ref.BlockName;
import ic2.core.ref.IBlockModelProvider;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

class Flatification
extends TerraformerBase {
    static Set<Block> removable = Collections.newSetFromMap(new IdentityHashMap());

    Flatification() {
    }

    @Override
    void init() {
        removable.add(Blocks.SNOW);
        removable.add(Blocks.ICE);
        removable.add((Block)Blocks.GRASS);
        removable.add(Blocks.STONE);
        removable.add(Blocks.GRAVEL);
        removable.add((Block)Blocks.SAND);
        removable.add(Blocks.DIRT);
        removable.add((Block)Blocks.LEAVES);
        removable.add((Block)Blocks.LEAVES2);
        removable.add(Blocks.LOG);
        removable.add((Block)Blocks.TALLGRASS);
        removable.add((Block)Blocks.RED_FLOWER);
        removable.add((Block)Blocks.YELLOW_FLOWER);
        removable.add(Blocks.SAPLING);
        removable.add(Blocks.WHEAT);
        removable.add((Block)Blocks.RED_MUSHROOM);
        removable.add((Block)Blocks.BROWN_MUSHROOM);
        removable.add(Blocks.PUMPKIN);
        removable.add(Blocks.MELON_BLOCK);
        removable.add((Block)BlockName.leaves.getInstance());
        removable.add((Block)BlockName.sapling.getInstance());
        removable.add((Block)BlockName.rubber_wood.getInstance());
    }

    @Override
    boolean terraform(World world, BlockPos pos) {
        BlockPos workPos = TileEntityTerra.getFirstBlockFrom(world, pos, 20);
        if (workPos == null) {
            return false;
        }
        if (world.getBlockState(workPos).getBlock() == Blocks.SNOW_LAYER) {
            workPos = workPos.down();
        }
        if (pos.getY() == workPos.getY()) {
            return false;
        }
        if (workPos.getY() < pos.getY()) {
            world.setBlockState(workPos.up(), Blocks.DIRT.getDefaultState());
            return true;
        }
        if (Flatification.canRemove(world.getBlockState(workPos).getBlock())) {
            world.setBlockToAir(workPos);
            return true;
        }
        return false;
    }

    private static boolean canRemove(Block block) {
        return removable.contains((Object)block);
    }
}

