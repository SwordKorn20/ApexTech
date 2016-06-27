/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ArrayListMultimap
 *  com.google.common.collect.Multimap
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 */
package ic2.api.item;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import ic2.api.item.IBoxable;
import ic2.api.item.IMetalArmor;
import java.util.Collection;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemWrapper {
    private static final Multimap<Item, IBoxable> boxableItems = ArrayListMultimap.create();
    private static final Multimap<Item, IMetalArmor> metalArmorItems = ArrayListMultimap.create();

    public static void registerBoxable(Item item, IBoxable boxable) {
        boxableItems.put((Object)item, (Object)boxable);
    }

    public static boolean canBeStoredInToolbox(ItemStack stack) {
        Item item = stack.getItem();
        for (IBoxable boxable : boxableItems.get((Object)item)) {
            if (!boxable.canBeStoredInToolbox(stack)) continue;
            return true;
        }
        if (item instanceof IBoxable && ((IBoxable)item).canBeStoredInToolbox(stack)) {
            return true;
        }
        return false;
    }

    public static void registerMetalArmor(Item item, IMetalArmor armor) {
        metalArmorItems.put((Object)item, (Object)armor);
    }

    public static boolean isMetalArmor(ItemStack stack, EntityPlayer player) {
        Item item = stack.getItem();
        for (IMetalArmor metalArmor : metalArmorItems.get((Object)item)) {
            if (!metalArmor.isMetalArmor(stack, player)) continue;
            return true;
        }
        if (item instanceof IMetalArmor && ((IMetalArmor)item).isMetalArmor(stack, player)) {
            return true;
        }
        return false;
    }
}

