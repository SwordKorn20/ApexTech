/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.world.World
 */
package ic2.api.crops;

import ic2.api.crops.CropProperties;
import ic2.api.crops.Crops;
import ic2.api.crops.ICropTile;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public abstract class CropCard {
    public abstract String getName();

    public abstract String getOwner();

    public String getDisplayName() {
        return this.getName();
    }

    public String getDiscoveredBy() {
        return "unknown";
    }

    public String desc(int i) {
        String[] att = this.getAttributes();
        if (att == null || att.length == 0) {
            return "";
        }
        if (i == 0) {
            String s = att[0];
            if (att.length >= 2) {
                s = s + ", " + att[1];
                if (att.length >= 3) {
                    s = s + ",";
                }
            }
            return s;
        }
        if (att.length < 3) {
            return "";
        }
        String s = att[2];
        if (att.length >= 4) {
            s = s + ", " + att[3];
        }
        return s;
    }

    public int getRootsLength(ICropTile cropTile) {
        return 1;
    }

    public abstract CropProperties getProperties();

    public String[] getAttributes() {
        return new String[0];
    }

    public abstract int getMaxSize();

    public int getGrowthDuration(ICropTile cropTile) {
        return this.getProperties().getTier() * 200;
    }

    public boolean canGrow(ICropTile cropTile) {
        return cropTile.getCurrentSize() < this.getMaxSize();
    }

    public int getWeightInfluences(ICropTile crop, int humidity, int nutrients, int air) {
        return humidity + nutrients + air;
    }

    public boolean canCross(ICropTile crop) {
        return crop.getCurrentSize() >= 3;
    }

    public boolean onRightClick(ICropTile cropTile, EntityPlayer player) {
        return cropTile.performManualHarvest();
    }

    public int getOptimalHarvestSize(ICropTile cropTile) {
        return this.getMaxSize();
    }

    public boolean canBeHarvested(ICropTile cropTile) {
        return cropTile.getCurrentSize() == this.getMaxSize();
    }

    public double dropGainChance() {
        return Math.pow(0.95, this.getProperties().getTier());
    }

    public abstract ItemStack getGain(ICropTile var1);

    public int getSizeAfterHarvest(ICropTile cropTile) {
        return 1;
    }

    public boolean onLeftClick(ICropTile cropTile, EntityPlayer player) {
        return cropTile.pick();
    }

    public float dropSeedChance(ICropTile crop) {
        if (crop.getCurrentSize() == 1) {
            return 0.0f;
        }
        float base = 0.5f;
        if (crop.getCurrentSize() == 2) {
            base /= 2.0f;
        }
        for (int i = 0; i < this.getProperties().getTier(); ++i) {
            base = (float)((double)base * 0.8);
        }
        return base;
    }

    public ItemStack getSeeds(ICropTile crop) {
        return crop.generateSeeds(crop.getCrop(), crop.getStatGrowth(), crop.getStatGain(), crop.getStatResistance(), crop.getScanLevel());
    }

    public void onNeighbourChange(ICropTile crop) {
    }

    public boolean isRedstoneSignalEmitter(ICropTile cropTile) {
        return false;
    }

    public int getEmittedRedstoneSignal(ICropTile cropTile) {
        return 0;
    }

    public void onBlockDestroyed(ICropTile crop) {
    }

    public int getEmittedLight(ICropTile crop) {
        return 0;
    }

    public boolean onEntityCollision(ICropTile crop, Entity entity) {
        return entity instanceof EntityLivingBase && entity.isSprinting();
    }

    public void tick(ICropTile cropTile) {
    }

    public boolean isWeed(ICropTile cropTile) {
        return cropTile.getCurrentSize() >= 2 && (cropTile.getCrop() == Crops.weed || cropTile.getStatGrowth() >= 24);
    }

    public World getWorld(ICropTile cropTile) {
        return cropTile.getWorld();
    }

    public String getUnlocalizedName() {
        return "crop." + this.getName() + ".name";
    }

    public List<ResourceLocation> getModelLocation() {
        ArrayList<ResourceLocation> ret = new ArrayList<ResourceLocation>();
        for (int i = 1; i <= this.getMaxSize(); ++i) {
            ret.add(new ResourceLocation(this.getOwner().toLowerCase(Locale.ENGLISH), "blocks/crop/" + this.getName() + "_" + i));
        }
        return ret;
    }
}

