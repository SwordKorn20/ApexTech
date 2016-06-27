/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidRegistry
 */
package ic2.core.util;

import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeInputFluidContainer;
import ic2.api.recipe.RecipeInputItemStack;
import ic2.api.recipe.RecipeInputOreDict;
import ic2.core.IC2;
import ic2.core.Platform;
import ic2.core.ref.IMultiBlock;
import ic2.core.ref.IMultiItem;
import ic2.core.util.Config;
import ic2.core.util.Util;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class ConfigUtil {
    public static List<String> asList(String str) {
        if ((str = str.trim()).isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.asList(str.split("\\s*,\\s*"));
    }

    public static List<IRecipeInput> asRecipeInputList(Config config, String key) {
        Config.Value value = config.get(key);
        try {
            try {
                return ConfigUtil.asRecipeInputList(value.getString());
            }
            catch (ParseException e) {
                throw new Config.ParseException("Invalid value", value, e);
            }
        }
        catch (Config.ParseException e) {
            ConfigUtil.displayError(e, key);
            return null;
        }
    }

    public static List<ItemStack> asStackList(Config config, String key) {
        Config.Value value = config.get(key);
        try {
            try {
                return ConfigUtil.asStackList(value.getString());
            }
            catch (ParseException e) {
                throw new Config.ParseException("Invalid value", value, e);
            }
        }
        catch (Config.ParseException e) {
            ConfigUtil.displayError(e, key);
            return null;
        }
    }

    public static ItemStack asStack(Config config, String key) {
        Config.Value value = config.get(key);
        try {
            try {
                return ConfigUtil.asStack(value.getString());
            }
            catch (ParseException e) {
                throw new Config.ParseException("Invalid value", value, e);
            }
        }
        catch (Config.ParseException e) {
            ConfigUtil.displayError(e, key);
            return null;
        }
    }

    public static String getString(Config config, String key) {
        return config.get(key).getString();
    }

    public static boolean getBool(Config config, String key) {
        Config.Value value = config.get(key);
        try {
            return value.getBool();
        }
        catch (Config.ParseException e) {
            ConfigUtil.displayError(e, key);
            return false;
        }
    }

    public static int getInt(Config config, String key) {
        Config.Value value = config.get(key);
        try {
            return value.getInt();
        }
        catch (Config.ParseException e) {
            ConfigUtil.displayError(e, key);
            return 0;
        }
    }

    public static float getFloat(Config config, String key) {
        Config.Value value = config.get(key);
        try {
            return value.getFloat();
        }
        catch (Config.ParseException e) {
            ConfigUtil.displayError(e, key);
            return 0.0f;
        }
    }

    public static double getDouble(Config config, String key) {
        Config.Value value = config.get(key);
        try {
            return value.getDouble();
        }
        catch (Config.ParseException e) {
            ConfigUtil.displayError(e, key);
            return 0.0;
        }
    }

    public static List<ItemStack> asStackList(String str) throws ParseException {
        List<String> parts = ConfigUtil.asList(str);
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>(parts.size());
        for (String part : parts) {
            ret.add(ConfigUtil.asStack(part));
        }
        return ret;
    }

    public static List<IRecipeInput> asRecipeInputList(String str) throws ParseException {
        return ConfigUtil.asRecipeInputList(str, false);
    }

    public static List<IRecipeInput> asRecipeInputList(String str, boolean allowNull) throws ParseException {
        List<String> parts = ConfigUtil.asList(str);
        ArrayList<IRecipeInput> ret = new ArrayList<IRecipeInput>(parts.size());
        for (String part : parts) {
            IRecipeInput input = ConfigUtil.asRecipeInput(part);
            if (input == null && !allowNull) {
                throw new ParseException("There is no item matching " + part + ".", -1);
            }
            ret.add(input);
        }
        return ret;
    }

    private static ItemStack asStack(String str, boolean checkAmount) throws ParseException {
        String[] parts = str.split("(?=(@|#|\\*))");
        String itemName = parts[0];
        Item item = Util.getItem(itemName);
        if (item == null) {
            return null;
        }
        ItemStack stack = new ItemStack(item);
        int amount = 1;
        for (int i = 1; i < parts.length; ++i) {
            String tmp = parts[i];
            if (tmp.startsWith("@")) {
                if (i + 1 < parts.length && parts[i + 1].equals("*")) {
                    stack = new ItemStack(item, 1, 32767);
                    ++i;
                    continue;
                }
                stack = new ItemStack(item, 1, Integer.parseInt(tmp.substring(1)));
                continue;
            }
            if (tmp.startsWith("#")) {
                if (item instanceof IMultiItem) {
                    stack = ((IMultiItem)item).getItemStack(tmp.substring(1));
                    continue;
                }
                if (item instanceof ItemBlock && ((ItemBlock)item).getBlock() instanceof IMultiBlock) {
                    stack = ((IMultiBlock)((ItemBlock)item).getBlock()).getItemStack(tmp.substring(1));
                    continue;
                }
                throw new ParseException("# is not supported on non-IC2-Items: " + str, 0);
            }
            if (!tmp.startsWith("*")) continue;
            if (!checkAmount) {
                throw new ParseException("We do not support amount here.", -1);
            }
            amount = Integer.parseInt(tmp.substring(1));
        }
        if (checkAmount) {
            stack.stackSize = amount;
        }
        return stack;
    }

    public static ItemStack asStack(String str) throws ParseException {
        return ConfigUtil.asStack(str, false);
    }

    public static ItemStack asStackWithAmount(String str) throws ParseException {
        return ConfigUtil.asStack(str, true);
    }

    public static String fromStack(ItemStack stack) {
        return ConfigUtil.fromStack(stack, false);
    }

    private static String fromStack(ItemStack stack, boolean amount) {
        String ret = Util.getName(stack.getItem()).toString();
        if (amount) {
            ret = ret + "*" + stack.stackSize;
        }
        if (stack.getItem() instanceof IMultiItem) {
            String variant = ((IMultiItem)stack.getItem()).getVariant(stack);
            if (variant != null) {
                ret = ret + "#" + variant;
            }
        } else if (stack.getItem() instanceof ItemBlock && ((ItemBlock)stack.getItem()).getBlock() instanceof IMultiBlock) {
            String variant = ((IMultiBlock)((ItemBlock)stack.getItem()).getBlock()).getVariant(stack);
            if (variant != null) {
                ret = ret + "#" + variant;
            }
        } else if (stack.getItemDamage() == 32767) {
            ret = ret + "@*";
        } else if (stack.getItemDamage() != 0) {
            ret = ret + "@" + stack.getItemDamage();
        }
        return ret;
    }

    public static String fromStackWithAmount(ItemStack stack) {
        return ConfigUtil.fromStack(stack, true);
    }

    public static IRecipeInput asRecipeInput(Config.Value value) {
        try {
            return ConfigUtil.asRecipeInput(value.getString());
        }
        catch (ParseException e) {
            throw new Config.ParseException("Invalid value", value, e);
        }
    }

    private static IRecipeInput asRecipeInput(String str, boolean checkAmount) throws ParseException {
        String[] parts = str.split("(?=(@|#|\\*))");
        String itemName = parts[0];
        if (!itemName.startsWith("OreDict:") && !itemName.startsWith("Fluid:")) {
            ItemStack stack = ConfigUtil.asStack(str, checkAmount);
            if (stack == null) {
                return null;
            }
            return new RecipeInputItemStack(stack);
        }
        Integer amount = null;
        Integer meta = null;
        for (int i = 1; i < parts.length; ++i) {
            String tmp = parts[i];
            if (tmp.startsWith("@")) {
                if (i + 1 < parts.length && parts[i + 1].equals("*")) {
                    meta = 32767;
                    ++i;
                    continue;
                }
                meta = Integer.parseInt(tmp.substring(1));
                continue;
            }
            if (!tmp.startsWith("*")) continue;
            if (!checkAmount) {
                throw new ParseException("We do not support amount here.", -1);
            }
            amount = Integer.parseInt(tmp.substring(1));
        }
        if (itemName.startsWith("OreDict:")) {
            if (amount == null) {
                amount = 1;
            }
            return new RecipeInputOreDict(itemName.substring("OreDict:".length()), amount, meta);
        }
        if (itemName.startsWith("Fluid:")) {
            if (amount == null) {
                amount = 1000;
            }
            return new RecipeInputFluidContainer(FluidRegistry.getFluid((String)itemName.substring("Fluid:".length())), amount);
        }
        return null;
    }

    public static IRecipeInput asRecipeInput(String str) throws ParseException {
        return ConfigUtil.asRecipeInput(str, false);
    }

    public static IRecipeInput asRecipeInputWithAmount(String str) throws ParseException {
        return ConfigUtil.asRecipeInput(str, true);
    }

    private static void displayError(Config.ParseException e, String key) {
        Object[] arrobject = new Object[3];
        arrobject[0] = key;
        arrobject[1] = e.getMessage();
        arrobject[2] = e.getCause() != null ? "\n\n" + e.getCause().getMessage() : "";
        IC2.platform.displayError("The IC2 config file contains an invalid entry for %s.\n\n%s%s", arrobject);
    }
}

