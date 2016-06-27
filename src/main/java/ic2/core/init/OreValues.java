/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package ic2.core.init;

import ic2.core.util.ItemComparableItemStack;
import ic2.core.util.StackUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.item.ItemStack;

public class OreValues {
    private static final Map<ItemComparableItemStack, Integer> stackValues = new HashMap<ItemComparableItemStack, Integer>();

    public static void add(ItemStack stack, int value) {
        if (value <= 0) {
            throw new IllegalArgumentException("value has to be > 0");
        }
        ItemComparableItemStack key = new ItemComparableItemStack(stack, true);
        Integer prev = stackValues.put(key, value);
        if (prev != null && prev > value) {
            stackValues.put(key, prev);
        }
    }

    public static int get(ItemStack stack) {
        if (stack == null) {
            return 0;
        }
        if (!StackUtil.check(stack)) {
            return 0;
        }
        Integer ret = stackValues.get(new ItemComparableItemStack(stack, false));
        return ret != null ? ret * stack.stackSize : 0;
    }

    public static int get(List<ItemStack> stacks) {
        int ret = 0;
        for (ItemStack stack : stacks) {
            ret += OreValues.get(stack);
        }
        return ret;
    }
}

