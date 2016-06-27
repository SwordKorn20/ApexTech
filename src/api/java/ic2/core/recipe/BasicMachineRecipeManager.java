/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.fml.common.eventhandler.EventBus
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.oredict.OreDictionary
 *  net.minecraftforge.oredict.OreDictionary$OreRegisterEvent
 */
package ic2.core.recipe;

import ic2.api.recipe.IMachineRecipeManager;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeInputItemStack;
import ic2.api.recipe.RecipeInputOreDict;
import ic2.api.recipe.RecipeOutput;
import ic2.core.IC2;
import ic2.core.init.MainConfig;
import ic2.core.util.Log;
import ic2.core.util.LogCategory;
import ic2.core.util.StackUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;

public class BasicMachineRecipeManager
implements IMachineRecipeManager {
    private final Map<IRecipeInput, RecipeOutput> recipes = new HashMap<IRecipeInput, RecipeOutput>();
    private final Map<Item, List<IMachineRecipeManager.RecipeIoContainer>> recipeCache = new IdentityHashMap<Item, List<IMachineRecipeManager.RecipeIoContainer>>();
    private final List<IMachineRecipeManager.RecipeIoContainer> uncacheableRecipes = new ArrayList<IMachineRecipeManager.RecipeIoContainer>();
    private boolean oreRegisterEventSubscribed;

    @Override
    public /* varargs */ boolean addRecipe(IRecipeInput input, NBTTagCompound metadata, boolean replace, ItemStack ... outputs) {
        return this.addRecipe(input, new RecipeOutput(metadata, outputs), replace);
    }

    @Override
    public RecipeOutput getOutputFor(ItemStack input, boolean adjustInput) {
        if (input == null) {
            return null;
        }
        IMachineRecipeManager.RecipeIoContainer data = this.getRecipe(input);
        if (data == null) {
            return null;
        }
        if (!(input.stackSize < data.input.getAmount() || input.getItem().hasContainerItem(input) && input.stackSize != data.input.getAmount())) {
            if (adjustInput) {
                if (input.getItem().hasContainerItem(input)) {
                    ItemStack container = input.getItem().getContainerItem(input);
                    input.setItem(container.getItem());
                    input.stackSize = container.stackSize;
                    input.setItemDamage(container.getItemDamage());
                    input.setTagCompound(container.getTagCompound());
                } else {
                    input.stackSize -= data.input.getAmount();
                }
            }
            return data.output;
        }
        return null;
    }

    @Override
    public Iterable<IMachineRecipeManager.RecipeIoContainer> getRecipes() {
        return new Iterable<IMachineRecipeManager.RecipeIoContainer>(){

            @Override
            public Iterator<IMachineRecipeManager.RecipeIoContainer> iterator() {
                return new Iterator<IMachineRecipeManager.RecipeIoContainer>(){
                    private final Iterator<Map.Entry<IRecipeInput, RecipeOutput>> recipeIt;
                    private IRecipeInput lastInput;

                    @Override
                    public boolean hasNext() {
                        return this.recipeIt.hasNext();
                    }

                    @Override
                    public IMachineRecipeManager.RecipeIoContainer next() {
                        Map.Entry<IRecipeInput, RecipeOutput> nextRaw = this.recipeIt.next();
                        this.lastInput = nextRaw.getKey();
                        return new IMachineRecipeManager.RecipeIoContainer(nextRaw.getKey(), nextRaw.getValue());
                    }

                    @Override
                    public void remove() {
                        this.recipeIt.remove();
                        BasicMachineRecipeManager.this.removeCachedRecipes(this.lastInput);
                    }
                };
            }

        };
    }

    @Override
    public boolean isIterable() {
        return true;
    }

    @SubscribeEvent
    public void onOreRegister(OreDictionary.OreRegisterEvent event) {
        Item item = event.getOre().getItem();
        if (item == null) {
            IC2.log.warn(LogCategory.Recipe, "Found null item ore dict registration.", new Throwable());
            return;
        }
        ArrayList<IMachineRecipeManager.RecipeIoContainer> matchingRecipes = new ArrayList<IMachineRecipeManager.RecipeIoContainer>();
        for (Map.Entry<IRecipeInput, RecipeOutput> data2 : this.recipes.entrySet()) {
            if (data2.getKey().getClass() != RecipeInputOreDict.class) continue;
            RecipeInputOreDict recipe = (RecipeInputOreDict)data2.getKey();
            if (!recipe.input.equals(event.getName())) continue;
            matchingRecipes.add(new IMachineRecipeManager.RecipeIoContainer(data2.getKey(), data2.getValue()));
        }
        for (IMachineRecipeManager.RecipeIoContainer data : matchingRecipes) {
            this.addToCache(item, data);
        }
    }

    private IMachineRecipeManager.RecipeIoContainer getRecipe(ItemStack input) {
        List<IMachineRecipeManager.RecipeIoContainer> recipes = this.recipeCache.get((Object)input.getItem());
        if (recipes != null) {
            for (IMachineRecipeManager.RecipeIoContainer recipe : recipes) {
                if (!recipe.input.matches(input)) continue;
                return recipe;
            }
        }
        for (IMachineRecipeManager.RecipeIoContainer data : this.uncacheableRecipes) {
            if (!data.input.matches(input)) continue;
            return data;
        }
        return null;
    }

    private boolean addRecipe(IRecipeInput input, RecipeOutput output, boolean replace) {
        if (input == null) {
            this.displayError("The recipe input is null");
            return false;
        }
        Iterator it = output.items.listIterator();
        while (it.hasNext()) {
            ItemStack stack = it.next();
            if (stack == null) {
                this.displayError("An output ItemStack is null.");
                return false;
            }
            if (!StackUtil.check(stack)) {
                this.displayError("The output ItemStack " + StackUtil.toStringSafe(stack) + " is invalid.");
                return false;
            }
            if (input.matches(stack) && (output.metadata == null || !output.metadata.hasKey("ignoreSameInputOutput"))) {
                this.displayError("The output ItemStack " + stack.toString() + " is the same as the recipe input " + input + ".");
                return false;
            }
            it.set(stack.copy());
        }
        for (ItemStack is : input.getInputs()) {
            IMachineRecipeManager.RecipeIoContainer data = this.getRecipe(is);
            if (data == null) continue;
            if (replace) {
                do {
                    this.recipes.remove(data.input);
                    this.removeCachedRecipes(data.input);
                } while ((data = this.getRecipe(is)) != null);
                continue;
            }
            return false;
        }
        this.recipes.put(input, output);
        this.addToCache(input, output);
        return true;
    }

    private void addToCache(IRecipeInput input, RecipeOutput output) {
        IMachineRecipeManager.RecipeIoContainer data = new IMachineRecipeManager.RecipeIoContainer(input, output);
        Collection<Item> items = this.getItemsFromRecipe(input);
        if (items != null) {
            for (Item item : items) {
                this.addToCache(item, data);
            }
            if (input.getClass() == RecipeInputOreDict.class && !this.oreRegisterEventSubscribed) {
                MinecraftForge.EVENT_BUS.register((Object)this);
                this.oreRegisterEventSubscribed = true;
            }
        } else {
            this.uncacheableRecipes.add(data);
        }
    }

    private void addToCache(Item item, IMachineRecipeManager.RecipeIoContainer data) {
        List<IMachineRecipeManager.RecipeIoContainer> recipes = this.recipeCache.get((Object)item);
        if (recipes == null) {
            recipes = new ArrayList<IMachineRecipeManager.RecipeIoContainer>();
            this.recipeCache.put(item, recipes);
        }
        if (!recipes.contains(data)) {
            recipes.add(data);
        }
    }

    private void removeCachedRecipes(IRecipeInput input) {
        Collection<Item> items = this.getItemsFromRecipe(input);
        if (items != null) {
            for (Item item : items) {
                List<IMachineRecipeManager.RecipeIoContainer> recipes = this.recipeCache.get((Object)item);
                if (recipes == null) {
                    IC2.log.warn(LogCategory.Recipe, "Inconsistent recipe cache, the entry for the item " + (Object)item + " is missing.");
                    continue;
                }
                recipes.remove(input);
                if (!recipes.isEmpty()) continue;
                this.recipeCache.remove((Object)item);
            }
        } else {
            Iterator<IMachineRecipeManager.RecipeIoContainer> it = this.uncacheableRecipes.iterator();
            while (it.hasNext()) {
                IMachineRecipeManager.RecipeIoContainer data = it.next();
                if (data.input != input) continue;
                it.remove();
            }
        }
    }

    private Collection<Item> getItemsFromRecipe(IRecipeInput recipe) {
        Class recipeClass = recipe.getClass();
        if (recipeClass == RecipeInputItemStack.class || recipeClass == RecipeInputOreDict.class) {
            List<ItemStack> inputs = recipe.getInputs();
            Set<Item> ret = Collections.newSetFromMap(new IdentityHashMap(inputs.size()));
            for (ItemStack stack : inputs) {
                ret.add(stack.getItem());
            }
            return ret;
        }
        return null;
    }

    private void displayError(String msg) {
        if (!MainConfig.ignoreInvalidRecipes) {
            throw new RuntimeException(msg);
        }
        IC2.log.warn(LogCategory.Recipe, msg);
    }

}

