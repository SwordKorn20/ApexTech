/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.inventory.InventoryCrafting
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.crafting.CraftingManager
 *  net.minecraft.item.crafting.IRecipe
 *  net.minecraft.world.World
 *  net.minecraftforge.common.ForgeHooks
 *  net.minecraftforge.fluids.IFluidContainerItem
 */
package ic2.core.recipe;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItemManager;
import ic2.api.recipe.IRecipeInput;
import ic2.core.init.MainConfig;
import ic2.core.recipe.AdvRecipe;
import ic2.core.util.StackUtil;
import java.util.List;
import java.util.Vector;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fluids.IFluidContainerItem;

public class AdvShapelessRecipe
implements IRecipe {
    public ItemStack output;
    public IRecipeInput[] input;
    public boolean hidden;

    public static /* varargs */ void addAndRegister(ItemStack result, Object ... args) {
        block2 : {
            try {
                CraftingManager.getInstance().getRecipeList().add(new AdvShapelessRecipe(result, args));
            }
            catch (RuntimeException e) {
                if (MainConfig.ignoreInvalidRecipes) break block2;
                throw e;
            }
        }
    }

    public /* varargs */ AdvShapelessRecipe(ItemStack result, Object ... args) {
        if (result == null) {
            AdvRecipe.displayError("null result", null, null, true);
        } else {
            result = result.copy();
        }
        this.input = new IRecipeInput[args.length - ic2.core.util.Util.countInArray(args, Boolean.class)];
        int inputIndex = 0;
        for (Object o : args) {
            if (o instanceof Boolean) {
                this.hidden = (Boolean)o;
                continue;
            }
            try {
                this.input[inputIndex++] = AdvRecipe.getRecipeObject(o);
                continue;
            }
            catch (Exception e) {
                e.printStackTrace();
                AdvRecipe.displayError("unknown type", "O: " + o + "\nT: " + o.getClass().getName(), result, true);
            }
        }
        if (inputIndex != this.input.length) {
            AdvRecipe.displayError("length calculation error", "I: " + inputIndex + "\nL: " + this.input.length, result, true);
        }
        this.output = result;
    }

    public boolean matches(InventoryCrafting inventorycrafting, World world) {
        return this.getCraftingResult(inventorycrafting) != null;
    }

    public ItemStack getCraftingResult(InventoryCrafting inventorycrafting) {
        int offerSize = inventorycrafting.getSizeInventory();
        if (offerSize < this.input.length) {
            return null;
        }
        Vector<IRecipeInput> unmatched = new Vector<IRecipeInput>();
        for (IRecipeInput o : this.input) {
            unmatched.add(o);
        }
        double outputCharge = 0.0;
        block1 : for (int i = 0; i < offerSize; ++i) {
            ItemStack offer = inventorycrafting.getStackInSlot(i);
            if (offer == null) continue;
            for (int j = 0; j < unmatched.size(); ++j) {
                if (!((IRecipeInput)unmatched.get(j)).matches(offer)) continue;
                outputCharge += ElectricItem.manager.getCharge(offer);
                unmatched.remove(j);
                continue block1;
            }
            return null;
        }
        if (!unmatched.isEmpty()) {
            return null;
        }
        ItemStack ret = this.output.copy();
        Item item = ret.getItem();
        ElectricItem.manager.charge(ret, outputCharge, Integer.MAX_VALUE, true, false);
        if (item instanceof IFluidContainerItem) {
            StackUtil.getOrCreateNbtData(ret);
        }
        return ret;
    }

    public int getRecipeSize() {
        return this.input.length;
    }

    public ItemStack getRecipeOutput() {
        return this.output;
    }

    public boolean canShow() {
        return AdvRecipe.canShow(this.input, this.output, this.hidden);
    }

    public ItemStack[] getRemainingItems(InventoryCrafting inv) {
        return ForgeHooks.defaultRecipeGetRemainingItems((InventoryCrafting)inv);
    }
}

