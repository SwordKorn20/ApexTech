/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockTorch
 *  net.minecraft.block.SoundType
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyBool
 *  net.minecraft.block.properties.PropertyDirection
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.BlockRenderLayer
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.Explosion
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block;

import ic2.core.block.BlockBase;
import ic2.core.block.EntityDynamite;
import ic2.core.block.EntityStickyDynamite;
import ic2.core.block.MaterialIC2TNT;
import ic2.core.ref.BlockName;
import java.util.Collection;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockDynamite
extends BlockBase {
    public static final IProperty<Boolean> linked = PropertyBool.create((String)"linked");

    public BlockDynamite() {
        super(BlockName.dynamite, MaterialIC2TNT.instance, null);
        this.setTickRandomly(true);
        this.setHardness(0.0f);
        this.setSoundType(SoundType.PLANT);
        this.setCreativeTab(null);
        this.setDefaultState(this.getDefaultState().withProperty(linked, (Comparable)Boolean.valueOf(false)).withProperty((IProperty)BlockTorch.FACING, (Comparable)EnumFacing.UP));
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer((Block)this, new IProperty[]{BlockTorch.FACING, linked});
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return Blocks.TORCH.getDefaultState().withProperty((IProperty)BlockTorch.FACING, state.getValue((IProperty)BlockTorch.FACING)).getBoundingBox(source, pos);
    }

    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @SideOnly(value=Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        for (EnumFacing dir : BlockTorch.FACING.getAllowedValues()) {
            if (!world.isBlockNormalCube(pos.offset(dir.getOpposite()), false)) continue;
            return true;
        }
        return false;
    }

    public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        if (facing == EnumFacing.DOWN || !world.isBlockNormalCube(pos.offset(facing.getOpposite()), false)) {
            for (EnumFacing facing2 : BlockTorch.FACING.getAllowedValues()) {
                if (!world.isBlockNormalCube(pos.offset(facing2.getOpposite()), false)) continue;
                facing = facing2;
                break;
            }
        }
        return this.getDefaultState().withProperty((IProperty)BlockTorch.FACING, (Comparable)facing);
    }

    public int getMetaFromState(IBlockState state) {
        return ((EnumFacing)state.getValue((IProperty)BlockTorch.FACING)).ordinal() << 1 | ((Boolean)state.getValue(linked) != false ? 1 : 0);
    }

    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(linked, (Comparable)Boolean.valueOf((meta & 1) != 0)).withProperty((IProperty)BlockTorch.FACING, (Comparable)EnumFacing.VALUES[meta >> 1]);
    }

    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        this.checkPlacement(world, pos, state);
    }

    public void randomTick(World world, BlockPos pos, IBlockState state, Random random) {
        this.checkPlacement(world, pos, state);
    }

    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block neighborBlock) {
        this.checkPlacement(world, pos, state);
    }

    public int quantityDropped(Random random) {
        return 0;
    }

    public int damageDropped(IBlockState state) {
        return 0;
    }

    public void onBlockDestroyedByExplosion(World world, BlockPos pos, Explosion explosion) {
        this.explode(world, pos, explosion != null ? explosion.getExplosivePlacedBy() : null, true);
    }

    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        if (!world.isRemote) {
            this.explode(world, pos, (EntityLivingBase)player, false);
        }
        return false;
    }

    private void checkPlacement(World world, BlockPos pos, IBlockState state) {
        if (world.isRemote) {
            return;
        }
        if (world.isBlockPowered(pos)) {
            this.explode(world, pos, null, false);
        } else if (!world.isBlockNormalCube(pos.offset(((EnumFacing)state.getValue((IProperty)BlockTorch.FACING)).getOpposite()), false)) {
            world.setBlockToAir(pos);
            this.dropBlockAsItem(world, pos, state, 0);
        }
    }

    private void explode(World world, BlockPos pos, EntityLivingBase player, boolean byExplosion) {
        world.setBlockToAir(pos);
        EntityStickyDynamite entity = new EntityStickyDynamite(world, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (float)pos.getZ() + 0.5f);
        entity.owner = player;
        entity.fuse = byExplosion ? 5 : 40;
        world.spawnEntityInWorld((Entity)entity);
        world.playSound(null, pos, SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0f, 1.0f);
    }

    public RayTraceResult collisionRayTrace(IBlockState state, World world, BlockPos pos, Vec3d start, Vec3d end) {
        return Blocks.TORCH.collisionRayTrace(state, world, pos, start, end);
    }
}

