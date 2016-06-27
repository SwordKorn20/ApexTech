/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.item.ItemStack
 */
package ic2.core.crop.cropcard;

import ic2.api.crops.CropProperties;
import ic2.api.crops.ICropTile;
import ic2.core.crop.IC2CropCard;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class CropBaseMetalCommon
extends IC2CropCard {
    protected final String cropName;
    protected final String[] cropAttributes;
    protected final Object[] cropRootsRequirement;
    protected final ItemStack cropDrop;

    public CropBaseMetalCommon(String cropName, String[] cropAttributes, Block[] cropRootsRequirement, ItemStack cropDrop) {
        this.cropName = cropName;
        this.cropAttributes = cropAttributes;
        this.cropRootsRequirement = cropRootsRequirement;
        this.cropDrop = cropDrop;
    }

    public CropBaseMetalCommon(String cropName, String[] cropAttributes, String[] cropRootsRequirement, ItemStack cropDrop) {
        this.cropName = cropName;
        this.cropAttributes = cropAttributes;
        this.cropRootsRequirement = cropRootsRequirement;
        this.cropDrop = cropDrop;
    }

    @Override
    public String getName() {
        return this.cropName;
    }

    @Override
    public CropProperties getProperties() {
        return new CropProperties(6, 2, 0, 0, 1, 0);
    }

    @Override
    public String[] getAttributes() {
        return this.cropAttributes;
    }

    @Override
    public int getMaxSize() {
        return 4;
    }

    @Override
    public boolean canGrow(ICropTile crop) {
        if (crop.getCurrentSize() < 3) {
            return true;
        }
        if (crop.getCurrentSize() == 3) {
            if (this.cropRootsRequirement == null || this.cropRootsRequirement.length == 0) {
                return true;
            }
            for (Object aux : this.cropRootsRequirement) {
                if (aux instanceof String && crop.isBlockBelow((String)aux)) {
                    return true;
                }
                if (!(aux instanceof Block) || !crop.isBlockBelow((Block)aux)) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    public int getRootsLength(ICropTile crop) {
        return 5;
    }

    @Override
    public boolean canBeHarvested(ICropTile crop) {
        return crop.getCurrentSize() == 4;
    }

    @Override
    public int getOptimalHarvestSize(ICropTile crop) {
        return 4;
    }

    @Override
    public ItemStack getGain(ICropTile crop) {
        return this.cropDrop.copy();
    }

    @Override
    public double dropGainChance() {
        return super.dropGainChance() / 2.0;
    }

    @Override
    public int getGrowthDuration(ICropTile crop) {
        if (crop.getCurrentSize() == 3) {
            return 2000;
        }
        return 800;
    }

    @Override
    public int getSizeAfterHarvest(ICropTile crop) {
        return 2;
    }
}

