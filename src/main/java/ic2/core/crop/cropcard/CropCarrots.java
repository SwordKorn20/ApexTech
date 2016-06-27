/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 */
package ic2.core.crop.cropcard;

import ic2.api.crops.CropProperties;
import ic2.api.crops.ICropTile;
import ic2.core.crop.IC2CropCard;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CropCarrots
extends IC2CropCard {
    @Override
    public String getName() {
        return "carrots";
    }

    @Override
    public CropProperties getProperties() {
        return new CropProperties(2, 0, 4, 0, 0, 2);
    }

    @Override
    public String[] getAttributes() {
        return new String[]{"Orange", "Food", "Carrots"};
    }

    @Override
    public int getMaxSize() {
        return 3;
    }

    @Override
    public boolean canGrow(ICropTile crop) {
        return crop.getCurrentSize() < 3 && crop.getLightLevel() >= 9;
    }

    @Override
    public int getOptimalHarvestSize(ICropTile crop) {
        return 3;
    }

    @Override
    public boolean canBeHarvested(ICropTile crop) {
        return crop.getCurrentSize() == 3;
    }

    @Override
    public ItemStack getGain(ICropTile crop) {
        return new ItemStack(Items.CARROT).copy();
    }

    @Override
    public int getSizeAfterHarvest(ICropTile crop) {
        return 1;
    }
}

