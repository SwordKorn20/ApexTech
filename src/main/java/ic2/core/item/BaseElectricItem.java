/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.item;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.item.IElectricItemManager;
import ic2.api.item.IItemHudInfo;
import ic2.core.item.ElectricItemManager;
import ic2.core.item.ItemIC2;
import ic2.core.ref.ItemName;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BaseElectricItem
extends ItemIC2
implements IElectricItem,
IItemHudInfo {
    protected final double maxCharge;
    protected final double transferLimit;
    protected final int tier;

    public BaseElectricItem(ItemName name, double maxCharge, double transferLimit, int tier) {
        super(name);
        this.maxCharge = maxCharge;
        this.transferLimit = transferLimit;
        this.tier = tier;
        this.setMaxDamage(27);
        this.setMaxStackSize(1);
        this.setNoRepair();
    }

    @Override
    public boolean canProvideEnergy(ItemStack stack) {
        return false;
    }

    @Override
    public double getMaxCharge(ItemStack stack) {
        return this.maxCharge;
    }

    @Override
    public int getTier(ItemStack stack) {
        return this.tier;
    }

    @Override
    public double getTransferLimit(ItemStack stack) {
        return this.transferLimit;
    }

    @Override
    public List<String> getHudInfo(ItemStack stack) {
        LinkedList<String> info = new LinkedList<String>();
        info.add(ElectricItem.manager.getToolTip(stack));
        return info;
    }

    @SideOnly(value=Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> subItems) {
        ElectricItemManager.addChargeVariants(item, subItems);
    }
}

