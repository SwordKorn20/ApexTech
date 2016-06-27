/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.enchantment.Enchantment
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Enchantments
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package ic2.core.item.tool;

import ic2.core.IC2;
import ic2.core.Platform;
import ic2.core.item.tool.ItemDrill;
import ic2.core.item.tool.ItemElectricTool;
import ic2.core.ref.ItemName;
import ic2.core.util.Keyboard;
import java.util.IdentityHashMap;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemDrillIridium
extends ItemDrill {
    public ItemDrillIridium() {
        super(ItemName.iridium_drill, 800, ItemElectricTool.HarvestLevel.Iridium);
        this.maxCharge = 300000;
        this.transferLimit = 1000;
        this.tier = 3;
        this.efficiencyOnProperMaterial = 24.0f;
    }

    @Override
    protected ItemStack getItemStack(double charge) {
        ItemStack ret = super.getItemStack(charge);
        IdentityHashMap<Enchantment, Integer> enchantmentMap = new IdentityHashMap<Enchantment, Integer>();
        enchantmentMap.put(Enchantments.FORTUNE, 3);
        EnchantmentHelper.setEnchantments(enchantmentMap, (ItemStack)ret);
        return ret;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        if (!IC2.platform.isSimulating()) {
            return super.onItemRightClick(stack, world, player, hand);
        }
        if (IC2.keyboard.isModeSwitchKeyDown(player)) {
            IdentityHashMap<Enchantment, Integer> enchantmentMap = new IdentityHashMap<Enchantment, Integer>();
            enchantmentMap.put(Enchantments.FORTUNE, 3);
            if (EnchantmentHelper.getEnchantmentLevel((Enchantment)Enchantments.SILK_TOUCH, (ItemStack)stack) == 0) {
                enchantmentMap.put(Enchantments.SILK_TOUCH, 1);
                IC2.platform.messagePlayer(player, "ic2.tooltip.mode", "ic2.tooltip.mode.silkTouch");
            } else {
                IC2.platform.messagePlayer(player, "ic2.tooltip.mode", "ic2.tooltip.mode.normal");
            }
            EnchantmentHelper.setEnchantments(enchantmentMap, (ItemStack)stack);
        }
        return super.onItemRightClick(stack, world, player, hand);
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float xOffset, float yOffset, float zOffset) {
        if (IC2.keyboard.isModeSwitchKeyDown(player)) {
            return EnumActionResult.PASS;
        }
        return super.onItemUse(stack, player, world, pos, hand, side, xOffset, yOffset, zOffset);
    }
}

