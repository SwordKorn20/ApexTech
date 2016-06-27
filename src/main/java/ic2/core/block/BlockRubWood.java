/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.SoundType
 *  net.minecraft.block.material.EnumPushReaction
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyEnum
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumFacing$Axis
 *  net.minecraft.util.IStringSerializable
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.BlockPos$MutableBlockPos
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 */
package ic2.core.block;

import ic2.core.block.BlockBase;
import ic2.core.block.state.IIdProvider;
import ic2.core.item.type.MiscResourceType;
import ic2.core.ref.BlockName;
import ic2.core.ref.ItemName;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRubWood
extends BlockBase {
    public static final PropertyEnum<RubberWoodState> stateProperty = PropertyEnum.create((String)"state", (Class)RubberWoodState.class);

    public BlockRubWood() {
        super(BlockName.rubber_wood, Material.WOOD);
        this.setTickRandomly(true);
        this.setHardness(1.0f);
        this.setSoundType(SoundType.WOOD);
        this.setDefaultState(this.blockState.getBaseState().withProperty(stateProperty, (Comparable)((Object)RubberWoodState.plain_y)));
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer((Block)this, new IProperty[]{stateProperty});
    }

    public IBlockState getStateFromMeta(int meta) {
        if (meta >= 0 && meta < values.length) {
            return this.getDefaultState().withProperty(stateProperty, (Comparable)((Object)values[meta]));
        }
        return this.getDefaultState();
    }

    public int getMetaFromState(IBlockState state) {
        return ((RubberWoodState)((Object)state.getValue(stateProperty))).ordinal();
    }

    public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        IBlockState state = super.onBlockPlaced(world, pos, facing, hitX, hitY, hitZ, meta, placer);
        return state.withProperty(stateProperty, (Comparable)((Object)BlockRubWood.getPlainAxisState(facing.getAxis())));
    }

    private static RubberWoodState getPlainAxisState(EnumFacing.Axis axis) {
        switch (axis) {
            case X: {
                return RubberWoodState.plain_x;
            }
            case Y: {
                return RubberWoodState.plain_y;
            }
            case Z: {
                return RubberWoodState.plain_z;
            }
        }
        throw new IllegalArgumentException("invalid axis: " + (Object)axis);
    }

    public void dropBlockAsItemWithChance(World world, BlockPos pos, IBlockState state, float chance, int fortune) {
        if (world.isRemote) {
            return;
        }
        int count = this.quantityDropped(world.rand);
        for (int j1 = 0; j1 < count; ++j1) {
            if (world.rand.nextFloat() > chance) continue;
            Item item = this.getItemDropped(state, world.rand, fortune);
            if (item != null) {
                BlockRubWood.spawnAsEntity((World)world, (BlockPos)pos, (ItemStack)new ItemStack(item, 1, 0));
            }
            if (((RubberWoodState)((Object)state.getValue(stateProperty))).isPlain() || world.rand.nextInt(6) != 0) continue;
            BlockRubWood.spawnAsEntity((World)world, (BlockPos)pos, (ItemStack)ItemName.misc_resource.getItemStack(MiscResourceType.resin));
        }
    }

    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        int range = 4;
        BlockPos.MutableBlockPos cPos = new BlockPos.MutableBlockPos();
        for (int y = - range; y <= range; ++y) {
            for (int z = - range; z <= range; ++z) {
                for (int x = - range; x <= range; ++x) {
                    cPos.setPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                    IBlockState cState = world.getBlockState((BlockPos)cPos);
                    Block cBlock = cState.getBlock();
                    if (!cBlock.isLeaves(cState, (IBlockAccess)world, (BlockPos)cPos)) continue;
                    cBlock.beginLeavesDecay(cState, world, new BlockPos((Vec3i)cPos));
                }
            }
        }
    }

    public void randomTick(World world, BlockPos pos, IBlockState state, Random random) {
        if (random.nextInt(7) == 0) {
            RubberWoodState rwState = (RubberWoodState)((Object)state.getValue(stateProperty));
            if (!rwState.canRegenerate()) {
                return;
            }
            world.setBlockState(pos, state.withProperty(stateProperty, (Comparable)((Object)rwState.getWet())));
        }
    }

    public EnumPushReaction getMobilityFlag(IBlockState state) {
        RubberWoodState rstate = (RubberWoodState)((Object)state.getValue(stateProperty));
        if (rstate == RubberWoodState.plain_x || rstate == RubberWoodState.plain_y || rstate == RubberWoodState.plain_z) {
            return EnumPushReaction.NORMAL;
        }
        return EnumPushReaction.BLOCK;
    }

    public boolean canSustainLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
        return true;
    }

    public boolean isWood(IBlockAccess world, BlockPos pos) {
        return true;
    }

    public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return 4;
    }

    public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return 20;
    }

    public static enum RubberWoodState implements IStringSerializable
    {
        plain_y(EnumFacing.Axis.Y, null, false),
        plain_x(EnumFacing.Axis.X, null, false),
        plain_z(EnumFacing.Axis.Z, null, false),
        dry_north(EnumFacing.Axis.Y, EnumFacing.NORTH, false),
        dry_south(EnumFacing.Axis.Y, EnumFacing.SOUTH, false),
        dry_west(EnumFacing.Axis.Y, EnumFacing.WEST, false),
        dry_east(EnumFacing.Axis.Y, EnumFacing.EAST, false),
        wet_north(EnumFacing.Axis.Y, EnumFacing.NORTH, true),
        wet_south(EnumFacing.Axis.Y, EnumFacing.SOUTH, true),
        wet_west(EnumFacing.Axis.Y, EnumFacing.WEST, true),
        wet_east(EnumFacing.Axis.Y, EnumFacing.EAST, true);
        
        public final EnumFacing.Axis axis;
        public final EnumFacing facing;
        public final boolean wet;
        private static final RubberWoodState[] values;

        private RubberWoodState(EnumFacing.Axis axis, EnumFacing facing, boolean wet) {
            this.axis = axis;
            this.facing = facing;
            this.wet = wet;
        }

        public String getName() {
            return this.name();
        }

        public boolean isPlain() {
            return this.facing == null;
        }

        public boolean canRegenerate() {
            return !this.isPlain() && !this.wet;
        }

        public RubberWoodState getWet() {
            if (this.isPlain()) {
                return null;
            }
            if (this.wet) {
                return this;
            }
            return values[this.ordinal() + 4];
        }

        public RubberWoodState getDry() {
            if (this.isPlain() || !this.wet) {
                return this;
            }
            return values[this.ordinal() - 4];
        }

        public static RubberWoodState getWet(EnumFacing facing) {
            switch (facing) {
                case NORTH: {
                    return wet_north;
                }
                case SOUTH: {
                    return wet_south;
                }
                case WEST: {
                    return wet_west;
                }
                case EAST: {
                    return wet_east;
                }
            }
            throw new IllegalArgumentException("incompatible facing: facing");
        }

        static {
            values = RubberWoodState.values();
        }
    }

}

