/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.inventory.Container
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package ic2.core.item.armor;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItemManager;
import ic2.core.block.generator.tileentity.TileEntitySolarGenerator;
import ic2.core.init.InternalName;
import ic2.core.item.armor.ItemArmorUtility;
import ic2.core.ref.ItemName;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemArmorSolarHelmet
extends ItemArmorUtility {
    public ItemArmorSolarHelmet() {
        super(ItemName.solar_helmet, InternalName.solar, EntityEquipmentSlot.HEAD);
        this.setMaxDamage(0);
    }

    public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
        double chargeAmount;
        boolean ret = false;
        if (player.inventory.armorInventory[2] != null && (chargeAmount = (double)TileEntitySolarGenerator.getSkyLight(player.worldObj, player.getPosition())) > 0.0) {
            boolean bl = ret = ElectricItem.manager.charge(player.inventory.armorInventory[2], chargeAmount, Integer.MAX_VALUE, true, false) > 0.0;
        }
        if (ret) {
            player.inventoryContainer.detectAndSendChanges();
        }
    }

    @Override
    public int getItemEnchantability() {
        return 0;
    }
}

