/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockCactus
 *  net.minecraft.block.BlockStaticLiquid
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.entity.player.PlayerCapabilities
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.init.MobEffects
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.potion.Potion
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraft.world.biome.Biome
 *  net.minecraftforge.common.BiomeDictionary
 *  net.minecraftforge.common.BiomeDictionary$Type
 */
package ic2.core.crop.cropcard;

import ic2.api.crops.CropProperties;
import ic2.api.crops.ICropTile;
import ic2.api.item.ItemWrapper;
import ic2.core.IC2;
import ic2.core.IC2DamageSource;
import ic2.core.crop.IC2CropCard;
import ic2.core.crop.TileEntityCrop;
import ic2.core.util.StackUtil;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCactus;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;

public class CropEating
extends IC2CropCard {
    private final double movementMultiplier = 0.5;
    private final double length = 1.0;
    private static final IC2DamageSource damage = new IC2DamageSource("cropEating");

    @Override
    public String getDiscoveredBy() {
        return "Hasudako";
    }

    @Override
    public String getName() {
        return "eatingplant";
    }

    @Override
    public CropProperties getProperties() {
        return new CropProperties(6, 1, 1, 3, 1, 4);
    }

    @Override
    public String[] getAttributes() {
        return new String[]{"Bad", "Food"};
    }

    @Override
    public int getMaxSize() {
        return 6;
    }

    @Override
    public boolean canGrow(ICropTile crop) {
        if (crop.getCurrentSize() < 3) {
            return crop.getLightLevel() > 10;
        }
        return crop.isBlockBelow((Block)Blocks.LAVA) && crop.getCurrentSize() < this.getMaxSize() && crop.getLightLevel() > 10;
    }

    @Override
    public int getOptimalHarvestSize(ICropTile crop) {
        return 4;
    }

    @Override
    public boolean canBeHarvested(ICropTile crop) {
        return crop.getCurrentSize() >= 4 && crop.getCurrentSize() < 6;
    }

    @Override
    public ItemStack getGain(ICropTile crop) {
        if (crop.getCurrentSize() >= 4 && crop.getCurrentSize() < 6) {
            return new ItemStack((Block)Blocks.CACTUS);
        }
        return null;
    }

    @Override
    public void tick(ICropTile crop) {
        List list;
        if (crop.getCurrentSize() == 1) {
            return;
        }
        TileEntityCrop te = (TileEntityCrop)crop;
        BlockPos coords = crop.getLocation();
        double xcentered = (double)coords.getX() + 0.5;
        double ycentered = (double)coords.getY() + 0.5;
        double zcentered = (double)coords.getZ() + 0.5;
        if (crop.getCustomData().getBoolean("eaten")) {
            StackUtil.dropAsEntity(crop.getWorld(), coords, new ItemStack(Items.ROTTEN_FLESH));
            crop.getCustomData().setBoolean("eaten", false);
        }
        if ((list = crop.getWorld().getEntitiesWithinAABB((Class)EntityLivingBase.class, new AxisAlignedBB(xcentered - 1.0, (double)coords.getY(), zcentered - 1.0, xcentered + 1.0, (double)coords.getY() + 1.0 + 1.0, zcentered + 1.0))).isEmpty()) {
            return;
        }
        Collections.shuffle(list);
        for (EntityLivingBase entity : list) {
            if (entity instanceof EntityPlayer && ((EntityPlayer)entity).capabilities.isCreativeMode) continue;
            entity.motionX = (xcentered - entity.posX) * 0.5;
            entity.motionZ = (zcentered - entity.posZ) * 0.5;
            if (entity.motionY > -0.05) {
                entity.motionY = -0.05;
            }
            entity.attackEntityFrom((DamageSource)damage, (float)crop.getCurrentSize() * 2.0f);
            if (!CropEating.hasMetalAromor(entity)) {
                entity.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 64, 50));
                entity.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, 64, 0));
                entity.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 64, 0));
            }
            if (this.canGrow(crop)) {
                te.setGrowthPoints((int)((double)te.getGrowthPoints() + (double)te.calcGrowthRate() * 0.5));
            }
            crop.getWorld().playSound(null, xcentered, ycentered, zcentered, SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.BLOCKS, 1.0f, IC2.random.nextFloat() * 0.1f + 0.9f);
            crop.getCustomData().setBoolean("eaten", true);
            break;
        }
    }

    @Override
    public int getRootsLength(ICropTile crop) {
        return 5;
    }

    @Override
    public int getGrowthDuration(ICropTile crop) {
        float multiplier = 1.0f;
        BlockPos coords = crop.getLocation();
        Biome biome = crop.getWorld().getBiomeGenForCoords(coords);
        if (BiomeDictionary.isBiomeOfType((Biome)biome, (BiomeDictionary.Type)BiomeDictionary.Type.SWAMP) || BiomeDictionary.isBiomeOfType((Biome)biome, (BiomeDictionary.Type)BiomeDictionary.Type.MOUNTAIN)) {
            multiplier /= 1.5f;
        }
        return (int)((float)super.getGrowthDuration(crop) * (multiplier /= 1.0f + (float)crop.getAirQuality() / 10.0f));
    }

    private static boolean hasMetalAromor(EntityLivingBase entity) {
        if (!(entity instanceof EntityPlayer)) {
            return false;
        }
        EntityPlayer player = (EntityPlayer)entity;
        for (ItemStack stack : player.inventory.armorInventory) {
            if (stack == null || !ItemWrapper.isMetalArmor(stack, player)) continue;
            return true;
        }
        return false;
    }
}

