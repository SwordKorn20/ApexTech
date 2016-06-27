/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.crafting.FurnaceRecipes
 *  net.minecraft.nbt.NBTTagCompound
 */
package ic2.core.block.invslot;

import ic2.api.recipe.RecipeOutput;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlotProcessable;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;

public class InvSlotProcessableSmelting
extends InvSlotProcessable {
    public InvSlotProcessableSmelting(TileEntityInventory base1, String name1, int count) {
        super(base1, name1, count);
    }

    @Override
    public boolean accepts(ItemStack stack) {
        return FurnaceRecipes.instance().getSmeltingResult(stack) != null;
    }

    @Override
    public RecipeOutput process() {
        ItemStack input = this.consume(1, true, true);
        if (input == null) {
            return null;
        }
        return new RecipeOutput(null, FurnaceRecipes.instance().getSmeltingResult(input).copy());
    }

    @Override
    public void consume() {
        this.consume(1, false, true);
    }
}

