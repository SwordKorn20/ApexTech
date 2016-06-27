/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.Container
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 */
package ic2.core.item;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.item.IElectricItemManager;
import ic2.core.IC2;
import ic2.core.Platform;
import ic2.core.item.DamageHandler;
import ic2.core.slot.ArmorSlot;
import ic2.core.util.StackUtil;
import ic2.core.util.Util;
import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ElectricItemManager
implements IElectricItemManager {
    @Override
    public double charge(ItemStack stack, double amount, int tier, boolean ignoreTransferLimit, boolean simulate) {
        IElectricItem item = (IElectricItem)stack.getItem();
        assert (item.getMaxCharge(stack) > 0.0);
        if (amount < 0.0 || stack.stackSize > 1 || item.getTier(stack) > tier) {
            return 0.0;
        }
        if (!ignoreTransferLimit && amount > item.getTransferLimit(stack)) {
            amount = item.getTransferLimit(stack);
        }
        NBTTagCompound tNBT = StackUtil.getOrCreateNbtData(stack);
        double newCharge = tNBT.getDouble("charge");
        amount = Math.min(amount, item.getMaxCharge(stack) - newCharge);
        if (!simulate) {
            if ((newCharge += amount) > 0.0) {
                tNBT.setDouble("charge", newCharge);
            } else {
                tNBT.removeTag("charge");
                if (tNBT.hasNoTags()) {
                    stack.setTagCompound(null);
                }
            }
            if (stack.getItem() instanceof IElectricItem) {
                item = (IElectricItem)stack.getItem();
                int maxDamage = DamageHandler.getMaxDamage(stack);
                DamageHandler.setDamage(stack, ElectricItemManager.mapChargeLevelToDamage(newCharge, item.getMaxCharge(stack), maxDamage));
            } else {
                DamageHandler.setDamage(stack, 0);
            }
        }
        return amount;
    }

    private static int mapChargeLevelToDamage(double charge, double maxCharge, int maxDamage) {
        if (maxDamage < 2) {
            return 0;
        }
        return maxDamage - (int)Util.map(charge, maxCharge, --maxDamage);
    }

    @Override
    public double discharge(ItemStack stack, double amount, int tier, boolean ignoreTransferLimit, boolean externally, boolean simulate) {
        IElectricItem item = (IElectricItem)stack.getItem();
        assert (item.getMaxCharge(stack) > 0.0);
        if (amount < 0.0 || stack.stackSize > 1 || item.getTier(stack) > tier) {
            return 0.0;
        }
        if (externally && !item.canProvideEnergy(stack)) {
            return 0.0;
        }
        if (!ignoreTransferLimit && amount > item.getTransferLimit(stack)) {
            amount = item.getTransferLimit(stack);
        }
        NBTTagCompound tNBT = StackUtil.getOrCreateNbtData(stack);
        double newCharge = tNBT.getDouble("charge");
        amount = Math.min(amount, newCharge);
        if (!simulate) {
            if ((newCharge -= amount) > 0.0) {
                tNBT.setDouble("charge", newCharge);
            } else {
                tNBT.removeTag("charge");
                if (tNBT.hasNoTags()) {
                    stack.setTagCompound(null);
                }
            }
            if (stack.getItem() instanceof IElectricItem) {
                item = (IElectricItem)stack.getItem();
                int maxDamage = DamageHandler.getMaxDamage(stack);
                DamageHandler.setDamage(stack, ElectricItemManager.mapChargeLevelToDamage(newCharge, item.getMaxCharge(stack), maxDamage));
            } else {
                DamageHandler.setDamage(stack, 0);
            }
        }
        return amount;
    }

    @Override
    public double getCharge(ItemStack stack) {
        return ElectricItem.manager.discharge(stack, Double.POSITIVE_INFINITY, Integer.MAX_VALUE, true, false, true);
    }

    @Override
    public double getMaxCharge(ItemStack stack) {
        return ElectricItem.manager.getCharge(stack) + ElectricItem.manager.charge(stack, Double.POSITIVE_INFINITY, Integer.MAX_VALUE, true, true);
    }

    @Override
    public boolean canUse(ItemStack stack, double amount) {
        return ElectricItem.manager.getCharge(stack) >= amount;
    }

    @Override
    public boolean use(ItemStack stack, double amount, EntityLivingBase entity) {
        ElectricItem.manager.chargeFromArmor(stack, entity);
        double transfer = ElectricItem.manager.discharge(stack, amount, Integer.MAX_VALUE, true, false, true);
        if (Util.isSimilar(transfer, amount)) {
            ElectricItem.manager.discharge(stack, amount, Integer.MAX_VALUE, true, false, false);
            ElectricItem.manager.chargeFromArmor(stack, entity);
            return true;
        }
        return false;
    }

    @Override
    public void chargeFromArmor(ItemStack target, EntityLivingBase entity) {
        boolean transferred = false;
        for (EntityEquipmentSlot slot : ArmorSlot.getAll()) {
            ItemStack source = entity.getItemStackFromSlot(slot);
            if (source == null) continue;
            int tier = source.getItem() instanceof IElectricItem ? ((IElectricItem)source.getItem()).getTier(target) : Integer.MAX_VALUE;
            double transfer = ElectricItem.manager.discharge(source, Double.POSITIVE_INFINITY, Integer.MAX_VALUE, true, true, true);
            if (transfer <= 0.0 || (transfer = ElectricItem.manager.charge(target, transfer, tier, true, false)) <= 0.0) continue;
            ElectricItem.manager.discharge(source, transfer, Integer.MAX_VALUE, true, true, false);
            transferred = true;
        }
        if (transferred && entity instanceof EntityPlayer && IC2.platform.isSimulating()) {
            ((EntityPlayer)entity).openContainer.detectAndSendChanges();
        }
    }

    @Override
    public String getToolTip(ItemStack stack) {
        double charge = ElectricItem.manager.getCharge(stack);
        double space = ElectricItem.manager.charge(stack, Double.POSITIVE_INFINITY, Integer.MAX_VALUE, true, true);
        return Util.toSiString(charge, 3) + "/" + Util.toSiString(charge + space, 3) + " EU";
    }

    public static ItemStack getCharged(Item item, double charge) {
        if (!(item instanceof IElectricItem)) {
            throw new IllegalArgumentException("no electric item");
        }
        ItemStack ret = new ItemStack(item);
        ElectricItem.manager.charge(ret, charge, Integer.MAX_VALUE, true, false);
        return ret;
    }

    public static void addChargeVariants(Item item, List<ItemStack> list) {
        list.add(ElectricItemManager.getCharged(item, 0.0));
        list.add(ElectricItemManager.getCharged(item, Double.POSITIVE_INFINITY));
    }

    @Override
    public int getTier(ItemStack stack) {
        if (stack == null || !(stack.getItem() instanceof IElectricItem)) {
            return 0;
        }
        return ((IElectricItem)stack.getItem()).getTier(stack);
    }
}

