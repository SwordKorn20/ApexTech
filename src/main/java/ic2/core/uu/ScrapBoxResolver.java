/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package ic2.core.uu;

import ic2.api.recipe.IScrapboxManager;
import ic2.api.recipe.Recipes;
import ic2.core.IC2;
import ic2.core.block.state.IIdProvider;
import ic2.core.item.type.CraftingItemType;
import ic2.core.ref.ItemName;
import ic2.core.util.Log;
import ic2.core.util.LogCategory;
import ic2.core.util.StackUtil;
import ic2.core.uu.IRecipeResolver;
import ic2.core.uu.RecipeTransformation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.item.ItemStack;

public class ScrapBoxResolver
implements IRecipeResolver {
    private static final double transformCost = 1.0;

    @Override
    public List<RecipeTransformation> getTransformations() {
        ArrayList<RecipeTransformation> ret = new ArrayList<RecipeTransformation>();
        Map<ItemStack, Float> dropMap = Recipes.scrapboxDrops.getDrops();
        for (Map.Entry<ItemStack, Float> drop : dropMap.entrySet()) {
            if (!StackUtil.check(drop.getKey())) {
                IC2.log.warn(LogCategory.Uu, "Invalid itemstack in scrapbox drops detected.");
                continue;
            }
            int amount = Math.max(1, Math.round(1.0f / drop.getValue().floatValue()));
            List<ItemStack> input = Arrays.asList(new ItemStack[]{StackUtil.copyWithSize(ItemName.crafting.getItemStack(CraftingItemType.scrap_box), amount)});
            ArrayList<List<ItemStack>> inputs = new ArrayList<List<ItemStack>>(1);
            inputs.add(input);
            ret.add(new RecipeTransformation(1.0, inputs, drop.getKey()));
        }
        return ret;
    }
}

