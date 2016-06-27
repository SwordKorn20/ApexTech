/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.inventory.Container
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.world.World
 */
package ic2.core.item.armor;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItemManager;
import ic2.core.init.InternalName;
import ic2.core.item.armor.ItemArmorUtility;
import ic2.core.ref.ItemName;
import ic2.core.util.StackUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ItemArmorStaticBoots
extends ItemArmorUtility {
    public ItemArmorStaticBoots() {
        super(ItemName.static_boots, InternalName.rubber, EntityEquipmentSlot.FEET);
    }

    public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
        double distance;
        boolean isNotWalking;
        if (player.inventory.armorInventory[2] == null) {
            return;
        }
        boolean ret = false;
        NBTTagCompound compound = StackUtil.getOrCreateNbtData(stack);
        boolean bl = isNotWalking = player.getRidingEntity() != null || player.isInWater();
        if (!compound.hasKey("x") || isNotWalking) {
            compound.setInteger("x", (int)player.posX);
        }
        if (!compound.hasKey("z") || isNotWalking) {
            compound.setInteger("z", (int)player.posZ);
        }
        if ((distance = Math.sqrt((compound.getInteger("x") - (int)player.posX) * (compound.getInteger("x") - (int)player.posX) + (compound.getInteger("z") - (int)player.posZ) * (compound.getInteger("z") - (int)player.posZ))) >= 5.0) {
            compound.setInteger("x", (int)player.posX);
            compound.setInteger("z", (int)player.posZ);
            boolean bl2 = ret = ElectricItem.manager.charge(player.inventory.armorInventory[2], Math.min(3.0, distance / 5.0), Integer.MAX_VALUE, true, false) > 0.0;
        }
        if (ret) {
            player.inventoryContainer.detectAndSendChanges();
        }
    }
}

