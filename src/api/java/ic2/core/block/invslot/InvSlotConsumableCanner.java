/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package ic2.core.block.invslot;

import ic2.api.recipe.RecipeOutput;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlotConsumableLiquid;
import ic2.core.block.invslot.InvSlotProcessable;
import ic2.core.block.invslot.InvSlotProcessableCanner;
import ic2.core.block.machine.tileentity.TileEntityCanner;
import net.minecraft.item.ItemStack;

public class InvSlotConsumableCanner
extends InvSlotConsumableLiquid {
    public InvSlotConsumableCanner(TileEntityCanner base1, String name1, int count) {
        super(base1, name1, count);
    }

    @Override
    public boolean accepts(ItemStack stack) {
        switch (((TileEntityCanner)this.base).getMode()) {
            case BottleSolid: {
                return ((InvSlotProcessableCanner)((TileEntityCanner)this.base).inputSlot).getOutput(stack, ((TileEntityCanner)this.base).inputSlot.get(), false, true) != null;
            }
            case BottleLiquid: 
            case EmptyLiquid: 
            case EnrichLiquid: {
                return super.accepts(stack);
            }
        }
        assert (false);
        return false;
    }

}

