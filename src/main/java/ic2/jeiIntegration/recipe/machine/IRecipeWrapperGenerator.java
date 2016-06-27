/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  mezz.jei.api.recipe.IRecipeWrapper
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.registry.RegistryNamespaced
 */
package ic2.jeiIntegration.recipe.machine;

import ic2.api.recipe.IListRecipeManager;
import ic2.api.recipe.IMachineRecipeManager;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeOutput;
import ic2.api.recipe.Recipes;
import ic2.core.block.state.IIdProvider;
import ic2.core.item.type.CraftingItemType;
import ic2.core.ref.ItemName;
import ic2.jeiIntegration.recipe.machine.IORecipeCategory;
import ic2.jeiIntegration.recipe.machine.IORecipeWrapper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.registry.RegistryNamespaced;

public interface IRecipeWrapperGenerator<T> {
    public static final IRecipeWrapperGenerator<IMachineRecipeManager> basicMachine = new IRecipeWrapperGenerator<IMachineRecipeManager>(){

        @Override
        public List getRecipeList(IORecipeCategory<IMachineRecipeManager> category) {
            ArrayList<IORecipeWrapper> recipes = new ArrayList<IORecipeWrapper>();
            for (IMachineRecipeManager.RecipeIoContainer container : ((IMachineRecipeManager)category.recipeManager).getRecipes()) {
                recipes.add(new IORecipeWrapper(container, category));
            }
            return recipes;
        }
    };
    public static final IRecipeWrapperGenerator<IMachineRecipeManager> recycler = new IRecipeWrapperGenerator<IMachineRecipeManager>(){

        @Override
        public List getRecipeList(IORecipeCategory<IMachineRecipeManager> category) {
            IRecipeInput input;
            if (Recipes.recyclerWhitelist.isEmpty()) {
                final ArrayList<ItemStack> items = new ArrayList<ItemStack>();
                for (Item i : Item.REGISTRY) {
                    ItemStack stack = new ItemStack(i, 1, 32767);
                    if (Recipes.recyclerBlacklist.contains(stack)) continue;
                    items.add(stack);
                }
                input = new IRecipeInput(){

                    @Override
                    public boolean matches(ItemStack subject) {
                        return !Recipes.recyclerBlacklist.contains(subject);
                    }

                    @Override
                    public List<ItemStack> getInputs() {
                        return items;
                    }

                    @Override
                    public int getAmount() {
                        return 1;
                    }
                };
            } else {
                final ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
                for (IRecipeInput input1 : Recipes.recyclerWhitelist) {
                    stacks.addAll(input1.getInputs());
                }
                input = new IRecipeInput(){

                    @Override
                    public boolean matches(ItemStack subject) {
                        return Recipes.recyclerWhitelist.contains(subject);
                    }

                    @Override
                    public List<ItemStack> getInputs() {
                        return stacks;
                    }

                    @Override
                    public int getAmount() {
                        return 1;
                    }
                };
            }
            return Collections.singletonList(new IORecipeWrapper(new IMachineRecipeManager.RecipeIoContainer(input, new RecipeOutput(null, ItemName.crafting.getItemStack(CraftingItemType.scrap))), category));
        }

    };

    public List<IRecipeWrapper> getRecipeList(IORecipeCategory<T> var1);

}

