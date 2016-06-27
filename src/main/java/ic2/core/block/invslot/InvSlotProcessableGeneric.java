/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 */
package ic2.core.block.invslot;

import ic2.api.recipe.IMachineRecipeManager;
import ic2.api.recipe.RecipeOutput;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlotProcessable;
import ic2.core.item.ItemUpgradeModule;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class InvSlotProcessableGeneric
extends InvSlotProcessable {
    public IMachineRecipeManager recipeManager;

    public InvSlotProcessableGeneric(TileEntityInventory base1, String name1, int count, IMachineRecipeManager recipeManager1) {
        super(base1, name1, count);
        this.recipeManager = recipeManager1;
    }

    @Override
    public boolean accepts(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemUpgradeModule) {
            return false;
        }
        ItemStack tmp = stack.copy();
        tmp.stackSize = Integer.MAX_VALUE;
        return this.getOutputFor(tmp, false, true) != null;
    }

    @Override
    public RecipeOutput process() {
        ItemStack input = this.get();
        if (input == null && !this.allowEmptyInput()) {
            return null;
        }
        RecipeOutput output = this.getOutputFor(input, false, false);
        if (output == null) {
            return null;
        }
        ArrayList<ItemStack> itemsCopy = new ArrayList<ItemStack>(output.items.size());
        for (ItemStack stack : output.items) {
            itemsCopy.add(stack.copy());
        }
        return new RecipeOutput(output.metadata, itemsCopy);
    }

    @Override
    public void consume() {
        ItemStack input = this.get();
        if (input == null && !this.allowEmptyInput()) {
            throw new IllegalStateException("consume from empty slot");
        }
        RecipeOutput output = this.getOutputFor(input, true, false);
        if (output == null) {
            throw new IllegalStateException("consume without a processing result");
        }
        if (input != null && input.stackSize <= 0) {
            this.put(null);
        }
    }

    public void setRecipeManager(IMachineRecipeManager recipeManager1) {
        this.recipeManager = recipeManager1;
    }

    protected RecipeOutput getOutputFor(ItemStack input, boolean adjustInput, boolean forAccept) {
        return this.recipeManager.getOutputFor(input, adjustInput);
    }

    protected boolean allowEmptyInput() {
        return false;
    }
}

