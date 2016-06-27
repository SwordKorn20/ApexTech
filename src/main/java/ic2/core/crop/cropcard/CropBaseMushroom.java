/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package ic2.core.crop.cropcard;

import ic2.api.crops.CropProperties;
import ic2.api.crops.ICropTile;
import ic2.core.crop.IC2CropCard;
import net.minecraft.item.ItemStack;

public class CropBaseMushroom
extends IC2CropCard {
    protected final String cropName;
    protected final String[] cropAttributes;
    protected final ItemStack cropDrop;

    public CropBaseMushroom(String cropName, String[] cropAttributes, ItemStack cropDrop) {
        this.cropName = cropName;
        this.cropAttributes = cropAttributes;
        this.cropDrop = cropDrop;
    }

    @Override
    public String getName() {
        return this.cropName;
    }

    @Override
    public CropProperties getProperties() {
        return new CropProperties(2, 0, 4, 0, 0, 4);
    }

    @Override
    public String[] getAttributes() {
        return this.cropAttributes;
    }

    @Override
    public int getMaxSize() {
        return 3;
    }

    @Override
    public boolean canGrow(ICropTile crop) {
        return crop.getCurrentSize() < this.getMaxSize() && crop.getStorageWater() > 0;
    }

    @Override
    public ItemStack getGain(ICropTile crop) {
        return this.cropDrop.copy();
    }

    @Override
    public int getGrowthDuration(ICropTile crop) {
        return 200;
    }
}

