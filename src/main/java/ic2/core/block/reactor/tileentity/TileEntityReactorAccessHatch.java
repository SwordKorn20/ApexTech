/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentString
 *  net.minecraft.world.World
 */
package ic2.core.block.reactor.tileentity;

import ic2.core.block.BlockTileEntity;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.comp.FluidReactorLookup;
import ic2.core.block.comp.TileEntityComponent;
import ic2.core.block.reactor.tileentity.TileEntityNuclearReactorElectric;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class TileEntityReactorAccessHatch
extends TileEntityBlock
implements IInventory {
    private final FluidReactorLookup lookup;

    public TileEntityReactorAccessHatch() {
        this.lookup = this.addComponent(new FluidReactorLookup(this));
    }

    @Override
    protected boolean onActivated(EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntityNuclearReactorElectric reactor = this.getReactor();
        if (reactor != null) {
            return reactor.getBlockType().onBlockActivated(this.worldObj, reactor.getPos(), this.worldObj.getBlockState(reactor.getPos()), player, hand, heldItem, side, hitX, hitY, hitZ);
        }
        return false;
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

    private TileEntityNuclearReactorElectric getReactor() {
        return this.lookup.getReactor();
    }
}

