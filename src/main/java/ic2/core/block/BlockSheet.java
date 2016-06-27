/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 */
package ic2.core.block;

import ic2.core.IC2;
import ic2.core.block.BlockMultiID;
import ic2.core.block.state.IIdProvider;
import ic2.core.block.type.IExtBlockType;
import ic2.core.item.type.MiscResourceType;
import ic2.core.ref.BlockName;
import ic2.core.ref.ItemName;
import ic2.core.util.Ic2BlockPos;
import ic2.core.util.Keyboard;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockSheet
extends BlockMultiID<SheetType> {
    private static final AxisAlignedBB aabb = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.125, 1.0);
    private static final EnumFacing[] positiveHorizontalFacings = new EnumFacing[]{EnumFacing.EAST, EnumFacing.SOUTH};

    public static BlockSheet create() {
        return (BlockSheet)BlockMultiID.create(BlockSheet.class, SheetType.class, new Object[0]);
    }

    public BlockSheet() {
        super(BlockName.sheet, Material.CIRCUITS);
    }

    public boolean isFullCube(IBlockState state) {
        return false;
    }

    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return aabb;
    }

    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, World world, BlockPos pos) {
        switch ((SheetType)this.getType(state)) {
            case resin: {
                return null;
            }
        }
        return super.getCollisionBoundingBox(state, world, pos);
    }

    public boolean canReplace(World world, BlockPos pos, EnumFacing side, ItemStack stack) {
        if (!super.canReplace(world, pos, side, stack)) {
            return false;
        }
        return this.isValidPosition(world, pos, this.getStateFromMeta(stack.getItemDamage()));
    }

    private boolean isValidPosition(World world, BlockPos pos, IBlockState state) {
        switch ((SheetType)this.getType(state)) {
            case resin: {
                return this.isNormalCubeBelow(world, pos);
            }
            case rubber: {
                for (EnumFacing facing : EnumFacing.HORIZONTALS) {
                    state = world.getBlockState(pos.offset(facing));
                    if (state != BlockName.sheet.getBlockState(SheetType.rubber) && !state.getBlock().isNormalCube(state, (IBlockAccess)world, pos)) continue;
                    return true;
                }
                return this.isNormalCubeBelow(world, pos);
            }
        }
        return false;
    }

    private boolean isNormalCubeBelow(World world, BlockPos pos) {
        pos = pos.down();
        IBlockState state = world.getBlockState(pos);
        return state.getBlock().isNormalCube(state, (IBlockAccess)world, pos);
    }

    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block neighborBlock) {
        if (!this.isValidPosition(world, pos, state)) {
            world.setBlockToAir(pos);
            this.dropBlockAsItem(world, pos, state, 0);
        }
    }

    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
        switch ((SheetType)this.getType(state)) {
            case resin: {
                entity.fallDistance = (float)((double)entity.fallDistance * 0.75);
                entity.motionX *= 0.6;
                entity.motionY *= 0.85;
                entity.motionZ *= 0.6;
                break;
            }
            case rubber: {
                if (world.isBlockNormalCube(pos.down(), false)) {
                    return;
                }
                if (entity instanceof EntityLivingBase && !BlockSheet.canSupportWeight(world, pos)) {
                    world.setBlockToAir(pos);
                    return;
                }
                if (entity.motionY > -0.4) break;
                entity.fallDistance = 0.0f;
                entity.motionX *= 1.1;
                entity.motionZ *= 1.1;
                if (entity instanceof EntityLivingBase) {
                    if (entity instanceof EntityPlayer && IC2.keyboard.isJumpKeyDown((EntityPlayer)entity)) {
                        entity.motionY *= -1.3;
                        break;
                    }
                    if (entity instanceof EntityPlayer && ((EntityPlayer)entity).isSneaking()) {
                        entity.motionY *= -0.1;
                        break;
                    }
                    entity.motionY *= -0.8;
                    break;
                }
                entity.motionY *= -0.8;
            }
        }
    }

    private static boolean canSupportWeight(World world, BlockPos pos) {
        int maxRange = 16;
        Ic2BlockPos cPos = new Ic2BlockPos();
        block0 : for (EnumFacing axis : positiveHorizontalFacings) {
            for (int dir = -1; dir <= 1; dir += 2) {
                cPos.set((Vec3i)pos);
                boolean supported = false;
                for (int i = 0; i < 16; ++i) {
                    cPos.move(axis, dir);
                    IBlockState state = cPos.getBlockState((IBlockAccess)world);
                    if (state.getBlock().isNormalCube(state, (IBlockAccess)world, (BlockPos)cPos)) {
                        supported = true;
                        break;
                    }
                    if (state != BlockName.sheet.getBlockState(SheetType.rubber)) break;
                    cPos.moveDown();
                    IBlockState baseState = cPos.getBlockState((IBlockAccess)world);
                    if (baseState.getBlock().isNormalCube(baseState, (IBlockAccess)world, (BlockPos)cPos)) {
                        supported = true;
                        break;
                    }
                    cPos.moveUp();
                }
                if (!supported) continue block0;
                if (dir != 1) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        switch ((SheetType)this.getType(state)) {
            case resin: {
                if (IC2.random.nextInt(5) != 0) {
                    ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
                    ret.add(ItemName.misc_resource.getItemStack(MiscResourceType.resin));
                    return ret;
                }
                return new ArrayList<ItemStack>();
            }
        }
        return super.getDrops(world, pos, state, fortune);
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tabs, List<ItemStack> itemList) {
        itemList.add(this.getItemStack(SheetType.rubber));
    }

    public static enum SheetType implements IIdProvider,
    IExtBlockType
    {
        resin(1.6f, 0.5f),
        rubber(0.8f, 2.0f);
        
        public static SheetType[] values;
        private final float hardness;
        private final float explosionResistance;

        private SheetType(float hardness, float explosionResistance) {
            this.hardness = hardness;
            this.explosionResistance = explosionResistance;
        }

        @Override
        public String getName() {
            return this.name();
        }

        @Override
        public int getId() {
            return this.ordinal();
        }

        @Override
        public float getHardness() {
            return this.hardness;
        }

        @Override
        public float getExplosionResistance() {
            return this.explosionResistance;
        }

        static {
            values = SheetType.values();
        }
    }

}

