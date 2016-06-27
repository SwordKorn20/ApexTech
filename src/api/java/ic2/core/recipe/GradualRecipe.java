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
 */
package ic2.core.recipe;

import ic2.core.init.MainConfig;
import ic2.core.item.ItemGradualInt;
import ic2.core.recipe.AdvRecipe;
import ic2.core.util.StackUtil;
import java.util.List;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public class GradualRecipe
implements IRecipe {
    public ItemGradualInt item;
    public ItemStack chargeMaterial;
    public int amount;
    public boolean hidden;

    public static /* varargs */ void addAndRegister(ItemStack itemToFill, int amount, Object ... args) {
        block10 : {
            try {
                if (itemToFill == null) {
                    AdvRecipe.displayError("Null item to fill", null, null, true);
                } else {
                    if (!(itemToFill.getItem() instanceof ItemGradualInt)) {
                        AdvRecipe.displayError("Filling item must extends ItemGradualInt", null, itemToFill, true);
                    }
                    ItemGradualInt fillingItem = (ItemGradualInt)itemToFill.getItem();
                    Boolean hidden = false;
                    ItemStack filler = null;
                    for (Object o : args) {
                        if (o instanceof Boolean) {
                            hidden = (Boolean)o;
                            continue;
                        }
                        try {
                            filler = AdvRecipe.getRecipeObject(o).getInputs().get(0);
                            break;
                        }
                        catch (IndexOutOfBoundsException e) {
                            AdvRecipe.displayError("Invalid filler item: " + o, null, itemToFill, true);
                            continue;
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                            AdvRecipe.displayError("unknown type", "O: " + o + "\nT: " + o.getClass().getName(), itemToFill, true);
                        }
                    }
                    CraftingManager.getInstance().getRecipeList().add(new GradualRecipe(fillingItem, filler, amount, hidden));
                }
            }
            catch (RuntimeException e) {
                if (MainConfig.ignoreInvalidRecipes) break block10;
                throw e;
            }
        }
    }

    public GradualRecipe(ItemGradualInt item, ItemStack chargeMaterial, int amount) {
        this(item, chargeMaterial, amount, false);
    }

    public GradualRecipe(ItemGradualInt item, ItemStack chargeMaterial, int amount, boolean hidden) {
        this.item = item;
        this.chargeMaterial = chargeMaterial;
        this.amount = amount;
        this.hidden = hidden;
    }

    public boolean matches(InventoryCrafting ic, World world) {
        return this.getCraftingResult(ic) != null;
    }

    public ItemStack getCraftingResult(InventoryCrafting ic) {
        ItemStack gridItem = null;
        int chargeMats = 0;
        for (int slot = 0; slot < ic.getSizeInventory(); ++slot) {
            ItemStack stack = ic.getStackInSlot(slot);
            if (stack == null) continue;
            if (gridItem == null && stack.getItem() == this.item) {
                gridItem = stack;
                continue;
            }
            if (StackUtil.checkItemEquality(stack, this.chargeMaterial)) {
                ++chargeMats;
                continue;
            }
            return null;
        }
        if (gridItem != null && chargeMats > 0) {
            ItemStack stack = gridItem.copy();
            int damage = this.item.getCustomDamage(stack) - this.amount * chargeMats;
            if (damage > this.item.getMaxCustomDamage(stack)) {
                damage = this.item.getMaxCustomDamage(stack);
            } else if (damage < 0) {
                damage = 0;
            }
            this.item.setCustomDamage(stack, damage);
            return stack;
        }
        return null;
    }

    public int getRecipeSize() {
        return 2;
    }

    public ItemStack getRecipeOutput() {
        return new ItemStack((Item)this.item);
    }

    public ItemStack[] getRemainingItems(InventoryCrafting inv) {
        return ForgeHooks.defaultRecipeGetRemainingItems((InventoryCrafting)inv);
    }

    public boolean canShow() {
        return AdvRecipe.canShow(new Object[]{this.chargeMaterial}, new ItemStack((Item)this.item), this.hidden);
    }
}

