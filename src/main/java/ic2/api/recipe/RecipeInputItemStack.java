/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 */
package ic2.api.recipe;

import ic2.api.recipe.IRecipeInput;
import java.util.Arrays;
import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class RecipeInputItemStack
implements IRecipeInput {
    public final ItemStack input;
    public final int amount;

    public RecipeInputItemStack(ItemStack aInput) {
        this(aInput, aInput.stackSize);
    }

    public RecipeInputItemStack(ItemStack aInput, int aAmount) {
        if (aInput.getItem() == null) {
            throw new IllegalArgumentException("Invalid item stack specfied");
        }
        this.input = aInput.copy();
        this.amount = aAmount;
    }

    @Override
    public boolean matches(ItemStack subject) {
        return subject.getItem() == this.input.getItem() && (subject.getItemDamage() == this.input.getItemDamage() || this.input.getItemDamage() == 32767);
    }

    @Override
    public int getAmount() {
        return this.amount;
    }

    @Override
    public List<ItemStack> getInputs() {
        return Arrays.asList(new ItemStack[]{this.input});
    }

    public String toString() {
        ItemStack stack = this.input.copy();
        this.input.stackSize = this.amount;
        return "RInputItemStack<" + (Object)stack + ">";
    }
}

