/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 */
package ic2.core.util;

import ic2.core.util.StackUtil;
import java.util.Set;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

public class ItemComparableItemStack {
    private final Item item;
    private final int meta;
    private final NBTTagCompound nbt;
    private final int hashCode;

    public ItemComparableItemStack(ItemStack stack, boolean copyNbt) {
        this.item = stack.getItem();
        this.meta = stack.getHasSubtypes() ? stack.getMetadata() : 0;
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt != null) {
            if (nbt.hasNoTags()) {
                nbt = null;
            } else {
                if (copyNbt) {
                    nbt = (NBTTagCompound)nbt.copy();
                }
                boolean copied = copyNbt;
                for (String key : StackUtil.ignoredNbtKeys) {
                    if (!copied && nbt.hasKey(key)) {
                        nbt = (NBTTagCompound)nbt.copy();
                        copied = true;
                    }
                    nbt.removeTag(key);
                }
                if (nbt.hasNoTags()) {
                    nbt = null;
                }
            }
        }
        this.nbt = nbt;
        this.hashCode = this.calculateHashCode();
    }

    private ItemComparableItemStack(ItemComparableItemStack src) {
        this.item = src.item;
        this.meta = src.meta;
        this.nbt = src.nbt != null ? (NBTTagCompound)src.nbt.copy() : null;
        this.hashCode = src.hashCode;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ItemComparableItemStack)) {
            return false;
        }
        ItemComparableItemStack cmp = (ItemComparableItemStack)obj;
        if (cmp.hashCode != this.hashCode) {
            return false;
        }
        if (cmp == this) {
            return true;
        }
        return cmp.item == this.item && cmp.meta == this.meta && (cmp.nbt == null && this.nbt == null || cmp.nbt != null && this.nbt != null && cmp.nbt.equals((Object)this.nbt));
    }

    public int hashCode() {
        return this.hashCode;
    }

    private int calculateHashCode() {
        int ret = 0;
        if (this.item != null) {
            ret = System.identityHashCode((Object)this.item);
        }
        ret = ret * 31 + this.meta;
        if (this.nbt != null) {
            ret = ret * 61 + this.nbt.hashCode();
        }
        return ret;
    }

    public ItemComparableItemStack copy() {
        if (this.nbt == null) {
            return this;
        }
        return new ItemComparableItemStack(this);
    }

    public ItemStack toStack() {
        return this.toStack(1);
    }

    public ItemStack toStack(int size) {
        if (this.item == null) {
            return null;
        }
        ItemStack ret = new ItemStack(this.item, size, this.meta);
        ret.setTagCompound(this.nbt);
        return ret;
    }
}

