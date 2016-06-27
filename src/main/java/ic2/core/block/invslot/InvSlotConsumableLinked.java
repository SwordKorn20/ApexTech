/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package ic2.core.block.invslot;

import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumable;
import ic2.core.util.StackUtil;
import net.minecraft.item.ItemStack;

public class InvSlotConsumableLinked
extends InvSlotConsumable {
    public final InvSlot linkedSlot;

    public InvSlotConsumableLinked(TileEntityInventory base1, String name1, int count, InvSlot linkedSlot1) {
        super(base1, name1, count);
        this.linkedSlot = linkedSlot1;
    }

    @Override
    public boolean accepts(ItemStack stack) {
        ItemStack required = this.linkedSlot.get();
        if (required == null) {
            return false;
        }
        return StackUtil.checkItemEqualityStrict(required, stack);
    }

    public ItemStack consumeLinked(boolean simulate) {
        ItemStack required = this.linkedSlot.get();
        if (required == null || required.stackSize <= 0) {
            return null;
        }
        ItemStack available = this.consume(required.stackSize, true, true);
        if (available != null && available.stackSize == required.stackSize) {
            return this.consume(required.stackSize, simulate, true);
        }
        return null;
    }
}

