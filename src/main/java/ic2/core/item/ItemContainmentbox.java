/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.EnumRarity
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumHand
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.item;

import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.Platform;
import ic2.core.item.IHandHeldInventory;
import ic2.core.item.ItemIC2;
import ic2.core.item.tool.HandHeldContainmentbox;
import ic2.core.ref.ItemName;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemContainmentbox
extends ItemIC2
implements IHandHeldInventory {
    public ItemContainmentbox() {
        super(ItemName.containment_box);
        this.setMaxStackSize(1);
    }

    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        if (!world.isRemote) {
            IC2.platform.launchGui(player, this.getInventory(player, stack));
        }
        return new ActionResult(EnumActionResult.SUCCESS, (Object)stack);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.UNCOMMON;
    }

    @Override
    public IHasGui getInventory(EntityPlayer player, ItemStack stack) {
        return new HandHeldContainmentbox(player, stack, 12);
    }
}

