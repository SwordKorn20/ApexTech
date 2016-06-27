/*
 * Decompiled with CFR 0_114.
 */
package ic2.core.uu;

import ic2.core.util.ItemComparableItemStack;
import ic2.core.uu.RecipeTransformation;
import java.util.List;

public interface ILateRecipeResolver {
    public List<RecipeTransformation> getTransformations(Iterable<ItemComparableItemStack> var1);
}

