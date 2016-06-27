/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockSand
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.item.EnumDyeColor
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.world.Explosion
 *  net.minecraft.world.World
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.common.property.IUnlistedProperty
 *  net.minecraftforge.fml.common.eventhandler.Event
 *  net.minecraftforge.fml.common.eventhandler.EventBus
 */
package ic2.core.block.wiring;

import ic2.api.energy.EnergyNet;
import ic2.api.energy.IEnergyNet;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergyConductor;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergyTile;
import ic2.api.network.INetworkTileEntityEventListener;
import ic2.core.IC2;
import ic2.core.IWorldTickCallback;
import ic2.core.Platform;
import ic2.core.TickHandler;
import ic2.core.block.BlockFoam;
import ic2.core.block.BlockTileEntity;
import ic2.core.block.BlockWall;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.TileEntityWall;
import ic2.core.block.comp.Obscuration;
import ic2.core.block.comp.TileEntityComponent;
import ic2.core.block.state.IIdProvider;
import ic2.core.block.state.Ic2BlockState;
import ic2.core.block.state.UnlistedProperty;
import ic2.core.block.wiring.CableFoam;
import ic2.core.block.wiring.CableType;
import ic2.core.item.block.ItemCable;
import ic2.core.item.tool.ItemToolCutter;
import ic2.core.item.type.CraftingItemType;
import ic2.core.network.NetworkManager;
import ic2.core.ref.BlockName;
import ic2.core.ref.IBlockModelProvider;
import ic2.core.ref.ItemName;
import ic2.core.ref.TeBlock;
import ic2.core.util.Ic2Color;
import ic2.core.util.SideGateway;
import ic2.core.util.StackUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSand;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventBus;

