/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 */
package ic2.core.block.machine.tileentity;

import ic2.api.recipe.IListRecipeManager;
import ic2.api.recipe.IMachineRecipeManager;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeOutput;
import ic2.api.recipe.Recipes;
import ic2.core.IC2;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.block.invslot.InvSlotProcessable;
import ic2.core.block.invslot.InvSlotProcessableGeneric;
import ic2.core.block.machine.tileentity.TileEntityStandardMachine;
import ic2.core.block.state.IIdProvider;
import ic2.core.init.MainConfig;
import ic2.core.item.type.CraftingItemType;
import ic2.core.recipe.BasicListRecipeManager;
import ic2.core.ref.ItemName;
import ic2.core.upgrade.UpgradableProperty;
import ic2.core.util.ConfigUtil;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityRecycler
extends TileEntityStandardMachine {
    public TileEntityRecycler() {
        super(1, 45, 1);
        this.inputSlot = new InvSlotProcessableGeneric(this, "input", 1, Recipes.recycler);
    }

    public static void init() {
        Recipes.recycler = new RecyclerRecipeManager();
        Recipes.recyclerWhitelist = new BasicListRecipeManager();
        Recipes.recyclerBlacklist = new BasicListRecipeManager();
    }

    public static void initLate() {
        for (IRecipeInput input2 : ConfigUtil.asRecipeInputList(MainConfig.get(), "balance/recyclerBlacklist")) {
            Recipes.recyclerBlacklist.add(input2);
        }
        for (IRecipeInput input2 : ConfigUtil.asRecipeInputList(MainConfig.get(), "balance/recyclerWhitelist")) {
            Recipes.recyclerWhitelist.add(input2);
        }
    }

    public static int recycleChance() {
        return 8;
    }

    @Override
    public String getStartSoundFile() {
        return "Machines/RecyclerOp.ogg";
    }

    @Override
    public String getInterruptSoundFile() {
        return "Machines/InterruptOne.ogg";
    }

    public static boolean getIsItemBlacklisted(ItemStack aStack) {
        if (Recipes.recyclerWhitelist.isEmpty()) {
            return Recipes.recyclerBlacklist.contains(aStack);
        }
        return !Recipes.recyclerWhitelist.contains(aStack);
    }

    @Override
    public void operateOnce(RecipeOutput output, List<ItemStack> processResult) {
        this.inputSlot.consume();
        if (IC2.random.nextInt(TileEntityRecycler.recycleChance()) == 0) {
            this.outputSlot.add(processResult);
        }
    }

    @Override
    public Set<UpgradableProperty> getUpgradableProperties() {
        return EnumSet.of(UpgradableProperty.Processing, UpgradableProperty.Transformer, UpgradableProperty.EnergyStorage, UpgradableProperty.ItemConsuming, UpgradableProperty.ItemProducing);
    }

    private static class RecyclerRecipeManager
    implements IMachineRecipeManager {
        @Override
        public /* varargs */ boolean addRecipe(IRecipeInput input, NBTTagCompound metadata, boolean replace, ItemStack ... outputs) {
            return false;
        }

        @Override
        public RecipeOutput getOutputFor(ItemStack input, boolean adjustInput) {
            RecipeOutput ret = !TileEntityRecycler.getIsItemBlacklisted(input) ? new RecipeOutput(null, ItemName.crafting.getItemStack(CraftingItemType.scrap)) : new RecipeOutput(null, new ItemStack[0]);
            if (adjustInput) {
                --input.stackSize;
            }
            return ret;
        }

        @Override
        public Iterable<IMachineRecipeManager.RecipeIoContainer> getRecipes() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isIterable() {
            return false;
        }
    }

}

