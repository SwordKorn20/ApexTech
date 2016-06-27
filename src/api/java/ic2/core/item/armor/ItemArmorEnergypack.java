/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.item.ItemStack
 */
package ic2.core.item.armor;

import ic2.core.init.InternalName;
import ic2.core.item.armor.ItemArmorElectric;
import ic2.core.ref.ItemName;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class ItemArmorEnergypack
extends ItemArmorElectric {
    public ItemArmorEnergypack() {
        super(ItemName.energy_pack, InternalName.energypack, EntityEquipmentSlot.CHEST, 2000000.0, 1000.0, 3);
    }

    @Override
    public boolean canProvideEnergy(ItemStack stack) {
        return true;
    }

    @Override
    public double getDamageAbsorptionRatio() {
        return 0.0;
    }

    @Override
    public int getEnergyPerDamage() {
        return 0;
    }
}

