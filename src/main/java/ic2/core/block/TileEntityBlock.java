/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.EnumDyeColor
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.ITickable
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.world.Explosion
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.FMLCommonHandler
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package ic2.core.block;

import ic2.api.network.INetworkDataProvider;
import ic2.api.network.INetworkUpdateListener;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.IWorldTickCallback;
import ic2.core.Platform;
import ic2.core.TickHandler;
import ic2.core.block.BlockTileEntity;
import ic2.core.block.comp.Components;
import ic2.core.block.comp.RedstoneEmitter;
import ic2.core.block.comp.TileEntityComponent;
import ic2.core.block.state.IIdProvider;
import ic2.core.block.state.Ic2BlockState;
import ic2.core.block.type.ResourceBlock;
import ic2.core.network.NetworkManager;
import ic2.core.ref.BlockName;
import ic2.core.ref.IBlockModelProvider;
import ic2.core.ref.MetaTeBlock;
import ic2.core.ref.TeBlock;
import ic2.core.util.AabbUtil;
import ic2.core.util.Log;
import ic2.core.util.LogCategory;
import ic2.core.util.SideGateway;
import ic2.core.util.Util;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.mutable.MutableObject;

public abstract class TileEntityBlock
extends TileEntity
implements INetworkDataProvider,
INetworkUpdateListener,
ITickable {
    public static final String teBlockName = "teBlk";
    protected static final int lightOpacityTranslucent = 0;
    protected static final int lightOpacityOpaque = 255;
    private static final NBTTagCompound emptyNbt = new NBTTagCompound();
    private static final List<AxisAlignedBB> defaultAabbs = Arrays.asList(new AxisAlignedBB[]{new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)});
    private static final List<TileEntityComponent> emptyComponents = Collections.emptyList();
    private static final Map<Class<?>, TickSubscription> tickSubscriptions = new HashMap();
    private static final byte loadStateInitial = 0;
    private static final byte loadStateQueued = 1;
    private static final byte loadStateLoaded = 2;
    private static final byte loadStateUnloaded = 3;
    protected final TeBlock teBlock;
    private Map<Class<? extends TileEntityComponent>, TileEntityComponent> components;
    private List<TileEntityComponent> updatableComponents;
    private boolean active = false;
    private byte facing = (byte)EnumFacing.DOWN.ordinal();
    private byte loadState = 0;
    private boolean enableWorldTick;

    public static TileEntityBlock instantiate(Class<? extends TileEntityBlock> cls) {
        try {
            return cls.newInstance();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public TileEntityBlock() {
        this.teBlock = TeBlock.get(this.getClass());
    }

    public final BlockTileEntity getBlockType() {
        return (BlockTileEntity)BlockName.te.getInstance();
    }

    public final IBlockState getBlockState() {
        return this.getBlockType().getDefaultState().withProperty(BlockTileEntity.typeProperty, (Comparable)this.teBlock.getMeta(this.getActive())).withProperty(BlockTileEntity.facingProperty, (Comparable)this.getFacing());
    }

    public final void invalidate() {
        if (this.loadState != 3) {
            this.onUnloaded();
        }
        super.invalidate();
    }

    public final void onChunkUnload() {
        if (this.loadState != 3) {
            this.onUnloaded();
        }
        super.onChunkUnload();
    }

    public void validate() {
        super.validate();
        if (this.worldObj == null || this.pos == null) {
            throw new IllegalStateException("no world/pos");
        }
        if (this.loadState != 0 && this.loadState != 3) {
            throw new IllegalStateException("invalid load state: " + this.loadState);
        }
        this.loadState = 1;
        IC2.tickHandler.requestSingleWorldTick(this.worldObj, new IWorldTickCallback(){

            @Override
            public void onTick(World world) {
                if (TileEntityBlock.this.worldObj != null && TileEntityBlock.this.pos != null && !TileEntityBlock.this.isInvalid() && TileEntityBlock.this.loadState == 1 && TileEntityBlock.this.worldObj.isBlockLoaded(TileEntityBlock.this.pos) && TileEntityBlock.this.worldObj.getTileEntity(TileEntityBlock.this.pos) == TileEntityBlock.this) {
                    TileEntityBlock.this.onLoaded();
                }
            }
        });
    }

    public final void onLoad() {
    }

    protected void onLoaded() {
        if (this.loadState != 1) {
            throw new IllegalStateException("invalid load state: " + this.loadState);
        }
        this.loadState = 2;
        this.enableWorldTick = this.requiresWorldTick();
        if (this.components != null) {
            for (TileEntityComponent component : this.components.values()) {
                component.onLoaded();
                if (!component.enableWorldTick()) continue;
                if (this.updatableComponents == null) {
                    this.updatableComponents = new ArrayList<TileEntityComponent>(4);
                }
                this.updatableComponents.add(component);
            }
        }
        if (!this.enableWorldTick && this.updatableComponents == null) {
            this.worldObj.tickableTileEntities.remove(this);
        }
    }

    protected void onUnloaded() {
        if (this.loadState == 3) {
            throw new IllegalStateException("invalid load state: " + this.loadState);
        }
        this.loadState = 3;
        if (this.components != null) {
            for (TileEntityComponent component : this.components.values()) {
                component.onUnloaded();
            }
        }
    }

    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        if (!this.getSupportedFacings().isEmpty()) {
            byte facingValue = nbt.getByte("facing");
            this.facing = facingValue >= 0 && facingValue < EnumFacing.VALUES.length && this.getSupportedFacings().contains((Object)EnumFacing.VALUES[facingValue]) ? facingValue : (!this.getSupportedFacings().isEmpty() ? (byte)this.getSupportedFacings().iterator().next().ordinal() : (byte)EnumFacing.DOWN.ordinal());
        }
        this.active = nbt.getBoolean("active");
        if (this.components != null && nbt.hasKey("components", 10)) {
            NBTTagCompound componentsNbt = nbt.getCompoundTag("components");
            for (String name : componentsNbt.getKeySet()) {
                Object component;
                Class cls = Components.getClass(name);
                if (cls == null || (component = this.getComponent(cls)) == null) {
                    IC2.log.warn(LogCategory.Block, "Can't find component {} while loading {}.", name, this);
                    continue;
                }
                NBTTagCompound componentNbt = componentsNbt.getCompoundTag(name);
                component.readFromNbt(componentNbt);
            }
        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        if (!this.getSupportedFacings().isEmpty()) {
            nbt.setByte("facing", this.facing);
        }
        nbt.setBoolean("active", this.active);
        if (this.components != null) {
            NBTTagCompound componentsNbt = null;
            for (TileEntityComponent component : this.components.values()) {
                NBTTagCompound componentNbt = component.writeToNbt();
                if (componentNbt == null) continue;
                if (componentsNbt == null) {
                    componentsNbt = new NBTTagCompound();
                    nbt.setTag("components", (NBTBase)componentsNbt);
                }
                componentsNbt.setTag(Components.getId(component.getClass()), (NBTBase)componentNbt);
            }
        }
        return nbt;
    }

    public NBTTagCompound getUpdateTag() {
        IC2.network.get(true).sendInitialData(this);
        return emptyNbt;
    }

    public final void update() {
        if (this.loadState != 2) {
            return;
        }
        if (this.updatableComponents != null) {
            for (TileEntityComponent component : this.updatableComponents) {
                component.onWorldTick();
            }
        }
        if (this.enableWorldTick) {
            if (this.worldObj.isRemote) {
                this.updateEntityClient();
            } else {
                this.updateEntityServer();
            }
        }
    }

    @SideOnly(value=Side.CLIENT)
    protected void updateEntityClient() {
    }

    protected void updateEntityServer() {
    }

    @Override
    public List<String> getNetworkedFields() {
        ArrayList<String> ret = new ArrayList<String>(3);
        ret.add("teBlk=" + (this.teBlock == null ? "invalid" : this.teBlock.getName()));
        ret.add("active");
        ret.add("facing");
        return ret;
    }

    @Override
    public void onNetworkUpdate(String field) {
        if (field.equals("active") || field.equals("facing")) {
            this.rerender();
        }
    }

    protected Ic2BlockState.Ic2BlockStateInstance getExtendedState(Ic2BlockState.Ic2BlockStateInstance state) {
        return state;
    }

    public void onPlaced(ItemStack stack, EntityLivingBase placer, EnumFacing facing) {
        if (!this.worldObj.isRemote) {
            // empty if block
        }
        if ((facing = this.getPlacementFacing(placer, facing)) != this.getFacing()) {
            this.setFacing(facing);
        }
        if (this.worldObj.isRemote) {
            this.rerender();
        }
    }

    protected RayTraceResult collisionRayTrace(Vec3d start, Vec3d end) {
        Vec3d startNormalized = start.subtract((double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ());
        double lengthSq = Util.square(end.xCoord - start.xCoord) + Util.square(end.yCoord - start.yCoord) + Util.square(end.zCoord - start.zCoord);
        double lengthInv = 1.0 / Math.sqrt(lengthSq);
        Vec3d direction = new Vec3d((end.xCoord - start.xCoord) * lengthInv, (end.yCoord - start.yCoord) * lengthInv, (end.zCoord - start.zCoord) * lengthInv);
        double minDistanceSq = lengthSq;
        Vec3d minIntersection = null;
        EnumFacing minIntersectionSide = null;
        MutableObject intersectionOut = new MutableObject();
        for (AxisAlignedBB aabb : this.getAabbs(false)) {
            EnumFacing side = AabbUtil.getIntersection(startNormalized, direction, aabb, intersectionOut);
            if (side == null) continue;
            Vec3d intersection = (Vec3d)intersectionOut.getValue();
            double distanceSq = Util.square(intersection.xCoord - startNormalized.xCoord) + Util.square(intersection.yCoord - startNormalized.yCoord) + Util.square(intersection.zCoord - startNormalized.zCoord);
            if (distanceSq >= minDistanceSq) continue;
            minDistanceSq = distanceSq;
            minIntersection = intersection;
            minIntersectionSide = side;
        }
        if (minIntersection == null) {
            return null;
        }
        return new RayTraceResult(minIntersection.addVector((double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ()), minIntersectionSide, this.pos);
    }

    public AxisAlignedBB getVisualBoundingBox() {
        return this.getAabb(false);
    }

    protected AxisAlignedBB getPhysicsBoundingBox() {
        return this.getAabb(true);
    }

    protected AxisAlignedBB getOutlineBoundingBox() {
        return this.getVisualBoundingBox();
    }

    protected void addCollisionBoxesToList(AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
        AxisAlignedBB maskNormalized = mask.offset((double)(- this.pos.getX()), (double)(- this.pos.getY()), (double)(- this.pos.getZ()));
        for (AxisAlignedBB aabb : this.getAabbs(true)) {
            if (!aabb.intersectsWith(maskNormalized)) continue;
            list.add(aabb.offset(this.pos));
        }
    }

    private AxisAlignedBB getAabb(boolean forCollision) {
        List<AxisAlignedBB> aabbs = this.getAabbs(forCollision);
        if (aabbs.isEmpty()) {
            throw new RuntimeException("No AABBs for " + this);
        }
        if (aabbs.size() == 1) {
            return aabbs.get(0);
        }
        double zS = Double.POSITIVE_INFINITY;
        double yS = Double.POSITIVE_INFINITY;
        double xS = Double.POSITIVE_INFINITY;
        double zE = Double.NEGATIVE_INFINITY;
        double yE = Double.NEGATIVE_INFINITY;
        double xE = Double.NEGATIVE_INFINITY;
        for (AxisAlignedBB aabb : aabbs) {
            xS = Math.min(xS, aabb.minX);
            yS = Math.min(yS, aabb.minY);
            zS = Math.min(zS, aabb.minZ);
            xE = Math.max(xE, aabb.maxX);
            yE = Math.max(yE, aabb.maxY);
            zE = Math.max(zE, aabb.maxZ);
        }
        return new AxisAlignedBB(xS, yS, zS, xE, yE, zE);
    }

    protected void onEntityCollision(Entity entity) {
    }

    protected boolean doesSideBlockRendering(EnumFacing side) {
        return TileEntityBlock.checkSide(this.getAabbs(false), side, false);
    }

    private static boolean checkSide(List<AxisAlignedBB> aabbs, EnumFacing side, boolean strict) {
        if (aabbs == defaultAabbs) {
            return true;
        }
        int dx = side.getFrontOffsetX();
        int dy = side.getFrontOffsetY();
        int dz = side.getFrontOffsetZ();
        int xS = (dx + 1) / 2;
        int yS = (dy + 1) / 2;
        int zS = (dz + 1) / 2;
        int xE = (dx + 2) / 2;
        int yE = (dy + 2) / 2;
        int zE = (dz + 2) / 2;
        if (strict) {
            for (AxisAlignedBB aabb : aabbs) {
                switch (side) {
                    case DOWN: {
                        if (aabb.minY >= 0.0) break;
                        return false;
                    }
                    case UP: {
                        if (aabb.maxY <= 1.0) break;
                        return false;
                    }
                    case NORTH: {
                        if (aabb.minZ >= 0.0) break;
                        return false;
                    }
                    case SOUTH: {
                        if (aabb.maxZ <= 1.0) break;
                        return false;
                    }
                    case WEST: {
                        if (aabb.minX >= 0.0) break;
                        return false;
                    }
                    case EAST: {
                        if (aabb.maxX <= 1.0) break;
                        return false;
                    }
                }
            }
        }
        for (AxisAlignedBB aabb : aabbs) {
            if (aabb.minX > (double)xS || aabb.minY > (double)yS || aabb.minZ > (double)zS || aabb.maxX < (double)xE || aabb.maxY < (double)yE || aabb.maxZ < (double)zE) continue;
            return true;
        }
        return false;
    }

    protected boolean isNormalCube() {
        List<AxisAlignedBB> aabbs = this.getAabbs(false);
        if (aabbs == defaultAabbs) {
            return true;
        }
        if (aabbs.size() != 1) {
            return false;
        }
        AxisAlignedBB aabb = aabbs.get(0);
        return aabb.minX <= 0.0 && aabb.minY <= 0.0 && aabb.minZ <= 0.0 && aabb.maxX >= 1.0 && aabb.maxY >= 1.0 && aabb.maxZ >= 1.0;
    }

    protected boolean isSideSolid(EnumFacing side) {
        return TileEntityBlock.checkSide(this.getAabbs(false), side, true);
    }

    protected int getLightOpacity() {
        return this.isNormalCube() ? 255 : 0;
    }

    protected int getLightValue() {
        return 0;
    }

    protected boolean onActivated(EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (this instanceof IHasGui) {
            if (!this.worldObj.isRemote) {
                return IC2.platform.launchGui(player, (IHasGui)((Object)this));
            }
            return true;
        }
        return false;
    }

    protected void onClicked(EntityPlayer player) {
    }

    protected void onNeighborChange(Block neighbor) {
        if (this.components != null) {
            for (TileEntityComponent component : this.components.values()) {
                component.onNeighborChange(neighbor);
            }
        }
    }

    protected int getStrongPower(EnumFacing side) {
        return 0;
    }

    protected int getWeakPower(EnumFacing side) {
        RedstoneEmitter rs = (RedstoneEmitter)this.getComponent(RedstoneEmitter.class);
        if (rs == null) {
            return 0;
        }
        return rs.getLevel();
    }

    protected boolean connectRedstone(EnumFacing side) {
        return this.getWeakPower(side) > 0;
    }

    protected int getComparatorInputOverride() {
        return 0;
    }

    protected boolean recolor(EnumFacing side, EnumDyeColor mcColor) {
        return false;
    }

    protected void onExploded(Explosion explosion) {
    }

    protected void onBlockBreak() {
    }

    protected boolean onRemovedByPlayer(EntityPlayer player, boolean willHarvest) {
        return true;
    }

    protected ItemStack getPickBlock(EntityPlayer player, RayTraceResult target) {
        return this.getBlockType().getItemStack(this.teBlock);
    }

    protected boolean canHarvest(EntityPlayer player, boolean defaultValue) {
        return defaultValue;
    }

    protected List<ItemStack> getSelfDrops(int fortune, boolean wrench) {
        ItemStack drop = this.getPickBlock(null, null);
        if ((drop = this.adjustDrop(drop, wrench)) == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(new ItemStack[]{drop});
    }

    protected List<ItemStack> getAuxDrops(int fortune) {
        return Collections.emptyList();
    }

    protected float getHardness() {
        return this.teBlock.hardness;
    }

    protected float getExplosionResistance(Entity exploder, Explosion explosion) {
        return this.teBlock.explosionResistance;
    }

    protected boolean canEntityDestroy(Entity entity) {
        return true;
    }

    public EnumFacing getFacing() {
        return EnumFacing.VALUES[this.facing];
    }

    protected boolean setFacingWrench(EnumFacing facing, EntityPlayer player) {
        if (!this.teBlock.allowWrenchRotating) {
            return false;
        }
        if (facing == this.getFacing()) {
            return false;
        }
        if (!this.getSupportedFacings().contains((Object)facing)) {
            return false;
        }
        this.setFacing(facing);
        return true;
    }

    protected boolean wrenchCanRemove(EntityPlayer player) {
        return true;
    }

    protected List<ItemStack> getWrenchDrops(EntityPlayer player, int fortune) {
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        ret.addAll(this.getSelfDrops(fortune, true));
        ret.addAll(this.getAuxDrops(fortune));
        return ret;
    }

    protected EnumFacing getPlacementFacing(EntityLivingBase placer, EnumFacing facing) {
        Set<EnumFacing> supportedFacings = this.getSupportedFacings();
        if (supportedFacings.isEmpty()) {
            return EnumFacing.DOWN;
        }
        if (placer == null) {
            return this.getSupportedFacings().iterator().next();
        }
        Vec3d dir = placer.getLookVec();
        EnumFacing bestFacing = null;
        double maxMatch = Double.NEGATIVE_INFINITY;
        for (EnumFacing cFacing : supportedFacings) {
            double match = dir.dotProduct(new Vec3d(cFacing.getOpposite().getDirectionVec()));
            if (match <= maxMatch) continue;
            maxMatch = match;
            bestFacing = cFacing;
        }
        return bestFacing;
    }

    protected List<AxisAlignedBB> getAabbs(boolean forCollision) {
        return defaultAabbs;
    }

    protected ItemStack adjustDrop(ItemStack drop, boolean wrench) {
        if (!wrench) {
            switch (this.teBlock.defaultDrop) {
                case Self: {
                    break;
                }
                case None: {
                    drop = null;
                    break;
                }
                case Generator: {
                    drop = this.getBlockType().getItemStack(TeBlock.generator);
                    break;
                }
                case Machine: {
                    drop = BlockName.resource.getItemStack(ResourceBlock.machine);
                    break;
                }
                case AdvMachine: {
                    drop = BlockName.resource.getItemStack(ResourceBlock.advanced_machine);
                }
            }
        }
        return drop;
    }

    protected Set<EnumFacing> getSupportedFacings() {
        return this.teBlock.supportedFacings;
    }

    protected void setFacing(EnumFacing facing) {
        if (facing == null) {
            throw new NullPointerException("null facing");
        }
        if (this.facing == facing.ordinal()) {
            throw new IllegalArgumentException("unchanged facing");
        }
        if (!this.getSupportedFacings().contains((Object)facing)) {
            throw new IllegalArgumentException("invalid facing: " + (Object)facing + ", supported: " + this.getSupportedFacings());
        }
        this.facing = (byte)facing.ordinal();
        if (!this.worldObj.isRemote) {
            IC2.network.get(true).updateTileEntityField(this, "facing");
        }
    }

    public boolean getActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        if (this.active == active) {
            return;
        }
        this.active = active;
        IC2.network.get(true).updateTileEntityField(this, "active");
    }

    protected final <T extends TileEntityComponent> T addComponent(T component) {
        TileEntityComponent prev;
        if (component == null) {
            throw new NullPointerException("null component");
        }
        if (this.components == null) {
            this.components = new IdentityHashMap<Class<? extends TileEntityComponent>, TileEntityComponent>(4);
        }
        if ((prev = this.components.put(component.getClass(), (TileEntityComponent)component)) != null) {
            throw new RuntimeException("conflicting component while adding " + component + ", already used by " + prev + ".");
        }
        return component;
    }

    public boolean hasComponent(Class<? extends TileEntityComponent> cls) {
        if (this.components == null) {
            return false;
        }
        return this.components.containsKey(cls);
    }

    public <T extends TileEntityComponent> T getComponent(Class<T> cls) {
        if (this.components == null) {
            return null;
        }
        return (T)this.components.get(cls);
    }

    public final Iterable<? extends TileEntityComponent> getComponents() {
        if (this.components == null) {
            return emptyComponents;
        }
        return this.components.values();
    }

    protected final void rerender() {
        IBlockState state = this.getBlockState();
        this.worldObj.notifyBlockUpdate(this.pos, state, state, 3);
    }

    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    private final synchronized boolean requiresWorldTick() {
        Class cls = this.getClass();
        TickSubscription subscription = tickSubscriptions.get(cls);
        if (subscription == null) {
            boolean hasUpdateClient = false;
            boolean hasUpdateServer = false;
            boolean isClient = FMLCommonHandler.instance().getSide().isClient();
            while (cls != TileEntityBlock.class && (!hasUpdateClient && isClient || !hasUpdateServer)) {
                boolean found;
                if (!hasUpdateClient && isClient) {
                    found = true;
                    try {
                        cls.getDeclaredMethod("updateEntityClient", new Class[0]);
                    }
                    catch (NoSuchMethodException e) {
                        found = false;
                    }
                    if (found) {
                        hasUpdateClient = true;
                    }
                }
                if (!hasUpdateServer) {
                    found = true;
                    try {
                        cls.getDeclaredMethod("updateEntityServer", new Class[0]);
                    }
                    catch (NoSuchMethodException e) {
                        found = false;
                    }
                    if (found) {
                        hasUpdateServer = true;
                    }
                }
                cls = cls.getSuperclass();
            }
            subscription = hasUpdateClient ? (hasUpdateServer ? TickSubscription.Both : TickSubscription.Client) : (hasUpdateServer ? TickSubscription.Server : TickSubscription.None);
            tickSubscriptions.put(this.getClass(), subscription);
        }
        if (this.worldObj.isRemote) {
            return subscription == TickSubscription.Both || subscription == TickSubscription.Client;
        }
        return subscription == TickSubscription.Both || subscription == TickSubscription.Server;
    }

    private static enum TickSubscription {
        None,
        Client,
        Server,
        Both;
        

        private TickSubscription() {
        }
    }

}

