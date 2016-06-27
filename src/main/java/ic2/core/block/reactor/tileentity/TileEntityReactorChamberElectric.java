/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentString
 *  net.minecraft.world.World
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidStack
 *  net.minecraftforge.fluids.FluidTankInfo
 *  net.minecraftforge.fluids.IFluidHandler
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block.reactor.tileentity;

import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.core.block.BlockTileEntity;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.comp.Redstone;
import ic2.core.block.comp.TileEntityComponent;
import ic2.core.block.reactor.tileentity.TileEntityNuclearReactorElectric;
import ic2.core.util.StackUtil;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityReactorChamberElectric
extends TileEntityBlock
implements IInventory,
IFluidHandler,
IEnergyEmitter {
    public final Redstone redstone;
    private TileEntityNuclearReactorElectric reactor;
    private long lastReactorUpdate;

    public TileEntityReactorChamberElectric() {
        this.redstone = this.addComponent(new Redstone(this));
    }

    @Override
    protected void onLoaded() {
        super.onLoaded();
        this.updateRedstoneLink();
    }

    private void updateRedstoneLink() {
        if (this.worldObj.isRemote) {
            return;
        }
        TileEntityNuclearReactorElectric reactor = this.getReactor();
        this.redstone.linkTo(reactor.redstone);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    protected void updateEntityClient() {
        super.updateEntityClient();
        TileEntityNuclearReactorElectric reactor = this.getReactor();
        if (reactor != null) {
            TileEntityNuclearReactorElectric.showHeatEffects(this.worldObj, this.pos, reactor.getHeat());
        }
    }

    @Override
    protected boolean onActivated(EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntityNuclearReactorElectric reactor = this.getReactor();
        if (reactor != null) {
            return reactor.getBlockType().onBlockActivated(this.worldObj, reactor.getPos(), this.worldObj.getBlockState(reactor.getPos()), player, hand, heldItem, side, hitX, hitY, hitZ);
        }
        return false;
    }

    @Override
    protected void onNeighborChange(Block neighbor) {
        super.onNeighborChange(neighbor);
        this.lastReactorUpdate = 0;
        if (this.getReactor() == null) {
            this.worldObj.setBlockToAir(this.getPos());
            for (ItemStack drop : this.getSelfDrops(0, true)) {
                StackUtil.dropAsEntity(this.worldObj, this.pos, drop);
            }
        }
    }

    public String getName() {
        TileEntityNuclearReactorElectric reactor = this.getReactor();
        return reactor != null ? reactor.getName() : "<null>";
    }

    public boolean hasCustomName() {
        TileEntityNuclearReactorElectric reactor = this.getReactor();
        return reactor != null ? reactor.hasCustomName() : false;
    }

    public ITextComponent getDisplayName() {
        TileEntityNuclearReactorElectric reactor = this.getReactor();
        return reactor != null ? reactor.getDisplayName() : new TextComponentString("<null>");
    }

    public int getSizeInventory() {
        TileEntityNuclearReactorElectric reactor = this.getReactor();
        return reactor != null ? reactor.getSizeInventory() : 0;
    }

    public ItemStack getStackInSlot(int index) {
        TileEntityNuclearReactorElectric reactor = this.getReactor();
        return reactor != null ? reactor.getStackInSlot(index) : null;
    }

    public ItemStack decrStackSize(int index, int count) {
        TileEntityNuclearReactorElectric reactor = this.getReactor();
        return reactor != null ? reactor.decrStackSize(index, count) : null;
    }

    public ItemStack removeStackFromSlot(int index) {
        TileEntityNuclearReactorElectric reactor = this.getReactor();
        return reactor != null ? reactor.removeStackFromSlot(index) : null;
    }

    public void setInventorySlotContents(int index, ItemStack stack) {
        TileEntityNuclearReactorElectric reactor = this.getReactor();
        if (reactor != null) {
            reactor.setInventorySlotContents(index, stack);
        }
    }

    public int getInventoryStackLimit() {
        TileEntityNuclearReactorElectric reactor = this.getReactor();
        return reactor != null ? reactor.getInventoryStackLimit() : 0;
    }

    public boolean isUseableByPlayer(EntityPlayer player) {
        TileEntityNuclearReactorElectric reactor = this.getReactor();
        return reactor != null ? reactor.isUseableByPlayer(player) : false;
    }

    public void openInventory(EntityPlayer player) {
        TileEntityNuclearReactorElectric reactor = this.getReactor();
        if (reactor != null) {
            reactor.openInventory(player);
        }
    }

    public void closeInventory(EntityPlayer player) {
        TileEntityNuclearReactorElectric reactor = this.getReactor();
        if (reactor != null) {
            reactor.closeInventory(player);
        }
    }

    public boolean isItemValidForSlot(int index, ItemStack stack) {
        TileEntityNuclearReactorElectric reactor = this.getReactor();
        return reactor != null ? reactor.isItemValidForSlot(index, stack) : false;
    }

    public int getField(int id) {
        TileEntityNuclearReactorElectric reactor = this.getReactor();
        return reactor != null ? reactor.getField(id) : 0;
    }

    public void setField(int id, int value) {
        TileEntityNuclearReactorElectric reactor = this.getReactor();
        if (reactor != null) {
            reactor.setField(id, value);
        }
    }

    public int getFieldCount() {
        TileEntityNuclearReactorElectric reactor = this.getReactor();
        return reactor != null ? reactor.getFieldCount() : 0;
    }

    public void clear() {
        TileEntityNuclearReactorElectric reactor = this.getReactor();
        if (reactor != null) {
            reactor.clear();
        }
    }

    public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
        TileEntityNuclearReactorElectric reactor = this.getReactor();
        return reactor != null ? reactor.fill(from, resource, doFill) : 0;
    }

    public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
        TileEntityNuclearReactorElectric reactor = this.getReactor();
        return reactor != null ? reactor.drain(from, resource, doDrain) : null;
    }

    public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
        TileEntityNuclearReactorElectric reactor = this.getReactor();
        return reactor != null ? reactor.drain(from, maxDrain, doDrain) : null;
    }

    public boolean canFill(EnumFacing from, Fluid fluid) {
        TileEntityNuclearReactorElectric reactor = this.getReactor();
        return reactor != null ? reactor.canFill(from, fluid) : false;
    }

    public boolean canDrain(EnumFacing from, Fluid fluid) {
        TileEntityNuclearReactorElectric reactor = this.getReactor();
        return reactor != null ? reactor.canDrain(from, fluid) : false;
    }

    public FluidTankInfo[] getTankInfo(EnumFacing from) {
        TileEntityNuclearReactorElectric reactor = this.getReactor();
        return reactor != null ? reactor.getTankInfo(from) : new FluidTankInfo[]{};
    }

    @Override
    public boolean emitsEnergyTo(IEnergyAcceptor receiver, EnumFacing side) {
        return true;
    }

    public TileEntityNuclearReactorElectric getReactorInstance() {
        return this.reactor;
    }

    private TileEntityNuclearReactorElectric getReactor() {
        long time = this.worldObj.getTotalWorldTime();
        if (time != this.lastReactorUpdate) {
            this.updateReactor();
            this.lastReactorUpdate = time;
        } else if (this.reactor != null && this.reactor.isInvalid()) {
            this.reactor = null;
        }
        return this.reactor;
    }

    private void updateReactor() {
        this.reactor = null;
        for (EnumFacing facing : EnumFacing.VALUES) {
            TileEntity te = this.worldObj.getTileEntity(this.pos.offset(facing));
            if (!(te instanceof TileEntityNuclearReactorElectric)) continue;
            this.reactor = (TileEntityNuclearReactorElectric)te;
            break;
        }
    }
}

