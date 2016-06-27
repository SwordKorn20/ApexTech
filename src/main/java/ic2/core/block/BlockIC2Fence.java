/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockFence
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyBool
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.renderer.block.model.ModelResourceLocation
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemArmor
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumFacing$Axis
 *  net.minecraft.util.EnumFacing$AxisDirection
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.client.model.ModelLoader
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block;

import ic2.api.item.ItemWrapper;
import ic2.core.IC2;
import ic2.core.block.BlockMultiID;
import ic2.core.block.machine.tileentity.TileEntityMagnetizer;
import ic2.core.block.state.EnumProperty;
import ic2.core.block.state.IIdProvider;
import ic2.core.network.NetworkManager;
import ic2.core.ref.BlockName;
import ic2.core.ref.IBlockModelProvider;
import ic2.core.util.Ic2BlockPos;
import ic2.core.util.Keyboard;
import ic2.core.util.SideGateway;
import ic2.core.util.Util;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockIC2Fence
extends BlockMultiID<IC2FenceType> {
    public static final Map<EnumFacing, IProperty<Boolean>> connectProperties = BlockIC2Fence.getConnectProperties();
    private static final double halfThickness = 0.125;
    private static final double height = 1.5;
    private static final Map<IProperty<Boolean>, AxisAlignedBB> aabbs = BlockIC2Fence.getAabbs();

    public static BlockIC2Fence create() {
        return (BlockIC2Fence)BlockMultiID.create(BlockIC2Fence.class, IC2FenceType.class, new Object[0]);
    }

    private BlockIC2Fence() {
        super(BlockName.fence, Material.IRON);
        IBlockState defaultState = this.blockState.getBaseState().withProperty((IProperty)this.typeProperty, this.typeProperty.getDefault());
        for (IProperty<Boolean> property : connectProperties.values()) {
            defaultState = defaultState.withProperty(property, (Comparable)Boolean.valueOf(false));
        }
        this.setDefaultState(defaultState);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public void registerModels(BlockName name) {
        Item item = Item.getItemFromBlock((Block)this);
        if (item == null) {
            return;
        }
        ResourceLocation loc = Util.getName(item);
        if (loc == null) {
            return;
        }
        for (IBlockState state : this.getTypeStates()) {
            ModelLoader.setCustomModelResourceLocation((Item)item, (int)this.getMetaFromState(state), (ModelResourceLocation)new ModelResourceLocation(loc.toString() + "/" + ((IC2FenceType)((Object)state.getValue((IProperty)this.typeProperty))).getName(), null));
        }
    }

    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        ArrayList<EnumProperty> properties = new ArrayList<EnumProperty>();
        properties.add(this.getTypeProperty());
        properties.addAll(connectProperties.values());
        return new BlockStateContainer((Block)this, properties.toArray((T[])new IProperty[0]));
    }

    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        IBlockState neighborState;
        boolean isPole = true;
        boolean magnetizerConnected = false;
        IBlockState ret = state;
        for (EnumFacing facing2 : EnumFacing.HORIZONTALS) {
            neighborState = world.getBlockState(pos.offset(facing2));
            if (BlockIC2Fence.isFence(neighborState)) {
                isPole = false;
                if (magnetizerConnected) break;
                ret = ret.withProperty(connectProperties.get((Object)facing2), (Comparable)Boolean.valueOf(true));
                continue;
            }
            if (!isPole || BlockIC2Fence.getMagnetizer(world, pos, facing2, state, false) == null) continue;
            magnetizerConnected = true;
            ret = ret.withProperty(connectProperties.get((Object)facing2), (Comparable)Boolean.valueOf(true));
        }
        if (!isPole && magnetizerConnected) {
            ret = state;
            for (EnumFacing facing2 : EnumFacing.HORIZONTALS) {
                neighborState = world.getBlockState(pos.offset(facing2));
                if (!BlockIC2Fence.isFence(neighborState)) continue;
                ret = ret.withProperty(connectProperties.get((Object)facing2), (Comparable)Boolean.valueOf(true));
            }
        }
        return ret;
    }

    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    public boolean isNormalCube(IBlockState state) {
        return false;
    }

    public boolean isBlockSolid(IBlockAccess world, BlockPos blockPos, EnumFacing side) {
        return side.getAxis() == EnumFacing.Axis.Y;
    }

    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity rawEntity) {
        boolean slow;
        if (!(rawEntity instanceof EntityPlayer)) {
            return;
        }
        boolean powered = this.isPowered(world, pos, (IC2FenceType)((Object)state.getValue((IProperty)this.typeProperty)));
        EntityPlayer player = (EntityPlayer)rawEntity;
        boolean metalShoes = BlockIC2Fence.hasMetalShoes(player);
        boolean descending = player.isSneaking();
        boolean bl = slow = player.motionY >= -0.25 || player.motionY < 1.6;
        if (slow) {
            player.fallDistance = 0.0f;
        }
        if (!powered) {
            if (descending && !slow && metalShoes) {
                player.motionY *= 0.9;
            }
        } else if (descending) {
            if (!slow) {
                player.motionY *= 0.8;
            }
        } else {
            player.motionY += 0.075;
            if (player.motionY > 0.0) {
                player.motionY *= 1.03;
            }
            double maxSpeed = IC2.keyboard.isAltKeyDown(player) ? 0.1 : (metalShoes ? 1.5 : 0.5);
            player.motionY = Math.min(player.motionY, maxSpeed);
        }
        if (!world.isRemote) {
            List<TileEntityMagnetizer> magnetizers = this.getMagnetizers((IBlockAccess)world, pos, false);
            for (TileEntityMagnetizer magnetizer : magnetizers) {
                IC2.network.get(true).updateTileEntityField(magnetizer, "energy");
            }
        }
    }

    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB mask, List<AxisAlignedBB> result, Entity collidingEntity) {
        state = this.getActualState(state, (IBlockAccess)world, pos);
        BlockIC2Fence.addCollisionBoxToList((BlockPos)pos, (AxisAlignedBB)mask, result, (AxisAlignedBB)aabbs.get(null));
        for (IProperty<Boolean> property : connectProperties.values()) {
            if (!((Boolean)state.getValue(property)).booleanValue()) continue;
            BlockIC2Fence.addCollisionBoxToList((BlockPos)pos, (AxisAlignedBB)mask, result, (AxisAlignedBB)aabbs.get(property));
        }
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        AxisAlignedBB ret = aabbs.get(null);
        double xS = ret.minX;
        double yS = 0.0;
        double zS = ret.minZ;
        double xE = ret.maxX;
        double yE = 1.0;
        double zE = ret.maxZ;
        state = this.getActualState(state, world, pos);
        for (IProperty<Boolean> property : connectProperties.values()) {
            if (!((Boolean)state.getValue(property)).booleanValue()) continue;
            AxisAlignedBB aabb = aabbs.get(property);
            xS = Math.min(xS, aabb.minX);
            zS = Math.min(zS, aabb.minZ);
            xE = Math.max(xE, aabb.maxX);
            zE = Math.max(zE, aabb.maxZ);
        }
        return new AxisAlignedBB(xS, 0.0, zS, xE, 1.0, zE);
    }

    private static boolean isFence(IBlockState state) {
        return state.getBlock() instanceof BlockIC2Fence || state.getBlock() instanceof BlockFence;
    }

    private static TileEntityMagnetizer getMagnetizer(IBlockAccess world, BlockPos pos, EnumFacing side, IBlockState state, boolean checkPower) {
        if (state.getBlock() != BlockName.te.getInstance()) {
            return null;
        }
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityMagnetizer) {
            TileEntityMagnetizer ret = (TileEntityMagnetizer)te;
            if (side != null && !side.getOpposite().equals((Object)ret.getFacing())) {
                return null;
            }
            if (!checkPower || ret.canBoost()) {
                return ret;
            }
        }
        return null;
    }

    public static boolean hasMetalShoes(EntityPlayer player) {
        Item item;
        ItemStack shoes = player.inventory.armorInventory[0];
        if (shoes != null && ((item = shoes.getItem()) == Items.IRON_BOOTS || item == Items.GOLDEN_BOOTS || item == Items.CHAINMAIL_BOOTS || ItemWrapper.isMetalArmor(shoes, player))) {
            return true;
        }
        return false;
    }

    private boolean isPowered(World world, BlockPos start, IC2FenceType type) {
        if (!type.canBoost) {
            return false;
        }
        List<TileEntityMagnetizer> magnetizers = this.getMagnetizers((IBlockAccess)world, start, true);
        if (magnetizers.isEmpty()) {
            return false;
        }
        double multiplier = 1.0 / (double)magnetizers.size();
        for (TileEntityMagnetizer magnetizer : magnetizers) {
            magnetizer.boost(multiplier);
        }
        return true;
    }

    private List<TileEntityMagnetizer> getMagnetizers(IBlockAccess world, BlockPos start, boolean checkPower) {
        int maxRange = 20;
        ArrayList<TileEntityMagnetizer> ret = new ArrayList<TileEntityMagnetizer>();
        Ic2BlockPos center = new Ic2BlockPos((Vec3i)start);
        Ic2BlockPos tmp = new Ic2BlockPos();
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            Ic2BlockPos nPos = tmp.set((Vec3i)center).move(facing);
            IBlockState state = nPos.getBlockState(world);
            if (BlockIC2Fence.isFence(state)) {
                return Collections.emptyList();
            }
            TileEntityMagnetizer te = BlockIC2Fence.getMagnetizer(world, nPos, facing, state, checkPower);
            if (te == null) continue;
            ret.add(te);
        }
        if (!ret.isEmpty()) {
            return ret;
        }
        int minDir = 0;
        int maxDir = 2;
        for (int dy = 1; dy <= 20; ++dy) {
            boolean abort = false;
            block2 : for (int dir = minDir; dir < maxDir; ++dir) {
                int offset = dir * 2 - 1;
                center.setY(start.getY() + offset * dy);
                IBlockState centerState = center.getBlockState(world);
                if (!(centerState.getBlock() instanceof BlockIC2Fence) || !((IC2FenceType)centerState.getValue((IProperty)this.typeProperty)).canBoost) {
                    if (dir == 0) {
                        minDir = 1;
                    } else {
                        maxDir = 1;
                    }
                    if (minDir != maxDir) break;
                    abort = true;
                    break;
                }
                int oldSize = ret.size();
                for (EnumFacing facing2 : EnumFacing.HORIZONTALS) {
                    Ic2BlockPos nPos = tmp.set((Vec3i)center).move(facing2);
                    IBlockState state = nPos.getBlockState(world);
                    if (BlockIC2Fence.isFence(state)) {
                        if (dir == 0) {
                            minDir = 1;
                        } else {
                            maxDir = 1;
                        }
                        if (minDir == maxDir) {
                            abort = true;
                        }
                        while (ret.size() > oldSize) {
                            ret.remove(ret.size() - 1);
                        }
                        continue block2;
                    }
                    TileEntityMagnetizer te = BlockIC2Fence.getMagnetizer(world, nPos, facing2, state, checkPower);
                    if (te == null) continue;
                    abort = true;
                    ret.add(te);
                }
            }
            if (abort) break;
        }
        return ret;
    }

    private static Map<EnumFacing, IProperty<Boolean>> getConnectProperties() {
        EnumMap<EnumFacing, IProperty<Boolean>> ret = new EnumMap<EnumFacing, IProperty<Boolean>>(EnumFacing.class);
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            ret.put(facing, (IProperty<Boolean>)PropertyBool.create((String)facing.getName()));
        }
        return ret;
    }

    private static Map<IProperty<Boolean>, AxisAlignedBB> getAabbs() {
        IdentityHashMap<IProperty<Boolean>, AxisAlignedBB> ret = new IdentityHashMap<IProperty<Boolean>, AxisAlignedBB>(connectProperties.size() + 1);
        double spaceL = 0.375;
        double spaceR = 0.625;
        ret.put(null, new AxisAlignedBB(0.375, 0.0, 0.375, 0.625, 1.5, 0.625));
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            double end;
            double start;
            if (facing.getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE) {
                start = 0.0;
                end = 0.375;
            } else {
                start = 0.625;
                end = 1.0;
            }
            AxisAlignedBB aabb = facing.getAxis() == EnumFacing.Axis.X ? new AxisAlignedBB(start, 0.0, 0.375, end, 1.5, 0.625) : new AxisAlignedBB(0.375, 0.0, start, 0.625, 1.5, end);
            ret.put(connectProperties.get((Object)facing), aabb);
        }
        return ret;
    }

    public static enum IC2FenceType implements IIdProvider
    {
        iron(true);
        
        public final boolean canBoost;

        private IC2FenceType(boolean canBoost) {
            this.canBoost = canBoost;
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

