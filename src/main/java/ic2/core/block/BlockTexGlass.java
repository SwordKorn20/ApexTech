/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.SoundType
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLiving$SpawnPlacementType
 *  net.minecraft.util.BlockRenderLayer
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block;

import ic2.core.block.BlockMultiID;
import ic2.core.block.state.IIdProvider;
import ic2.core.ref.BlockName;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockTexGlass
extends BlockMultiID<GlassType> {
    public static BlockTexGlass create() {
        return (BlockTexGlass)BlockMultiID.create(BlockTexGlass.class, GlassType.class, new Object[0]);
    }

    private BlockTexGlass() {
        super(BlockName.glass, Material.GLASS);
        this.setHardness(5.0f);
        this.setResistance(180.0f);
        this.setSoundType(SoundType.GLASS);
    }

    public int quantityDropped(Random random) {
        return 0;
    }

    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    public boolean isFullBlock(IBlockState state) {
        return true;
    }

    @SideOnly(value=Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, EntityLiving.SpawnPlacementType type) {
        return false;
    }

    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        if (world.getBlockState(pos.offset(side)).getBlock() == this) {
            return false;
        }
        return super.shouldSideBeRendered(state, world, pos, side);
    }

    public static enum GlassType implements IIdProvider
    {
        reinforced;
        

        private GlassType() {
        }

        @Override
        public String getName() {
            return this.name();
        }

        @Override
        public int getId() {
            return this.ordinal();
        }
    }

}

