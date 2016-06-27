/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.MobEffects
 *  net.minecraft.inventory.Container
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.item.EnumRarity
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
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
 *  net.minecraftforge.fml.common.eventhandler.EventBus
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.item.armor;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItemManager;
import ic2.core.IC2;
import ic2.core.Platform;
import ic2.core.init.InternalName;
import ic2.core.item.armor.ItemArmorElectric;
import ic2.core.ref.ItemName;
import ic2.core.util.Keyboard;
import ic2.core.util.StackUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemArmorNanoSuit
extends ItemArmorElectric {
    public ItemArmorNanoSuit(ItemName name, EntityEquipmentSlot armorType) {
        super(name, InternalName.nano, armorType, 1000000.0, 1600.0, 3);
        if (armorType == EntityEquipmentSlot.FEET) {
            MinecraftForge.EVENT_BUS.register((Object)this);
        }
    }

    @Override
    public ISpecialArmor.ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) {
        if (source == DamageSource.fall && this.armorType == EntityEquipmentSlot.FEET) {
            int energyPerDamage = this.getEnergyPerDamage();
            int damageLimit = Integer.MAX_VALUE;
            if (energyPerDamage > 0) {
                damageLimit = (int)Math.min((double)damageLimit, 25.0 * ElectricItem.manager.getCharge(armor) / (double)energyPerDamage);
            }
            return new ISpecialArmor.ArmorProperties(10, damage < 8.0 ? 1.0 : 0.875, damageLimit);
        }
        return super.getProperties(player, armor, source, damage, slot);
    }

    @SubscribeEvent
    public void onEntityLivingFallEvent(LivingFallEvent event) {
        ItemStack armor;
        EntityLivingBase entity;
        if (IC2.platform.isSimulating() && event.getEntity() instanceof EntityLivingBase && (armor = (entity = (EntityLivingBase)event.getEntity()).getItemStackFromSlot(EntityEquipmentSlot.FEET)) != null && armor.getItem() == this) {
            int fallDamage = (int)event.getDistance() - 3;
            if (fallDamage >= 8) {
                return;
            }
            double energyCost = this.getEnergyPerDamage() * fallDamage;
            if (energyCost <= ElectricItem.manager.getCharge(armor)) {
                ElectricItem.manager.discharge(armor, energyCost, Integer.MAX_VALUE, true, false, false);
                event.setCanceled(true);
            }
        }
    }

    public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
        NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(stack);
        byte toggleTimer = nbtData.getByte("toggleTimer");
        boolean ret = false;
        if (this.armorType == EntityEquipmentSlot.HEAD) {
            IC2.platform.profilerStartSection("NanoHelmet");
            boolean Nightvision = nbtData.getBoolean("Nightvision");
            short hubmode = nbtData.getShort("HudMode");
            if (IC2.keyboard.isAltKeyDown(player) && IC2.keyboard.isModeSwitchKeyDown(player) && toggleTimer == 0) {
                toggleTimer = 10;
                boolean bl = Nightvision = !Nightvision;
                if (IC2.platform.isSimulating()) {
                    nbtData.setBoolean("Nightvision", Nightvision);
                    if (Nightvision) {
                        IC2.platform.messagePlayer(player, "Nightvision enabled.", new Object[0]);
                    } else {
                        IC2.platform.messagePlayer(player, "Nightvision disabled.", new Object[0]);
                    }
                }
            }
            if (IC2.keyboard.isAltKeyDown(player) && IC2.keyboard.isHudModeKeyDown(player) && toggleTimer == 0) {
                toggleTimer = 10;
                hubmode = hubmode == 2 ? 0 : (short)(hubmode + 1);
                if (IC2.platform.isSimulating()) {
                    nbtData.setShort("HudMode", hubmode);
                    switch (hubmode) {
                        case 0: {
                            IC2.platform.messagePlayer(player, "HUD disabled.", new Object[0]);
                            break;
                        }
                        case 1: {
                            IC2.platform.messagePlayer(player, "HUD (basic) enabled.", new Object[0]);
                            break;
                        }
                        case 2: {
                            IC2.platform.messagePlayer(player, "HUD (extended) enabled", new Object[0]);
                        }
                    }
                }
            }
            if (IC2.platform.isSimulating() && toggleTimer > 0) {
                toggleTimer = (byte)(toggleTimer - 1);
                nbtData.setByte("toggleTimer", toggleTimer);
            }
            if (Nightvision && IC2.platform.isSimulating() && ElectricItem.manager.use(stack, 1.0, (EntityLivingBase)player)) {
                BlockPos pos = new BlockPos(MathHelper.floor_double((double)player.posX), MathHelper.floor_double((double)player.posZ), MathHelper.floor_double((double)player.posY));
                int skylight = player.worldObj.getLightFromNeighbors(pos);
                if (skylight > 8) {
                    IC2.platform.removePotion((EntityLivingBase)player, MobEffects.NIGHT_VISION);
                    player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 100, 0, true, true));
                } else {
                    IC2.platform.removePotion((EntityLivingBase)player, MobEffects.BLINDNESS);
                    player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 300, 0, true, true));
                }
                ret = true;
            }
            IC2.platform.profilerEndSection();
        }
        if (ret) {
            player.inventoryContainer.detectAndSendChanges();
        }
    }

    @Override
    public double getDamageAbsorptionRatio() {
        return 0.9;
    }

    @Override
    public int getEnergyPerDamage() {
        return 5000;
    }

    @SideOnly(value=Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.UNCOMMON;
    }
}

