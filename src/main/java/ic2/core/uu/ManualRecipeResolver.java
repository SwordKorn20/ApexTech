/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package ic2.core.uu;

import ic2.core.block.state.IIdProvider;
import ic2.core.item.type.NuclearResourceType;
import ic2.core.ref.ItemName;
import ic2.core.uu.IRecipeResolver;
import ic2.core.uu.RecipeTransformation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.item.ItemStack;

public class ManualRecipeResolver
implements IRecipeResolver {
    private static final double transformCost = 0.0;

    @Override
    public List<RecipeTransformation> getTransformations() {
        ArrayList<RecipeTransformation> ret = new ArrayList<RecipeTransformation>();
        ret.add(new RecipeTransformation(0.0, this.toList(ItemName.uranium_fuel_rod.getItemStack()), ItemName.nuclear.getItemStack(NuclearResourceType.depleted_uranium)));
        ret.add(new RecipeTransformation(0.0, this.toList(ItemName.dual_uranium_fuel_rod.getItemStack()), ItemName.nuclear.getItemStack(NuclearResourceType.depleted_dual_uranium)));
        ret.add(new RecipeTransformation(0.0, this.toList(ItemName.quad_uranium_fuel_rod.getItemStack()), ItemName.nuclear.getItemStack(NuclearResourceType.depleted_quad_uranium)));
        ret.add(new RecipeTransformation(0.0, this.toList(ItemName.mox_fuel_rod.getItemStack()), ItemName.nuclear.getItemStack(NuclearResourceType.depleted_mox)));
        ret.add(new RecipeTransformation(0.0, this.toList(ItemName.dual_mox_fuel_rod.getItemStack()), ItemName.nuclear.getItemStack(NuclearResourceType.depleted_dual_mox)));
        ret.add(new RecipeTransformation(0.0, this.toList(ItemName.quad_mox_fuel_rod.getItemStack()), ItemName.nuclear.getItemStack(NuclearResourceType.depleted_quad_mox)));
        return ret;
    }

    private /* varargs */ List<List<ItemStack>> toList(ItemStack ... stacks) {
        ArrayList<List<ItemStack>> ret = new ArrayList<List<ItemStack>>(stacks.length);
        for (ItemStack stack : stacks) {
            ret.add(Arrays.asList(new ItemStack[]{stack}));
        }
        return ret;
    }
}

