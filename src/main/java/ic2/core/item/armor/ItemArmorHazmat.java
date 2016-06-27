/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockLiquid
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyInteger
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.init.MobEffects
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.potion.Potion
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.World
 *  net.minecraftforge.common.ISpecialArmor
 *  net.minecraftforge.common.ISpecialArmor$ArmorProperties
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.event.entity.living.LivingFallEvent
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidStack
 *  net.minecraftforge.fml.common.eventhandler.EventBus
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package ic2.core.item.armor;

import ic2.core.IC2;
import ic2.core.IC2DamageSource;
import ic2.core.Platform;
import ic2.core.init.InternalName;
import ic2.core.item.armor.ItemArmorUtility;
import ic2.core.ref.FluidName;
import ic2.core.ref.ItemName;
import ic2.core.slot.ArmorSlot;
import ic2.core.util.LiquidUtil;
import ic2.core.util.StackUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.mutable.MutableObject;

public class ItemArmorHazmat
extends ItemArmorUtility {
    public ItemArmorHazmat(ItemName name, EntityEquipmentSlot type) {
        super(name, InternalName.hazmat, type);
        this.setMaxDamage(64);
        if (this.armorType == EntityEquipmentSlot.FEET) {
            MinecraftForge.EVENT_BUS.register((Object)this);
        }
    }

    @Override
    public ISpecialArmor.ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) {
        if (this.armorType == EntityEquipmentSlot.HEAD && this.hazmatAbsorbs(source) && ItemArmorHazmat.hasCompleteHazmat(player)) {
            if (source == DamageSource.inFire || source == DamageSource.lava) {
                player.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 60, 1));
            }
            return new ISpecialArmor.ArmorProperties(10, 1.0, Integer.MAX_VALUE);
        }
        if (this.armorType == EntityEquipmentSlot.FEET && source == DamageSource.fall) {
            return new ISpecialArmor.ArmorProperties(10, damage < 8.0 ? 1.0 : 0.875, (armor.getMaxDamage() - armor.getItemDamage() + 2) * 2 * 25);
        }
        return new ISpecialArmor.ArmorProperties(0, 0.05, (armor.getMaxDamage() - armor.getItemDamage() + 2) / 2 * 25);
    }

    @Override
    public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {
        if (this.hazmatAbsorbs(source) && ItemArmorHazmat.hasCompleteHazmat(entity)) {
            return;
        }
        int damageTotal = damage * 2;
        if (this.armorType == EntityEquipmentSlot.FEET && source == DamageSource.fall) {
            damageTotal = (damage + 1) / 2;
        }
        stack.damageItem(damageTotal, entity);
    }

    @SubscribeEvent
    public void onEntityLivingFallEvent(LivingFallEvent event) {
        if (IC2.platform.isSimulating() && event.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)event.getEntity();
            ItemStack armor = player.inventory.armorInventory[0];
            if (armor != null && armor.getItem() == this) {
                int fallDamage = (int)event.getDistance() - 3;
                if (fallDamage >= 8) {
                    return;
                }
                int armorDamage = (fallDamage + 1) / 2;
                if (armorDamage <= armor.getMaxDamage() - armor.getItemDamage() && armorDamage >= 0) {
                    armor.damageItem(armorDamage, (EntityLivingBase)player);
                    event.setCanceled(true);
                }
            }
        }
    }

    public boolean isRepairable() {
        return true;
    }

    @Override
    public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
        return 1;
    }

    public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
        if (!world.isRemote && this.armorType == EntityEquipmentSlot.HEAD) {
            if (player.isBurning() && ItemArmorHazmat.hasCompleteHazmat((EntityLivingBase)player)) {
                if (this.isInLava(player)) {
                    player.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 20, 0, true, true));
                }
                player.extinguish();
            }
            int maxAir = 300;
            int refillThreshold = 100;
            int airToMbMul = 1000;
            int airToMbDiv = 150;
            int minAmount = 7;
            int air = player.getAir();
            if (air <= 100) {
                int needed = (300 - air) * 1000 / 150;
                int supplied = 0;
                MutableObject output = new MutableObject();
                for (int i = 0; i < player.inventory.mainInventory.length && needed > 0; ++i) {
                    FluidStack fs;
                    ItemStack cStack = player.inventory.mainInventory[i];
                    if (cStack == null || (fs = LiquidUtil.drainContainer(cStack, FluidName.air.getInstance(), needed, output, LiquidUtil.FluidContainerOutputMode.InPlacePreferred, true)) == null || fs.amount < 7 || output.getValue() != null && !StackUtil.storeInventoryItem((ItemStack)output.getValue(), player, false) || (fs = LiquidUtil.drainContainer(cStack, FluidName.air.getInstance(), needed, output, LiquidUtil.FluidContainerOutputMode.InPlacePreferred, false)) == null) continue;
                    if (cStack.stackSize <= 0) {
                        player.inventory.mainInventory[i] = null;
                    }
                    supplied += fs.amount;
                    needed -= fs.amount;
                }
                player.setAir(air + supplied * 150 / 1000);
            }
        }
    }

    public boolean isInLava(EntityPlayer player) {
        int y;
        int z;
        int x = MathHelper.floor_double((double)player.posX);
        IBlockState state = player.worldObj.getBlockState(new BlockPos(x, y = MathHelper.floor_double((double)(player.posY + 0.02)), z = MathHelper.floor_double((double)player.posZ)));
        if (state.getBlock() instanceof BlockLiquid && (state.getMaterial() == Material.LAVA || state.getMaterial() == Material.FIRE)) {
            float height = (float)(y + 1) - BlockLiquid.getLiquidHeightPercent((int)((Integer)state.getValue((IProperty)BlockLiquid.LEVEL)));
            return player.posY < (double)height;
        }
        return false;
    }

    public static boolean hasCompleteHazmat(EntityLivingBase living) {
        for (EntityEquipmentSlot slot : ArmorSlot.getAll()) {
            ItemStack stack = living.getItemStackFromSlot(slot);
            if (stack != null && stack.getItem() instanceof ItemArmorHazmat) continue;
            return false;
        }
        return true;
    }

    public boolean hazmatAbsorbs(DamageSource source) {
        return source == DamageSource.inFire || source == DamageSource.inWall || source == DamageSource.lava || source == DamageSource.onFire || source == IC2DamageSource.electricity || source == IC2DamageSource.radiation;
    }

    @Override
    public boolean isMetalArmor(ItemStack itemstack, EntityPlayer player) {
        return false;
    }
}

