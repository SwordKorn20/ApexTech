/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemFood
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 */
package ic2.core.block.invslot;

import ic2.api.recipe.ICannerBottleRecipeManager;
import ic2.api.recipe.IMachineRecipeManager;
import ic2.api.recipe.RecipeOutput;
import ic2.api.recipe.Recipes;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlotConsumableSolidCanner;
import ic2.core.block.invslot.InvSlotProcessableGeneric;
import ic2.core.block.machine.tileentity.TileEntitySolidCanner;
import ic2.core.block.state.IIdProvider;
import ic2.core.item.type.CraftingItemType;
import ic2.core.ref.ItemName;
import ic2.core.util.StackUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class InvSlotProcessableSolidCanner
extends InvSlotProcessableGeneric {
    public InvSlotProcessableSolidCanner(TileEntitySolidCanner base1, String name1, int count) {
        super(base1, name1, count, null);
    }

    @Override
    public void consume() {
        super.consume();
        ItemStack containerStack = ((TileEntitySolidCanner)this.base).canInputSlot.get();
        if (containerStack != null && containerStack.stackSize <= 0) {
            ((TileEntitySolidCanner)this.base).canInputSlot.put(null);
        }
    }

    @Override
    protected RecipeOutput getOutputFor(ItemStack input, boolean adjustInput, boolean forAccept) {
        return this.getOutput(((TileEntitySolidCanner)this.base).canInputSlot.get(), input, adjustInput, forAccept);
    }

    @Override
    protected boolean allowEmptyInput() {
        return true;
    }

    protected RecipeOutput getOutput(ItemStack container, ItemStack fill, boolean adjustInput, boolean forAccept) {
        RecipeOutput output = Recipes.cannerBottle.getOutputFor(container, fill, adjustInput, forAccept);
        if (output == null) {
            if (forAccept ? container == null && fill == null : container == null || fill == null) {
                return null;
            }
            if (forAccept && container == null) {
                if (fill.getItem() instanceof ItemFood) {
                    ItemStack ret = ItemName.filled_tin_can.getItemStack();
                    ret.stackSize = (((ItemFood)fill.getItem()).getHealAmount(fill) + 1) / 2;
                    return new RecipeOutput(null, ret);
                }
            } else if (forAccept && fill == null) {
                if (StackUtil.checkItemEquality(container, ItemName.crafting.getItemStack(CraftingItemType.tin_can))) {
                    return new RecipeOutput(null, ItemName.filled_tin_can.getItemStack());
                }
            } else if (fill.getItem() instanceof ItemFood && StackUtil.checkItemEquality(container, ItemName.crafting.getItemStack(CraftingItemType.tin_can))) {
                ItemStack ret = StackUtil.copyWithSize(ItemName.filled_tin_can.getItemStack(), ((ItemFood)fill.getItem()).getHealAmount(fill));
                RecipeOutput tmp = new RecipeOutput(null, ret);
                if (forAccept || container != null && container.stackSize >= ret.stackSize && fill.stackSize >= 1) {
                    if (adjustInput) {
                        if (container != null) {
                            container.stackSize -= ret.stackSize;
                        }
                        --fill.stackSize;
                    }
                    return tmp;
                }
            }
        }
        return output;
    }
}