public class TileEntityCable
extends TileEntityBlock
implements IEnergyConductor,
INetworkTileEntityEventListener {
    public static final float insulationThickness = 0.0625f;
    public static final IUnlistedProperty<CableRenderState> renderStateProperty = new UnlistedProperty<CableRenderState>("renderstate", CableRenderState.class);
    private CableType cableType = CableType.copper;
    private int insulation;
    private Ic2Color color = Ic2Color.black;
    private CableFoam foam = CableFoam.None;
    private Ic2Color foamColor = BlockWall.defaultColor;
    private final Obscuration obscuration;
    private byte connectivity = 0;
    private volatile CableRenderState renderState;
    private volatile TileEntityWall.WallRenderState wallRenderState;
    public boolean addedToEnergyNet = false;
    private IWorldTickCallback continuousUpdate = null;
    private static final int EventRemoveConductor = 0;

    public TileEntityCable(CableType cableType, int insulation) {
        this();
        this.cableType = cableType;
        this.insulation = insulation;
        this.updateRenderState();
    }

    public TileEntityCable() {
        this.obscuration = this.addComponent(new Obscuration(this, new Runnable(){

            @Override
            public void run() {
                IC2.network.get(true).updateTileEntityField(TileEntityCable.this, "obscuration");
            }
        }));
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.cableType = CableType.values[nbt.getByte("cableType") & 255];
        this.insulation = nbt.getByte("insulation") & 255;
        this.color = Ic2Color.values[nbt.getByte("color") & 255];
        this.foam = CableFoam.values[nbt.getByte("foam") & 255];
        this.foamColor = Ic2Color.values[nbt.getByte("foamColor") & 255];
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setByte("cableType", (byte)this.cableType.ordinal());
        nbt.setByte("insulation", (byte)this.insulation);
        nbt.setByte("color", (byte)this.color.ordinal());
        nbt.setByte("foam", (byte)this.foam.ordinal());
        nbt.setByte("foamColor", (byte)this.foamColor.ordinal());
        return nbt;
    }

    @Override
    protected void onLoaded() {
        super.onLoaded();
        if (this.worldObj.isRemote) {
            this.updateRenderState();
        } else {
            MinecraftForge.EVENT_BUS.post((Event)new EnergyTileLoadEvent(this));
            this.addedToEnergyNet = true;
            this.updateConnectivity();
            if (this.foam == CableFoam.Soft) {
                this.changeFoam(this.foam, true);
            }
        }
    }

    @Override
    protected void onUnloaded() {
        if (IC2.platform.isSimulating() && this.addedToEnergyNet) {
            MinecraftForge.EVENT_BUS.post((Event)new EnergyTileUnloadEvent(this));
            this.addedToEnergyNet = false;
        }
        if (this.continuousUpdate != null) {
            IC2.tickHandler.removeContinuousWorldTick(this.worldObj, this.continuousUpdate);
            this.continuousUpdate = null;
        }
        super.onUnloaded();
    }

    @Override
    protected ItemStack getPickBlock(EntityPlayer player, RayTraceResult target) {
        return ItemCable.getCable(this.cableType, this.insulation);
    }

    @Override
    protected List<AxisAlignedBB> getAabbs(boolean forCollision) {
        if (this.foam == CableFoam.Hardened || this.foam == CableFoam.Soft && !forCollision) {
            return super.getAabbs(forCollision);
        }
        float th = this.cableType.thickness + (float)(this.insulation * 2) * 0.0625f;
        float sp = (1.0f - th) / 2.0f;
        ArrayList<AxisAlignedBB> ret = new ArrayList<AxisAlignedBB>(7);
        ret.add(new AxisAlignedBB((double)sp, (double)sp, (double)sp, (double)(sp + th), (double)(sp + th), (double)(sp + th)));
        for (EnumFacing facing : EnumFacing.VALUES) {
            float zE;
            boolean hasConnection;
            float zS;
            boolean bl = hasConnection = (this.connectivity & 1 << facing.ordinal()) != 0;
            if (!hasConnection) continue;
            float yS = zS = sp;
            float xS = zS;
            float yE = zE = sp + th;
            float xE = zE;
            switch (facing) {
                case DOWN: {
                    yS = 0.0f;
                    yE = sp;
                    break;
                }
                case UP: {
                    yS = sp + th;
                    yE = 1.0f;
                    break;
                }
                case NORTH: {
                    zS = 0.0f;
                    zE = sp;
                    break;
                }
                case SOUTH: {
                    zS = sp + th;
                    zE = 1.0f;
                    break;
                }
                case WEST: {
                    xS = 0.0f;
                    xE = sp;
                    break;
                }
                case EAST: {
                    xS = sp + th;
                    xE = 1.0f;
                    break;
                }
                default: {
                    throw new RuntimeException();
                }
            }
            ret.add(new AxisAlignedBB((double)xS, (double)yS, (double)zS, (double)xE, (double)yE, (double)zE));
        }
        return ret;
    }

    @Override
    public Ic2BlockState.Ic2BlockStateInstance getExtendedState(Ic2BlockState.Ic2BlockStateInstance state) {
        TileEntityWall.WallRenderState wallRenderState;
        state = super.getExtendedState(state);
        CableRenderState cableRenderState = this.renderState;
        if (cableRenderState != null) {
            state = state.withProperties(new Object[]{renderStateProperty, cableRenderState});
        }
        if ((wallRenderState = this.wallRenderState) != null) {
            state = state.withProperties(new Object[]{TileEntityWall.renderStateProperty, wallRenderState});
        }
        return state;
    }

    @Override
    public void onNeighborChange(Block neighbor) {
        super.onNeighborChange(neighbor);
        if (!this.worldObj.isRemote) {
            this.updateConnectivity();
        }
    }

    private void updateConnectivity() {
        byte newConnectivity = 0;
        int mask = 1;
        for (EnumFacing dir : EnumFacing.VALUES) {
            IEnergyTile tile = EnergyNet.instance.getSubTile(this.worldObj, this.pos.offset(dir));
            if ((tile instanceof IEnergyAcceptor && ((IEnergyAcceptor)tile).acceptsEnergyFrom(this, dir.getOpposite()) || tile instanceof IEnergyEmitter && ((IEnergyEmitter)tile).emitsEnergyTo(this, dir.getOpposite())) && this.canInteractWith(tile)) {
                newConnectivity = (byte)(newConnectivity | mask);
            }
            mask *= 2;
        }
        if (this.connectivity != newConnectivity) {
            this.connectivity = newConnectivity;
            IC2.network.get(true).updateTileEntityField(this, "connectivity");
        }
    }

    @Override
    protected boolean onActivated(EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (this.foam == CableFoam.Soft && StackUtil.consumeFromPlayerHand(player, StackUtil.sameItem((Block)Blocks.SAND), 1) != null) {
            this.changeFoam(CableFoam.Hardened, false);
            return true;
        }
        if (this.foam == CableFoam.None && StackUtil.consumeFromPlayerHand(player, StackUtil.sameStack(BlockName.foam.getItemStack(BlockFoam.FoamType.normal)), 1) != null) {
            this.foam();
            return true;
        }
        return super.onActivated(player, hand, heldItem, side, hitX, hitY, hitZ);
    }

    @Override
    protected void onClicked(EntityPlayer player) {
        super.onClicked(player);
        ItemStack stack = player.getActiveItemStack();
        if (stack != null && stack.getItem() instanceof ItemToolCutter && this.tryRemoveInsulation()) {
            if (!this.worldObj.isRemote) {
                StackUtil.dropAsEntity(this.worldObj, this.pos, ItemName.crafting.getItemStack(CraftingItemType.rubber));
            }
            ItemToolCutter.onInsulationRemoved(stack, this.worldObj, this.pos);
            if (stack.stackSize == 0) {
                player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
            }
        }
    }

    @Override
    protected float getHardness() {
        switch (this.foam) {
            case Soft: {
                return BlockName.foam.getInstance().getBlockHardness(null, null, null);
            }
            case Hardened: {
                return BlockName.wall.getInstance().getBlockHardness(null, null, null);
            }
        }
        return super.getHardness();
    }

    @Override
    protected float getExplosionResistance(Entity exploder, Explosion explosion) {
        switch (this.foam) {
            case Hardened: {
                return BlockName.wall.getInstance().getExplosionResistance(exploder);
            }
        }
        return super.getHardness();
    }

    @Override
    protected int getLightOpacity() {
        return this.foam == CableFoam.Hardened ? 255 : 0;
    }

    @Override
    protected boolean recolor(EnumFacing side, EnumDyeColor mcColor) {
        Ic2Color newColor = Ic2Color.get(mcColor);
        if (this.foam == CableFoam.None && (this.color == newColor || this.cableType.minColoredInsulation > this.insulation) || this.foam == CableFoam.Soft || this.foam == CableFoam.Hardened && this.foamColor == newColor) {
            return false;
        }
        if (!this.worldObj.isRemote) {
            if (this.foam == CableFoam.None) {
                if (this.addedToEnergyNet) {
                    MinecraftForge.EVENT_BUS.post((Event)new EnergyTileUnloadEvent(this));
                }
                this.addedToEnergyNet = false;
                this.color = newColor;
                MinecraftForge.EVENT_BUS.post((Event)new EnergyTileLoadEvent(this));
                this.addedToEnergyNet = true;
                IC2.network.get(true).updateTileEntityField(this, "color");
                this.updateConnectivity();
            } else {
                this.foamColor = newColor;
                IC2.network.get(true).updateTileEntityField(this, "foamColor");
                this.obscuration.clear();
            }
        }
        return true;
    }

    @Override
    protected boolean onRemovedByPlayer(EntityPlayer player, boolean willHarvest) {
        if (this.changeFoam(CableFoam.None, false)) {
            return false;
        }
        return super.onRemovedByPlayer(player, willHarvest);
    }

    public boolean isFoamed() {
        return this.foam != CableFoam.None;
    }

    public boolean foam() {
        return this.changeFoam(CableFoam.Soft, false);
    }

    public boolean tryAddInsulation() {
        if (this.insulation >= this.cableType.maxInsulation) {
            return false;
        }
        ++this.insulation;
        if (!this.worldObj.isRemote) {
            IC2.network.get(true).updateTileEntityField(this, "insulation");
        }
        return true;
    }

    private boolean tryRemoveInsulation() {
        if (this.insulation <= 0) {
            return false;
        }
        --this.insulation;
        if (!this.worldObj.isRemote) {
            IC2.network.get(true).updateTileEntityField(this, "insulation");
        }
        return true;
    }

    @Override
    public boolean wrenchCanRemove(EntityPlayer player) {
        return false;
    }

    @Override
    public boolean acceptsEnergyFrom(IEnergyEmitter emitter, EnumFacing direction) {
        return this.canInteractWith(emitter);
    }

    @Override
    public boolean emitsEnergyTo(IEnergyAcceptor receiver, EnumFacing direction) {
        return this.canInteractWith(receiver);
    }

    public boolean canInteractWith(IEnergyTile te) {
        if (te instanceof TileEntityCable) {
            return this.canInteractWithCable((TileEntityCable)te);
        }
        return true;
    }

    public boolean canInteractWithCable(TileEntityCable cable) {
        return this.color == Ic2Color.black || cable.color == Ic2Color.black || this.color == cable.color;
    }

    @Override
    public double getConductionLoss() {
        return this.cableType.loss;
    }

    @Override
    public double getInsulationEnergyAbsorption() {
        switch (this.cableType) {
            case glass: {
                return 2.147483647E9;
            }
        }
        return EnergyNet.instance.getPowerFromTier(this.insulation + 1);
    }

    @Override
    public double getInsulationBreakdownEnergy() {
        return 9001.0;
    }

    @Override
    public double getConductorBreakdownEnergy() {
        return this.cableType.capacity + 1;
    }

    @Override
    public void removeInsulation() {
    }

    @Override
    public void removeConductor() {
        this.worldObj.setBlockToAir(this.pos);
        IC2.network.get(true).initiateTileEntityEvent(this, 0, true);
    }

    @Override
    public List<String> getNetworkedFields() {
        ArrayList<String> ret = new ArrayList<String>();
        ret.add("cableType");
        ret.add("insulation");
        ret.add("color");
        ret.add("foam");
        ret.add("connectivity");
        ret.add("obscuration");
        ret.addAll(super.getNetworkedFields());
        return ret;
    }

    @Override
    public void onNetworkUpdate(String field) {
        this.updateRenderState();
        if (field.equals("foam") && (this.foam == CableFoam.None || this.foam == CableFoam.Hardened)) {
            this.relight();
        }
        this.rerender();
        super.onNetworkUpdate(field);
    }

    private void relight() {
    }

    @Override
    public void onNetworkEvent(int event) {
        switch (event) {
            case 0: {
                this.worldObj.playSound(null, this.pos, SoundEvents.ENTITY_GENERIC_BURN, SoundCategory.BLOCKS, 0.5f, 2.6f + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.8f);
                for (int l = 0; l < 8; ++l) {
                    this.worldObj.spawnParticle(EnumParticleTypes.SMOKE_LARGE, (double)this.pos.getX() + Math.random(), (double)this.pos.getY() + 1.2, (double)this.pos.getZ() + Math.random(), 0.0, 0.0, 0.0, new int[0]);
                }
                break;
            }
            default: {
                IC2.platform.displayError("An unknown event type was received over multiplayer.\nThis could happen due to corrupted data or a bug.\n\n(Technical information: event ID " + event + ", tile entity below)\n" + "T: " + this + " (" + (Object)this.pos + ")", new Object[0]);
            }
        }
    }

    private boolean changeFoam(CableFoam foam, boolean duringLoad) {
        if (this.foam == foam && !duringLoad) {
            return false;
        }
        if (this.worldObj.isRemote) {
            return true;
        }
        this.foam = foam;
        if (this.continuousUpdate != null) {
            IC2.tickHandler.removeContinuousWorldTick(this.worldObj, this.continuousUpdate);
            this.continuousUpdate = null;
        }
        if (foam != CableFoam.Hardened) {
            this.obscuration.clear();
            if (this.foamColor != BlockWall.defaultColor) {
                this.foamColor = BlockWall.defaultColor;
                if (!duringLoad) {
                    IC2.network.get(true).updateTileEntityField(this, "foamColor");
                }
            }
        }
        if (foam == CableFoam.Soft) {
            this.continuousUpdate = new IWorldTickCallback(){

                @Override
                public void onTick(World world) {
                    if (world.rand.nextFloat() < BlockFoam.getHardenChance(TileEntityCable.this.worldObj, TileEntityCable.this.pos, TileEntityCable.this.getBlockType().getState(TeBlock.cable), BlockFoam.FoamType.normal)) {
                        TileEntityCable.this.changeFoam(CableFoam.Hardened, false);
                    }
                }
            };
            IC2.tickHandler.requestContinuousWorldTick(this.worldObj, this.continuousUpdate);
        }
        if (!duringLoad) {
            IC2.network.get(true).updateTileEntityField(this, "foam");
            this.worldObj.notifyNeighborsOfStateChange(this.pos, (Block)this.getBlockType());
        }
        return true;
    }

    private void updateRenderState() {
        this.renderState = new CableRenderState(this.cableType, this.insulation, this.color, this.foam, this.connectivity);
        this.wallRenderState = new TileEntityWall.WallRenderState(this.foamColor, this.obscuration.getRenderState());
    }

    public static class CableRenderState {
        public final CableType type;
        public final int insulation;
        public final Ic2Color color;
        public final CableFoam foam;
        public final int connectivity;

        public CableRenderState(CableType type, int insulation, Ic2Color color, CableFoam foam, int connectivity) {
            this.type = type;
            this.insulation = insulation;
            this.color = color;
            this.foam = foam;
            this.connectivity = connectivity;
        }

        public int hashCode() {
            int ret = this.type.hashCode();
            ret = ret * 31 + this.insulation;
            ret = ret * 31 + this.color.hashCode();
            ret = ret * 31 + this.foam.hashCode();
            ret = ret * 31 + this.connectivity;
            return ret;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof CableRenderState)) {
                return false;
            }
            CableRenderState o = (CableRenderState)obj;
            return o.type == this.type && o.insulation == this.insulation && o.color == this.color && o.foam == this.foam && o.connectivity == this.connectivity;
        }
    }

}

