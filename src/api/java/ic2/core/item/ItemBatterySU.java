/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumHand
 *  net.minecraft.world.World
 */
package ic2.core.item;

import ic2.api.item.ElectricItem;
import ic2.api.item.IBoxable;
import ic2.api.item.IElectricItemManager;
import ic2.api.item.IItemHudInfo;
import ic2.core.item.ItemIC2;
import ic2.core.ref.ItemName;
import ic2.core.util.Util;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemBatterySU
extends ItemIC2
implements IBoxable,
IItemHudInfo {
    public int capacity;
    public int tier;

    public ItemBatterySU(ItemName internalName, int capacity1, int tier1) {
        super(internalName);
        this.capacity = capacity1;
        this.tier = tier1;
    }

    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        double energy = this.capacity;
        for (int i = 0; i < 9 && energy > 0.0; ++i) {
            ItemStack target = player.inventory.mainInventory[i];
            if (target == null || target == stack) continue;
            energy -= ElectricItem.manager.charge(target, energy, this.tier, true, false);
        }
        if (!Util.isSimilar(energy, (double)this.capacity)) {
            --stack.stackSize;
            return new ActionResult(EnumActionResult.SUCCESS, (Object)stack);
        }
        return new ActionResult(EnumActionResult.PASS, (Object)stack);
    }

    @Override
    public boolean canBeStoredInToolbox(ItemStack itemstack) {
        return true;
    }

    @Override
    public List<String> getHudInfo(ItemStack stack) {
        LinkedList<String> info = new LinkedList<String>();
        info.add("" + this.capacity + " EU");
        return info;
    }
}

