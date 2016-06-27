/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.nbt.NBTTagList
 *  net.minecraft.util.EnumFacing
 */
package ic2.core.block.invslot;

import ic2.core.IC2;
import ic2.core.block.TileEntityInventory;
import ic2.core.util.Log;
import ic2.core.util.LogCategory;
import ic2.core.util.StackUtil;
import ic2.core.util.Util;
import java.util.Iterator;
import java.util.NoSuchElementException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;

public class InvSlot
implements Iterable<ItemStack> {
    public final TileEntityInventory base;
    public final String name;
    private final ItemStack[] contents;
    protected final Access access;
    public final InvSide preferredSide;
    private int stackSizeLimit;

    public InvSlot(TileEntityInventory base, String name, Access access, int count) {
        this(base, name, access, count, InvSide.ANY);
    }

    public InvSlot(TileEntityInventory base, String name, Access access, int count, InvSide preferredSide) {
        if (count <= 0) {
            throw new IllegalArgumentException("invalid slot count: " + count);
        }
        this.contents = new ItemStack[count];
        this.base = base;
        this.name = name;
        this.access = access;
        this.preferredSide = preferredSide;
        this.stackSizeLimit = 64;
        base.addInvSlot(this);
    }

    public InvSlot(int count) {
        this.contents = new ItemStack[count];
        this.base = null;
        this.name = null;
        this.access = Access.NONE;
        this.preferredSide = InvSide.ANY;
    }

    public void readFromNbt(NBTTagCompound nbt) {
        NBTTagList contentsTag = nbt.getTagList("Contents", 10);
        for (int i = 0; i < contentsTag.tagCount(); ++i) {
            NBTTagCompound contentTag = contentsTag.getCompoundTagAt(i);
            int index = contentTag.getByte("Index") & 255;
            if (index >= this.size()) {
                IC2.log.error(LogCategory.Block, "Can't load item stack for %s, slot %s, index %d is out of bounds.", Util.asString(this.base), this.name, index);
                continue;
            }
            ItemStack stack = ItemStack.loadItemStackFromNBT((NBTTagCompound)contentTag);
            if (stack == null) {
                IC2.log.warn(LogCategory.Block, "Can't load item stack for %s, slot %s, index %d, no matching item for %d:%d.", Util.asString(this.base), this.name, index, contentTag.getShort("id"), contentTag.getShort("Damage"));
                continue;
            }
            if (this.get(index) != null) {
                IC2.log.error(LogCategory.Block, "Loading content to non-empty slot for %s, slot %s, index %d, replacing %s with %s.", new Object[]{Util.asString(this.base), this.name, index, this.get(index), stack});
            }
            this.put(index, stack);
        }
    }

    public void writeToNbt(NBTTagCompound nbt) {
        NBTTagList contentsTag = new NBTTagList();
        for (int i = 0; i < this.contents.length; ++i) {
            if (this.contents[i] == null) continue;
            NBTTagCompound contentTag = new NBTTagCompound();
            contentTag.setByte("Index", (byte)i);
            this.contents[i].writeToNBT(contentTag);
            contentsTag.appendTag((NBTBase)contentTag);
        }
        nbt.setTag("Contents", (NBTBase)contentsTag);
    }

    public int size() {
        return this.contents.length;
    }

    public ItemStack get() {
        return this.get(0);
    }

    public ItemStack get(int index) {
        return this.contents[index];
    }

    public void put(ItemStack content) {
        this.put(0, content);
    }

    public void put(int index, ItemStack content) {
        this.contents[index] = content;
        this.onChanged();
    }

    public void clear() {
        for (int i = 0; i < this.contents.length; ++i) {
            this.contents[i] = null;
        }
    }

    public void onChanged() {
    }

    public boolean accepts(ItemStack stack) {
        return true;
    }

    public boolean canInput() {
        return this.access == Access.I || this.access == Access.IO;
    }

    public boolean canOutput() {
        return this.access == Access.O || this.access == Access.IO;
    }

    public boolean isEmpty() {
        for (ItemStack stack : this.contents) {
            if (stack == null) continue;
            return false;
        }
        return true;
    }

    public void organize() {
        block0 : for (int dstIndex = 0; dstIndex < this.contents.length - 1; ++dstIndex) {
            ItemStack dst = this.contents[dstIndex];
            if (dst != null && dst.stackSize >= dst.getMaxStackSize()) continue;
            for (int srcIndex = dstIndex + 1; srcIndex < this.contents.length; ++srcIndex) {
                ItemStack src = this.contents[srcIndex];
                if (src == null) continue;
                if (dst == null) {
                    this.contents[srcIndex] = null;
                    this.contents[dstIndex] = dst = src;
                    continue;
                }
                if (!StackUtil.checkItemEqualityStrict(dst, src)) continue;
                int space = Math.min(this.getStackSizeLimit(), dst.getMaxStackSize() - dst.stackSize);
                if (src.stackSize <= space) {
                    this.contents[srcIndex] = null;
                    dst.stackSize += src.stackSize;
                    continue;
                }
                src.stackSize -= space;
                dst.stackSize += space;
                continue block0;
            }
        }
    }

    public int getStackSizeLimit() {
        return this.stackSizeLimit;
    }

    public void setStackSizeLimit(int stackSizeLimit) {
        this.stackSizeLimit = stackSizeLimit;
    }

    @Override
    public Iterator<ItemStack> iterator() {
        return new Iterator<ItemStack>(){
            private int idx;

            @Override
            public boolean hasNext() {
                return this.idx < InvSlot.this.contents.length;
            }

            @Override
            public ItemStack next() {
                if (this.idx >= InvSlot.this.contents.length) {
                    throw new NoSuchElementException();
                }
                return InvSlot.this.contents[this.idx++];
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public String toString() {
        String ret = this.name + "[" + this.contents.length + "]: ";
        for (int i = 0; i < this.contents.length; ++i) {
            ret = ret + (Object)this.contents[i];
            if (i >= this.contents.length - 1) continue;
            ret = ret + ", ";
        }
        return ret;
    }

    protected ItemStack[] backup() {
        ItemStack[] ret = new ItemStack[this.contents.length];
        for (int i = 0; i < this.contents.length; ++i) {
            ret[i] = this.contents[i] == null ? null : this.contents[i].copy();
        }
        return ret;
    }

    protected void restore(ItemStack[] backup) {
        for (int i = 0; i < this.contents.length; ++i) {
            this.contents[i] = backup[i];
        }
    }

    public void onPickupFromSlot(EntityPlayer player, ItemStack stack) {
    }

    public static enum InvSide {
        ANY,
        TOP,
        BOTTOM,
        SIDE,
        NOTSIDE;
        

        private InvSide() {
        }

        public boolean matches(EnumFacing side) {
            return this == ANY || side == EnumFacing.DOWN && this == BOTTOM || side == EnumFacing.UP && this == TOP || (side == EnumFacing.DOWN || side == EnumFacing.UP) && this == NOTSIDE || side != EnumFacing.UP && side != EnumFacing.DOWN && this == SIDE;
        }
    }

    public static enum Access {
        NONE,
        I,
        O,
        IO;
        

        private Access() {
        }
    }

}

