/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraftforge.fluids.FluidStack
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.item.armor;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.item.IElectricItemManager;
import ic2.core.item.ElectricItemManager;
import ic2.core.item.armor.ItemArmorJetpack;
import ic2.core.ref.ItemName;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemArmorJetpackElectric
extends ItemArmorJetpack
implements IElectricItem {
    public ItemArmorJetpackElectric() {
        super(ItemName.jetpack_electric);
        this.setMaxDamage(27);
        this.setMaxStackSize(1);
        this.setNoRepair();
    }

    @Override
    public FluidStack drain(ItemStack stack, int maxDrain, boolean doDrain) {
        return null;
    }

    @Override
    public int fill(ItemStack stack, FluidStack resource, boolean doFill) {
        return 0;
    }

    @Override
    public double getCharge(ItemStack stack) {
        return ElectricItem.manager.getCharge(stack);
    }

    @Override
    public void use(ItemStack stack, double amount) {
        ElectricItem.manager.discharge(stack, amount, Integer.MAX_VALUE, true, false, false);
    }

    @Override
    public boolean canProvideEnergy(ItemStack stack) {
        return false;
    }

    @Override
    public double getMaxCharge(ItemStack stack) {
        return 30000.0;
    }

    @Override
    public int getTier(ItemStack stack) {
        return 1;
    }

    @Override
    public double getTransferLimit(ItemStack stack) {
        return 60.0;
    }

    @Override
    public List<String> getHudInfo(ItemStack stack) {
        LinkedList<String> info = new LinkedList<String>();
        info.add(ElectricItem.manager.getToolTip(stack));
        return info;
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> subItems) {
        ElectricItemManager.addChargeVariants(item, subItems);
    }

    @Override
    public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack) {
        return false;
    }
}

