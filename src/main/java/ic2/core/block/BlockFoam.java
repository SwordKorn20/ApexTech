/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockSand
 *  net.minecraft.block.SoundType
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLiving$SpawnPlacementType
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 */
package ic2.core.block;

import ic2.core.block.BlockMultiID;
import ic2.core.block.BlockScaffold;
import ic2.core.block.BlockWall;
import ic2.core.block.state.EnumProperty;
import ic2.core.block.state.IIdProvider;
import ic2.core.block.type.ResourceBlock;
import ic2.core.ref.BlockName;
import ic2.core.util.Ic2Color;
import ic2.core.util.StackUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSand;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFoam
extends BlockMultiID<FoamType> {
    public static BlockFoam create() {
        return (BlockFoam)BlockMultiID.create(BlockFoam.class, FoamType.class, new Object[0]);
    }

    private BlockFoam() {
        super(BlockName.foam, Material.CLOTH);
        this.setTickRandomly(true);
        this.setHardness(0.01f);
        this.setResistance(10.0f);
        this.setSoundType(SoundType.CLOTH);
    }

    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
        return true;
    }

    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos) {
        return null;
    }

    public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return false;
    }

    public void randomTick(World world, BlockPos pos, IBlockState state, Random random) {
        FoamType type = (FoamType)((Object)state.getValue((IProperty)this.typeProperty));
        float chance = BlockFoam.getHardenChance(world, pos, state, type) * 4096.0f / 3.0f;
        if (random.nextFloat() < chance) {
            world.setBlockState(pos, ((FoamType)((Object)state.getValue((IProperty)this.typeProperty))).getResult());
        }
    }

    public static float getHardenChance(World world, BlockPos pos, IBlockState state, FoamType type) {
        int light = world.getLightFromNeighbors(pos);
        if (!state.useNeighborBrightness() && state.getBlock().getLightOpacity(state, (IBlockAccess)world, pos) == 0) {
            for (EnumFacing side : EnumFacing.VALUES) {
                light = Math.max(light, world.getLight(pos.offset(side), false));
            }
        }
        int avgTime = type.hardenTime * (16 - light);
        return 1.0f / (float)(avgTime * 20);
    }

    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (StackUtil.consumeFromPlayerHand(player, StackUtil.sameItem((Block)Blocks.SAND), 1) != null) {
            world.setBlockState(pos, ((FoamType)((Object)state.getValue((IProperty)this.typeProperty))).getResult());
            return true;
        }
        return false;
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        return ((FoamType)((Object)state.getValue((IProperty)this.typeProperty))).getDrops();
    }

    public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, EntityLiving.SpawnPlacementType type) {
        return false;
    }

    public static enum FoamType implements IIdProvider
    {
        normal(300),
        reinforced(600);
        
        public final int hardenTime;

        private FoamType(int hardenTime) {
            this.hardenTime = hardenTime;
        }

        @Override
        public String getName() {
            return this.name();
        }

        @Override
        public int getId() {
            return this.ordinal();
        }

        public List<ItemStack> getDrops() {
            switch (this) {
                case normal: {
                    return new ArrayList<ItemStack>();
                }
                case reinforced: {
                    ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
                    ret.add(BlockName.scaffold.getItemStack(BlockScaffold.ScaffoldType.iron));
                    return ret;
                }
            }
            throw new UnsupportedOperationException();
        }

        public IBlockState getResult() {
            switch (this) {
                case normal: {
                    return BlockName.wall.getBlockState(BlockWall.defaultColor);
                }
                case reinforced: {
                    return BlockName.resource.getBlockState(ResourceBlock.reinforced_stone);
                }
            }
            throw new UnsupportedOperationException();
        }
    }

}

