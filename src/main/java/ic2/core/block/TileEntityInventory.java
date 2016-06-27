/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.ISidedInventory
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentString
 */
package ic2.core.block;

import ic2.core.block.BlockTileEntity;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.invslot.InvSlot;
import ic2.core.ref.TeBlock;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public abstract class TileEntityInventory
extends TileEntityBlock
implements ISidedInventory {
    private final List<InvSlot> invSlots = new ArrayList<InvSlot>();

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        super.readFromNBT(nbtTagCompound);
        NBTTagCompound invSlotsTag = nbtTagCompound.getCompoundTag("InvSlots");
        for (InvSlot invSlot : this.invSlots) {
            invSlot.readFromNbt(invSlotsTag.getCompoundTag(invSlot.name));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        NBTTagCompound invSlotsTag = new NBTTagCompound();
        for (InvSlot invSlot : this.invSlots) {
            NBTTagCompound invSlotTag = new NBTTagCompound();
            invSlot.writeToNbt(invSlotTag);
            invSlotsTag.setTag(invSlot.name, (NBTBase)invSlotTag);
        }
        nbt.setTag("InvSlots", (NBTBase)invSlotsTag);
        return nbt;
    }

    public int getSizeInventory() {
        int ret = 0;
        for (InvSlot invSlot : this.invSlots) {
            ret += invSlot.size();
        }
        return ret;
    }

    public ItemStack getStackInSlot(int index) {
        for (InvSlot invSlot : this.invSlots) {
            if (index < invSlot.size()) {
                return invSlot.get(index);
            }
            index -= invSlot.size();
        }
        return null;
    }

    public ItemStack decrStackSize(int index, int amount) {
        ItemStack stack = this.getStackInSlot(index);
        if (stack == null) {
            return null;
        }
        if (amount >= stack.stackSize) {
            this.setInventorySlotContents(index, null);
            return stack;
        }
        if (amount != 0) {
            if (amount < 0) {
                int space = Math.min(this.getInvSlot(index).getStackSizeLimit(), stack.getMaxStackSize()) - stack.stackSize;
                amount = Math.max(amount, - space);
            }
            stack.stackSize -= amount;
            this.getInvSlot(index).onChanged();
        }
        ItemStack ret = stack.copy();
        ret.stackSize = amount;
        return ret;
    }

    public ItemStack removeStackFromSlot(int index) {
        ItemStack ret = this.getStackInSlot(index);
        if (ret != null) {
            this.setInventorySlotContents(index, null);
        }
        return ret;
    }

    public void setInventorySlotContents(int index, ItemStack stack) {
        for (InvSlot invSlot : this.invSlots) {
            if (index < invSlot.size()) {
                invSlot.put(index, stack);
                break;
            }
            index -= invSlot.size();
        }
    }

    public void markDirty() {
        super.markDirty();
        for (InvSlot invSlot : this.invSlots) {
            invSlot.onChanged();
        }
    }

    public String getName() {
        TeBlock teBlock = TeBlock.get(this.getClass());
        String name = teBlock == null ? "invalid" : teBlock.name();
        return this.getBlockType().getUnlocalizedName() + "." + name;
    }

    public boolean hasCustomName() {
        return false;
    }

    public ITextComponent getDisplayName() {
        return new TextComponentString(this.getName());
    }

    public int getInventoryStackLimit() {
        int max = 0;
        for (InvSlot slot : this.invSlots) {
            max = Math.max(max, slot.getStackSizeLimit());
        }
        return max;
    }

    public boolean isUseableByPlayer(EntityPlayer player) {
        return !this.isInvalid() && player.getDistanceSq(this.pos) <= 64.0;
    }

    public void openInventory(EntityPlayer player) {
    }

    public void closeInventory(EntityPlayer player) {
    }

    public boolean isItemValidForSlot(int index, ItemStack stack) {
        InvSlot invSlot = this.getInvSlot(index);
        return invSlot != null && invSlot.canInput() && invSlot.accepts(stack);
    }

    public int[] getSlotsForFace(EnumFacing side) {
        int[] ret = new int[this.getSizeInventory()];
        int i = 0;
        while (i < ret.length) {
            ret[i] = i++;
        }
        return ret;
    }

    public boolean canInsertItem(int index, ItemStack stack, EnumFacing side) {
        InvSlot targetSlot = this.getInvSlot(index);
        if (targetSlot == null) {
            return false;
        }
        if (!targetSlot.canInput() || !targetSlot.accepts(stack)) {
            return false;
        }
        if (targetSlot.preferredSide != InvSlot.InvSide.ANY && targetSlot.preferredSide.matches(side)) {
            return true;
        }
        for (InvSlot invSlot : this.invSlots) {
            if (invSlot == targetSlot || invSlot.preferredSide == InvSlot.InvSide.ANY || !invSlot.preferredSide.matches(side) || !invSlot.canInput() || !invSlot.accepts(stack)) continue;
            return false;
        }
        return true;
    }

    public boolean canExtractItem(int index, ItemStack stack, EnumFacing side) {
        InvSlot targetSlot = this.getInvSlot(index);
        if (targetSlot == null) {
            return false;
        }
        if (!targetSlot.canOutput()) {
            return false;
        }
        boolean correctSide = targetSlot.preferredSide.matches(side);
        if (targetSlot.preferredSide != InvSlot.InvSide.ANY && correctSide) {
            return true;
        }
        for (InvSlot invSlot : this.invSlots) {
            if (invSlot == targetSlot || invSlot.preferredSide == InvSlot.InvSide.ANY && correctSide || !invSlot.preferredSide.matches(side) || !invSlot.canOutput()) continue;
            return false;
        }
        return true;
    }

    public int getField(int id) {
        return 0;
    }

    public void setField(int id, int value) {
    }

    public int getFieldCount() {
        return 0;
    }

    public void clear() {
        for (InvSlot invSlot : this.invSlots) {
            invSlot.clear();
        }
    }

    public void addInvSlot(InvSlot invSlot) {
        this.invSlots.add(invSlot);
    }

    public int getBaseIndex(InvSlot invSlot) {
        int ret = 0;
        for (InvSlot slot : this.invSlots) {
            if (slot == invSlot) {
                return ret;
            }
            ret += slot.size();
        }
        return -1;
    }

    public InvSlot getInvSlot(String name) {
        for (InvSlot invSlot : this.invSlots) {
            if (!invSlot.name.equals(name)) continue;
            return invSlot;
        }
        return null;
    }

    private InvSlot getInvSlot(int index) {
        for (InvSlot invSlot : this.invSlots) {
            if (index < invSlot.size()) {
                return invSlot;
            }
            index -= invSlot.size();
        }
        return null;
    }

    @Override
    protected List<ItemStack> getAuxDrops(int fortune) {
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>(super.getAuxDrops(fortune));
        for (InvSlot slot : this.invSlots) {
            for (ItemStack stack : slot) {
                if (stack == null) continue;
                ret.add(stack);
            }
        }
        return ret;
    }
}

