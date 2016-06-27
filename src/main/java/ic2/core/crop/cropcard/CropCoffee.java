/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package ic2.core.crop.cropcard;

import ic2.api.crops.CropProperties;
import ic2.api.crops.ICropTile;
import ic2.core.block.state.IIdProvider;
import ic2.core.crop.IC2CropCard;
import ic2.core.item.type.CropResItemType;
import ic2.core.ref.ItemName;
import net.minecraft.item.ItemStack;

public class CropCoffee
extends IC2CropCard {
    @Override
    public String getName() {
        return "coffee";
    }

    @Override
    public String getDiscoveredBy() {
        return "Snoochy";
    }

    @Override
    public CropProperties getProperties() {
        return new CropProperties(7, 1, 4, 1, 2, 0);
    }

    @Override
    public String[] getAttributes() {
        return new String[]{"Leaves", "Ingredient", "Beans"};
    }

    @Override
    public int getMaxSize() {
        return 5;
    }

    @Override
    public boolean canGrow(ICropTile crop) {
        return crop.getCurrentSize() < 5 && crop.getLightLevel() >= 9;
    }

    @Override
    public int getWeightInfluences(ICropTile crop, int humidity, int nutrients, int air) {
        return (int)(0.4 * (double)humidity + 1.4 * (double)nutrients + 1.2 * (double)air);
    }

    @Override
    public int getGrowthDuration(ICropTile crop) {
        if (crop.getCurrentSize() == 3) {
            return (int)((double)super.getGrowthDuration(crop) * 0.5);
        }
        if (crop.getCurrentSize() == 4) {
            return (int)((double)super.getGrowthDuration(crop) * 1.5);
        }
        return super.getGrowthDuration(crop);
    }

    @Override
    public boolean canBeHarvested(ICropTile crop) {
        return crop.getCurrentSize() >= 4;
    }

    @Override
    public ItemStack getGain(ICropTile crop) {
        if (crop.getCurrentSize() == 4) {
            return null;
        }
        return ItemName.crop_res.getItemStack(CropResItemType.coffee_beans);
    }

    @Override
    public int getSizeAfterHarvest(ICropTile crop) {
        return 3;
    }
}

