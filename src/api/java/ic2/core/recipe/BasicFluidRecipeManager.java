/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.fluids.FluidStack
 *  net.minecraftforge.fml.common.eventhandler.EventBus
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.oredict.OreDictionary
 *  net.minecraftforge.oredict.OreDictionary$OreRegisterEvent
 */
package ic2.core.recipe;

import ic2.api.recipe.IFluidRecipeManager;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeInputItemStack;
import ic2.api.recipe.RecipeInputOreDict;
import ic2.api.recipe.RecipeOutputFluid;
import ic2.core.IC2;
import ic2.core.init.MainConfig;
import ic2.core.util.LiquidUtil;
import ic2.core.util.Log;
import ic2.core.util.LogCategory;
import java.util.ArrayList;
import java.util.Collection;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;

public class BasicFluidRecipeManager
implements IFluidRecipeManager {
    private final Map<IRecipeInput, RecipeOutputFluid> recipes = new HashMap<IRecipeInput, RecipeOutputFluid>();
    private final Map<Item, Map<Integer, IFluidRecipeManager.RecipeIoContainerFluid>> recipeCache = new IdentityHashMap<Item, Map<Integer, IFluidRecipeManager.RecipeIoContainerFluid>>();
    private final List<IFluidRecipeManager.RecipeIoContainerFluid> uncacheableRecipes = new ArrayList<IFluidRecipeManager.RecipeIoContainerFluid>();
    private boolean oreRegisterEventSubscribed;

    @Override
    public /* varargs */ boolean addRecipe(IRecipeInput input, NBTTagCompound metadata, boolean replace, FluidStack ... outputs) {
        return this.addRecipe(input, new RecipeOutputFluid(metadata, outputs), replace);
    }

    @Override
    public RecipeOutputFluid getOutputFor(ItemStack input, boolean adjustInput) {
        if (input == null) {
            return null;
        }
        IFluidRecipeManager.RecipeIoContainerFluid data = this.getRecipe(input);
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
    public Iterable<IFluidRecipeManager.RecipeIoContainerFluid> getRecipes() {
        return new Iterable<IFluidRecipeManager.RecipeIoContainerFluid>(){

            @Override
            public Iterator<IFluidRecipeManager.RecipeIoContainerFluid> iterator() {
                return new Iterator<IFluidRecipeManager.RecipeIoContainerFluid>(){
                    private final Iterator<Map.Entry<IRecipeInput, RecipeOutputFluid>> recipeIt;
                    private IRecipeInput lastInput;

                    @Override
                    public boolean hasNext() {
                        return this.recipeIt.hasNext();
                    }

                    @Override
                    public IFluidRecipeManager.RecipeIoContainerFluid next() {
                        Map.Entry<IRecipeInput, RecipeOutputFluid> nextRaw = this.recipeIt.next();
                        this.lastInput = nextRaw.getKey();
                        return new IFluidRecipeManager.RecipeIoContainerFluid(nextRaw.getKey(), nextRaw.getValue());
                    }

                    @Override
                    public void remove() {
                        this.recipeIt.remove();
                        BasicFluidRecipeManager.this.removeCachedRecipes(this.lastInput);
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
        ArrayList<IFluidRecipeManager.RecipeIoContainerFluid> datas = new ArrayList<IFluidRecipeManager.RecipeIoContainerFluid>();
        for (Map.Entry<IRecipeInput, RecipeOutputFluid> data2 : this.recipes.entrySet()) {
            if (data2.getKey().getClass() != RecipeInputOreDict.class) continue;
            RecipeInputOreDict recipe = (RecipeInputOreDict)data2.getKey();
            if (!recipe.input.equals(event.getName())) continue;
            datas.add(new IFluidRecipeManager.RecipeIoContainerFluid(data2.getKey(), data2.getValue()));
        }
        for (IFluidRecipeManager.RecipeIoContainerFluid data : datas) {
            this.addToCache(event.getOre(), data);
        }
    }

    private IFluidRecipeManager.RecipeIoContainerFluid getRecipe(ItemStack input) {
        Map<Integer, IFluidRecipeManager.RecipeIoContainerFluid> metaMap = this.recipeCache.get((Object)input.getItem());
        if (metaMap != null) {
            Object data = metaMap.get(32767);
            if (data != null) {
                return data;
            }
            int meta = input.getItemDamage();
            data = metaMap.get(meta);
            if (data != null) {
                return data;
            }
        }
        for (IFluidRecipeManager.RecipeIoContainerFluid data : this.uncacheableRecipes) {
            if (!data.input.matches(input)) continue;
            return data;
        }
        return null;
    }

    private boolean addRecipe(IRecipeInput input, RecipeOutputFluid output, boolean replace) {
        if (input == null) {
            this.displayError("The recipe input is null");
            return false;
        }
        Iterator it = output.outputs.listIterator();
        while (it.hasNext()) {
            FluidStack fluidStack = it.next();
            if (fluidStack == null) {
                this.displayError("An output FluidStack is null.");
                return false;
            }
            if (!LiquidUtil.check(fluidStack)) {
                this.displayError("The output FluidStack " + LiquidUtil.toStringSafe(fluidStack) + " is invalid.");
                return false;
            }
            it.set(fluidStack.copy());
        }
        for (ItemStack is : input.getInputs()) {
            IFluidRecipeManager.RecipeIoContainerFluid data = this.getRecipe(is);
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

    private void addToCache(IRecipeInput input, RecipeOutputFluid output) {
        IFluidRecipeManager.RecipeIoContainerFluid data = new IFluidRecipeManager.RecipeIoContainerFluid(input, output);
        List<ItemStack> stacks = this.getStacksFromRecipe(input);
        if (stacks != null) {
            for (ItemStack stack : stacks) {
                this.addToCache(stack, data);
            }
            if (input.getClass() == RecipeInputOreDict.class && !this.oreRegisterEventSubscribed) {
                MinecraftForge.EVENT_BUS.register((Object)this);
                this.oreRegisterEventSubscribed = true;
            }
        } else {
            this.uncacheableRecipes.add(data);
        }
    }

    private void addToCache(ItemStack stack, IFluidRecipeManager.RecipeIoContainerFluid data) {
        Item item = stack.getItem();
        Map<Integer, IFluidRecipeManager.RecipeIoContainerFluid> metaMap = this.recipeCache.get((Object)item);
        if (metaMap == null) {
            metaMap = new HashMap<Integer, IFluidRecipeManager.RecipeIoContainerFluid>();
            this.recipeCache.put(item, metaMap);
        }
        int meta = stack.getItemDamage();
        metaMap.put(meta, data);
    }

    private void removeCachedRecipes(IRecipeInput input) {
        List<ItemStack> stacks = this.getStacksFromRecipe(input);
        if (stacks != null) {
            for (ItemStack stack : stacks) {
                Item item = stack.getItem();
                int meta = stack.getItemDamage();
                Map<Integer, IFluidRecipeManager.RecipeIoContainerFluid> map = this.recipeCache.get((Object)item);
                if (map == null) {
                    IC2.log.warn(LogCategory.Recipe, "Inconsistent recipe cache, the entry for the item " + (Object)item + " is missing.");
                    continue;
                }
                map.remove(meta);
                if (!map.isEmpty()) continue;
                this.recipeCache.remove((Object)item);
            }
        } else {
            Iterator<IFluidRecipeManager.RecipeIoContainerFluid> it = this.uncacheableRecipes.iterator();
            while (it.hasNext()) {
                IFluidRecipeManager.RecipeIoContainerFluid data = it.next();
                if (data.input != input) continue;
                it.remove();
            }
        }
    }

    private List<ItemStack> getStacksFromRecipe(IRecipeInput recipe) {
        if (recipe.getClass() == RecipeInputItemStack.class) {
            return recipe.getInputs();
        }
        if (recipe.getClass() == RecipeInputOreDict.class) {
            Integer meta = ((RecipeInputOreDict)recipe).meta;
            if (meta == null) {
                return recipe.getInputs();
            }
            ArrayList<ItemStack> ret = new ArrayList<ItemStack>(recipe.getInputs());
            ListIterator<ItemStack> it = ret.listIterator();
            while (it.hasNext()) {
                ItemStack stack = it.next();
                if (stack.getItemDamage() == meta.intValue()) continue;
                stack = stack.copy();
                stack.setItemDamage(meta.intValue());
                it.set(stack);
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

