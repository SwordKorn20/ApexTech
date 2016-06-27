/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.FoodStats
 *  net.minecraft.world.World
 */
package ic2.core.item;

import ic2.core.IC2;
import ic2.core.Platform;
import ic2.core.block.state.IIdProvider;
import ic2.core.item.ItemIC2;
import ic2.core.item.type.CraftingItemType;
import ic2.core.ref.ItemName;
import ic2.core.util.StackUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.FoodStats;
import net.minecraft.world.World;

public class ItemTinCan
extends ItemIC2 {
    public ItemTinCan() {
        super(ItemName.filled_tin_can);
    }

    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        if (!world.isRemote && player.getFoodStats().needFood()) {
            return this.onEaten(player, stack);
        }
        return new ActionResult(EnumActionResult.PASS, (Object)stack);
    }

    public ActionResult<ItemStack> onEaten(EntityPlayer player, ItemStack stack) {
        int amount = Math.min(stack.stackSize, 20 - player.getFoodStats().getFoodLevel());
        if (amount <= 0) {
            return new ActionResult(EnumActionResult.PASS, (Object)stack);
        }
        ItemStack emptyStack = StackUtil.copyWithSize(ItemName.crafting.getItemStack(CraftingItemType.tin_can), amount);
        if (StackUtil.storeInventoryItem(emptyStack, player, true)) {
            player.getFoodStats().addStats(amount, (float)amount);
            stack.stackSize -= amount;
            StackUtil.storeInventoryItem(emptyStack, player, false);
            IC2.platform.playSoundSp("Tools/eat.ogg", 1.0f, 1.0f);
            new ActionResult(EnumActionResult.SUCCESS, (Object)stack);
        }
        return new ActionResult(EnumActionResult.PASS, (Object)stack);
    }
}

