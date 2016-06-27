/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 */
package ic2.core.item.tool;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.item.IElectricItemManager;
import ic2.api.item.IItemHudInfo;
import ic2.core.item.ElectricItemManager;
import ic2.core.item.tool.ItemToolWrench;
import ic2.core.ref.ItemName;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemToolWrenchElectric
extends ItemToolWrench
implements IElectricItem,
IItemHudInfo {
    public ItemToolWrenchElectric() {
        super(ItemName.electric_wrench);
        this.setMaxDamage(27);
        this.setMaxStackSize(1);
        this.setNoRepair();
    }

    @Override
    public List<String> getHudInfo(ItemStack stack) {
        LinkedList<String> info = new LinkedList<String>();
        info.add(ElectricItem.manager.getToolTip(stack));
        return info;
    }

    @Override
    public boolean canTakeDamage(ItemStack stack, int amount) {
        return ElectricItem.manager.getCharge(stack) >= (double)(amount *= 100);
    }

    @Override
    public void damage(ItemStack stack, int amount, EntityPlayer player) {
        ElectricItem.manager.use(stack, 100 * amount, (EntityLivingBase)player);
    }

    @Override
    public boolean canProvideEnergy(ItemStack stack) {
        return false;
    }

    @Override
    public double getMaxCharge(ItemStack stack) {
        return 12000.0;
    }

    @Override
    public int getTier(ItemStack stack) {
        return 1;
    }

    @Override
    public double getTransferLimit(ItemStack stack) {
        return 250.0;
    }

    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> subItems) {
        ElectricItemManager.addChargeVariants(item, subItems);
    }

    public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack) {
        return false;
    }
}

