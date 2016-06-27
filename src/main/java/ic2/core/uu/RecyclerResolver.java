/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package ic2.core.uu;

import ic2.api.recipe.IMachineRecipeManager;
import ic2.api.recipe.RecipeOutput;
import ic2.api.recipe.Recipes;
import ic2.core.block.machine.tileentity.TileEntityRecycler;
import ic2.core.block.state.IIdProvider;
import ic2.core.item.type.CraftingItemType;
import ic2.core.ref.ItemName;
import ic2.core.util.ItemComparableItemStack;
import ic2.core.uu.ILateRecipeResolver;
import ic2.core.uu.RecipeTransformation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.item.ItemStack;

public class RecyclerResolver
implements ILateRecipeResolver {
    private static final double transformCost = 55.0 * (double)TileEntityRecycler.recycleChance() / 4000.0 * 107.0;

    @Override
    public List<RecipeTransformation> getTransformations(Iterable<ItemComparableItemStack> obtainableStacks) {
        ArrayList<ItemStack> input = new ArrayList<ItemStack>();
        for (ItemComparableItemStack obtainableStack : obtainableStacks) {
            ItemStack stack = obtainableStack.toStack(TileEntityRecycler.recycleChance());
            if (stack == null || Recipes.recycler.getOutputFor((ItemStack)stack, (boolean)false).items.isEmpty()) continue;
            input.add(stack);
        }
        ArrayList<List<ItemStack>> inputs = new ArrayList<List<ItemStack>>(1);
        inputs.add(input);
        return Arrays.asList(new RecipeTransformation(transformCost, inputs, ItemName.crafting.getItemStack(CraftingItemType.scrap)));
    }
}

