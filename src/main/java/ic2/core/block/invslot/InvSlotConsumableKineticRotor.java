/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 */
package ic2.core.block.invslot;

import ic2.api.item.IKineticRotor;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumableClass;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class InvSlotConsumableKineticRotor
extends InvSlotConsumableClass {
    private final IKineticRotor.GearboxType type;

    public InvSlotConsumableKineticRotor(TileEntityInventory base1, String name1, InvSlot.Access access1, int count, InvSlot.InvSide preferredSide1, IKineticRotor.GearboxType type) {
        super(base1, name1, access1, count, preferredSide1, IKineticRotor.class);
        this.type = type;
    }

    @Override
    public boolean accepts(ItemStack stack) {
        if (super.accepts(stack)) {
            return ((IKineticRotor)stack.getItem()).isAcceptedType(stack, this.type);
        }
        return false;
    }
}

