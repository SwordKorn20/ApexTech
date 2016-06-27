/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 */
package ic2.core.apihelper;

import ic2.api.item.IItemAPI;
import ic2.core.block.state.IIdProvider;
import ic2.core.ref.BlockName;
import ic2.core.ref.IBlockModelProvider;
import ic2.core.ref.IItemModelProvider;
import ic2.core.ref.ItemName;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemAPI
implements IItemAPI {
    @Override
    public ItemStack getItemStack(String name, String variant) {
        if (name == null) {
            return null;
        }
        for (BlockName blockName : BlockName.values) {
            if (!name.equals(blockName.name())) continue;
            return blockName.getItemStack(variant);
        }
        for (BlockName itemName : ItemName.values) {
            if (!name.equals(itemName.name())) continue;
            return itemName.getItemStack(variant);
        }
        return null;
    }

    @Override
    public Block getBlock(String name) {
        if (name == null) {
            return null;
        }
        for (BlockName blockName : BlockName.values) {
            if (!name.equals(blockName.name())) continue;
            return blockName.getInstance();
        }
        return null;
    }

    @Override
    public Item getItem(String name) {
        if (name == null) {
            return null;
        }
        for (ItemName itemName : ItemName.values) {
            if (!name.equals(itemName.name())) continue;
            return itemName.getInstance();
        }
        Block block = this.getBlock(name);
        if (block != null) {
            return Item.getItemFromBlock((Block)block);
        }
        return null;
    }
}

