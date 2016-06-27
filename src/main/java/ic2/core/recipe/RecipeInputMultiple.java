/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package ic2.core.recipe;

import ic2.api.recipe.IRecipeInput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.item.ItemStack;

public class RecipeInputMultiple
implements IRecipeInput {
    private IRecipeInput[] inputs;

    public /* varargs */ RecipeInputMultiple(IRecipeInput ... inputs) {
        this.inputs = inputs;
    }

    public RecipeInputMultiple(List<IRecipeInput> inputs) {
        this.inputs = inputs.toArray(new IRecipeInput[0]);
    }

    @Override
    public boolean matches(ItemStack subject) {
        for (IRecipeInput input : this.inputs) {
            if (!input.matches(subject)) continue;
            return true;
        }
        return false;
    }

    @Override
    public int getAmount() {
        return 1;
    }

    @Override
    public List<ItemStack> getInputs() {
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        for (IRecipeInput input : this.inputs) {
            list.addAll(input.getInputs());
        }
        return list;
    }
}

