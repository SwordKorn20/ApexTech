/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.item;

import ic2.api.item.ICustomDamageItem;
import ic2.core.item.ItemIC2;
import ic2.core.ref.ItemName;
import ic2.core.util.StackUtil;
import ic2.core.util.Util;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemGradualInt
extends ItemIC2
implements ICustomDamageItem {
    private static final boolean alwaysShowDurability = true;
    private static final String nbtKey = "advDmg";
    private final int maxDamage;

    public ItemGradualInt(ItemName name, int maxDamage) {
        super(name);
        this.setNoRepair();
        this.maxDamage = maxDamage;
    }

    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    public double getDurabilityForDisplay(ItemStack stack) {
        return (double)this.getCustomDamage(stack) / (double)this.getMaxCustomDamage(stack);
    }

    @Override
    public int getCustomDamage(ItemStack stack) {
        NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);
        return nbt.getInteger("advDmg");
    }

    @Override
    public int getMaxCustomDamage(ItemStack stack) {
        return this.maxDamage;
    }

    @Override
    public void setCustomDamage(ItemStack stack, int damage) {
        NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);
        nbt.setInteger("advDmg", damage);
        int maxStackDamage = stack.getMaxDamage();
        if (maxStackDamage > 2) {
            stack.setItemDamage(1 + (int)Util.map(damage, this.maxDamage, maxStackDamage - 2));
        }
    }

    @Override
    public boolean applyCustomDamage(ItemStack stack, int damage, EntityLivingBase src) {
        this.setCustomDamage(stack, this.getCustomDamage(stack) + damage);
        return true;
    }

    @SideOnly(value=Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> subItems) {
        ItemStack stack = new ItemStack(item);
        this.setCustomDamage(stack, 0);
        subItems.add(stack);
    }
}

