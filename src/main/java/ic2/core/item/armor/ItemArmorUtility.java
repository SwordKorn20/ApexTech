/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.item.ItemArmor
 *  net.minecraft.item.ItemArmor$ArmorMaterial
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.DamageSource
 *  net.minecraftforge.common.ISpecialArmor
 *  net.minecraftforge.common.ISpecialArmor$ArmorProperties
 */
package ic2.core.item.armor;

import ic2.core.init.InternalName;
import ic2.core.item.armor.ItemArmorIC2;
import ic2.core.ref.ItemName;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.ISpecialArmor;

public class ItemArmorUtility
extends ItemArmorIC2
implements ISpecialArmor {
    public ItemArmorUtility(ItemName name, InternalName armorName, EntityEquipmentSlot type) {
        super(name, ItemArmor.ArmorMaterial.DIAMOND, armorName, type, null);
    }

    public int getItemEnchantability() {
        return 0;
    }

    @Override
    public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack) {
        return false;
    }

    public ISpecialArmor.ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) {
        return new ISpecialArmor.ArmorProperties(0, 0.0, 0);
    }

    public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
        return 0;
    }

    public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {
    }
}

