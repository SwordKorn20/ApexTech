/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockDispenser
 *  net.minecraft.dispenser.BehaviorDefaultDispenseItem
 *  net.minecraft.dispenser.IBlockSource
 *  net.minecraft.dispenser.IPosition
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.world.World
 */
package ic2.core.item;

import ic2.api.recipe.IScrapboxManager;
import ic2.api.recipe.Recipes;
import ic2.core.block.state.IIdProvider;
import ic2.core.item.type.CraftingItemType;
import ic2.core.ref.ItemName;
import ic2.core.util.StackUtil;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BehaviorScrapboxDispense
extends BehaviorDefaultDispenseItem {
    protected ItemStack dispenseStack(IBlockSource blockSource, ItemStack stack) {
        if (StackUtil.checkItemEquality(stack, ItemName.crafting.getItemStack(CraftingItemType.scrap_box))) {
            EnumFacing facing = EnumFacing.getFront((int)blockSource.getBlockMetadata());
            IPosition position = BlockDispenser.getDispensePosition((IBlockSource)blockSource);
            BehaviorScrapboxDispense.doDispense((World)blockSource.getWorld(), (ItemStack)Recipes.scrapboxDrops.getDrop(stack, true), (int)6, (EnumFacing)facing, (IPosition)position);
        }
        return stack;
    }
}

