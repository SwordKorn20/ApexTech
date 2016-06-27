/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.EnumCreatureAttribute
 *  net.minecraft.entity.monster.EntityMob
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.EnumSkyBlock
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 */
package ic2.core.block.wiring;

import ic2.api.energy.EnergyNet;
import ic2.api.energy.IEnergyNet;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergyTile;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItemManager;
import ic2.core.IC2;
import ic2.core.IWorldTickCallback;
import ic2.core.Ic2Player;
import ic2.core.TickHandler;
import ic2.core.block.BlockTileEntity;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.comp.Energy;
import ic2.core.block.comp.Redstone;
import ic2.core.block.comp.TileEntityComponent;
import ic2.core.network.NetworkManager;
import ic2.core.util.SideGateway;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class TileEntityLuminator
extends TileEntityBlock {
    private static final int manualChargeCapacity = 10000;
    private static final Map<EnumFacing, List<AxisAlignedBB>> aabbMap = TileEntityLuminator.getAabbMap();
    private final Energy energy;
    private final Redstone redstone;
    private boolean invertRedstone;
    public static boolean ignoreBlockStay = false;

    public TileEntityLuminator() {
        this.energy = this.addComponent(Energy.asBasicSink(this, 5.0));
        this.redstone = this.addComponent(new Redstone(this));
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.invertRedstone = nbt.getBoolean("invert");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setBoolean("invert", this.invertRedstone);
        return nbt;
    }

    @Override
    public void onLoaded() {
        this.energy.setDirections(Collections.singleton(this.getFacing().getOpposite()), Collections.<EnumFacing>emptySet());
        super.onLoaded();
        IC2.tickHandler.requestSingleWorldTick(this.worldObj, new IWorldTickCallback(){

            @Override
            public void onTick(World world) {
                TileEntityLuminator.this.checkPlacement();
            }
        });
    }

    @Override
    protected EnumFacing getPlacementFacing(EntityLivingBase placer, EnumFacing facing) {
        return facing;
    }

    @Override
    protected void updateEntityServer() {
        boolean lit;
        super.updateEntityServer();
        boolean bl = lit = this.isLit() && this.energy.useEnergy(0.25);
        if (this.getActive() != lit) {
            this.setActive(lit);
            this.updateLight();
        }
    }

    private boolean isLit() {
        return this.redstone.hasRedstoneInput() != this.invertRedstone;
    }

    @Override
    protected boolean onActivated(EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!this.worldObj.isRemote) {
            ItemStack stack = player.getActiveItemStack();
            double amount = 10000.0 - this.energy.getEnergy();
            if (stack != null && amount > 0.0 && (amount = ElectricItem.manager.discharge(stack, amount, this.energy.getSinkTier(), true, true, false)) > 0.0) {
                this.energy.forceAddEnergy(amount);
            } else {
                this.invertRedstone = !this.invertRedstone;
                IC2.network.get(!this.worldObj.isRemote).updateTileEntityField(this, "invertRedstone");
            }
        }
        return true;
    }

    @Override
    protected void onNeighborChange(Block neighbor) {
        super.onNeighborChange(neighbor);
        this.checkPlacement();
    }

    private void checkPlacement() {
        if (!TileEntityLuminator.isValidPosition(this.worldObj, this.pos.offset(this.getFacing().getOpposite()), this.getFacing())) {
            this.getBlockType().harvestBlock(this.worldObj, (EntityPlayer)Ic2Player.get(this.worldObj), this.pos, this.worldObj.getBlockState(this.pos), (TileEntity)this, null);
            this.worldObj.setBlockToAir(this.pos);
        }
    }

    public static boolean isValidPosition(World world, BlockPos pos, EnumFacing side) {
        if (world.isRemote || ignoreBlockStay) {
            return true;
        }
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock().isSideSolid(state, (IBlockAccess)world, pos, side)) {
            return true;
        }
        IEnergyTile tile = EnergyNet.instance.getSubTile(world, pos);
        return tile instanceof IEnergyEmitter;
    }

    @Override
    protected List<AxisAlignedBB> getAabbs(boolean forCollision) {
        return aabbMap.get((Object)this.getFacing());
    }

    @Override
    public int getLightValue() {
        return this.getActive() ? 15 : 0;
    }

    @Override
    protected void onEntityCollision(Entity entity) {
        super.onEntityCollision(entity);
        if (this.getActive() && entity instanceof EntityMob) {
            boolean isUndead = entity instanceof EntityLivingBase && ((EntityLivingBase)entity).getCreatureAttribute() == EnumCreatureAttribute.UNDEAD;
            entity.setFire(isUndead ? 20 : 10);
        }
    }

    @Override
    protected int getComparatorInputOverride() {
        return this.energy.getComparatorValue();
    }

    @Override
    protected boolean setFacingWrench(EnumFacing facing, EntityPlayer player) {
        this.invertRedstone = !this.invertRedstone;
        return true;
    }

    @Override
    public boolean wrenchCanRemove(EntityPlayer player) {
        return false;
    }

    @Override
    public void onNetworkUpdate(String field) {
        super.onNetworkUpdate(field);
        if (field.equals("active")) {
            this.updateLight();
        }
    }

    private void updateLight() {
        this.worldObj.checkLightFor(EnumSkyBlock.BLOCK, this.pos);
    }

    private static Map<EnumFacing, List<AxisAlignedBB>> getAabbMap() {
        EnumMap<EnumFacing, List<AxisAlignedBB>> ret = new EnumMap<EnumFacing, List<AxisAlignedBB>>(EnumFacing.class);
        double height = 0.0625;
        double remHeight = 0.9375;
        for (EnumFacing side : EnumFacing.VALUES) {
            int dx = side.getFrontOffsetX();
            int dy = side.getFrontOffsetY();
            int dz = side.getFrontOffsetZ();
            double xS = (double)((dx + 1) / 2) * 0.9375;
            double yS = (double)((dy + 1) / 2) * 0.9375;
            double zS = (double)((dz + 1) / 2) * 0.9375;
            double xE = 0.0625 + (double)((dx + 2) / 2) * 0.9375;
            double yE = 0.0625 + (double)((dy + 2) / 2) * 0.9375;
            double zE = 0.0625 + (double)((dz + 2) / 2) * 0.9375;
            ret.put(side.getOpposite(), Arrays.asList(new AxisAlignedBB[]{new AxisAlignedBB(xS, yS, zS, xE, yE, zE)}));
        }
        return ret;
    }

}

