/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 */
package ic2.core.crop.cropcard;

import ic2.api.crops.CropProperties;
import ic2.api.crops.ICropTile;
import ic2.core.crop.IC2CropCard;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class CropWeed
extends IC2CropCard {
    @Override
    public String getName() {
        return "weed";
    }

    @Override
    public CropProperties getProperties() {
        return new CropProperties(0, 0, 0, 1, 0, 5);
    }

    @Override
    public String[] getAttributes() {
        return new String[]{"Weed", "Bad"};
    }

    @Override
    public int getMaxSize() {
        return 5;
    }

    @Override
    public int getOptimalHarvestSize(ICropTile crop) {
        return 1;
    }

    @Override
    public boolean onLeftClick(ICropTile crop, EntityPlayer player) {
        return false;
    }

    @Override
    public boolean canBeHarvested(ICropTile crop) {
        return false;
    }

    @Override
    public ItemStack getGain(ICropTile crop) {
        return null;
    }

    @Override
    public int getGrowthDuration(ICropTile crop) {
        return 300;
    }

    @Override
    public boolean onEntityCollision(ICropTile crop, Entity entity) {
        return false;
    }
}

