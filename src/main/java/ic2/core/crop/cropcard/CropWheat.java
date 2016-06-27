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

public class CropWheat
extends IC2CropCard {
    @Override
    public String getName() {
        return "wheat";
    }

    @Override
    public String getDiscoveredBy() {
        return "Notch";
    }

    @Override
    public CropProperties getProperties() {
        return new CropProperties(1, 0, 4, 0, 0, 2);
    }

    @Override
    public String[] getAttributes() {
        return new String[]{"Yellow", "Food", "Wheat"};
    }

    @Override
    public int getMaxSize() {
        return 7;
    }

    @Override
    public boolean canGrow(ICropTile crop) {
        return crop.getCurrentSize() < 7 && crop.getLightLevel() >= 9;
    }

    @Override
    public ItemStack getGain(ICropTile crop) {
        return new ItemStack(Items.WHEAT, 1);
    }

    @Override
    public ItemStack getSeeds(ICropTile crop) {
        if (crop.getStatGain() <= 1 && crop.getStatGrowth() <= 1 && crop.getStatResistance() <= 1) {
            return new ItemStack(Items.WHEAT_SEEDS);
        }
        return super.getSeeds(crop);
    }

    @Override
    public int getSizeAfterHarvest(ICropTile crop) {
        return 2;
    }
}

